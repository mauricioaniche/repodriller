package br.com.metricminer2.parser.jdt;

import java.util.HashSet;
import java.util.Set;

import br.com.metricminer2.parser.antlr.java8.Java8BaseListener;
import br.com.metricminer2.parser.antlr.java8.Java8Parser;
import br.com.metricminer2.parser.antlr.java8.Java8Parser.InterfaceTypeContext;
import br.com.metricminer2.parser.antlr.java8.Java8Parser.NormalClassDeclarationContext;

public class ClassInfo extends Java8BaseListener {

	private boolean isEnum;
	private String name;
	private Set<String> subtypes;
	private boolean hasSubtype;
	
	public ClassInfo() {
		subtypes = new HashSet<String>();
	}
	
	@Override public void enterClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
		if(name!=null) return;
		
		if(ctx.normalClassDeclaration()!=null) {
		
			NormalClassDeclarationContext clazz = ctx.normalClassDeclaration();
			name = clazz.Identifier().getText();
			
			if(clazz.superinterfaces()!=null) {
				hasSubtype = true;
				for(InterfaceTypeContext type : clazz.superinterfaces().interfaceTypeList().interfaceType()) {
					subtypes.add(type.getText());
				}
			}
			
			if(clazz.superclass()!=null) {
				hasSubtype = true;
				subtypes.add(clazz.superclass().classType().Identifier().getText());
			}
		} else {
			isEnum = true;
			name = ctx.enumDeclaration().Identifier().getText();
			
		}
	}

	@Override public void enterEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
		if(name!=null) return;
		
		isEnum = true;
		name = ctx.Identifier().getText();
	}
	
	public boolean isEnum() {
		return isEnum;
	}
	
	public boolean isSubtypeOrImplementsInterface() {
		return hasSubtype;
	}
	
	public Set<String> subtypeAndInterfaces() {
		return subtypes;
	}

	public String getName() {
		return name;
	}
}
