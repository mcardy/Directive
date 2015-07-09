package com.minnymin.util.directive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Directive command annotation
 * 
 * @author minnymin3
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Directive {

	/**
	 * The names to register the command under
	 */
	public String[] names();
	
	/**
	 * Description of the command, default nothing
	 */
	public String description() default "";
	
	/**
	 * Permission of the command, default nothing
	 */
	public String permission() default "";
	
}
