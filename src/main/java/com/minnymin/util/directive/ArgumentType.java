package com.minnymin.util.directive;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.CommandElement;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.world.DimensionType;

import com.google.common.collect.ImmutableMap;

/**
 * Argument types for specifying arguments in Directive
 * 
 * @author minnymin3
 *
 */
public enum ArgumentType {

	BOOLEAN("ChoicesCommandElement", false, ArgumentUtils.toArray(Map.class, boolean.class),
			ArgumentUtils.BOOLEAN_CHOICES, 5),
	INTEGER("IntegerElement"),
	LOCATION("CatalogedTypeCommandElement", true, ArgumentUtils.toArray(Class.class), DimensionType.class),
	PLAYER("PlayerCommandElement", true, ArgumentUtils.toArray(boolean.class), false),
	REMAINING("RemainingJoinedStringsCommandElement", false, ArgumentUtils.toArray(boolean.class), false),
	STRING("StringElement"),
	WORLD("WorldPropertiesCommandElement", true),

	// Optional Arguments
	OPTIONAL_BOOLEAN(ArgumentType.BOOLEAN),
	OPTIONAL_INTEGER(ArgumentType.INTEGER),
	OPTIONAL_LOCATION(ArgumentType.LOCATION),
	OPTIONAL_PLAYER(ArgumentType.PLAYER),
	OPTIONAL_REMAINING(ArgumentType.REMAINING),
	OPTIONAL_STRING(ArgumentType.STRING),
	OPTIONAL_WORLD(ArgumentType.WORLD);

	private Constructor<?> constructor;
	private boolean needGame;
	private Object[] options;

	private boolean optional = false;
	private ArgumentType optionalMaster;

	/**
	 * Creates an optional argument type from another argument type
	 * 
	 * @param master The argument type to make optional
	 */
	private ArgumentType(ArgumentType master) {
		this.optional = true;
		this.optionalMaster = master;
	}

	/**
	 * Creates a new ArgumentType with class name in GenericArguments
	 * 
	 * @param className Name in GenericArguments$className
	 */
	private ArgumentType(String className) {
		this(className, false);
	}

	/**
	 * Creates a new ArgumentType with class name in GenericArguments and
	 * optional game
	 * 
	 * @param className Name in GenericArguments$className
	 * @param needGame Whether or not game is required
	 */
	private ArgumentType(String className, boolean needGame) {
		this(className, needGame, ArgumentUtils.blankArray());
	}

	/**
	 * Creates a new ArgumentType with class name in GenericArguments, optional
	 * game, constructor's arguments and passed options
	 * 
	 * @param className Name in GenericArguments$className
	 * @param needGame Whether or not game is required
	 * @param arguments Constructor arguments after Text and optional Game
	 * @param options Options to pass after Text and optional Game to
	 *            constructor
	 */
	@SuppressWarnings("unchecked")
	private ArgumentType(String className, boolean needGame, Class<?>[] arguments, Object... options) {
		try {
			Class<? extends CommandElement> cls = (Class<? extends CommandElement>) Class
					.forName("org.spongepowered.api.util.command.args.GenericArguments$" + className);
			this.constructor = cls.getDeclaredConstructor(ArgumentUtils.argumentArray(needGame, arguments));
			this.constructor.setAccessible(true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		this.needGame = needGame;
		this.options = options;
	}

	/**
	 * Creates a new ArgumentType with class name, optional game, constructor's
	 * arguments and passed options
	 * 
	 * @param className Name of class
	 * @param needGame Whether or not game is required
	 * @param arguments Constructor arguments after Text and optional Game
	 * @param options Options to pass after Text and optional Game to
	 *            constructor
	 */
	private ArgumentType(Class<? extends CommandElement> cls, boolean needGame, Class<?>[] arguments, Object... options) {
		try {
			this.constructor = cls.getDeclaredConstructor(ArgumentUtils.argumentArray(needGame, arguments));
			this.constructor.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		this.needGame = needGame;
		this.options = options;
	}

	/**
	 * Constructs new element
	 * 
	 * @param key The key of the argument
	 * @param game The game object, passed if required
	 * @return A new CommandElement of this object
	 */
	public CommandElement construct(String key, Game game) {
		if (this.optional) {
			return GenericArguments.optional(this.optionalMaster.construct(key, game));
		} else {
			try {
				if (this.needGame) {
					return (CommandElement) this.constructor.newInstance(ArgumentUtils.constructorArray(key, game,
							options));
				} else {
					return (CommandElement) this.constructor.newInstance(ArgumentUtils.constructorArray(key, options));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Static utility methods and maps
	 * 
	 * @author minnymin3
	 *
	 */
	private static class ArgumentUtils {
		private static final Map<String, Boolean> BOOLEAN_CHOICES = ImmutableMap.<String, Boolean> builder()
				.put("true", true).put("t", true).put("y", true).put("yes", true).put("verymuchso", true)
				.put("1", true).put("false", false).put("f", false).put("n", false).put("no", false)
				.put("notatall", false).put("0", false).build();

		private static Class<?>[] toArray(Class<?>... cls) {
			return cls;
		}

		private static Class<?>[] blankArray() {
			return new Class<?>[0];
		}

		private static Object[] constructorArray(String key, Game game, Object[] options) {
			List<Object> list = new ArrayList<Object>();
			list.add(Texts.of(key));
			list.add(game);
			for (Object o : options) {
				list.add(o);
			}
			return list.toArray(new Object[list.size()]);
		}

		private static Object[] constructorArray(String key, Object[] options) {
			List<Object> list = new ArrayList<Object>();
			list.add(Texts.of(key));
			for (Object o : options) {
				list.add(o);
			}
			return list.toArray(new Object[list.size()]);
		}

		private static Class<?>[] argumentArray(boolean game, Class<?>[] options) {
			List<Class<?>> list = new ArrayList<Class<?>>();
			list.add(Text.class);
			if (game) {
				list.add(Game.class);
			}
			for (Class<?> c : options) {
				list.add(c);
			}
			return list.toArray(new Class<?>[list.size()]);
		}
	}

}
