package br.com.metricminer2.parser.jdt;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

public class PrintASTVisitor extends ASTVisitor {

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(AnnotationTypeMemberDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(AnonymousClassDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayCreation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ArrayType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(AssertStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Assignment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Block node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(BlockComment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(BreakStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(CastExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(CatchClause node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(CharacterLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ConditionalExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ContinueStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(CreationReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Dimension node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(DoStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(EmptyStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnhancedForStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnumConstantDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ExpressionMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(FieldAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(FieldDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ForStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ImportDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(InfixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Initializer node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(InstanceofExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(IntersectionType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Javadoc node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(LabeledStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(LambdaExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(LineComment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MarkerAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MemberRef node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MemberValuePair node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodRef node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(MethodRefParameter node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(Modifier node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(NameQualifiedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(NormalAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(NullLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(NumberLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(PackageDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ParameterizedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(PostfixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(PrefixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(PrimitiveType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(QualifiedName node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(QualifiedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ReturnStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SimpleName node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SimpleType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SingleMemberAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SingleVariableDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(StringLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperFieldAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperMethodInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SuperMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SwitchCase node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SwitchStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(SynchronizedStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TagElement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TextElement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ThisExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(ThrowStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TryStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeDeclarationStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(TypeParameter node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(UnionType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(WhileStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void endVisit(WildcardType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.endVisit(node);
	}

	@Override
	public void postVisit(ASTNode node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.postVisit(node);
	}

	@Override
	public void preVisit(ASTNode node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		super.preVisit(node);
	}

	@Override
	public boolean preVisit2(ASTNode node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.preVisit2(node);
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(AssertStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Assignment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Block node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(BlockComment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(BreakStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		
		
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(CreationReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Dimension node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(EmptyStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumConstantDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Initializer node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(InstanceofExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(IntersectionType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Javadoc node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(LabeledStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(LambdaExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(LineComment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MemberRef node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MemberValuePair node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRef node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRefParameter node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(Modifier node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(NameQualifiedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(NumberLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ParameterizedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(PrimitiveType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(QualifiedType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodInvocation node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchCase node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TagElement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TextElement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ThisExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclarationStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeLiteral node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeParameter node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(UnionType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationExpression node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	@Override
	public boolean visit(WildcardType node) {
		class Local {}; System.out.println(Local.class.getEnclosingMethod().getName() + "/" + Local.class.getEnclosingMethod().getParameters()[0].getType().getName());
		return super.visit(node);
	}

	

}
