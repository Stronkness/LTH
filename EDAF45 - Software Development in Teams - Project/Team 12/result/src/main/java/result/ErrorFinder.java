package result;

/**
 * Class used for finding errors in a result. Every kind of error is
 * implemented as a new instance of this interface, and added to a Matcher.
 * The Matcher applies each error finder to the result.
 *
 * @param <D>
 * @param <R>
 */
public interface ErrorFinder<D extends Driver, R extends Result> {
    /**
     * Applies this error match to the given result.
     *
     * @param driver The driver
     * @param result The driver's result
     * @return The result, possibly decorated with error information.
     */
    R apply(D driver, R result);
}
