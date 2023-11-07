package com.example.findaseat.Utils;

public class BoolResPair<A,B> {
    private final A first;
    private final B second;

    public BoolResPair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
