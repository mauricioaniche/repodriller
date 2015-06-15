package br.com.metricminer2.parser.jdt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class JDTMethods {

	public static String fullMethodName(MethodDeclaration node) {
		String name = node.getName().toString();

		if(node.parameters().isEmpty()) return name + "/0";
		
		List<String> params = new ArrayList<String>();
		for(Object o : node.parameters()) {
			SingleVariableDeclaration v = (SingleVariableDeclaration) o;
			String paramName = v.getType().toString();
			if(v.isVarargs()) paramName += "...";
			
			params.add(paramName);
			
		}
		
		return name + "/" + node.parameters().size() + "[" + StringUtils.join(params, ",") + "]";
	}

}
