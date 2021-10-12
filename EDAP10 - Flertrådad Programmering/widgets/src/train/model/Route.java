package train.model;

/** A train route: a never-ending sequence of track segments. */
public interface Route {
    
    /** Returns the next segment in this route. */
    Segment next();
}
