/**
 * Elementary symbol-table (also called dictionary or map) * Sequential search
 * in an unordered linked list
 *
 * @author uzaycetin SequentialSearchST
 * @param <Key>
 * @param <Value>
 */
public class SequentialSearchST<Key, Value> {

    // first node in the linked list
    private Node first;

    /////////////////////////////////////////////////////
    // Each node holds
    //     * key-value pair
    //     * and a reference to anothe node
    private class Node { // linked-list node

        Key key;
        Value val;
        Node next;

        public Node(Key key, Value val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    /////////////////////////////////////////////////////
    // Analysis of SEARCH Algorithm
    //      One loop : Order(n)
    public Value get(Key key) {
        // starting from the first node, advance one by one
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) {
                return x.val; // search hit
            }
        }
        return null; // search miss
    }

    /////////////////////////////////////////////////////
    // Analysis of INSERTION Algorithm
    //      One loop : Order(n)
    public void put(Key key, Value val) {
        // starting from the first node, advance one by one
        for (Node x = first; x != null; x = x.next) {
            if (key.equals(x.key)) { // Update value if found
                x.val = val;
                return;
            } // Search hit: update val.
        }
        first = new Node(key, val, first); // Search miss: add new node to the begining.
    }

    /////////////////////////////////////////////////////
    public void show() {
        for (Node x = first; x != null; x = x.next) {
            System.out.print(" , (" + x.key+ ": "+x.val + ")");
        }
        System.out.println("\n");
    }

    /**
     * Removes the specified key and its associated value from this symbol table
     * (if the key is in this symbol table).
     *
     */
    public void delete(Key key) {
        if (key != null) {
            first = delete(first, key);
        }
    }

    // delete key in linked list beginning at Node x
    // warning: function call stack too large if table is large
    private Node delete(Node x, Key key) {
        if (x == null) {
            return null;
        }
        if (key.equals(x.key)) {
            return x.next;
        }
        // iterate through the list
        x.next = delete(x.next, key);
        return x;
    }


    // This method can not delete last node
    public void delMiddle(Key key){
        // Control for the first node
        if(key.equals(first.key)){
            first = first.next;
        }

        // Control for the last node
        // TO-DO

        Node prev = first;
        while(prev.next != null){
            if(key.equals(prev.next.key)){
                //System.out.print(key + " found");
                if(prev.next.next != null)
                    prev.next = prev.next.next;
            }

            System.out.print("-> (" + prev.key+ ": "+prev.val + ")");
            prev = prev.next;
        }
        System.out.print("LAST-> (" + prev.key+ ": "+prev.val + ")");
        System.out.println("\n");

    }



    /////////////////////////////////////////////////////
    public static void main(String[] args) {
        SequentialSearchST<String, Integer> st;
        st = new SequentialSearchST<>();

        String[] keys = {"H", "E", "L", "L", "O"};
        //  0    1    2    3    4

        for (int i = 0; i < 5; i++) {
            st.put(keys[i], i);
        }

        st.put("X", 8);
        st.show();

        st.put("X", 7);
        st.show();

        st.delMiddle("H");

        st.delete("H");
        st.show();



    }
}