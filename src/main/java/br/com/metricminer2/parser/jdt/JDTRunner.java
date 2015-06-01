package br.com.metricminer2.parser.jdt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JDTRunner {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void visit(ASTVisitor visitor, InputStream f) {
		try {
			String conteudo = IOUtils.toString(f);
			
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(conteudo.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			
			Map options = JavaCore.getOptions();
			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
			parser.setCompilerOptions(options);
			
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(visitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
