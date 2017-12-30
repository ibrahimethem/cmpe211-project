/**
 * @author ibrahim
 */
public class Heap {

    // This class should not be instantiated.
    private Heap() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param pq the array to be sorted
     */
    public static Object[] sort(SeparateChainingHashST<String,Double> pq) {
        Object[] result = pq.getKeys();
        int n = result.length;
        //todo
        for (int k = n/2; k >= 1; k--)
            sink(result, k, n,pq);
        while (n > 1) {
            exch(result, 1, n--);
            sink(result, 1, n,pq);
        }

        return result;
    }

    /***************************************************************************
     * Helper functions to restore the heap invariant.
     ***************************************************************************/

    private static void sink(Object[] pq, int k, int n,SeparateChainingHashST<String,Double> pq1) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && less(pq, j, j+1,pq1)) j++;
            if (!less(pq, k, j,pq1)) break;
            exch(pq, k, j);
            k = j;
        }
    }

    /***************************************************************************
     * Helper functions for comparisons and swaps.
     * Indices are "off-by-one" to support 1-based indexing.
     ***************************************************************************/
    private static boolean less(Object[] pq, int i, int j,SeparateChainingHashST<String,Double> pq1) {
        return pq1.get( (String)pq[i-1] ).compareTo(pq1.get( (String)pq[j-1] )) < 0;
    }

    private static void exch(Object[] pq, int i, int j) {
        Object swap = pq[i-1];
        pq[i-1] = pq[j-1];
        pq[j-1] = swap;
    }

}
