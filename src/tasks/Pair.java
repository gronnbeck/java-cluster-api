package tasks;

/**
 * A neat (and generic) way to collect two types of data in the same object.
 * @param <L> an object stored at the left side of a pair
 * @param <R> an object stored at the right side of a pair
 */
public class Pair<L,R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @return the left side of the pair
     */
    public L getLeft() { return left; }

    /**
     *
     * @return the right side of the pair
     */
    public R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }

}