@SourceAnnotation
@TargetAnnotation1("default behaviour, defined annotation > meta annotation")
@TargetAnnotation2("override")
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
