package com.ryanm.preflect;

import com.ryanm.preflect.annote.Variable;

/**
 * Thrown to indicate something is wrong with the structure of your
 * {@link Variable}s
 * 
 * @author ryanm
 */
public class StructuralError extends Error
{
	private static final long serialVersionUID = 1L;

	StructuralError( final String reason )
	{
		super( reason );
	}
}
