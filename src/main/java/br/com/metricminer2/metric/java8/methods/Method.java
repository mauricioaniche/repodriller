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

public class Method {

	private String name;
	private int lines;
	public Method(String name, int lines) {
		this.name = name;
		this.lines = lines;
	}
	public String getName() {
		return name;
	}
	public int getLines() {
		return lines;
	}
	public boolean isGetter() {
		return name.startsWith("get");
	}
	public boolean isSetter() {
		return name.startsWith("set");
	}
	
	public boolean isGetterOrSetter() {
		return isGetter() || isSetter();
	}
	
	
	
	
}
