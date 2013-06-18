package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;

/**
 * @author ryanm
 */
public class StringType extends Codec<String> {
	/***/
	public StringType() {
		super( String.class );
	}

	@Override
	public String encode( String value ) {
		return value;
	}

	@Override
	public String decode( String encoded, Class<? extends String> type ) {
		return encoded;
	}

}
