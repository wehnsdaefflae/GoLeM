package net.sophomatics.util;

/**
 * Created by mark on 14.07.15.
 */
public class Tuple<A, B> implements Comparable<Tuple<A, B>> {
    public final A a;
    public final B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "(" + this.a + ", " + this.b + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;

        } else if (other == this) {
            return true;

        } else if (!(other instanceof Tuple)){
            return false;
        }

        Tuple cast = (Tuple) other;
        if ((this.a == null && cast.a != null) || (this.a != null && !this.a.equals(cast.a))) {
            return false;

        } else if ((this.b == null && cast.b != null) || (this.b != null && !this.b.equals(cast.b))) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 3;
        result = prime * result + ((this.a == null) ? 0 : this.a.hashCode());
        result = prime * result + ((this.b == null) ? 0 : this.b.hashCode());
        return result;
    }

    @Override
    public int compareTo(Tuple<A, B> other) {
        if (other == null) {
            return -1;
        } else if (other == this) {
            return 0;
        }

        if (this.a instanceof Comparable && other.a instanceof Comparable) {
            Comparable aComp = (Comparable) this.a;
            int aC = aComp.compareTo(other.a);
            if (aC != 0) {
                return aC;
            }
        }

        if (this.b instanceof Comparable && other.b instanceof Comparable) {
            Comparable bComp = (Comparable) this.b;
            return bComp.compareTo(other.b);
        }

        return 0;
    }
}
