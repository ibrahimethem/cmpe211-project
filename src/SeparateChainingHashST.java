/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 */
public class SeparateChainingHashST<Key, Value> {

    private int N = 0; // number of key-value pairs
    private int M; // hash table size
    private SequentialSearchST<Key, Value>[] st; // array of ST objects

    private ArrayList<Key> keys;

    public SeparateChainingHashST() {
        // this code is about a factor of 1,000 faster than SequentialSearchST
        this(512); // default value for M
    }

    public SeparateChainingHashST(int M) { // Create M linked lists.
        this.M = M;
        // We need a cast because Java prohibits arrays with generics.
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[M];
        keys = new ArrayList<Key>();
        for (int i = 0; i < M; i++) {
            st[i] = new SequentialSearchST();
        }
    }

    private int hash(Key key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

    public Value get(Key key) {
        return (Value) st[hash(key)].get(key);
    }

    public void put(Key key, Value val) {
        if (st[hash(key)].get(key) == null) {
            N++;
            keys.add(key);
        }
        st[hash(key)].put(key, val);
    }

    public void delete(Key key) {
        if (key == null) return;

        int i = hash(key);
        if (st[i].get(key) != null)
            N--;
            st[i].delete(key);

    }
    public void show() {
        for (int i = 0; i < M; i++) {
            st[i].show();
        }
    }

    public int getN() {
        return N;
    }

    public Object[] getKeys() {
        Object[] tempValues = new Object[keys.size()];

        for(int i = 0; i< keys.size(); i++) {
            tempValues[i] = keys.get(i);
        }
        return tempValues;
    }

    /////////////////////////////////////////////////////
    public static void main(String[] args) {
        SeparateChainingHashST<String, Integer> st;
        st = new SeparateChainingHashST<>(16);

        String[] keys = {"H", "E", "L", "L", "O", "T", "U", "R"};
        //  0    1    2    3    4

        for (int i = 0; i < 8; i++) {
            st.put(keys[i], i);
        }
        st.show();

    }
}
