package br.com.metricminer2.scm.metrics.cc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang3.StringUtils;

import br.com.metricminer2.scm.metrics.antlr.java8.Java8AntlrFullMethodName;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8BaseListener;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8Parser;
import br.com.metricminer2.scm.metrics.antlr.java8.Java8Parser.ExpressionContext;

public class CCListener extends Java8BaseListener {

    private Map<String, Integer> ccPerMethod;
    private Stack<String> methodStack;

    public CCListener() {
        ccPerMethod = new HashMap<String, Integer>();
        methodStack = new Stack<String>();
    }
    
	@Override public void enterConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
		String methodName = Java8AntlrFullMethodName.fullMethodName(ctx);
		methodStack.push(methodName);
		increaseCc();
	}
	@Override public void exitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) { 
		methodStack.pop();
	}

	@Override public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
		String methodName = Java8AntlrFullMethodName.fullMethodName(ctx);
		methodStack.push(methodName);
		increaseCc();
	}
	
	@Override public void exitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) { 
		methodStack.pop();
	}

	
	@Override public void enterForStatement(@NotNull Java8Parser.ForStatementContext ctx) {
		increaseCc();
	}
	
	@Override public void enterEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
		increaseCc();
	}

	@Override public void enterIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) {
		findAndOr(ctx.expression());
		increaseCc();
	}
	
	@Override public void enterConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) {
		if(ctx.ifTernaryExpression()!=null) increaseCc();
	}
	
	@Override public void enterIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
		findAndOr(ctx.expression());
		increaseCc();
	}
	
	@Override public void enterIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) {
		findAndOr(ctx.expression());
		increaseCc();
	}

	private void findAndOr(ExpressionContext exp) {
		if(exp==null) return;
		
		
		for(int i = 0; i < exp.getChildCount(); i++) {
			if(exp.getChild(i).getClass().equals(ExpressionContext.class)) {
				findAndOr((ExpressionContext)exp.getChild(i));
			}
			
			String expr = exp.getChild(i).getText().replace("&&", "&").replace("||", "|");
			int ands = StringUtils.countMatches(expr, "&");
			int ors = StringUtils.countMatches(expr, "|");
			
			increaseCc(ands + ors);
			
		}
	}
	
	@Override public void enterExpression(Java8Parser.ExpressionContext ctx) {
		for(int i = 0; i < ctx.getChildCount(); i++) {
			if(ctx.getChild(i).getText().equals("?")) increaseCc();
		}
	}
	
	@Override public void enterWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { 
		increaseCc();
	}
	
	@Override public void enterSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
		
		for(int i=0; i< ctx.switchBlock().switchBlockStatementGroup().size();i++) {
			if(!ctx.switchBlock().switchBlockStatementGroup().get(i).getText().startsWith("default:")) {
				increaseCc();
			}
		}
		
	}

	@Override public void enterDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { 
		increaseCc();
		
	}
	@Override public void enterCatchClause(Java8Parser.CatchClauseContext ctx) { 
		increaseCc();
		
	}

    private void increaseCc() {
    	increaseCc(1);
    }

    private void increaseCc(int qtd) {
    	
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
