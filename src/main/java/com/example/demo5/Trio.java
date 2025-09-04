package com.example.demo5;

public class Trio<K, V1, V2> {
    private K key;
    private V1 value1;
    private V2 value2;


    public Trio(K key, V1 value1, V2 value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }


    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V1 getValue1() {
        return value1;
    }

    public void setValue1(V1 value) {
        this.value1 = value;
    }

    public V2 getValue2() {
        return value2;
    }

    public void setValue2(V2 value) {
        this.value2 = value;
    }
}
