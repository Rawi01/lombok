@SourceAnnotation
@TargetAnnotation({"a", "b"})
class MetaAnnotationArray {
	private String test;
}

@interface SourceAnnotation {
}

@interface TargetAnnotation {
	String[] value();
}
