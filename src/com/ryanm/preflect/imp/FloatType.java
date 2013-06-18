package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;
import com.ryanm.preflect.ParseException;

/**
 * @author ryanm
 */
public class FloatType extends Codec<Number> {
	/***/
	public FloatType() {
		super( float.class );
	}

	@Override
	public String encode( Number value ) {
		return value.toString();
	}

	@Override
	public Number decode( String encoded, Class<? extends Number> type )
			throws ParseException {
		try {
			return new Float( encoded );
		}
		catch( NumberFormatException nfe ) {
			throw new ParseException( nfe );
		}
	}

}
