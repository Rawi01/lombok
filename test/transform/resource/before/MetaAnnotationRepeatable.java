//version 8:
//conf: lombok.metaAnnotations += @SourceAnnotation @TargetAnnotation("add this one")+ @TargetAnnotation("and this one")+

import java.lang.annotation.Repeatable;

@SourceAnnotation
@TargetAnnotation("+-flag, add additional")
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