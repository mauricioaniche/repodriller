package br.com.metricminer2.scm.metrics.cohesion;

import org.antlr.v4.runtime.misc.NotNull;

import br.com.metricminer2.scm.metrics.antlr.java8.Java8BaseListener;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8Parser;

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
