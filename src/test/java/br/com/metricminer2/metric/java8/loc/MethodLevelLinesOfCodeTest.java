package br.com.metricminer2.metric.java8.loc;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.loc.MethodLevelLinesOfCode;


public class MethodLevelLinesOfCodeTest {

	private MethodLevelLinesOfCode metric;
	@Before
	public void setUp() {
		this.metric = new MethodLevelLinesOfCode();
	}
	
	@Test
	public void shouldCountNumberOfLinesInAMethod() {
		Map<String, Double> value = metric.calculate(
						classDeclaration(
								"public void x() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"
								));
		
		assertEquals(4, value.get("x/0"), 0.0001);
	}
	
	@Test
	public void shouldCountNumberOfLinesInOverridedMethod() {
		Map<String, Double> value = metric.calculate(
						classDeclaration(
								"public void x() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"+
								"public void x(int a) {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"
								));
		
		assertEquals(4, value.get("x/0"),  0.0001);
		assertEquals(4, value.get("x/1[int]"),  0.0001);
	}
	
	@Test
	public void shouldCountNumberOfLinesInAMethodRegardlessOfModifier() {
		Map<String, Double> value = metric.calculate(
						classDeclaration(
								"public void x1() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"+
								"private void x2() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"+
								"protected void x3() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"+
								"void x4() {\r\n"+
								"line1();\r\n"+
								"line2();\r\n"+
								"line3();\r\n"+
								"}"
								));
		
		assertEquals(4, value.get("x1/0"), 0.0001);
		assertEquals(4, value.get("x2/0"), 0.0001);
		assertEquals(4, value.get("x3/0"), 0.0001);
		assertEquals(4, value.get("x4/0"), 0.0001);
	}
}
