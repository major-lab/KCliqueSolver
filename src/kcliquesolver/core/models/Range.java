package kcliquesolver.core.models;

import java.util.InputMismatchException;

public class Range {
    protected final int first;
    protected final int second;

    public Range(int first, int second) throws InputMismatchException{
        if(first > second){
            throw new InputMismatchException("First index must be <= to second index (first = " +
                    first +", second = " + second + ")");
        }
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }
}