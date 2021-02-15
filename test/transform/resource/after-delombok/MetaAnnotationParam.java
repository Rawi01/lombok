@SourceAnnotation
@TargetAnnotation(a = "a", b = 1, c = 1, d = 1.0F, e = 1.0, f = false)
class MetaAnnotationParam {
}

@SourceAnnotation2
@TargetAnnotation(a = "b", b = 1, c = 1L, d = 1.0F, e = 1.0, f = true)
class MetaAnnotationParam2 {
}

@interface SourceAnnotation {
}

@interface SourceAnnotation2 {
}

@interface TargetAnnotation {
	String a();

	int b();

	long c();

	float d();

	double e();

	boolean f();
}
