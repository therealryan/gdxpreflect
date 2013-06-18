package com.ryanm.preflect.test;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ryanm.preflect.Extract;
import com.ryanm.preflect.Preflect;
import com.ryanm.preflect.Preflect.Log;

/**
 * Simple superclass for json extraction tests
 */
@RunWith( JUnit4.class )
public abstract class ExtractionTest {

	public abstract Object[] roots();

	public abstract String expectedJson();

	@Test
	public void doTest() throws Exception {
		StringWriter sw = new StringWriter();
		TestLogger tl = new TestLogger();
		Preflect.log = tl;
		Extract.extract( sw, roots() );
		Assert.assertEquals( "json is wrong!", expectedJson(), sw.toString() );
		Assert.assertEquals( "Logging is non-empty!", "", tl.sb.toString() );
	}

	private static class TestLogger implements Log {

		private StringBuilder sb = new StringBuilder();

		@Override
		public void log( String message ) {
			sb.append( message ).append( "\n" );
		}

		@Override
		public void error( String message, Throwable e ) {
			sb.append( message ).append( " : " ).append( e.getMessage() )
					.append( "\n" );
		}

	}

}
