package com.ryanm.preflect.test.cases.extract;

import java.io.StringWriter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ryanm.preflect.Extract;
import com.ryanm.preflect.StructuralError;
import com.ryanm.preflect.annote.Variable;

/**
 * Exercises various error cases in the use of {@link Variable}
 */
@RunWith( JUnit4.class )
@SuppressWarnings( "unused" )
public class Errors {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	public StringWriter sw = new StringWriter();

	@Variable
	public static class DuplicateVars {
		@Variable( "var" )
		public int i;

		@Variable( "var" )
		public int j;
	}

	@Test
	public void duplicateVariables() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Duplicate variable name \"var\" detected in "
				+ DuplicateVars.class );
		Extract.extract( sw, new DuplicateVars() );
	}

	@Variable
	public static class DuplicateEncapsulatedVars {
		@Variable( "var" )
		public int i;

		@Variable( "var" )
		public int getI() {
			return 0;
		}
	}

	@Test
	public void duplicateEncapsulatedVariables() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Duplicate variable name \"var\" detected in "
				+ DuplicateEncapsulatedVars.class );
		Extract.extract( sw, new DuplicateEncapsulatedVars() );
	}

	@Variable
	public static class MoreThanTwoMethods {
		@Variable( "var" )
		public int get() {
			return 0;
		}

		@Variable( "var" )
		public void set( int i ) {
		}

		@Variable( "var" )
		public void wat( int i ) {
		}
	}

	@Test
	public void moreThanTwoMethods() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "More than two methods annotated with "
				+ "the same name \"var\" in " + MoreThanTwoMethods.class );
		Extract.extract( sw, new MoreThanTwoMethods() );
	}

	public static class NotVariable {
	}

	@Test
	public void notVariable() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( NotVariable.class
				+ " is not annotated as a @Variable" );
		Extract.extract( sw, new NotVariable() );
	}

	@Variable
	public static class BadGetter {
		@Variable
		public int wat( int idonteven ) {
			return idonteven;
		}
	}

	@Test
	public void badGetter() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Getter method " + BadGetter.class.getName()
				+ ".wat must have zero arguments and a non-void return" );
		Extract.extract( sw, new BadGetter() );
	}

	@Variable
	public static class BadSetter {
		@Variable
		public void set( int i, int j ) {
		}
	}

	@Test
	public void badSetter() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Setter method " + BadSetter.class.getName()
				+ ".set must have one argument or fewer and a void return" );
		Extract.extract( sw, new BadSetter() );
	}

	@Variable
	public static class BadAction {
		@Variable
		public void action( int i ) {
		}
	}

	@Test
	public void badAction() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Uniquely annotated method "
				+ BadAction.class.getName()
				+ ".action has arguments, it is not an action or a getter" );
		Extract.extract( sw, new BadAction() );
	}

	@Variable
	public static class Loop {
		@Variable
		public Loop loop = this;

		@Override
		public String toString() {
			return "@Variable self-reference type";
		}
	}

	@Test
	public void loop() throws Exception {
		exception.expect( StructuralError.class );
		exception.expectMessage( "Loop detected! We've seen @Variable "
				+ "self-reference type before" );
		Extract.extract( sw, new Loop() );
	}
}
