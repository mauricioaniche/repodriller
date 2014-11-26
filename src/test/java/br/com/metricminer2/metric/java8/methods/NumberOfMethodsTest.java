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

package br.com.metricminer2.metric.java8.methods;

import static br.com.metricminer2.metric.ParserTestUtils.classDeclaration;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.methods.NumberOfMethods;


public class NumberOfMethodsTest {

	private NumberOfMethods metric;
	
	@Before
	public void setUp() {
		this.metric = new NumberOfMethods();
	}
	
	@Test
	public void shouldCountPrivateMethods() {
		double value = metric.calculate(
						classDeclaration(
								"private void a1() {}"+
								"private void a2() {}"+
								"private void a3() {}"+
								"private void a4() {}"
								));
		
		assertEquals(4, value, 0.0001);
	}
	
	@Test
	public void shouldCountPublicMethods() {
		double value = metric.calculate(
						classDeclaration(
								"public void a1() {}"+
								"public void a2() {}"+
								"public void a3() {}"+
								"public void a4() {}"
								));
		
		assertEquals(4, value, 0.0001);
	}
	
	@Test
	public void shouldCountProtectedMethods() {
		double value = metric.calculate(
						classDeclaration(
								"protected void a1() {}"+
								"protected void a2() {}"+
								"protected void a3() {}"+
								"protected void a4() {}"
								));
		
		assertEquals(4, value, 0.0001);
	}
	
	@Test
	public void shouldCountDefaultMethods() {
		double value = metric.calculate(
						classDeclaration(
								"void a1() {}"+
								"void a2() {}"+
								"void a3() {}"+
								"void a4() {}"
								));
		
		assertEquals(4, value, 0.0001);
	}
	
	@Test
	public void shouldCountConstructors() {
		double value = metric.calculate(
						classDeclaration(
								"public Program() {}"+
								"protected Program() {}"+
								"private Program() {}"+
								"Program() {}"
								));
		
		assertEquals(4, value, 0.0001);
	}
	
	@Test
	public void shouldDifferentMethodsWithSameName() {
		double value = metric.calculate(
						classDeclaration(
								"public void a1() {}"+
								"public void a1(int x) {}"+
								"public void a1(int x, int y) {}"
								));
		
		assertEquals(3, value, 0.0001);
		
	}
}
