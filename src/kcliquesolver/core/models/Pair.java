package kcliquesolver.core.models;

public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    public Pair(T1 first_, T2 second_) {
        this.first = first_;
        this.second = second_;
    }

    public T1 getFirst() {
        return this.first;
    }

    public T2 getSecond() {
        return this.second;
    }
}
