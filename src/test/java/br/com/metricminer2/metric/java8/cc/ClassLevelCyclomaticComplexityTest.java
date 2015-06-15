/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.metricminer2.metric.java8.cc;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static org.junit.Assert.assertEquals;
import jdk.nashorn.internal.ir.annotations.Ignore;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.cc.ClassLevelCyclomaticComplexity;

public class ClassLevelCyclomaticComplexityTest {

	private ClassLevelCyclomaticComplexity metric;

	@Before
	public void setUp() {
		metric = new ClassLevelCyclomaticComplexity();
	}
	
	@Test
	public void shouldCalculateCCForAPlainClass() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }")
				);
		
		assertEquals(1, (int) cc);
	}


	
	@Test
	public void shouldAddOneForEachIf() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y) a=a+1;" +
						"if(x) b=b+1;" +
						"return a+b; }")
				);
		
		assertEquals(3, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachIfAndIgnoreElse() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y) a=a+1; else a=a+2;" +
						"if(x) b=b+1; else b=b+2;" +
						"return a+b; }")
				);
		
		assertEquals(3, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachIfAndElseIfAndIgnoreElse() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y) a=a+1; else if(y2) a=a+2; else a=a+3;" +
						"if(x) b=b+1; else if(x2) b=b+2; else b=b+3;" +
						"return a+b; }")
				);
		
		assertEquals(5, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachAndInAnIf() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y && y1) a=a+1;" +
						"if(x && x2) b=b+1;" +
						"return a+b; }")
				);
		
		assertEquals(5, (int) cc);
	}

	@Test
	public void shouldAddOneForEachOrInAnIf() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y || y1) a=a+1;" +
						"if(x || x2) b=b+1;" +
						"return a+b; }")
				);
		
		assertEquals(5, (int) cc);
	}

	@Test
	public void shouldAddOneForEachAndOrOrInAnIf() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(y && y1 || y2) a=a+1;" +
						"if(x && x2 || x3) b=b+1;" +
						"return a+b; }")
				);
		
		assertEquals(7, (int) cc);
	}

	
	@Test
	public void shouldAddOneForEachFor() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"for(int i = 0; i < 10; i++) {"+
						"a=a+1;" +
						"b=b+1; }" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachForEach() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
								"for(Item a : itens) {"+
								"a=a+1;" +
								"b=b+1; }" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
		
	}
	
	@Test
	public void shouldAddOneForEachWhile() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"while(true) {"+
						"a=a+1;" +
						"b=b+1; }" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachDoWhile() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"do {"+
						"a=a+1;" +
						"b=b+1; } while(true);" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachCase() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"switch(a) {"+
						"case 1: return bla; break;" +
						"case 2: return bla; break;" +
						"case 3: return bla; break;" +
						"}" +
						"return a+b; }")
				);
		
		assertEquals(4, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachCaseButIgnoreDefault() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"switch(a) {"+
						"case 1: return bla;" +
						"case 2: return bla;" +
						"case 3: return bla;" +
						"default: return bla;" +
						"}" +
						"return a+b; }")
				);
		
		assertEquals(4, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachCatch() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"try { bla(); }" +
						"catch(Exception e1) { ble(); }" +
						"catch(Exception e2) { bli(); }" +
						"return a+b; }")
				);
		
		assertEquals(3, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachCatchButIgnoreFinally() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"try { bla(); }" +
						"catch(Exception e1) { ble(); }" +
						"catch(Exception e2) { bli(); }" +
						"finally { bli(); }" +
						"return a+b; }")
				);
		
		assertEquals(3, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachTernaryIf() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"a=a==10? 1 : 2;" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
	}

	@Test
	public void shouldUnderstandLamba() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
								"Runnable r = () -> {"
								+ "if (a == 2) return;"
								+ "};" +
						"return 2; }")
				);
		
		assertEquals(2, (int) cc);
	}
	
	@Test
	public void shouldAddOneForEachTernaryIfInMiddleOfExpression() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"x.invoke((a=a==10? 1 : 2));" +
						"return a+b; }")
				);
		
		assertEquals(2, (int) cc);
	}

	@Test
	public void shouldCalculateCCForAllOperatorsTogethers() {
		double cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"if(x) bla();" +
						"for(;;) bla();" +
						"while(x) bla();" +
						"do { bla(); } while(x);" +
						"switch(x) { case 1: bla(); } " +
						"try{}catch(Exception e){}" +
						"if(x && y) bla();" +
						"if(x || y) bla();" +
						"int a = (x ? 1 : 0);" +
						"return a+b; }")
				);
		
		assertEquals(12, (int) cc);
	}

	
	@Test @Ignore
	public void shouldCalculateCCForStaticBlock() {
		double cc = metric.calculate(
				classDeclaration(
						"static {"+
						 "if(a) x();"+
						"}")
				);
		
		assertEquals(2, (int) cc);
	}
	
}
