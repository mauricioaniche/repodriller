package br.com.metricminer2.scm.metrics;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParserTestUtils {

	public static String interfaceDeclaration(String body) {
		return "interface Foo { " + body + "}";
	}
	
	public static String classDeclaration(String body) {
		return "class Program { \n" + body + "\n}\n";
	}
	
	public static String imports(String imports, String body) {
		return imports + body;
	}
	
	public static InputStream toInputStream(String text) {
		return new ByteArrayInputStream(text.getBytes());
	}
}
