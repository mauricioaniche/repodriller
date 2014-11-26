package br.com.metricminer2.metric.java8.cohesion;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static br.com.metricminer2.metric.ParserTestUtils.interfaceDeclaration;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.cohesion.LackOfCohesion;


public class LackOfCohesionTest {

	private LackOfCohesion metric;

	@Before
	public void setUp() {
		metric = new LackOfCohesion();
	}
	@Test
	public void shouldCalculateLcomForASimpleClass() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { a = a + 1; }\r\n"
								));
	
		assertEquals(0, value, 0.000001);
	}
	
	@Test
	public void shouldCalculateLcomForAMethodThatConsumesOnlyOneAttribute() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"private int b;\r\n"+
								"public void x() { a = a + 1; }\r\n"
								));
	
		assertEquals(0.5, value, 0.000001);
	}

	@Test
	public void shouldCalculateLcomForAClassThatDoesNotConsumeAttributes() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { }\r\n"
								));
	
		assertEquals(1, value, 0.000001);
	}
	
	@Test
	public void shouldCalculateLcomForAClassThatContainsMethodThatConsumesOnlyOneAttributeEach() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"private int b;\r\n"+
								"public void x() { a = a + 1; }\r\n"+
								"public void y() { b = b + 1; }\r\n"
								));
	
		assertEquals(0.5, value, 0.000001);
	}
	
	@Test
	public void shouldCalculateLcomForAClassInWhichTheAttributesAreSpread() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { a = a + 1; b.getBla(); }\r\n"+
								"private int b;\r\n"+
								"public void y() { System.out.println(c); }\r\n"+
								"private int c;\r\n"
								));
	
		assertEquals(0.5, value, 0.000001);
	}
	
	@Test
	public void shouldPutLessCohesiveValueForAClassWithoutAttributesOrMethods() {
		double value = metric.calculate(
			"public class Total extends ElementWrapper {"+
			    "public Total(Element internal) {"+
			        "super(internal);"+
			    "}"+
			    "public Total(Factory factory) {"+
			        "super(factory, ThreadConstants.THRTOTAL);"+
			    "}"+
			    "public int getValue() {"+
			        "String val = getText();"+
			        "return (val != null) ? Integer.parseInt(val) : -1;"+
			    "}"+
			    "public void setValue(int value) {"+
			        "setText(String.valueOf(value));"+
			    "}"+
			"}");
		
		assertEquals(1,value,0.00001);

	}

	@Test
	public void shouldIgnoreNonAttributes() {
		
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"private int c;\r\n"+
								"public void x() { Object b; a = a + 1; b.getBla(); b++; }\r\n"
								));
	
		assertEquals(0.5, value, 0.000001);
	}
	
	@Test
	public void shouldNotCountInnerClassesInNumberOfMethodsButShouldConsiderItsBody() {
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { " + 
								"new Thread(new Runnable() { public void run() { a=a+1; b++; } }).start();"+
								"}\r\n"
								));
	
		assertEquals(0, value, 0.000001);
	}
	
	@Test
	public void shouldCalculateBadCohesiveClassesWithInnerClasses() {
		double value = metric.calculate(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { " + 
								"new Thread(new Runnable() { public void run() { b++; } }).start();"+
								"}\r\n"
								));

		assertEquals(1, value, 0.000001);
	}
	
	@Test
	public void shouldReturnZeroToInterfaces() {
		double value = metric.calculate(
						interfaceDeclaration(
								"void x();" 
								));

		assertEquals(0, value, 0.000001);
	}
	
}
