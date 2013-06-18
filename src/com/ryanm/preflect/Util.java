package com.ryanm.preflect;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.utils.JsonWriter;
import com.ryanm.preflect.annote.Category;
import com.ryanm.preflect.annote.Order;
import com.ryanm.preflect.annote.Summary;
import com.ryanm.preflect.annote.Variable;
import com.ryanm.preflect.annote.WidgetHint;

/**
 * Util methods
 * 
 * @author ryanm
 */
@SuppressWarnings( "rawtypes" )
class Util {
	/**
	 * JSON key for variable name
	 */
	static final String NAME = "name";

	/**
	 * JSON key for variable type
	 */
	static final String TYPE = "type";

	/**
	 * JSON key for variable summary
	 */
	static final String DESC = "desc";

	/**
	 * JSON key for variable category
	 */
	static final String CAT = "cat";

	/**
	 * JSON key for variable order
	 */
	static final String ORDER = "order";

	/**
	 * JSON key for variable order
	 */
	static final String VALUE = "value";

	/**
	 * JSON key for a boolean that indicates a variable is read-only
	 */
	static final String READ_ONLY = "readonly";

	/**
	 * Tag for the configuration string in the {@link Intent}
	 */
	static final String CONF_TAG = "conf";

	/**
	 * Tag for the name of the activity that started the {@link PreflectActivity}
	 * in the {@link Intent}
	 */
	static final String RETURNTO_TAG = "returnto";

	/**
	 * Tag for the flag to indicate if we should let the user save multiple
	 * configurations
	 */
	static final String SHOW_PERSIST_MENU = "showpersist";

	/**
	 * Tag for the flag to indicate if we should make the user confirm
	 * configuration changes
	 */
	static final String SHOW_CONFIRM_MENU = "showconfirm";

	private Util() {
	}

	/**
	 * Maps from primitive name to primitive class
	 */
	private static final Map<String, Class> primitiveClassMap =
			new HashMap<String, Class>();
	static {
		Class[] prims =
				new Class[] { boolean.class, byte.class, short.class, char.class,
						int.class, float.class, long.class, double.class, void.class };
		for( Class c : prims ) {
			primitiveClassMap.put( c.getName(), c );
		}

	}

	/**
	 * Basically {@link Class#forName(String)}, but handles primitive types like
	 * int.class
	 * 
	 * @param name
	 * @return the class for that name, or <code>null</code> if not found
	 */
	static Class getType( String name ) {
		Class c = primitiveClassMap.get( name );
		if( c == null ) {
			try {
				c = Class.forName( name );
			}
			catch( ClassNotFoundException e ) {
				Preflect.error( "CNFE for \"" + name + "\"", e );
			}
		}

		return c;
	}

	/**
	 * @param f
	 * @return {@link Category} name, or <code>null</code> if there is not one
	 *         present
	 */
	static String getCategory( AccessibleObject f ) {
		Category c = f.getAnnotation( Category.class );
		if( c != null ) {
			return c.value();
		}
		return null;
	}

	/**
	 * @param f
	 * @return {@link Variable} description, or <code>null</code> if there is not
	 *         one present
	 */
	static String getDescription( AccessibleObject f ) {
		Summary d = f.getAnnotation( Summary.class );
		if( d != null ) {
			return d.value();
		}
		return null;
	}

	/**
	 * @param o
	 * @return {@link Variable} description, or <code>null</code> if there is not
	 *         one present
	 */
	static String getDescription( Object o ) {
		Summary d = o.getClass().getAnnotation( Summary.class );
		if( d != null ) {
			return d.value();
		}
		return null;
	}

	/**
	 * The configuration name of a field
	 * 
	 * @param f
	 * @return The name of the field, or <code>null</code> if the field is not
	 *         {@link Variable}
	 */
	static String getName( Field f ) {
		String name = null;

		Variable v = f.getAnnotation( Variable.class );

		if( v != null ) {
			name = v.value();

			if( name.length() == 0 ) {
				// check if type is configurable
				Variable tc = f.getType().getAnnotation( Variable.class );
				if( tc != null && tc.value().length() != 0 ) {
					name = tc.value();
				}
				else {
					name = f.getName();
				}
			}
		}

		return name;
	}

	/**
	 * The configuration name of an encapsulated variable
	 * 
	 * @param m
	 * @return The name of the variable, or <code>null</code> if the method is not
	 *         {@link Variable}
	 */
	static String getName( Method m ) {
		Variable v = m.getAnnotation( Variable.class );
		String name = null;

		if( v != null ) {
			name = v.value();

			if( name.length() == 0 ) {
				name = m.getName();
			}
		}

		return name;
	}

	/**
	 * The configuration name of an object
	 * 
	 * @param o
	 * @return The name, or <code>null</code> if o is not {@link Variable}
	 */
	static String getName( Object o ) {
		Variable c = o.getClass().getAnnotation( Variable.class );
		String name = null;

		if( c != null ) {
			name = c.value();

			if( name.length() == 0 ) {
				name = o.getClass().getSimpleName();
			}

			assert name != null;
			return name;
		}
		else {
			throw new StructuralError( o.getClass()
					+ " is not annotated as a @Variable" );
		}
	}

