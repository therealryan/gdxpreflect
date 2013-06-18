package com.ryanm.preflect.imp;

import com.ryanm.preflect.Codec;
import com.ryanm.preflect.ParseException;

/**
 * @author ryanm
 */
public class VoidType extends Codec<Void> {
	/***/
	public VoidType() {
		super( void.class );
	}

	@Override
	public String encode( Void value ) {
		return "void";
	}

	@Override
	public Void decode( String encoded, Class<? extends Void> runtimeType )
			throws ParseException {
		throw new IllegalStateException( "Why?" );
	}

}
