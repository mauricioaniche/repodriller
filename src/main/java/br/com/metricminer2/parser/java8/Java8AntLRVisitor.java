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

package br.com.metricminer2.parser.java8;

import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import br.com.metricminer2.parser.java8.Java8Parser.CompilationUnitContext;
 
public class Java8AntLRVisitor {

	public void visit(Java8Listener visitor, InputStream is) {
		try {
			CharStream input = new ANTLRInputStream(is);
			Java8Lexer lex = new Java8Lexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			Java8Parser parser = new Java8Parser(tokens);
			parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
			parser.removeErrorListeners();
			parser.setErrorHandler(new BailErrorStrategy());
			try {
				CompilationUnitContext compilationUnitContext = parser
						.compilationUnit();
				new ParseTreeWalker().walk(visitor, compilationUnitContext);
			} catch (RuntimeException ex) {

					tokens.reset(); // rewind input stream
					
					// back to standard listeners/handlers
					parser.addErrorListener(ConsoleErrorListener.INSTANCE);
					parser.setErrorHandler(new DefaultErrorStrategy());
					parser.getInterpreter()
							.setPredictionMode(PredictionMode.LL);
					CompilationUnitContext compilationUnitContext = parser
							.compilationUnit();
					new ParseTreeWalker().walk(visitor, compilationUnitContext);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}