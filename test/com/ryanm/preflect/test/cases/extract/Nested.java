package com.ryanm.preflect.test.cases.extract;

import com.ryanm.preflect.annote.Variable;
import com.ryanm.preflect.test.ExtractionTest;

/**
 * Demonstrates nested config
 */
public class Nested extends ExtractionTest {

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
				+ 		"number:{readonly:\"false\",type:float,value:\"0.0\"},"
				+ 		"subConf:{"
				+ 			"string:{readonly:\"false\",type:java.lang.String,value:null},"
				+ 			"subsubConf:{"
				+ 				"deep:{readonly:\"false\",type:int,value:\"0\"}}"
				+ 		"}"
				+ 	"}"
				+ "}";
		// @formatter:on
	}

	@Variable
	public class Configurable {
		@Variable
		public float number = 0;

		@Variable
		public SubConfigurable subConf = new SubConfigurable();
	}

	@Variable
	public class SubConfigurable {
		@Variable
		public String string;

		@Variable
		public SubSubConfigurable subsubConf = new SubSubConfigurable();
	}

	@Variable
	public static class SubSubConfigurable {
		@Variable
		public int deep = 0;
	}

}
