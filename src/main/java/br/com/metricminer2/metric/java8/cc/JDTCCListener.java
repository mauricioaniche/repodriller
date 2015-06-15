package br.com.metricminer2.metric.java8.cc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.WhileStatement;

import br.com.metricminer2.parser.jdt.JDTMethods;

public class JDTCCListener extends ASTVisitor {

    private Map<String, Integer> ccPerMethod;
    private Stack<String> methodStack;

    public JDTCCListener() {
        ccPerMethod = new HashMap<String, Integer>();
        methodStack = new Stack<String>();
    }
    
    public boolean visit(MethodDeclaration node) {
    	
    	String methodName = JDTMethods.fullMethodName(node);
    	methodStack.push(methodName);
    	increaseCc();
    	return super.visit(node);
    }
    
    public void endVisit(MethodDeclaration node) {
    	methodStack.pop();
    }

    @Override
    public boolean visit(ForStatement node) {
    	increaseCc();
    	
    	return super.visit(node);
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(ConditionalExpression node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(DoStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(WhileStatement node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(SwitchCase node) {
    	if(!node.isDefault())
    		increaseCc();
    	return super.visit(node);
    }
    
    @Override
    public boolean visit(Initializer node) {
    	methodStack.push("(static block)");
    	increaseCc();
    	return super.visit(node);
    }

    @Override
    public void endVisit(Initializer node) {
    	methodStack.pop();
    }

    @Override
    public boolean visit(CatchClause node) {
    	increaseCc();
    	return super.visit(node);
    }
    
    public boolean visit(IfStatement node) {
    	
		String expr = node.getExpression().toString().replace("&&", "&").replace("||", "|");
		int ands = StringUtils.countMatches(expr, "&");
		int ors = StringUtils.countMatches(expr, "|");
		
		increaseCc(ands + ors);
    	increaseCc();
    	
    	return super.visit(node);
    }
    
    private void increaseCc() {
    	increaseCc(1);
    }

    private void increaseCc(int qtd) {
    	
    	// i dont know the method... ignore!
    	if(methodStack.isEmpty()) return;
    	
    	String currentMethod = methodStack.peek();
    	if (!ccPerMethod.containsKey(currentMethod))
    		ccPerMethod.put(currentMethod, 0);
    	
    	ccPerMethod.put(currentMethod, ccPerMethod.get(currentMethod) + qtd);
    	
    }
    
    public int getCc() {
        int cc = 0;
        for (Entry<String, Integer> method : ccPerMethod.entrySet()) {
            cc += method.getValue();
        }
        return cc;
    }

    public int getCc(String method) {
        return ccPerMethod.get(method);
    }

    public double getAvgCc() {
        double avg = 0;

        for (Entry<String, Integer> method : ccPerMethod.entrySet()) {
            avg += method.getValue();
        }

        return avg / ccPerMethod.size();
    }

    public Map<String, Integer> getCcPerMethod() {
        return ccPerMethod;
    }
}