	/**
	 * Adds the description and ranges to the json
	 * 
	 * @param conf
	 * @param ao
	 * @param value
	 *          the object that is accessed through ao
	 * @throws JSONException
	 */
	static void getOptional( TerseJsonWriter jsw, AccessibleObject ao,
			Object value ) throws IOException {
		jsw.setOpt( CAT, getCategory( ao ) );

		Order o = ao.getAnnotation( Order.class );
		if( o != null ) {
			jsw.set( ORDER, new Integer( o.value() ) );
		}

		WidgetHint th = ao.getAnnotation( WidgetHint.class );
		if( th != null ) {
			jsw.set( TYPE, th.value().getName() );
		}

		String description = getDescription( ao );
		if( description == null && value != null ) {
			description = getDescription( value );
		}
		jsw.setOpt( DESC, description );
	}

	static Map<String, String> getVarInfo( Class<?> c,
			Map<String, String> overrides ) {
		Map<String, String> info = new TreeMap<String, String>();

		Summary s = c.getAnnotation( Summary.class );
		if( s != null ) {
			info.put( DESC, s.value() );
		}
		if( overrides.containsKey( DESC ) ) {
			info.put( DESC, overrides.get( DESC ) );
		}

		Category cat = c.getAnnotation( Category.class );
		if( cat != null ) {
			info.put( CAT, cat.value() );
		}
		if( overrides.containsKey( CAT ) ) {
			info.put( CAT, overrides.get( CAT ) );
		}

		return info;
	}

	/**
	 * Gets the non-value information for a variable
	 * 
	 * @param f
	 *          The field
	 * @param value
	 *          The value of the field
	 * @return Key-value pairs that should be written to the json
	 */
	static Map<String, String> getVarInfo( Field f ) {
		Map<String, String> info = new TreeMap<String, String>();
		info.put( TYPE, f.getType().getName() );
		info.put( READ_ONLY, String.valueOf( Modifier.isFinal( f.getModifiers() ) ) );
		supplementVarInfo( info, f, f.getType() );

		return info;
	}

	static Map<String, String> getVarInfo( Method getter, Method setter ) {
		Map<String, String> info = new TreeMap<String, String>();

		// validate
		if( getter != null ) {
			if( getter.getReturnType() == void.class
					|| getter.getParameterTypes().length != 0 ) {
				throw new StructuralError( "Getter method "
						+ getter.getDeclaringClass().getName() + "." + getter.getName()
						+ " must have zero arguments and a non-void return" );
			}
		}

		if( setter != null ) {
			if( setter.getReturnType() != void.class
					|| setter.getParameterTypes().length > 1 ) {
				throw new StructuralError( "Setter method "
						+ setter.getDeclaringClass().getName() + "." + setter.getName()
						+ " must have one argument or fewer and a void return" );
			}
		}

		// extract
		if( getter != null && setter == null ) {
			// readonly
			info.put( READ_ONLY, "true" );
			supplementVarInfo( info, getter, getter.getReturnType() );
		}
		else if( getter == null && setter != null ) {
			// action?
			if( setter.getParameterTypes().length == 0 ) {
				info.put( READ_ONLY, "false" );
				supplementVarInfo( info, setter, void.class );
			}
			else {
				throw new StructuralError( "Uniquely annotated method "
						+ setter.getDeclaringClass().getName() + "." + setter.getName()
						+ " has arguments, it is not an action or a getter" );
			}
		}
		else {
			assert getter != null;
			assert setter != null;

			if( getter.getReturnType() != setter.getParameterTypes()[0] ) {
				throw new StructuralError( "Return type of " + getter.getName()
						+ " must match argument type of " + setter.getName() + " in "
						+ setter.getDeclaringClass() );
			}

			info.put( READ_ONLY, "false" );
			supplementVarInfo( info, setter, getter.getReturnType() );
			supplementVarInfo( info, getter, getter.getReturnType() );

			// reinforce widgethint
			WidgetHint wh = getter.getAnnotation( WidgetHint.class );
			if( wh == null ) {
				wh = setter.getAnnotation( WidgetHint.class );
			}
			if( wh != null ) {
				info.put( TYPE, wh.value().getName() );
			}
		}

		return info;
	}

	private static Map<String, String> supplementVarInfo(
			Map<String, String> info, AccessibleObject ao, Class<?> valueType ) {
		info.put( TYPE, valueType.getName() );
		WidgetHint wh = ao.getAnnotation( WidgetHint.class );
		if( wh != null ) {
			info.put( TYPE, wh.value().getName() );
		}

		Summary s = ao.getAnnotation( Summary.class );
		if( s == null ) {
			s = valueType.getAnnotation( Summary.class );
		}
		if( s != null ) {
			info.put( DESC, s.value() );
		}

		Order o = ao.getAnnotation( Order.class );
		if( o != null ) {
			info.put( ORDER, String.valueOf( o.value() ) );
		}

		Category cat = ao.getAnnotation( Category.class );
		if( cat != null ) {
			info.put( CAT, cat.value() );
		}

		return info;
	}

	public static class TerseJsonWriter extends JsonWriter {
		public TerseJsonWriter( Writer w ) {
			super( w );
		}

		public JsonWriter setOpt( String name, Object value ) throws IOException {
			if( name != null && value != null ) {
				super.set( name, value );
			}
			return this;
		}
	}
}
