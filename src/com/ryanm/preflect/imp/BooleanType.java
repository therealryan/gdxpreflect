package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;

/**
 * @author ryanm
 */
public class BooleanType extends Codec<Boolean> {
	/***/
	public BooleanType() {
		super( boolean.class );
	}

	@Override
	public String encode( Boolean value ) {
		return value.toString();
	}

	@Override
	public Boolean decode( String encoded, Class<? extends Boolean> type ) {
		return new Boolean( encoded );
	}

}
