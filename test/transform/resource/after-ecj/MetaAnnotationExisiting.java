@SourceAnnotation @TargetAnnotation1("default behaviour, defined annotation > meta annotation") @TargetAnnotation2(value = "override") class MetaAnnotationExisiting {
  MetaAnnotationExisiting() {
    super();
  }
}
@interface SourceAnnotation {
}
@interface TargetAnnotation1 {
  String value();
}
@interface TargetAnnotation2 {
  String value();
}
