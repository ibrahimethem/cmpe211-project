import java.io.FileInputStream;
import java.util.Scanner;

/**
 * @author ibrahim
 * @author kadir
 * @author ilker
 */
public class MovieProgram {

    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> userData;
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> movieData;

    private static SeparateChainingHashST<Integer,String> movies;
    private static SeparateChainingHashST<Integer,String> people;


    public static void main(String[] args) {

        StopWatch time2 = new StopWatch();
        readData();
        System.out.println("Read data time = (mils) " + time2.elapsedTimeInMilSec());

        StopWatch time1 = new StopWatch();
        topMatches(userData,"150",10).show();

        System.out.println("Top Matches time = (mils) " +time1.elapsedTimeInMilSec() + "\n");

        StopWatch time3 = new StopWatch();
        SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> itemSimilarity = calculateSimilarItems(movieData,5);
        System.out.println("Calculate Similar Items = (Sec) " + time3.elapsedTime());
        //Object[] itemSim = itemSimilarity.getKeys();

        //for (int i=0;i<itemSim.length;i++) {
            //System.out.printf(itemSim[i] + " : ");
            //itemSimilarity.get((String)itemSim[i]).show();
        //}


    }


    public static void readData() {

        movies = new SeparateChainingHashST<>();
        people = new SeparateChainingHashST<>();

        userData = new SeparateChainingHashST<>();
        movieData = new SeparateChainingHashST<>();

        String[][] tempDataTable = new String[100000][4];
        String[][] tempMoviesTable = new String[1682][3];
        try {
            String[] items = new String[100000];
            Scanner scan = new Scanner(new FileInputStream("u.data"));
            for (int i = 0; scan.hasNextLine(); i++) {
                items[i] = scan.nextLine();
                tempDataTable[i] = items[i].split("\t");
            }
            String[] movieItems = new String[1682];
            Scanner scanItem = new Scanner(new FileInputStream("u.item"));
            for (int i = 0; scanItem.hasNextLine(); i++) {
                movieItems[i] = scanItem.nextLine();
                tempMoviesTable[i] = movieItems[i].split("\\|",3);
            }

            for(int i=0;i< tempMoviesTable.length;i++) {
                movies.put(Integer.parseInt(tempMoviesTable[i][0]),tempMoviesTable[i][1]);
            }

            for (int i = 0; i < tempDataTable.length; i++) {
                if (userData.get(tempDataTable[i][0]) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>();
                    userData.put(tempDataTable[i][0], tempST);
                    people.put(i+1,tempDataTable[i][0]);
                }
                if(movieData.get(movies.get(Integer.parseInt(tempDataTable[i][1]))) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>();
                    movieData.put(movies.get(Integer.parseInt(tempDataTable[i][1])),tempST);
                }
                double myDouble = Double.parseDouble(tempDataTable[i][2]);
                userData.get(tempDataTable[i][0]).put(movies.get(Integer.parseInt(tempDataTable[i][1])), myDouble);
                movieData.get(movies.get(Integer.parseInt(tempDataTable[i][1]))).put(tempDataTable[i][0],myDouble);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static double dist(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2, String movie) {

        double score1 = data.get(person1).get(movie);
        double score2 = data.get(person2).get(movie);

        return Math.pow((score1 - score2), 2);
    }


    public static String[] intersection(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item1, String item2) {

        String[] tempSi;// = new String[data.getN()];
        if(data.getN() == movies.getN()) {
            tempSi = new String[userData.getN()];
        } else {
            tempSi = new String[movieData.getN()];
        }
        int interCounter = 0;
        for (int i = 0; i < tempSi.length; i++) {
            //String itemID = movies.get(i+1);
            String itemID;
            if (data.getN() == movieData.getN()) {
                itemID = "" + (i + 1);
            } else if (data.getN() == userData.getN()) {
                itemID = movies.get(i + 1);
            } else {
                System.out.println("error in intersection method");
                continue;
            }

            if (data.get(item1).get(itemID) != null && data.get(item2).get(itemID) != null) {
                tempSi[interCounter] = itemID;
                interCounter++;
            }
        }
        //System.out.println(interCounter);
        String[] si = new String[interCounter];
        for (int i = 0; i < si.length; i++) {
            si[i] = tempSi[i];
        }
        return si;

    }

    public static double sim_pearson(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person1, String person2) {
        String[] si = intersection(mySSST, person1, person2);
        int n = si.length;
        double sum1 = 0;
        double sum2 = 0;
        double sum1sq = 0;
        double sum2sq = 0;
        double pSum = 0;

        if (n == 0) {
            return 0.0;
        }

        for (int i = 0; i < si.length; i++) {
            sum1 += mySSST.get(person1).get(si[i]);
            sum2 += mySSST.get(person2).get(si[i]);
            sum1sq += Math.pow(mySSST.get(person1).get(si[i]), 2);
            sum2sq += Math.pow(mySSST.get(person2).get(si[i]), 2);
            pSum += mySSST.get(person1).get(si[i]) * mySSST.get(person2).get(si[i]);
        }
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.pow((sum1sq - Math.pow(sum1, 2) / n) * (sum2sq - Math.pow(sum2, 2) / n), 0.5);

        if (den == 0) {
            return 0.0;
        }

        return num / den;

    }


    public static double sim_distance(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String thing1, String thing2) {
        double distance = 0.0;
        String[] common = intersection(data, thing1, thing2);
        if (common.length == 0) {
            return 0;
        }
        for (int i = 0; i < common.length; i++) {
            try {
                distance += dist(data, thing1, thing2, common[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1.0 / (1.0 + Math.pow(distance, 0.5));
    }

    public static double score(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person, String movie) {

        double totalSim = 0.0;
        double totalSim2 = 0.0;
        for (int i = 1; i <= data.getN(); i++) {
            String personID = "" + i;
            if (personID.equals(person)) {
                continue;
            } else if (data.get(personID).get(movie) != null) {
                totalSim += sim_distance(data, person, personID) * data.get(personID).get(movie);
                totalSim2 += sim_distance(data, person, personID);
            }
        }

        return totalSim / totalSim2;
    }

    public static SeparateChainingHashST<String,Double> topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item, int n) {


        Object[] temp;
        //int[] id = new int[data.getN()];
        //temp[data.getN()-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>();
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);
        for (int i = 0; i<data.getN(); i++) {
            //String itemID = "" + (i+1);

            String itemID;
            if(data.getN() == movieData.getN()) {
                itemID = movies.get(i+1);
            } else if(data.getN() == userData.getN()) {
                itemID =  "" + (i+1);
            } else {
                System.out.println("error in topMatches method");
                continue;
            }
            //id[i]=i+1;
            if (itemID.equals(item) ) {
                continue;
            }
            else {
                //id[i]=i+1;
                double sim = sim_pearson(data, item, itemID);
                scores.put(itemID, sim);
                //id[i]=i+1;
                //temp[i] = sim;
            }
        }

        temp = Heap.sort(scores);
        for(int i=0;i<n;i++) {
            //System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
            //int itemID = id[id.length-i-1];
            resultScore.put((String)temp[temp.length-1-i],scores.get((String)temp[temp.length-1-i]));

        }

        return resultScore;
    }


    public static SeparateChainingHashST<String,Double> getRecommendation(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person) {
        SeparateChainingHashST<String, Double> totals = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, Double> simSums = new SeparateChainingHashST<>();

        for(int i=0;i<userData.getN();i++) {
            String personID = "" + (i + 1);
            double sim = sim_pearson(mySSST,person,personID);
            if (personID.equals(person)) {
                continue;
            } else {

                if(sim<=0) {
                    continue;
                }
                for(int j=0;j<movieData.getN();j++) {
                    String moviesID = movies.get(j+1);
                    if(mySSST.get(person).get(moviesID) == null && mySSST.get(personID).get(moviesID) != null) {
                        double score = mySSST.get(personID).get(moviesID);
                        totals.put(moviesID,0.0);
                        totals.put(moviesID, (totals.get(moviesID) + (sim * score)));

                        simSums.put(moviesID,0.0);
                        simSums.put(moviesID,simSums.get(moviesID) + sim);
                    }
                }
            }
        }

        SeparateChainingHashST<String, Double> rankings = new SeparateChainingHashST<>();
        Object[] temp = new Object[movies.getN()];
        //int tempCounter = 0;

        for(int i=0;i<movieData.getN();i++) {

            String moviesID = movies.get(i+1);
            if(totals.get(moviesID) == null) {
                continue;
            } else {
                double tempKey = (totals.get(moviesID) / simSums.get(moviesID));
                rankings.put(moviesID,tempKey);
                //temp[tempCounter] = tempKey;
                //tempCounter++;
            }
        }


        temp = Heap.sort(rankings);

        SeparateChainingHashST<String,Double> result = new SeparateChainingHashST<>(1);

        for(int i=0;i<5;i++){

            //System.out.println(temp[temp.length - 1 - i]);
            result.put((String)temp[temp.length - 1 - i],rankings.get((String)temp[temp.length - 1 - i]));
        }

        return result;

    }

    // todo \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\|||||///////////////////////////////////////

    public static SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> calculateSimilarItems(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, int n) {
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();

        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> itemPrefs = movieData;//transformPrefs(mySSST);

        int c=0;
        for (int i=0;i<itemPrefs.getN();i++) {
            String itemID = movies.get(i+1);
            c++;
            if (c%10 == 0) {
                System.out.println(c + "/"+ itemPrefs.getN());
            }

            SeparateChainingHashST<String,Double> scores = topMatches(itemPrefs,itemID,n);
            result.put(itemID,scores);
        }

        return result;
    }

    public static SeparateChainingHashST<String,Double> getRecommendedItems(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data,
                                                                            SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> itemMatch,String person) {
        SeparateChainingHashST<String, Double> userRatings = data.get(person);
        Object[] userRatingKeys = userRatings.getKeys();
        SeparateChainingHashST<String, Double> scores = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, Double> totalSim = new SeparateChainingHashST<>();
        Object[] itemMatchKeys = itemMatch.getKeys();
        for(int i=0;i<userRatingKeys.length;i++) {
            int itemID = (int)userRatingKeys[i];
            System.out.println(itemID);
            for(int j=0;j<itemMatchKeys.length;j++) {
                String itemID2 = (String)itemMatchKeys[j];
            }
        }

        return null;
    }

    //todo ////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> transformPrefs (SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST){
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();

        for (int i=0;i<movies.getN();i++) {
            result.put(movies.get(i+1),new SeparateChainingHashST<>(16));
        }
        for (int i=0;i<userData.getN();i++){
            String personID = "" + (i+1);
            for (int j=0;j<movies.getN();j++) {
                String moviesID = movies.get(j+1);
                if(mySSST.get(personID).get(moviesID) != null) {
                    result.get(moviesID).put(personID,mySSST.get(personID).get(moviesID));
                } else {
                    result.get(moviesID).put(personID,null);
                }
            }
        }

        return result;
    }

/*

    ////////// INSERTION SORT //////////

    public static Object[] insertionSort(SeparateChainingHashST<String,Double> data,int h) { // Sort a[] into increasing order.
        int N = data.getN();
        Object[] result = data.getKeys();
        // h-sort the array.
        for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
            for (int j = i; j >= h && less(data.get((String)result[j]), data.get((String)result[j - h])); j -= h) {
                //Exchanges takes places between every h distant pairs, not only adjacent items
                exchange(result, j, j - h);
                //exchange2(result, i, j-h);
            }
        }
        return result;
    }
    /**
     * Here exchange takes place for only adjacent items.
     * We obtain the original insertion sort
     * @param data
     *
    public static Object[] insertionSort(SeparateChainingHashST<String,Double> data) { // Sort a[] into increasing order.
        return insertionSort(data,1);
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    /**
     * Helper Function to swap items
     * @param a
     * @param i
     * @param j
     *
    private static void exchange(Object[] a, int i, int j) {
        Object t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    */

}
