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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.metric.java8.cc.MethodLevelCyclomaticComplexity;


public class MethodLevelCyclomaticComplexityTest {

	private MethodLevelCyclomaticComplexity metric;

	@Before
	public void setUp() {
		metric = new MethodLevelCyclomaticComplexity();
	}
	@Test
	public void shouldCalculateCCForOverridedMethods() {
		Map<String, Double> cc = metric.calculate(
				classDeclaration(
						"public int method() {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(int x) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(double x) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(double x, int... y) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(double x, Map<String> y) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(double x, Map<String,String> y) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"+
						"public int method(int x, double y) {" +
						"a=a+1;" +
						"b=b+1;" +
						"return a+b; }"
						)
				);
		
		assertEquals(1, cc.get("method/0"), 0.0001);
		assertEquals(1, cc.get("method/1[int]"), 0.0001);
		assertEquals(1, cc.get("method/1[double]"), 0.0001);
		assertEquals(1, cc.get("method/2[double,int...]"), 0.0001);
		assertEquals(1, cc.get("method/2[double,Map<String>]"), 0.0001);
		assertEquals(1, cc.get("method/2[double,Map<String,String>]"), 0.0001);
		assertEquals(1, cc.get("method/2[int,double]"), 0.0001);
	}
	

	
	@Test
	public void shouldCalculateForMoreThanOneMethod() {
		Map<String, Double> cc = metric.calculate(
				classDeclaration(
						"public int method1() {" +
						"if(x) {}" +
						"return a+b; }"+
						"public int method2() {" +
						"a=a==10? 1 : 2;" +
						"return a+b; }"+
						"public int method3() {" +
						"for(;;) {}" +
						"return a+b; }"
						
						)
				);
		
		assertEquals(2, cc.get("method1/0"), 0.0001);
		assertEquals(2, cc.get("method2/0"), 0.0001);
		assertEquals(2, cc.get("method3/0"), 0.0001);
	}
	
	@Test
	public void shouldCalculateForInnerMethodsSeparately() {
		Map<String, Double> cc = metric.calculate(
				classDeclaration(
						"public int method1() {" +
						"if(x) {}" +
						"new Thread(new Runnable() { public void run() { nothing(); }}).start();" +
						"return a+b; }"
						)
				);
		
		assertEquals(2, cc.get("method1/0"), 0.0001);
		assertEquals(1, cc.get("run/0"), 0.0001);
		
	}

	@Test
	public void shouldCalculateCCInConstructors() {
		Map<String, Double> cc = metric.calculate(
				classDeclaration(
						"public Program() {" +
						"if(x) bla(); }"+
						"public Program(int a, double b) {" +
						"if(x) bla(); }"		
						));

		assertEquals(2, cc.get("Program/0"), 0.0001);
		assertEquals(2, cc.get("Program/2[int,double]"), 0.0001);
	}
}
