class SuperBuilderWithPrefixes {
	int mField;
	int xOtherField;
	java.util.List<String> mItems;
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static abstract class SuperBuilderWithPrefixesBuilder<C extends SuperBuilderWithPrefixes, B extends SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilder<C, B>> {
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private int field;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private int otherField;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private java.util.ArrayList<String> items;
		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public B field(final int field) {
			this.field = field;
			return self();
		}
		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public B otherField(final int otherField) {
			this.otherField = otherField;
			return self();
		}
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public B item(final String item) {
			if (this.items == null) this.items = new java.util.ArrayList<String>();
			this.items.add(item);
			return self();
		}
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public B items(final java.util.Collection<? extends String> items) {
			if (items == null) {
				throw new java.lang.NullPointerException("items cannot be null");
			}
			if (this.items == null) this.items = new java.util.ArrayList<String>();
			this.items.addAll(items);
			return self();
		}
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public B clearItems() {
			if (this.items != null) this.items.clear();
			return self();
		}
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		protected abstract B self();
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public abstract C build();
		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public java.lang.String toString() {
			return "SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilder(field=" + this.field + ", otherField=" + this.otherField + ", items=" + this.items + ")";
		}
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	private static final class SuperBuilderWithPrefixesBuilderImpl extends SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilder<SuperBuilderWithPrefixes, SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilderImpl> {
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private SuperBuilderWithPrefixesBuilderImpl() {
		}
		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		protected SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilderImpl self() {
			return this;
		}
		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public SuperBuilderWithPrefixes build() {
			return new SuperBuilderWithPrefixes(this);
		}
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	protected SuperBuilderWithPrefixes(final SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilder<?, ?> b) {
		this.mField = b.field;
		this.xOtherField = b.otherField;
		java.util.List<String> items;
		switch (b.items == null ? 0 : b.items.size()) {
		case 0: 
			items = java.util.Collections.emptyList();
			break;
		case 1: 
			items = java.util.Collections.singletonList(b.items.get(0));
			break;
		default: 
			items = java.util.Collections.unmodifiableList(new java.util.ArrayList<String>(b.items));
		}
		this.mItems = items;
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public static SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilder<?, ?> builder() {
		return new SuperBuilderWithPrefixes.SuperBuilderWithPrefixesBuilderImpl();
	}
}
