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
