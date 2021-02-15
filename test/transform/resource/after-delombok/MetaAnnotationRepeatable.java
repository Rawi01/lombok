//version 8:
import java.lang.annotation.Repeatable;

@SourceAnnotation
@TargetAnnotation("+-flag, add additional")
@TargetAnnotation("add this one")
@TargetAnnotation("and this one")
class MetaAnnotationRepeatable {
}

@interface SourceAnnotation {
}

@Repeatable(RepeatableAnnotation.class)
@interface TargetAnnotation {
	String value();
}

@interface RepeatableAnnotation {
	TargetAnnotation[] value();
}
