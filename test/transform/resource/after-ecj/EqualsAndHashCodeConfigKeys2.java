@lombok.EqualsAndHashCode class EqualsAndHashCodeConfigKeys2Object extends Object {
  EqualsAndHashCodeConfigKeys2Object() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCodeConfigKeys2Object)))
        return false;
    final EqualsAndHashCodeConfigKeys2Object other = (EqualsAndHashCodeConfigKeys2Object) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @lombok.Generated boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCodeConfigKeys2Object);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated int hashCode() {
    final int result = 1;
    return result;
  }
}
@lombok.EqualsAndHashCode class EqualsAndHashCodeConfigKeys2Parent {
  EqualsAndHashCodeConfigKeys2Parent() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCodeConfigKeys2Parent)))
        return false;
    final EqualsAndHashCodeConfigKeys2Parent other = (EqualsAndHashCodeConfigKeys2Parent) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @lombok.Generated boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCodeConfigKeys2Parent);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated int hashCode() {
    final int result = 1;
    return result;
  }
}
@lombok.EqualsAndHashCode class EqualsAndHashCodeConfigKeys2 extends EqualsAndHashCodeConfigKeys2Parent {
  int x;
  EqualsAndHashCodeConfigKeys2() {
    super();
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated boolean equals(final java.lang.Object o) {
    if ((o == this))
        return true;
    if ((! (o instanceof EqualsAndHashCodeConfigKeys2)))
        return false;
    final EqualsAndHashCodeConfigKeys2 other = (EqualsAndHashCodeConfigKeys2) o;
    if ((! other.canEqual((java.lang.Object) this)))
        return false;
    if ((! super.equals(o)))
        return false;
    if ((this.x != other.x))
        return false;
    return true;
  }
  protected @java.lang.SuppressWarnings("all") @lombok.Generated boolean canEqual(final java.lang.Object other) {
    return (other instanceof EqualsAndHashCodeConfigKeys2);
  }
  public @java.lang.Override @java.lang.SuppressWarnings("all") @lombok.Generated int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    result = ((result * PRIME) + this.x);
    return result;
  }
}
