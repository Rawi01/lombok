//conf: lombok.metaAnnotations += @SourceAnnotation @TargetAnnotation1("not added") @TargetAnnotation2("override")!

@SourceAnnotation
@TargetAnnotation1("default behaviour, defined annotation > meta annotation")
@TargetAnnotation2("!-flag, override exisiting")
class MetaAnnotationExisiting {
}

@interface SourceAnnotation {
}

@interface TargetAnnotation1 {
	String value();
}

@interface TargetAnnotation2 {
	String value();
}