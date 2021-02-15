import java.lang.annotation.Repeatable;
@SourceAnnotation @TargetAnnotation("+-flag, add additional") @TargetAnnotation(value = "add this one") @TargetAnnotation(value = "and this one") class MetaAnnotationRepeatable {
  MetaAnnotationRepeatable() {
    super();
  }
}
@interface SourceAnnotation {
}
@Repeatable(RepeatableAnnotation.class) @interface TargetAnnotation {
  String value();
}
@interface RepeatableAnnotation {
  TargetAnnotation[] value();
}
