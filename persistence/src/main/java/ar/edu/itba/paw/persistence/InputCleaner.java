package ar.edu.itba.paw.persistence;

public class InputCleaner {
    // this method removes % and _ from user inputs to prevent use of sql wildcards inappropriately
    public static String clean(String query) {
        return query.replaceAll("[%_]", "");
    }

    // private constructor for no instantiation
    private InputCleaner(){};
}