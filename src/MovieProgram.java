import java.io.FileInputStream;
import java.util.Arrays;
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
    private static SeparateChainingHashST<Integer,String> persons;

    public static SeparateChainingHashST<String, Double> temp;
    public static int personNumber = 1000;
    public static int moviesNumber = 1000;

    public static void main(String[] args) {

        readData();
        movieData = transformPrefs(userData);
        //movieData.get("182").show();
        //movieData.get("Crude Oasis, The (1995)").show();

        //System.out.println(persons.getN());

        //topMatches(movieData,movies.get(301),5).show();
        //System.out.println(score(userData,"120",movies.get(1000)));

        //System.out.println(userData.getN());
        //System.out.println(movies.getN());
        //System.out.println(movieData.getN());

        //System.out.println(intersection(userData,"200","205").length);
        //System.out.println(sim_distance(movieData,"300","305"));

        //topMatches(movieData,"182",10);

        System.out.println(sim_distance(userData,"111","199"));
        topMatches(userData, "11", 5).show();

        //getRecommendation(userData,"250");

    }


    //bu dosyayi tarama methodu nerdeyse bitti biraz daha degisiklik yapmaliyiz
    public static void readData() {

        movies = new SeparateChainingHashST<>();
        persons = new SeparateChainingHashST<>();
        userData = new SeparateChainingHashST<>(32);
        String[][] tempDataTable = new String[100000][4];
        String[][] tempMoviesTable = new String[1682][3];
        try {
            String[] items = new String[100000];
            Scanner scan = new Scanner(new FileInputStream("u.data"));
            for (int i = 0; scan.hasNextLine(); i++) {
                items[i] = scan.nextLine();
                tempDataTable[i] = items[i].split("\t");
            }
            //String[][] tempMoviesTable = new String[1682][3];
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
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>(4);
                    userData.put(tempDataTable[i][0], tempST);
                    persons.put(i+1,tempDataTable[i][0]);
                }
                double myDouble = Double.parseDouble(tempDataTable[i][2]);
                userData.get(tempDataTable[i][0]).put(movies.get(Integer.parseInt(tempDataTable[i][1])), myDouble);


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

        String[] tempSi = new String[data.getN()];
        int interCounter = 0;
        for (int i = 0; i < tempSi.length; i++) {
            String itemID = movies.get(i+1);
            /*
            String itemID;
            if(data.getN() == movieData.getN()) {
                itemID = movies.get(i+1);
            } else if(data.getN() == userData.getN()) {
                itemID = "" + (i+1);
            } else {
                System.out.println("error in intersection method");
                continue;
            }*/
            if (data.get(item1).get(itemID) != null && data.get(item2).get(itemID) != null) {
                tempSi[interCounter] = itemID;
                interCounter++;
            }
        }
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
            System.out.println("no intersection");
            return 0;
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
            System.out.println("den = 0");
            return 0;
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
        for (int i = 1; i <= userData.getN(); i++) {
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

    public static SeparateChainingHashST topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item, int n) {


        double[] temp = new double[data.getN()];
        int[] id = new int[data.getN()];
        temp[data.getN()-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>(4);
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);

        for (int i = 0; i<data.getN(); i++) {
            String itemID = "" + (i+1);
            /*
            String itemID;
            if(data.getN() == movieData.getN()) {
                itemID = movies.get(i+1);
            } else if(data.getN() == userData.getN()) {
                itemID =  "" + (i+1);
            } else {
                System.out.println("error in topMatches method");
                continue;
            }*/
            id[i]=i+1;
            if (itemID.equals(item) ) {
                continue;
            }
            else {
                double sim = sim_distance(data, item, itemID);
                scores.put(itemID, sim);
                temp[i] = sim;
            }
        }

        insertionSort(temp,id);
        for(int i=0;i<n;i++) {
            //System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
            String itemID = "" + id[id.length-i-1];
            resultScore.put(itemID,temp[id.length-i-1]);
        }

        return resultScore;
    }
    public static SeparateChainingHashST topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String movie, int n,int m) {

        double[] temp = new double[moviesNumber];
        int[] id = new int[moviesNumber];
        temp[moviesNumber-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>(16);
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);


        for (int i = 0; i < moviesNumber; i++) {
            m=0;

            String movieID = movies.get(i+1);
            id[i]=i+1;
            if (movieID.equals(movie) ) {
                continue;
            }
            else {
                double sim = sim_distance(mySSST, movie, movieID);
                scores.put(movieID, sim);
                temp[i] = sim;

            }
        }

        insertionSort(temp,id);
        for(int i=0;i<n;i++) {
            //System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
            String personID = "" + id[id.length-i-1];
            resultScore.put(personID,temp[id.length-i-1]);
        }

        return resultScore;
    }


    public static void getRecommendation(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person) {
        SeparateChainingHashST<String, Double> totals = new SeparateChainingHashST<>(2);
        SeparateChainingHashST<String, Double> simSums = new SeparateChainingHashST<>(2);

        for(int i=0;i<personNumber;i++) {
            String personID = "" + (i + 1);
            double sim = sim_pearson(mySSST,person,personID);
            if (personID.equals(person)) {
                continue;
            } else {

                if(sim<=0) {
                    continue;
                }
                for(int j=0;j<moviesNumber;j++) {
                    String moviesID = "" + (j+1);
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

        SeparateChainingHashST<Double, String > rankings = new SeparateChainingHashST<>(32);
        double[] temp = new double[moviesNumber];
        int tempCounter = 0;

        for(int i=0;i<moviesNumber;i++) {

            String moviesID = "" + (i+1);
            if(totals.get(moviesID) == null) {
                continue;
            } else {
                double tempKey = (totals.get(moviesID) / simSums.get(moviesID));
                rankings.put(tempKey,moviesID);
                temp[tempCounter] = tempKey;
                tempCounter++;

            }
        }

        rankings.show();

        Arrays.sort(temp);

        for(int i=0;i<50;i++){



            //System.out.println(temp[temp.length - 1 - i]);
        }

    }
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> transformPrefs (SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST){
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();
        SeparateChainingHashST<String,Double> temp = new SeparateChainingHashST<>();

        for (int i=0;i<userData.getN();i++){
            String personID = "" + (i+1);
            for (int j=0;j<movies.getN();j++) {
                String moviesID = movies.get(j+1);
                if(mySSST.get(personID).get(moviesID) != null) {
                    temp.put(personID,mySSST.get(personID).get(moviesID));
                    result.put(moviesID,temp);
                }
            }
        }

        return result;
    }



    ////////// INSERTION SORT //////////

    public static void insertionSort(double[] a,int[] b,int h) { // Sort a[] into increasing order.
        int N = a.length;
        // h-sort the array.
        for (int i = h; i < N; i++) { // Insert a[i] among a[i-h], a[i-2*h], a[i-3*h]... .
            for (int j = i; j >= h && less(a[j], a[j - h]); j -= h) {
                //Exchanges takes places between every h distant pairs, not only adjacent items
                exchange(a, j, j - h);
                exchange2(b, i, j-h);
            }
        }
    }
    /**
     * Here exchange takes place for only adjacent items.
     * We obtain the original insertion sort
     * @param a
     */
    public static void insertionSort(double[] a,int[]b) { // Sort a[] into increasing order.
        insertionSort(a,b,1);
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    /**
     * Helper Function to swap items
     * @param a
     * @param i
     * @param j
     */
    private static void exchange(double[] a, int i, int j) {
        double t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
    private static void exchange2(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] =  t;
    }



}
