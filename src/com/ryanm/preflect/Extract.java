package com.ryanm.preflect;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.ryanm.preflect.Util.TerseJsonWriter;
import com.ryanm.preflect.annote.Variable;

/**
 * Methods to extract the current configuration of a tree to json
 */
@SuppressWarnings( { "rawtypes", "unchecked" } )
public class Extract {

	/**
	 * Extracts the current configuration from a object forest
	 * 
	 * @param to
	 *          config json will be written to here
	 * @param roots
	 *          the roots of the config trees
	 * @throws StructuralError
	 *           If there's something wrong with the structure of your
	 *           {@link Variable} tree
	 * @throws IOException
	 *           if writing to the stream fails
	 * @throws ReflectiveOperationException
	 *           if something goes wrong with reflecting the values in your
	 *           {@link Variable} tree
	 */
	public static void extract( Writer to, Object... roots ) throws IOException,
			ReflectiveOperationException {
		TerseJsonWriter jsw = new TerseJsonWriter( to );
		jsw.setOutputType( OutputType.minimal );

		jsw.object();
		for( Object o : roots ) {
			jsw.object( Util.getName( o ) );
			extractObject( jsw, o, Collections.EMPTY_MAP, new HashSet<Integer>() );
			jsw.pop();
		}
		jsw.pop();

		jsw.close();
	}

	private static void extractObject( TerseJsonWriter jsw, Object o,
			Map<String, String> overrides, Set<Integer> encountered )
			throws IOException, ReflectiveOperationException {
		Integer id = new Integer( System.identityHashCode( o ) );
		if( encountered.contains( id ) ) {
			throw new StructuralError( "Loop detected! We've seen " + o + " before" );
		}
		encountered.add( id );

		for( Map.Entry<String, String> e : Util
				.getVarInfo( o.getClass(), overrides ).entrySet() ) {
			jsw.set( e.getKey(), e.getValue() );
		}

		Set<String> names = new HashSet<String>();

		// fields
		for( Field f : o.getClass().getFields() ) {
			String name = Util.getName( f );
			if( name != null ) {
				if( names.contains( name ) ) {
					throw new StructuralError( "Duplicate variable name \"" + name
							+ "\" detected in " + o.getClass() );
				}
				else {
					names.add( name );
					jsw.object( name );
					extractConfig( jsw, f, o, encountered );
					jsw.pop();
				}
			}
		}

		// methods

		LinkedList<Method> methods = new LinkedList<Method>();
		for( Method m : o.getClass().getMethods() ) {
			if( m.isAnnotationPresent( Variable.class ) ) {
				methods.add( m );
			}
		}

		while( !methods.isEmpty() ) {
			Method m = methods.removeFirst();

			String name = Util.getName( m );

			if( name != null ) {
				// find the counterpart
				Method n = null;
				Iterator<Method> iter = methods.iterator();
				while( iter.hasNext() ) {
					Method method = iter.next();
					if( name.equals( Util.getName( method ) ) ) {
						if( n == null ) {
							n = method;
							iter.remove();
						}
						else {
							throw new StructuralError(
									"More than two methods annotated with the same name \""
											+ name + "\" in " + o.getClass() );
						}
					}
				}

				if( names.contains( name ) ) {
					throw new StructuralError( "Duplicate variable name \"" + name
							+ "\" detected in " + o.getClass() );
				}
				else {
					names.add( name );
					jsw.object( name );
					extractConfig( jsw, o, m, n, encountered );
					jsw.pop();
				}
			}
		}
	}

	static void extractConfig( TerseJsonWriter jsw, Field f, Object o,
			Set<Integer> encountered ) throws IOException,
			ReflectiveOperationException {
		Map<String, String> varInfo = Util.getVarInfo( f );

		Object value = f.get( o );
		Codec codec = Codec.get( f.getType() );
		if( codec != null ) {
			for( Map.Entry<String, String> kvp : varInfo.entrySet() ) {
				jsw.set( kvp.getKey(), kvp.getValue() );
			}
			jsw.set( Util.VALUE, value != null ? codec.encode( value ) : null );
		}
		else if( value != null ) {
			extractObject( jsw, value, varInfo, encountered );
		}
	}

	private static void extractConfig( TerseJsonWriter jsw, Object o, Method m,
			Method n, Set<Integer> encountered ) throws IOException,
			ReflectiveOperationException {
		Method setter = m;
		Method getter = n;
		if( ( getter != null && getter.getReturnType() == void.class )
				|| ( setter != null && setter.getReturnType() != void.class ) ) {
			setter = n;
			getter = m;
		}
		Map<String, String> varInfo = Util.getVarInfo( getter, setter );

		if( getter != null ) {
			Object value = getter.invoke( o );
			Codec codec = Codec.get( getter.getReturnType() );
			if( codec != null ) {
				for( Map.Entry<String, String> kvp : varInfo.entrySet() ) {
					jsw.set( kvp.getKey(), kvp.getValue() );
				}
				jsw.set( Util.VALUE, value != null ? codec.encode( value ) : null );
			}
			else if( value != null ) {
				extractObject( jsw, value, varInfo, encountered );
			}
		}
		else {
			// action
			for( Map.Entry<String, String> kvp : varInfo.entrySet() ) {
				jsw.set( kvp.getKey(), kvp.getValue() );
			}
		}
	}
}
