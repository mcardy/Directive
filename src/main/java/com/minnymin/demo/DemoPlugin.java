package com.minnymin.demo;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;

import com.minnymin.util.directive.Directive;
import com.minnymin.util.directive.DirectiveHandler;

/**
 * A demo plugin to demonstrate Directive command framework
 * 
 * @author minnymin3
 *
 */
@Plugin(id = "demo", name = "Demo Plugin")
public class DemoPlugin {

	@Subscribe
    public void onPreInit(PreInitializationEvent event) {
		// Create a new DirectiveHandler with the plugin object (this) and the game
		DirectiveHandler handler = new DirectiveHandler(this, event.getGame());
		
		// Add all of your directive commands through the addDirectives method
		handler.addDirectives(DemoPlugin.class);
		
		// After you have added all of the directives, register them
		handler.registerDirectives();
	}
	
	// Directive contains name array, description string and permission string
	@Directive(names = { "example", "test" }, description = "An example command", permission = "demo.command")
	// Method is static, returns CommandResult and has CommandSource and CommandContext as arguments
	public static CommandResult exampleCommand(CommandSource src, CommandContext args) {
		src.sendMessage(Texts.of("Hello world! This is an example command"));
		return CommandResult.success();
	}
	
	// Sub commands are seperated by '.'
	// Ex, this is '/example sub'
	@Directive(names = {"example.sub"})
	public static CommandResult exampleSubCommand(CommandSource src, CommandContext args) {
		src.sendMessage(Texts.of("Hello world! This is an example sub command"));
		return CommandResult.success();
	}
	
}
