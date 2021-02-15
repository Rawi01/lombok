//conf: lombok.metaAnnotations += @SourceAnnotation() @TargetAnnotation(a="a",b=1,c=1,d=1.0f,e=1.0,f=false)
//conf: lombok.metaAnnotations += @SourceAnnotation2() @TargetAnnotation(a="b",b=1,c=1L,d=1F,e=1D,f=true)

@SourceAnnotation
class MetaAnnotationParam {
}

@SourceAnnotation2
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