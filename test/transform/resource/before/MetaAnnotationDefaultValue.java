//conf: lombok.metaAnnotations += @SourceAnnotation() @TargetAnnotation(<value|"test">)

@SourceAnnotation
class MetaAnnotationDefaultValue {
}

@interface SourceAnnotation {
	String value() default "test";
}

@interface TargetAnnotation {
	String value();
}