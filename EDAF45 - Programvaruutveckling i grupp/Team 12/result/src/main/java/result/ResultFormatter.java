package result;

/**
 * Used to format result. Sub classes used for different types of races.
 *
 * @param <T>
 */
public interface ResultFormatter<T extends Result> {

    /**
     * Separator used in-between columns in results.
     */
    String SEP = ";";

    /**
     * Produce a complete formatted result row for one driver.
     *
     * @param driverResult The result for one driver.
     * @return Properly formatted result row
     */
    String formatDriver(T driverResult);

}
