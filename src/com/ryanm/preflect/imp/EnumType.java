package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;
import com.ryanm.preflect.ParseException;

/**
 * @author ryanm
 */
@SuppressWarnings( "rawtypes" )
public class EnumType extends Codec<Enum> {
	/***/
	public EnumType() {
		super( Enum.class );
	}

	@SuppressWarnings( { "unchecked" } )
	@Override
	public Enum decode( String encoded, Class type ) throws ParseException {
		try {
			Enum ev = Enum.valueOf( type, encoded );
			assert ev != null;
			return ev;
		}
		catch( IllegalArgumentException iae ) {
			throw new ParseException( "No value \"" + encoded
					+ "\" exists in enum \"" + type.getName() + "\"" );
		}
	}

	@Override
	public String encode( Enum value ) {
		String s = value.name();
		assert s != null;
		return s;
	}

}
