class MetaAnnotationToLombok {
	private String test;
	@NoEtters
	private String no;

	@java.lang.SuppressWarnings("all")
	public String getTest() {
		return this.test;
	}
}

@interface NoEtters {
}
