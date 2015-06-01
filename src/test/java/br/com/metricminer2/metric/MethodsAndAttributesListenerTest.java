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

package br.com.metricminer2.metric;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.parser.java8.Java8AntLRVisitor;
import br.com.metricminer2.parser.java8.MethodsAndAttributesListener;


public class MethodsAndAttributesListenerTest {

	private MethodsAndAttributesListener visitor;

	@Before
	public void setUp() {
		visitor = new MethodsAndAttributesListener();
	}
	
	@Test
	public void shouldSeparateMethodsByVisibility() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public void a() { }\r\n"+
								"protected void b() { }\r\n"+
								"private void c() { }\r\n"+
								"void d() { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
		
		assertEquals(4, visitor.getMethods().size());

		assertEquals(1, visitor.getPublicMethods().size());
		assertTrue(visitor.getPublicMethods().get(0).getName().equals("a/0"));
		
		assertEquals(1, visitor.getProtectedMethods().size());
		assertTrue(visitor.getProtectedMethods().get(0).getName().equals("b/0"));

		assertEquals(1, visitor.getPrivateMethods().size());
		assertTrue(visitor.getPrivateMethods().get(0).getName().equals("c/0"));

		assertEquals(1, visitor.getDefaultMethods().size());
		assertTrue(visitor.getDefaultMethods().get(0).getName().equals("d/0"));
	}
	
	@Test
	public void shouldSeparateStaticMethodsByVisibility() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public static void a() { }\r\n"+
								"static public void aa() { }\r\n"+
								"protected static void b() { }\r\n"+
								"private static void c() { }\r\n"+
								"static void d() { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
		
		assertEquals(5, visitor.getMethods().size());

		assertEquals(2, visitor.getPublicMethods().size());
		assertTrue(visitor.getPublicMethods().get(0).getName().equals("a/0"));
		assertTrue(visitor.getPublicMethods().get(1).getName().equals("aa/0"));
		
		assertEquals(1, visitor.getProtectedMethods().size());
		assertTrue(visitor.getProtectedMethods().get(0).getName().equals("b/0"));

		assertEquals(1, visitor.getPrivateMethods().size());
		assertTrue(visitor.getPrivateMethods().get(0).getName().equals("c/0"));

		assertEquals(1, visitor.getDefaultMethods().size());
		assertTrue(visitor.getDefaultMethods().get(0).getName().equals("d/0"));
	}
	
	@Test
	public void shouldCountMethodLines() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public void a() { \r\n" +
								"line1();\r\n" +
								"line2();\r\n" +
								"line3();\r\n" +
								"}\r\n"+
								"public b() { \r\n" +
								"line1();\r\n" +
								"line2();\r\n" +
								"line3();\r\n" +
								"line4();\r\n" +
								"}\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
		
		assertEquals(4, visitor.getPublicMethods().get(0).getLines());
		assertEquals(5, visitor.getConstructorMethods().get(0).getLines());
	}
	
	@Test
	public void shouldCountAllMethodsInAClass() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public void a() { }\r\n"+
								"public void b() { }\r\n"+
								"public void c() { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
		
		assertEquals(3, visitor.getMethods().size());
	}
	
	@Test
	public void shouldCountAllMethodsInAClassWithDifferentModifiers() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public void a() { }\r\n"+
								"protected void b() { }\r\n"+
								"private void c() { }\r\n" +
								"void d() { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
		
		assertEquals(4, visitor.getMethods().size());
	}
	
	@Test
	public void shouldCountConstructors() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"public void a() { }\r\n"+
								"public Program() { }\r\n" +
								"public Program(int param) { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(3, visitor.getMethods().size());
	}
	
	@Test
	public void shouldCountAttributes() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"private int a;\r\n"+
								"private double b;\r\n"+
								"private String c;\r\n"+
								"public void x() { }\r\n" +
								"public void y() { }\r\n" +
								"public Program(int param) { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(3, visitor.getAttributes().size());
	}
	
	@Test
	public void shouldCountAttributesWithDifferentModifiers() throws IOException {
		
		InputStream is = new ByteArrayInputStream(
						classDeclaration(
								"private int a;\r\n"+
								"public double b;\r\n"+
								"protected String c;\r\n"+
								"String d;\r\n"+
								"public void x() { }\r\n" +
								"public void y() { int d;}\r\n" +
								"public Program(int param) { }\r\n"
								).getBytes());
		
		new Java8AntLRVisitor().visit(visitor, is);
	
		assertEquals(4, visitor.getAttributes().size());
		
		assertEquals(1, visitor.getPrivateAttributes().size());
		assertTrue(visitor.getPrivateAttributes().contains("a"));
		
		assertEquals(1, visitor.getPublicAttributes().size());
		assertTrue(visitor.getPublicAttributes().contains("b"));

		assertEquals(1, visitor.getProtectedAttributes().size());
		assertTrue(visitor.getProtectedAttributes().contains("c"));

		assertEquals(1, visitor.getDefaultAttributes().size());
		assertTrue(visitor.getDefaultAttributes().contains("d"));
	}
	
}
