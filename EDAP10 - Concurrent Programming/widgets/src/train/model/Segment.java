package train.model;

/**
 * A short track section. The segment can by occupied by at most one train at a given time.
 * A segment is connected to a TrainView by its Route, so when enter()/exit() are called,
 * this is reflected in the TrainView.
 */
public interface Segment {
    
    /**
     * Marks this segment as occupied by the current thread (train).
     * The segment must not be occupied already.
     */
    void enter();

    /**
     * Mark this segment as free. This is only allowed if the segment 
     * is currently occupied by the current thread (train). 
     */
    void exit();
}
