//conf: lombok.metaAnnotations += @NoEtters @lombok.Getter(lombok.AccessLevel.NONE) @lombok.Setter(lombok.AccessLevel.NONE)

import lombok.Getter;

@Getter
class MetaAnnotationToLombok {
	private String test;
	
	@NoEtters
	private String no;
}

@interface NoEtters {
}