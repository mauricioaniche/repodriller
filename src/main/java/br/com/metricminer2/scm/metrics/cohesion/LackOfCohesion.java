package br.com.metricminer2.scm.metrics.cohesion;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.metricminer2.scm.metrics.ClassLevelCodeMetric;
import br.com.metricminer2.scm.metrics.MetricException;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8AntLRVisitor;
import br.com.metricminer2.scm.metrics.common.MethodsAndAttributesListener;
import br.com.metricminer2.scm.metrics.methods.Method;

public class LackOfCohesion implements ClassLevelCodeMetric {

	private MethodsAndAttributesListener firstVisitor;
	private MethodsPerAttributeListener secondVisitor;
	private InterfaceDetectorListener interfaceDetector;

	@Override
	public double calculate(String sourceCode) {
		
		try {

			interfaceDetector = new InterfaceDetectorListener();
			new Java8AntLRVisitor().visit(interfaceDetector, new ByteArrayInputStream(sourceCode.getBytes()));
			if(interfaceDetector.interfaceDetected()) return 0;
			
			firstVisitor = new MethodsAndAttributesListener();
			new Java8AntLRVisitor().visit(firstVisitor, new ByteArrayInputStream(sourceCode.getBytes()));
			
			List<String> attributes = firstVisitor.getAttributes();
			
			secondVisitor = new MethodsPerAttributeListener(attributes);
			new Java8AntLRVisitor().visit(secondVisitor, new ByteArrayInputStream(sourceCode.getBytes()));
			
			return lcom(firstVisitor.getAttributes(), firstVisitor.getMethods(), secondVisitor.getMethodsPerAttribute());

        } catch (Throwable t) {
            throw new MetricException(this, sourceCode, t);
        }
	}
	
	private double lcom(List<String> attributes, List<Method> methods, Map<String, Set<String>> methodsPerAttribute) {
		
		double result = 1 - 1/(m(methods)*f(attributes))*sumMf(methodsPerAttribute);
		return Double.isNaN(result) ? 1 : result;
		
	}
	
	private double f(List<String> attributes) {
		return attributes.size();
	}

	private double m(List<Method> methods) {
		return methods.size();
	}


	private double sumMf(Map<String, Set<String>> methodsPerAttribute) {
		double total = 0;
		for (Entry<String, Set<String>> methods : methodsPerAttribute.entrySet()) {
			total += methods.getValue().size();
		}
		return total;
	}


	@Override
	public String getName() {
		return "class-lcom-hs";
	}
	
	@Override
	public boolean accepts(String fileName) {
		return fileName.toLowerCase().endsWith("java");
	}


}
