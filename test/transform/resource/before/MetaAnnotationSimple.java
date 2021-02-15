//conf: lombok.metaAnnotations += @SourceAnnotation @tests.TargetAnnotation
package tests;

@SourceAnnotation
class MetaAnnotationSimple {
}

@interface SourceAnnotation {
}

@interface TargetAnnotation {
}