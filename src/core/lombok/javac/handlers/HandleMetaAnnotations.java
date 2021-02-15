package lombok.javac.handlers;

import static lombok.javac.handlers.JavacHandlerUtil.*;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.mangosdk.spi.ProviderFor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCAnnotation;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import lombok.ConfigurationKeys;
import lombok.core.AST.Kind;
import lombok.core.HandlerPriority;
import lombok.core.configuration.MetaAnnotation;
import lombok.core.configuration.MetaAnnotation.Annotation;
import lombok.core.configuration.MetaAnnotation.Argument;
import lombok.core.configuration.MetaAnnotation.ArgumentType;
import lombok.core.configuration.MetaAnnotation.ArgumentValue;
import lombok.core.configuration.MetaAnnotation.ArrayArgumentValue;
import lombok.core.configuration.MetaAnnotation.SingleArgumentValue;
import lombok.javac.JavacASTAdapter;
import lombok.javac.JavacASTVisitor;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

@ProviderFor(JavacASTVisitor.class)
@HandlerPriority(-2 ^ 20) // Adding lombok annotations only works if this handle is the first one
public class HandleMetaAnnotations extends JavacASTAdapter {

	@Override
	public void visitType(JavacNode typeNode, JCClassDecl type) {
		handleMetaAnnotations(typeNode, type.mods, type);
	}

	@Override
	public void visitField(JavacNode fieldNode, JCVariableDecl field) {
		handleMetaAnnotations(fieldNode, field.mods, field);
	}

	private void handleMetaAnnotations(JavacNode node, JCModifiers mods, JCTree source) {
		if (mods.annotations.size() == 0) return;

		java.util.List<JCAnnotation> result = new ArrayList<JCAnnotation>(mods.annotations);

		java.util.List<MetaAnnotation> metas = node.getAst().readConfiguration(ConfigurationKeys.META_ANNOTATION);
		for (JCAnnotation annotation : mods.annotations) {
			for (MetaAnnotation meta : metas) {
				boolean typeMatches = typeMatches(meta.getSourceName(), node, annotation.annotationType);
				if (typeMatches) {
					MetaAnnotation.Annotation sourceAnnotation = meta.getSource();
					for (MetaAnnotation.Annotation targetAnnotation : meta.getTargets()) {
						if (targetAnnotation.replace) {
							removeAnnotation(node, targetAnnotation.name, result);
						}

						JavacNode annotationNode = node.getNodeFor(annotation);
						JCAnnotation newAnnotation = createAnnotation(annotationNode, targetAnnotation, sourceAnnotation);
						recursiveSetGeneratedBy(newAnnotation, annotationNode);
						addAnnotation(node, newAnnotation, result, targetAnnotation.add);
					}
				}
			}
		}
		
		ListBuffer<JCAnnotation> resultBuffer = new ListBuffer<JCAnnotation>();
		resultBuffer.addAll(result);
		mods.annotations = resultBuffer.toList();
	}

	private JCAnnotation createAnnotation(JavacNode source, Annotation targetAnnotation, Annotation sourceAnnotation) {
		ListBuffer<JCExpression> args = new ListBuffer<JCExpression>();
		for (Argument argument : targetAnnotation.args) {
			args.add(createAnnotationArgument(source, sourceAnnotation, argument));
		}

		JCExpression annType = chainDotsString(source, targetAnnotation.name);
		JavacTreeMaker maker = source.getTreeMaker();
		return maker.Annotation(annType, args.toList());
	}

	private JCExpression createAnnotationArgument(JavacNode source, Annotation sourceAnnotation, Argument argument) {
		JavacTreeMaker maker = source.getTreeMaker();
		JCExpression lhs = maker.Ident(source.toName(argument.name));
		JCExpression rhs = createExpression(argument.value, source);
		return maker.Assign(lhs, rhs);
	}

	private JCExpression createExpression(ArgumentValue argumentValue, JavacNode source) {
		JavacTreeMaker maker = source.getTreeMaker();

		if (argumentValue instanceof ArrayArgumentValue) {
			ArrayArgumentValue arrayArgumentValue = (ArrayArgumentValue) argumentValue;

			ListBuffer<JCExpression> valueExpressions = new ListBuffer<JCExpression>();
			for (ArgumentValue value : arrayArgumentValue.values) {
				valueExpressions.add(createExpression(value, source));
			}
			return maker.NewArray(null, List.<JCExpression>nil(), valueExpressions.toList());

		} else {
			SingleArgumentValue arg = (SingleArgumentValue) argumentValue;

			JCExpression expression;
			String value = arg.value;
			if (arg.type == ArgumentType.DOUBLE) {
				expression = maker.Literal(Double.parseDouble(value));
			} else if (arg.type == ArgumentType.FLOAT) {
				expression = maker.Literal(Float.parseFloat(value));
			} else if (arg.type == ArgumentType.INTEGER) {
				expression = maker.Literal(Integer.parseInt(value));
			} else if (arg.type == ArgumentType.LONG) {
				expression = maker.Literal(Long.parseLong(value));
			} else if (arg.type == ArgumentType.BOOLEAN) {
				expression = maker.Literal(Boolean.parseBoolean(value));
			} else if (arg.type == ArgumentType.STRING) {
				expression = maker.Literal(value);
			} else if (arg.type == ArgumentType.BOUND) {
				expression = resolveBoundExpression(source, arg);
			} else {
				expression = chainDotsString(source, value);
			}
			return expression;
		}
	}

	private JCExpression resolveBoundExpression(JavacNode source, SingleArgumentValue argumentValue) {
		JCAnnotation sourceAnnotation = (JCAnnotation) source.get();

		// Use defined value first
		for (JCExpression arg : sourceAnnotation.args) {
			if (arg instanceof JCIdent && "value".equals("value")) {
				return cloneType(source.getTreeMaker(), arg, source);
			}
			if (arg instanceof JCAssign) {
				JCAssign assign = (JCAssign) arg;
				if (assign.lhs.toString().equals(argumentValue.value)) {
					return cloneType(source.getTreeMaker(), assign.rhs, source);
				}
			}
		}

		// Nothing found, use default value
		if (argumentValue.defaultValue != null) {
			return createExpression(argumentValue.defaultValue, source);
		}

		throw new IllegalArgumentException(MessageFormat.format("Unable to resolve bound parameter ''{0}''", argumentValue.value));
	}

	private void addAnnotation(JavacNode node, JCAnnotation annotation, java.util.List<JCAnnotation> newAnnotations, boolean ignoreExisiting) {
		if (!ignoreExisiting && hasAnnotation(annotation.annotationType.toString(), node)) return;

		newAnnotations.add(annotation);
		node.add(annotation, Kind.ANNOTATION);
	}

	private void removeAnnotation(JavacNode node, JCAnnotation annotation, java.util.List<JCAnnotation> newAnnotations) {
		newAnnotations.remove(annotation);

		JavacNode annotationNode = node.getNodeFor(annotation);
		if (annotationNode != null) node.removeChild(annotationNode);
	}

	private void removeAnnotation(JavacNode node, String fqn, java.util.List<JCAnnotation> newAnnotations) {
		for (JCAnnotation annotation : new ArrayList<JCAnnotation>(newAnnotations)) {
			if (typeMatches(fqn, node, annotation.annotationType)) {
				removeAnnotation(node, annotation, newAnnotations);
			}
		}
	}
}
