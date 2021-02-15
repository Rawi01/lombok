//conf: lombok.metaAnnotations += @SourceAnnotation @TargetAnnotation(a1=<a1>, a2=<a2|{"a2", "b2"}>)

@SourceAnnotation(a1 = {"a", "b"})
class MetaAnnotationBoundArray {
	private String test;
}

@interface SourceAnnotation {
	String[] a1();
	String[] a2() default {"a2", "b2"};
}

@interface TargetAnnotation {
	String[] a1();
	String[] a2();
}