package lombok.eclipse.handlers;

import static lombok.eclipse.EcjAugments.ASTNode_handled;
import static lombok.eclipse.handlers.EclipseHandlerUtil.createTypeReference;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.mangosdk.spi.ProviderFor;

import lombok.ConfigurationKeys;
import lombok.core.AST.Kind;
import lombok.core.HandlerPriority;
import lombok.core.configuration.MetaAnnotation;
import lombok.core.configuration.MetaAnnotation.Argument;
import lombok.core.configuration.MetaAnnotation.ArgumentType;
import lombok.core.configuration.MetaAnnotation.ArgumentValue;
import lombok.core.configuration.MetaAnnotation.ArrayArgumentValue;
import lombok.core.configuration.MetaAnnotation.SingleArgumentValue;
import lombok.eclipse.EclipseASTAdapter;
import lombok.eclipse.EclipseASTVisitor;
import lombok.eclipse.EclipseNode;

@ProviderFor(EclipseASTVisitor.class)
@HandlerPriority(-2 ^ 20) // Adding lombok annotations only works if this handle is the first one
public class HandleMetaAnnotations extends EclipseASTAdapter {

	@Override
	public void visitType(EclipseNode typeNode, TypeDeclaration type) {
		type.annotations = handleMetaAnnotation(typeNode, type.annotations);
	}

	@Override
	public void visitField(EclipseNode fieldNode, FieldDeclaration field) {
		field.annotations = handleMetaAnnotation(fieldNode, field.annotations);
	}

	public Annotation[] handleMetaAnnotation(EclipseNode node, Annotation[] annotations) {
		if (annotations == null) return annotations;
		if (ASTNode_handled.get(node.get())) return annotations;

		List<Annotation> result = new ArrayList<Annotation>(Arrays.asList(annotations));

		List<MetaAnnotation> metas = node.getAst().readConfiguration(ConfigurationKeys.META_ANNOTATION);
		for (Annotation annotation : annotations) {
			for (MetaAnnotation meta : metas) {
				boolean typeMatches = EclipseHandlerUtil.typeMatches(meta.getSourceName(), node, annotation.type);
				if (typeMatches) {
					MetaAnnotation.Annotation sourceAnnotation = meta.getSource();
					for (MetaAnnotation.Annotation targetAnnotation : meta.getTargets()) {
						if (targetAnnotation.replace) {
							removeAnnotation(node, targetAnnotation.name, result);
						}

						Annotation newAnnotation = createAnnotation(annotation, targetAnnotation, sourceAnnotation);
						newAnnotation.traverse(new SetGeneratedByVisitor(annotation), (ClassScope) null);
						addAnnotation(node, newAnnotation, result, targetAnnotation.add);
					}
					ASTNode_handled.set(node.get(), true);
				}
			}
		}
		
		if (result.isEmpty()) return null;
		
		return result.toArray(new Annotation[0]);
	}

	private Annotation createAnnotation(Annotation annotation, MetaAnnotation.Annotation targetAnnotation, MetaAnnotation.Annotation sourceAnnotation) {
		// Create annotation parameters
		List<MemberValuePair> args = new ArrayList<MemberValuePair>();
		for (Argument argument : targetAnnotation.args) {
			args.add(createMemberValuePair(annotation, sourceAnnotation, argument));
		}

		TypeReference typeReference = createTypeReference(targetAnnotation.name, annotation);

		if (args.size() == 0) {
			return new MarkerAnnotation(typeReference, 0);
		} else {
			NormalAnnotation na = new NormalAnnotation(typeReference, 0);
			na.memberValuePairs = args.toArray(new MemberValuePair[0]);
			return na;
		}
	}

	private MemberValuePair createMemberValuePair(Annotation annotation, MetaAnnotation.Annotation sourceAnnotation, Argument argument) {
		Expression value = createExpression(argument.value, annotation);
		return new MemberValuePair(argument.name.toCharArray(), 0, 0, value);
	}

	private Expression createExpression(ArgumentValue argumentValue, Annotation source) {
		if (argumentValue instanceof ArrayArgumentValue) {
			ArrayArgumentValue arrayArgumentValue = (ArrayArgumentValue) argumentValue;

			List<Expression> valueExpressions = new ArrayList<Expression>();
			for (ArgumentValue a : arrayArgumentValue.values) {
				valueExpressions.add(createExpression(a, source));
			}

			ArrayInitializer initializer = new ArrayInitializer();
			initializer.expressions = valueExpressions.toArray(new Expression[0]);
			return initializer;
		} else {
			SingleArgumentValue arg = (SingleArgumentValue) argumentValue;

			Expression expression;
			String value = arg.value;
			if (arg.type == ArgumentType.DOUBLE) {
				expression = new DoubleLiteral(value.toCharArray(), 0, 0);
			} else if (arg.type == ArgumentType.FLOAT) {
				expression = new FloatLiteral(value.toCharArray(), 0, 0);
			} else if (arg.type == ArgumentType.INTEGER) {
				expression = IntLiteral.buildIntLiteral(value.toCharArray(), 0, 0);
			} else if (arg.type == ArgumentType.LONG) {
				expression = LongLiteral.buildLongLiteral(value.toCharArray(), 0, 0);
			} else if (arg.type == ArgumentType.BOOLEAN) {
				expression = Boolean.parseBoolean(value) ? new TrueLiteral(0, 0) : new FalseLiteral(0, 0);
			} else if (arg.type == ArgumentType.STRING) {
				expression = new StringLiteral(value.toCharArray(), 0, 0, -1);
			} else if (arg.type == ArgumentType.BOUND) {
				expression = resolveBoundExpression(source, arg);
			} else {
				expression = EclipseHandlerUtil.createNameReference(value, source);
			}
			return expression;
		}
	}

	private Expression resolveBoundExpression(Annotation source, SingleArgumentValue argumentValue) {
		char[] boundArgumentName = argumentValue.value.toCharArray();

		// Use defined value first
		for (MemberValuePair memberValuePair : source.memberValuePairs()) {
			if (Arrays.equals(memberValuePair.name, boundArgumentName)) {
				return EclipseHandlerUtil.copyAnnotationMemberValue(memberValuePair.value);
			}
		}

		// Nothing found, use default value
		if (argumentValue.defaultValue != null) {
			return createExpression(argumentValue.defaultValue, source);
		}

		throw new IllegalArgumentException(MessageFormat.format("Unable to resolve bound parameter ''{0}''", argumentValue.value));
	}

	private void addAnnotation(EclipseNode node, Annotation annotation, List<Annotation> newAnnotations, boolean ignoreExisiting) {
		if (!ignoreExisiting && EclipseHandlerUtil.hasAnnotation(annotation.type.toString(), node)) return;

		newAnnotations.add(annotation);
		node.add(annotation, Kind.ANNOTATION);
	}

	private void removeAnnotation(EclipseNode node, Annotation annotation, List<Annotation> newAnnotations) {
		newAnnotations.remove(annotation);

		EclipseNode annotationNode = node.getNodeFor(annotation);
		if (annotationNode != null) node.removeChild(annotationNode);
	}

	private void removeAnnotation(EclipseNode node, String fqn, List<Annotation> newAnnotations) {
		for (Annotation annotation : new ArrayList<Annotation>(newAnnotations)) {
			if (EclipseHandlerUtil.typeMatches(fqn, node, annotation.type)) {
				removeAnnotation(node, annotation, newAnnotations);
			}
		}
	}
}
