package br.com.metricminer2.parser.jdt;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class JDTClassInfo extends ASTVisitor {

	private boolean isEnum;
	private String name;
	private Set<String> subtypes;
	private boolean hasSubtype;
	
	public JDTClassInfo() {
		subtypes = new HashSet<String>();
	}

	public boolean visit(EnumDeclaration node) {
		if(name!=null) return false;
		
		isEnum = true;
		name = node.getName().getIdentifier();
		
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		
		if(name!=null) return false;
		
		name = node.getName().getIdentifier();

		if(node.getSuperclassType() != null) {
			hasSubtype = true;
			subtypes.add(node.getSuperclassType().toString());
		}
		
		if(node.superInterfaceTypes().size() > 0) {
			hasSubtype = true;
			for(Object obj : node.superInterfaceTypes()) {
				Type t = (Type) obj;
				
				subtypes.add(t.toString());
			}
		}
		
		return super.visit(node);
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
