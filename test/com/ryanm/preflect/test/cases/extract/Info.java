package com.ryanm.preflect.test.cases.extract;

import com.ryanm.preflect.annote.Category;
import com.ryanm.preflect.annote.Order;
import com.ryanm.preflect.annote.Summary;
import com.ryanm.preflect.annote.Variable;
import com.ryanm.preflect.annote.WidgetHint;
import com.ryanm.preflect.test.ExtractionTest;

/**
 * Tests the extraction of additional information, overridden names,
 * {@link Summary}, {@link Category}, {@link Order}, {@link WidgetHint}, etc
 */
@SuppressWarnings( "unused" )
public class Info extends ExtractionTest {

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
+ 		"desc:\"Root summary\","
+ 		"number:{cat:\"number category\",desc:\"number summary\","
+ 			"order:\"1\",readonly:\"false\",type:java.lang.Integer,value:\"0\"},"
+ 		"Overridden:{"
+ 			"cat:\"subconf category\",desc:\"Standard summary\","
+ 			"encap:{cat:\"encap category\",desc:\"encap summary\","
+ 				"order:\"2\",readonly:\"false\",type:int,value:\"3\"}" + "},"
+ 		"Standard:{"
+			"desc:\"Overridden summary\","
+ 		"encap:{cat:\"encap category\",desc:\"encap summary\","
+ 			"order:\"2\",readonly:\"false\",type:int,value:\"3\"}" + "},"
+ 		"action:{cat:\"action category\",desc:\"action summary\","
+ 			"order:\"4\",readonly:\"false\",type:void},"
+ 		"encap:{cat:\"enacap category\",desc:\"encap summary\","
+ 			"order:\"2\",readonly:\"false\",type:java.lang.Integer,value:\"1\"},"
+ 		"\"RO encap\":{cat:\"RO encap category\",desc:\"RO encap summary\","
+ 			"order:\"3\",readonly:\"true\",type:java.lang.Integer,value:\"2\"}"
+ 	"}"
+ "}";
		// @formatter:on
	}

	@Variable
	@Summary( "Root summary" )
	public static class Configurable {

		@Variable( "number" )
		@Summary( "number summary" )
		@Category( "number category" )
		@WidgetHint( Integer.class )
		@Order( 1 )
		public int i = 0;

		@Variable( "encap" )
		@Summary( "encap summary" )
		@Order( 2 )
		public int getEncap() {
			return 1;
		}

		@Variable( "encap" )
		@Category( "enacap category" )
		@WidgetHint( Integer.class )
		public void setEncap( int i ) {
		}

		@Variable( "RO encap" )
		@Summary( "RO encap summary" )
		@Category( "RO encap category" )
		@WidgetHint( Integer.class )
		@Order( 3 )
		public int readOnlyEncap() {
			return 2;
		}

		@Variable( "action" )
		@Summary( "action summary" )
		@Category( "action category" )
		@Order( 4 )
		public void action() {
		}

		@Variable( "Overridden" )
		@Category( "subconf category" )
		public SubConfigurable subA = new SubConfigurable();

		@Variable
		@Summary( "Overridden summary" )
		public SubConfigurable subConf = new SubConfigurable();
	}

	@Variable( "Standard" )
	@Summary( "Standard summary" )
	public static class SubConfigurable {

		@Variable( "encap" )
		@Summary( "encap summary" )
		@Order( 2 )
		public int getNumber() {
			return 3;
		}

		@Variable( "encap" )
		@Category( "encap category" )
		public void setNumber( int n ) {
		}
	}

}
