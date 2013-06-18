package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;
import com.ryanm.preflect.ParseException;

/**
 * @author ryanm
 */
public class IntType extends Codec<Number> {
	/***/
	public IntType() {
		super( int.class );
	}

	@Override
	public String encode( Number value ) {
		String s = value.toString();
		assert s != null;
		return s;
	}

	@Override
	public Number decode( String encoded, Class<? extends Number> type )
			throws ParseException {
		try {
			return new Integer( encoded );
		}
		catch( NumberFormatException nfe ) {
			throw new ParseException( nfe );
		}
	}

}
