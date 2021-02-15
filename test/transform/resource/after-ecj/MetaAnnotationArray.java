@SourceAnnotation @TargetAnnotation(value = {"a", "b"}) class MetaAnnotationArray {
  private String test;
  MetaAnnotationArray() {
    super();
  }
}
@interface SourceAnnotation {
}
@interface TargetAnnotation {
  String[] value();
}
