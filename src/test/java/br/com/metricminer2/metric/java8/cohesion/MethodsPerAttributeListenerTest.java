package br.com.metricminer2.metric.java8.cohesion;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.Java8AntLRVisitor;
import br.com.metricminer2.metric.java8.cohesion.MethodsPerAttributeListener;

public class MethodsPerAttributeListenerTest {

	private MethodsPerAttributeListener visitor;
	private List<String> attributes;

	@Before
	public void setUp() {
		attributes = Arrays.asList("a", "b", "c");
		
		visitor = new MethodsPerAttributeListener(attributes);
	}
	
	@Test
	public void shouldCountNumberOfAttributesThatAMethodDealsWith() {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"private int a;\r\n"+
								"public void x() { a = a + 1; }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(1, visitor.getMethodsPerAttribute().get("a").size());
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("x/0"));
	}

	@Test
	public void shouldCountNumberOfAttributesThatManyMethodsDealWith() {
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"private int a;\r\n"+
								"private int b;\r\n"+
								"public void x() { a = a + 1; }\r\n"+
								"public void y() { a = a + 1; b = b + 1; }\r\n"
				).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(2, visitor.getMethodsPerAttribute().get("a").size());
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("x/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("y/0"));

		assertEquals(1, visitor.getMethodsPerAttribute().get("b").size());
		assertTrue(visitor.getMethodsPerAttribute().get("b").contains("y/0"));
	}
	
	@Test
	public void shouldCountNumberOfAttributesThatAMethodDealsWithInVeryStrangeWays() {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"private int a;\r\n"+
								"public void a1() { a = a + 1; }\r\n"+
								"public void a2() { System.out.println(a); }\r\n"+
								"public void a3() { someMethod(a); }\r\n"+
								"public void a5() { a++; }\r\n"+
								"public void a4() { a = new Object(); }\r\n"+
								"public void a6() { a.getBla(); }\r\n"+
								"public void a7() { return a; }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(7, visitor.getMethodsPerAttribute().get("a").size());
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a1/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a2/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a3/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a4/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a5/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a6/0"));
		assertTrue(visitor.getMethodsPerAttribute().get("a").contains("a7/0"));
	}
	
}
