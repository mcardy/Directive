package com.minnymin.util.directive;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

/**
 * A branch of directives
 *
 * @author minnymin3
 *
 */
public class DirectiveTree {

	private Set<DirectiveTree> subDirectives;
	private String label;
	private Method executor;

	/**
	 * Creates a new branch
	 * @param label The label of the branch
	 * @param executor The executor of the branch
	 */
	public DirectiveTree(String label, Method executor) {
		this.executor = executor;
		this.label = label;
		this.subDirectives = new HashSet<DirectiveTree>();
	}

	/**
	 * Adds a sub directive with the given label
	 * @param label The label of the directive
	 * @param tree The tree of the directive
	 */
	public void addSubDirective(DirectiveTree tree) {
		this.subDirectives.add(tree);
	}

	/**
	 * Gets a sub directive by the provided label
	 * @param label The directive to get
	 * @return A DirectiveTree
	 */
	public DirectiveTree getSubDirective(String label) {
		for (DirectiveTree tree : this.subDirectives) {
			if (tree.getLabel().equalsIgnoreCase(label)) {
				return tree;
			}
		}
		return null;
	}

	public String getLabel() {
		return this.label;
	}

	/**
	 * Checks if this is the end of the tree or not
	 * @return False if end of tree
	 */
	public boolean isBranch() {
		return this.subDirectives.isEmpty();
	}

	/**
	 * Gets the executor for this branch
	 * @return A method executor annotated by @Directive
	 */
	public Method getExecutor() {
		return this.executor;
	}

	/**
	 * Sets the executor for this branch
	 * @param executor A method executor annotated by @Directive
	 */
	public void setExecutor(Method executor) {
		this.executor = executor;
	}

	/**
	 * Gets the spec of this branch containing all sub branches as childs
	 * @return A new CommandSpec for this branch
	 */
	public CommandSpec getSpec() {
		DirectiveExecutor executor = new DirectiveExecutor(this.executor);
		CommandSpec.Builder spec = CommandSpec.builder();
		spec.executor(executor);
		spec.arguments(GenericArguments.remainingJoinedStrings(Texts.of("raw")));

		if (this.executor != null) {
			Directive directive = this.executor.getAnnotation(Directive.class);
			spec.description(Texts.of(directive.description()));
			if (directive.permission() != "") {
				spec.permission(directive.permission());
			}
		}

		for (DirectiveTree entry : this.subDirectives) {
			spec.child(entry.getSpec(), entry.getLabel());
		}
		return spec.build();
	}

	private class DirectiveExecutor implements CommandExecutor {

		private Method executor;

		public DirectiveExecutor(Method executor) {
			this.executor = executor;
		}

		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (this.executor != null) {
				try {
					String rawArguments = args.<String>getOne("raw").get();
					args.putArg("args", rawArguments.split(" "));
					return (CommandResult) this.executor.invoke(null, src, args);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} else {
				src.sendMessage(Texts.of("This command is not handled!"));
			}
			return CommandResult.empty();
		}

	}

}
