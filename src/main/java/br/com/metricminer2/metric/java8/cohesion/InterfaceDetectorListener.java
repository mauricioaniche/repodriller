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

package br.com.metricminer2.metric.java8.cohesion;

import org.antlr.v4.runtime.misc.NotNull;

import br.com.metricminer2.metric.java8.Java8BaseListener;
import br.com.metricminer2.metric.java8.Java8Parser;

public class InterfaceDetectorListener extends Java8BaseListener {
	
	private boolean interfaceDetected;
	private boolean firstTime = true;

	@Override public void enterTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) {
		if(firstTime) {
			if(ctx.interfaceDeclaration()!=null) interfaceDetected = true;
			firstTime = false;
		}
	}
	
	public boolean interfaceDetected() {
		return interfaceDetected;
	}

}
