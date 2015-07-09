# Directive
A lightweight, annotation-based command framework for Sponge

# Usage
## Creating commands

Let's begin by creating a new command called example

```java
@Directive(name = {"example"}, description = "An example command", permission = "my.example.command")
public static CommandResult exampleCommand(CommandSource src, CommandContext args) {
  src.sendMessage(Texts.of("Hello Sender!"));
  return CommandResult.success();
}
```

To mark a method as a directive command, add the @Directive annotation to it.
This annotation has the following attributes: name, description and permission.
Name is an array of aliases, description is a short description of the command
and permission is the permission of the command. These are equivalent to Sponge's
CommandSpec arguments.

Directive methods must satisfy the following:
- Method must be static
- Method must return CommandResult
- Method must have arguments of CommandSource and CommandContext

Sub commands can be registered by adding periods into the name of the command.
For example, 'example.test' would register test as a subcommand of example and
could be executed by '/example test'.

## Registering commands

To begin, create a new DirectiveHandler. This is the object you will use to
register directives.

```java
@Subscribe
public void onPreInit(PreInitializationEvent event) {
  // Create a new DirectiveHandler with the plugin object (this) and the game
  DirectiveHandler handler = new DirectiveHandler(this, event.getGame());
}
```

From here, utilize the method handler.addDirectives(Class cls). For example, if
you had a class called MyCommands that contained all of your commands, you would
call ```handler.addDirectives(MyCommands.class)```.

Once you have added all of your directives, the method handler.registerDirectives()
must be called to add all of the commands through Sponge's API.

## Argument parsing
Directive now has an argument parsing system. More enums may be added to the
ArgumentType class.

```java
@Directive(names = {"exargs"}, argumentLabels = {"msg"}, arguments = {ArgumentType.OPTIONAL_STRING})
public static CommandResult exampleArgumentCommand(CommandSource src, CommandContext args) {
	src.sendMessage(Texts.of(args.<String>getOne("msg").get()));
	return CommandResult.success();
}
```

Arrays of argumentLabels and arguments can be created in the @Directive annotation.
They must be the same length and the order they are in is the order that they will
be added to the command spec. You can then access the arguments just as you would
if it was a normal Sponge command through CommandContext.

A full example plugin can be found at
https://github.com/minnymin3/Directive/blob/master/src/main/java/com/minnymin/demo/DemoPlugin.java
