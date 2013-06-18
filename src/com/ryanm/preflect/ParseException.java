package com.ryanm.preflect;

/**
 * Use this to indicate failure when codec-ing in your {@link Codec}
 * 
 * @author ryanm
 */
public class ParseException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ParseException( final String message )
	{
		super( message );
	}

	/**
	 * @param cause
	 */
	public ParseException( final Throwable cause )
	{
		super( cause );
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParseException( final String message, final Throwable cause )
	{
		super( message, cause );
	}
}
