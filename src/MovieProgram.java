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

    private static SeparateChainingHashST<String,String> movies;

    public static SeparateChainingHashST<String, Double> temp;
    public static int personNumber = userData.getN();
    public static int moviesNumber = movieData.getN();

    public static void main(String[] args) {

        userData = readData();
        movieData = transformPrefs(userData);
        //movieData.get("182").show();

        System.out.println(userData.getN());
        System.out.println(movieData.getN());

        //System.out.println(intersection(userData,"200","205").length);
        //System.out.println(similarity(movieData,"300","305"));

        //topMatches(movieData,"182",10);
        //topMatches(userData, "130", 10).show();

        //getRecommendation(userData,"250");

    }


    //bu dosyayi tarama methodu nerdeyse bitti biraz daha degisiklik yapmaliyiz
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> readData() {

        movies = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> tempUserData = new SeparateChainingHashST<>(32);
        String[][] tempDataTable = new String[100000][4];
        try {
            String[] items = new String[100000];
            Scanner scan = new Scanner(new FileInputStream("u.data"));
            for (int i = 0; scan.hasNextLine(); i++) {
                items[i] = scan.nextLine();
                tempDataTable[i] = items[i].split("\t");
            }

            for (int i = 0; i < tempDataTable.length; i++) {
                if (tempUserData.get(tempDataTable[i][0]) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>(8);
                    tempUserData.put(tempDataTable[i][0], tempST);
                } else {
                    double myDouble = Double.parseDouble(tempDataTable[i][2]);
                    tempUserData.get(tempDataTable[i][0]).put(tempDataTable[i][1], myDouble);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempUserData;

    }


    public static double dist(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2, String movie) {

        double score1 = data.get(person1).get(movie);
        double score2 = data.get(person2).get(movie);

        return Math.pow((score1 - score2), 2);
    }


    public static String[] intersection(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2) {


        String[] tempSi = new String[data.getN()];
        int interCounter = 0;
        for (int i = 0; i < tempSi.length; i++) {
            String movieID = "" + (i + 1);
            if (data.get(person1).get(movieID) != null && data.get(person2).get(movieID) != null) {
                tempSi[interCounter] = movieID;
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
            System.out.println("n = 0");
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
            //System.out.println("den = 0");
            return 0;
        }

        return num / den;


    }


    public static double similarity(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2) {
        double distance = 0.0;
        String[] common = intersection(data, person1, person2);
        if (common.length == 0) {
            return 0;
        }
        for (int i = 0; i < common.length; i++) {
            //System.out.println(i);
            try {
                distance += dist(data, person1, person2, common[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1.0 / (1.0 + Math.pow(distance, 0.5));
    }

    public static double score(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person, String movie) {

        double totalSim = 0.0;
        double totalSim2 = 0.0;
        for (int i = 1; i <= 943; i++) {
            String personID = "" + i;
            if (personID.equals(person)) {
                continue;
            } else if (mySSST.get(personID).get(movie) != null) {
                totalSim += similarity(mySSST, person, personID) * mySSST.get(personID).get(movie);
                totalSim2 += similarity(mySSST, person, personID);
            }
        }

        return totalSim / totalSim2;
    }

    public static SeparateChainingHashST topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person, int n) {


        double[] temp = new double[personNumber];
        int[] id = new int[personNumber];
        temp[personNumber-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>(1);
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);


        for (int i = 0; i < personNumber; i++) {
            String personID = "" + (i+1);
            id[i]=i+1;
            if (personID.equals(person) ) {
                continue;
            }
            else {
                double sim = similarity(mySSST, person, personID);
                scores.put(personID, sim);
                temp[i] = sim;

            }
        }

        insertionSort(temp,id);
        for(int i=0;i<n;i++) {
            System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
            String personID = "" + id[id.length-i-1];
            resultScore.put(personID,temp[id.length-i-1]);
        }

        return resultScore;
    }
    public static SeparateChainingHashST topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String movie, int n,int m) {

        double[] temp = new double[moviesNumber];
        int[] id = new int[moviesNumber];
        temp[moviesNumber-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>(1);
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);


        for (int i = 0; i < moviesNumber; i++) {
            m=0;

            String movieID = "" + (i+1);
            id[i]=i+1;
            if (movieID.equals(movie) ) {
                continue;
            }
            else {
                double sim = similarity(mySSST, movie, movieID);
                scores.put(movieID, sim);
                temp[i] = sim;

            }
        }

        insertionSort(temp,id);
        for(int i=0;i<n;i++) {
            System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
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
        for (int i=0;i<personNumber;i++){
            String personID = "" + (i+1);
            for (int j=0;j<moviesNumber;j++) {
                String moviesID = "" + (j+1);
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
