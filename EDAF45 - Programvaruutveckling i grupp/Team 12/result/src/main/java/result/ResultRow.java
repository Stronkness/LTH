package result;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing one row in the result. Has sub classes for different types of races.
 */
public abstract class ResultRow implements Result {

    private final String number;
    private final String name;

    public ResultRow(String number, String name) {
        this.number = number;
        this.name = name;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getErrors() {
        return new ArrayList<>();
    }
}
