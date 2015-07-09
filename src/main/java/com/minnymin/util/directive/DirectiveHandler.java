package com.minnymin.util.directive;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.Game;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

/**
 * Handles collection and registration of directives
 * 
 * @author minnymin3
 *
 */
public class DirectiveHandler {

	private Set<DirectiveTree> commands;
	private Object plugin;
	private Game game;
	
	/**
	 * Initialize the directive handler
	 * @param plugin Plugin object
	 * @param game Game from event initialization
	 */
	public DirectiveHandler(Object plugin, Game game) {
		this.commands = new HashSet<DirectiveTree>();
		this.plugin = plugin;
		this.game = game;
	}
	
	/**
	 * Adds all directives in a class
	 * @param cls The class to add
	 */
	public void addDirectives(Class<?> cls) {
		for (Method m : cls.getMethods()) {
			if (m.getAnnotation(Directive.class) != null) {
				try {
					addDirective(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Adds a directive method
	 * @param executor The executor method
	 * @throws Exception Thrown if method has an issue
	 */
	public void addDirective(Method executor) throws Exception {
		Directive directive = executor.getAnnotation(Directive.class);
		if (directive == null) {
			throw new Exception("Error registering directive method " + executor.getName() + ": No directive");
		}
		if (!Modifier.isStatic(executor.getModifiers())) {
			throw new Exception("Error registering directive method " + executor.getName() + ": Method not static!");
		}
		if (executor.getParameterTypes()[0] != CommandSource.class ||
				executor.getParameterTypes()[1] != CommandContext.class) {
			throw new Exception("Error registering directive method " + executor.getName() + ": Incorrect arguments." +
				"Must have CommandSource as first argument and CommandContext as second.");
		}
		if (executor.getReturnType() != CommandResult.class) {
			throw new Exception("Error registering directive method " + executor.getName() + ": Incorrect return type." +
				"Directive must return type of CommandResult.");
		}
		String[] labels = directive.names();
		for (String label : labels) {
			String[] cmd = label.split("\\.");
			DirectiveTree current = null;
			DirectiveTree next = null;
			for (int i = 0; i < cmd.length; i++) {
				// Set the next directive
				if (current == null) {
					next = this.getDirective(cmd[i]);
				} else {
					next = current.getSubDirective(cmd[i]);
				}
				// Check the next directive, create if necessary
				if (next == null && current == null) {
					next = new DirectiveTree(cmd[i], null);
					this.commands.add(next);
				} else if (next == null) {
					next = new DirectiveTree(cmd[i], null);
					current.addSubDirective(next);
				}
				// Set current to next
				current = next;
				// Set executor if not set yet
				if (i+1==cmd.length) {
					current.setExecutor(executor);
				}
			}
		}
	}
	
	/**
	 * Gets a directive tree from a base label
	 * @param label The label to find
	 * @return A directive tree from the base label
	 */
	public DirectiveTree getDirective(String label) {
		for (DirectiveTree tree : this.commands) {
			if (tree.getLabel().equalsIgnoreCase(label)) {
				return tree;
			}
		}
		return null;
	}
	
	/**
	 * Registers all directives after they have been added
	 */
	public void registerDirectives() {
		for (DirectiveTree tree : this.commands) {
			game.getCommandDispatcher().register(this.plugin, tree.getSpec(), tree.getLabel());
		}
	}
	
}
