@SourceAnnotation
@TargetAnnotation("test")
class MetaAnnotationDefaultValue {
}

@interface SourceAnnotation {
	String value() default "test";
}

@interface TargetAnnotation {
	String value();
}
