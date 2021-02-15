@SourceAnnotation(a1 = {"a", "b"}) @TargetAnnotation(a1 = {"a", "b"},a2 = {"a2", "b2"}) class MetaAnnotationBoundArray {
  private String test;
  MetaAnnotationBoundArray() {
    super();
  }
}
@interface SourceAnnotation {
  String[] a1();
  String[] a2() default {"a2", "b2"};
}
@interface TargetAnnotation {
  String[] a1();
  String[] a2();
}
