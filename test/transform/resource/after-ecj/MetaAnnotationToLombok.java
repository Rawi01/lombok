import lombok.Getter;
@Getter class MetaAnnotationToLombok {
  private String test;
  private @NoEtters @lombok.Getter(value = lombok.AccessLevel.NONE) @lombok.Setter(value = lombok.AccessLevel.NONE) String no;
  MetaAnnotationToLombok() {
    super();
  }
  public @java.lang.SuppressWarnings("all") String getTest() {
    return this.test;
  }
}
@interface NoEtters {
}
