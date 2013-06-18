package com.ryanm.preflect;

import java.util.HashMap;
import java.util.Map;

import com.ryanm.preflect.imp.BooleanType;
import com.ryanm.preflect.imp.EnumType;
import com.ryanm.preflect.imp.FloatType;
import com.ryanm.preflect.imp.IntType;
import com.ryanm.preflect.imp.StringType;
import com.ryanm.preflect.imp.VoidType;

/**
 * Extend this to control how variable values are converted to json and back.
 * Remember to {@link #register(Codec)} your new variable type or it won't be
 * used
 * 
 * @author ryanm
 * @param <T>
 *          Type of variable to handle
 */
public abstract class Codec<T> {
	/**
	 * Variable type
	 */
	public final Class<? extends T> type;

	/**
	 * @param type
	 */
	protected Codec( final Class<? extends T> type ) {
		this.type = type;
	}

	/**
	 * Encodes the value of a given object
	 * 
	 * @param value
	 *          The value to encode
	 * @return The String encoding for the value, or null if encoding was not
	 *         possible
	 */
	public abstract String encode( T value );

	/**
	 * Decodes the encoded string into a value object
	 * 
	 * @param encoded
	 *          The encoded string
	 * @param runtimeType
	 *          The desired type of the object
	 * @return The value of the encoded string
	 * @throws ParseException
	 *           If there is a problem parsing the encoded string
	 */
	public abstract T decode( String encoded, Class<? extends T> runtimeType )
			throws ParseException;

	private static final Map<Class<?>, Codec<?>> types =
			new HashMap<Class<?>, Codec<?>>();

	static {
		register( new BooleanType() );
		register( new EnumType() );
		register( new FloatType() );
		register( new IntType() );
		register( new StringType() );
		register( new VoidType() );
	}

	/**
	 * Call this to register your {@link Codec}s
	 * 
	 * @param varType
	 */
	public static <T> void register( final Codec<T> varType ) {
		types.put( varType.type, varType );
	}

	/**
	 * @param type
	 * @return A {@link Codec} for the type, or <code>null</code> if none is found
	 */
	static Codec<?> get( Class<?> type ) {
		Codec<?> t = types.get( type );

		while( t == null && type != null ) {
			type = type.getSuperclass();
			t = types.get( type );
		}

		return t;
	}
}
