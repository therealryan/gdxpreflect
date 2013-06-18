package com.ryanm.preflect;

import com.ryanm.preflect.annote.Variable;

/**
 * Handy utility for manipulating object trees that have been marked up with
 * {@link Variable} annotations
 * 
 * @author ryanm
 */
public class Preflect {
	/**
	 * Sink for log messages. Implement as desired.
	 */
	public static Log log = null;

	/**
	 * When <code>true</code>, a {@link StructuralError} will be thrown if a
	 * private field or method is encountered with a {@link Variable} annotation,
	 * when <code>false</code>, no error is raised and the variable is ignored.
	 * Default value is <code>true</code>
	 */
	public static boolean errorOnNonPublicVariables = true;

	/**
	 * The name under which applied configurations are automatically saved. Set to
	 * <code>null</code> to disable automatic saving
	 */
	public static String autoSaveName = "default";

	private Preflect() {
	}

	public static void log( String message ) {
		if( log != null ) {
			log.log( message );
		}
	}

	public static void error( String message, Throwable e ) {
		if( log != null ) {
			log.error( message, e );
		}
	}

	/**
	 * Interface that Preflect will log to. Implement and set #log to direct
	 * messages as you wish
	 */
	public static interface Log {
		public void log( String message );

		public void error( String message, Throwable e );
	}
}
