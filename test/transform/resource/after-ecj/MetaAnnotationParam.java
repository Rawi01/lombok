@SourceAnnotation @TargetAnnotation(a = "a",b = 1,c = 1,d = 1.0,e = 1.0,f = false) class MetaAnnotationParam {
  MetaAnnotationParam() {
    super();
  }
}
@SourceAnnotation2 @TargetAnnotation(a = "b",b = 1,c = 1,d = 1,e = 1,f = true) class MetaAnnotationParam2 {
  MetaAnnotationParam2() {
    super();
  }
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
