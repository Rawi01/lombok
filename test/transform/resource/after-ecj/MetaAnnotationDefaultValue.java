@SourceAnnotation @TargetAnnotation(value = "test") class MetaAnnotationDefaultValue {
  MetaAnnotationDefaultValue() {
    super();
  }
}
@interface SourceAnnotation {
  String value() default "test";
}
@interface TargetAnnotation {
  String value();
}
