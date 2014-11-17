package br.com.metricminer2.scm.metrics.antlr.java8;

import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import br.com.metricminer2.scm.metrics.antlr.java8.Java8Parser.CompilationUnitContext;

public class Java8AntLRVisitor {

	public void visit(Java8Listener visitor, InputStream is) {
		try {
			CharStream input = new ANTLRInputStream(is);
			
			Java8Lexer lex = new Java8Lexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			Java8Parser parser = new Java8Parser(tokens);
			CompilationUnitContext r = parser.compilationUnit();

			new ParseTreeWalker().walk(visitor, r);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
