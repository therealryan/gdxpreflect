package com.ryanm.preflect.test.cases.extract;

import com.ryanm.preflect.annote.Variable;
import com.ryanm.preflect.test.ExtractionTest;

/**
 * Demonstrates basic variable extraction
 */
public class Basic extends ExtractionTest {

	@Override
	public Object[] roots() {
		return new Object[] { new Configurable() };
	}

	@Override
	public String expectedJson() {
		// @formatter:off
		return 
	"{"
+ 	"Configurable:{"
+ 		"bool:{readonly:\"false\",type:boolean,value:\"false\"},"
+ 		"integer:{readonly:\"false\",type:int,value:\"0\"},"
+ 		"number:{readonly:\"false\",type:float,value:\"0.0\"},"
+ 		"stringOfNull:{readonly:\"false\",type:java.lang.String,value:\"null\"},"
+ 		"nullString:{readonly:\"false\",type:java.lang.String,value:null},"
+ 		"myEnum:{readonly:\"false\",type:"+MyEnum.class.getName()+",value:FOO},"
+ 		"readOnly:{readonly:\"true\",type:int,value:\"0\"},"
+ 		"action:{readonly:\"false\",type:void},"
+ 		"\"encapsulated number\":{readonly:\"false\",type:int,value:\"0\"},"
+ 		"\"encapsulated readonly\":{readonly:\"true\",type:int,value:\"0\"}"
+ 	"}"
+ "}";
		// @formatter:on
	}

	@Variable
	public class Configurable {
		@Variable
		public boolean bool;
		@Variable
		public int integer;
		@Variable
		public float number;
		@Variable
		public String stringOfNull = "null";
		@Variable
		public String nullString;
		@Variable
		public MyEnum myEnum = MyEnum.FOO;
		@Variable
		public final int readOnly = 0;

		@Variable( "encapsulated number" )
		public void setNumber( int i ) {
			integer = i;
		}

		@Variable( "encapsulated number" )
		public int getNumber() {
			return 0;
		}

		@Variable( "encapsulated readonly" )
		public int getReadOnly() {
			return 0;
		}

		@Variable
		public void action() {
		}
	}

	public enum MyEnum {
		FOO, BAR, BAZ;
	}

}
