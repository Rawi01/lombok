import java.util.List;
import java.util.*;
class BuilderGenericMethod<T> {
	public <N extends Number> Map<N, T> foo(int a, long b) {
		return null;
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public class MapBuilder<N extends Number> {
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private int a;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		private long b;
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		MapBuilder() {
		}
		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public BuilderGenericMethod<T>.MapBuilder<N> a(final int a) {
			this.a = a;
			return this;
		}
		/**
		 * @return {@code this}.
		 */
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public BuilderGenericMethod<T>.MapBuilder<N> b(final long b) {
			this.b = b;
			return this;
		}
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public Map<N, T> build() {
			return BuilderGenericMethod.this.<N>foo(this.a, this.b);
		}
		@java.lang.Override
		@java.lang.SuppressWarnings("all")
		@lombok.Generated
		public java.lang.String toString() {
			return "BuilderGenericMethod.MapBuilder(a=" + this.a + ", b=" + this.b + ")";
		}
	}
	@java.lang.SuppressWarnings("all")
	@lombok.Generated
	public <N extends Number> BuilderGenericMethod<T>.MapBuilder<N> builder() {
		return this.new MapBuilder<N>();
	}
}
