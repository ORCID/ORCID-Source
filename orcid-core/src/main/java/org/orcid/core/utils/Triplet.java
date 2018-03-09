package org.orcid.core.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.google.common.base.Objects;
/**
 * @author Angel Montenegro
 */
public class Triplet<F, S, T> {

    public final F first;
    public final S second;
    public final T third;

    /**
     * @param <F>
     *            the first element type
     * 
     * @param <S>
     *            the second element type
     * 
     * @param <T>
     *            the third element type
     * 
     * @param first
     *            the first element, may be null
     * 
     * @param second
     *            the second element, may be null
     * 
     * @param third
     *            the third element, may be null
     * 
     * @return a triplet formed from the three parameters, not null
     */
    public static <F, S, T> Triplet<F, S, T> of(F first, S second, T third) {
        return new Triplet<F, S, T>(first, second, third);
    }

    public Triplet(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    /**
     * Compares the triplet. The types must be {@code Comparable}.
     * 
     * @param other
     *            the other triplet
     * @return negative if this is less, zero if equal, positive if greater
     */
    public int compareTo(Triplet<F, S, T> other) {
        if (other == null)
            return 1;
        return new CompareToBuilder().append(getFirst(), other.getFirst()).append(getSecond(), other.getSecond()).append(getThird(), other.getThird()).toComparison();
    }

    /**
     * Compares this triplet to another based on its elements.
     * 
     * @param obj
     *            the object to compare to, null returns false
     * @return true if the elements of the triplet are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triplet<?, ?, ?>) {
            Triplet<?, ?, ?> other = (Triplet<?, ?, ?>) obj;
            return ObjectUtils.equals(getFirst(), other.getFirst()) && ObjectUtils.equals(getSecond(), other.getSecond())
                    && ObjectUtils.equals(getThird(), other.getThird());
        }
        return false;
    }
    
    /**
     * Returns a suitable hash code.
     * The hash code follows the definition in {@code Map.Entry}.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int myPrime = 73;
        return myPrime * Objects.hashCode(getFirst(), getSecond(), getThird());        
    }
}
