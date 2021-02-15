//conf: lombok.metaAnnotations += @SourceAnnotation() @TargetAnnotation({"a", "b"})
@SourceAnnotation
class MetaAnnotationArray {
	private String test;
}

@interface SourceAnnotation {
}

@interface TargetAnnotation {
	String[] value();
}