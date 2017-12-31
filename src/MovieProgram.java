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

        double score1 = data.get(person1).get(movie);// What is the rating that person1 gives to this movie
        double score2 = data.get(person2).get(movie);//What is the rating that person2 gives to this movie

        return Math.pow((score1 - score2), 2);// in order not to return a minus we give square of result	
    }

    // It takes 2 ıtem and returns string array that holds cammon movie or user.
    public static String[] intersection(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item1, String item2) {

        String[] tempSi;// = new String[data.getN()];
        if(data.getN() == movies.getN()) {//getN method returns data's all value in separateChainning class. If we are dealing with movies then length of
        	// string array will be length of values in data which is 943
            tempSi = new String[userData.getN()];
        } else {// if not , length will be movies number in the u.item which is 1682.
            tempSi = new String[movieData.getN()];
        }
        int interCounter = 0;
        for (int i = 0; i < tempSi.length; i++) {
            //String itemID = movies.get(i+1);
            String itemID;// we need to get movie or person in this variable
            if (data.getN() == movieData.getN()) {// if we call movie then
                itemID = "" + (i + 1);// we are getting person in here string type
            } else if (data.getN() == userData.getN()) {
                itemID = movies.get(i + 1);
            } else {// if we have a diffrent data neither person or movie.
                System.out.println("error in intersection method");
                continue;
            }

            if (data.get(item1).get(itemID) != null && data.get(item2).get(itemID) != null) {//these two person has to give a rating the movies
            	// OR the movies has a rating of person.
                tempSi[interCounter] = itemID;// giving the items into temporary string array
                interCounter++;// we have count the item can enter this condition . 
            }
        }
        //System.out.println(interCounter);
        String[] si = new String[interCounter];// This is the one will be returned. Array length will be interCounter that holds how many movie or person that cammon.
        for (int i = 0; i < si.length; i++) {//
            si[i] = tempSi[i];// we could not return tempSi array because of its size
        }
        return si;

    }
    // Similarity between two person or movie
    public static double sim_pearson(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person1, String person2) {
        String[] si = intersection(mySSST, person1, person2);// taking those person's cammon movies.
        int n = si.length;
        double sum1 = 0;
        double sum2 = 0;
        double sum1sq = 0;
        double sum2sq = 0;
        double pSum = 0;

        if (n == 0) {
            return 0.0;
        }

        for (int i = 0; i < si.length; i++) {// and getting into this math equation
            sum1 += mySSST.get(person1).get(si[i]);// the rating of person1 that gives the cammon movies
            sum2 += mySSST.get(person2).get(si[i]);//the rating of person2 that gives the cammon movies
            sum1sq += Math.pow(mySSST.get(person1).get(si[i]), 2);// and the squares
            sum2sq += Math.pow(mySSST.get(person2).get(si[i]), 2);
            pSum += mySSST.get(person1).get(si[i]) * mySSST.get(person2).get(si[i]);// multiply these two person's rating
        }
        double num = pSum - (sum1 * sum2 / n);// ın order to find sim.
        double den = Math.pow((sum1sq - Math.pow(sum1, 2) / n) * (sum2sq - Math.pow(sum2, 2) / n), 0.5);

        if (den == 0) {
            return 0.0;
        }

        return num / den;

    }

    //Similarity between to item which can be movie or user
    public static double sim_distance(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String thing1, String thing2) {
        double distance = 0.0;
        String[] common = intersection(data, thing1, thing2);// In order to find cammon - movie or person - use intersection method. 
        if (common.length == 0) {// if there is no cammon item then return 0
            return 0;
        }
        for (int i = 0; i < common.length; i++) {
            try {
                distance += dist(data, thing1, thing2, common[i]);//We are using dist method in order to find distance between these two item
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
            if (personID.equals(person)) {// When the personID -which holds all the person in the loop - is the person, then continue with the other one
                continue;
            } else if (data.get(personID).get(movie) != null) {
                totalSim += sim_distance(data, person, personID) * data.get(personID).get(movie);
                totalSim2 += sim_distance(data, person, personID);
            }
        }

        return totalSim / totalSim2;
    }
    //Take a item and returns a most n similar item like it 
    public static SeparateChainingHashST<String,Double> topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item, int n) {


        Object[] temp;
        //int[] id = new int[data.getN()];
        //temp[data.getN()-1]=0;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>();
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);
        for (int i = 0; i<data.getN(); i++) {
            //String itemID = "" + (i+1);

            String itemID;
            if(data.getN() == movieData.getN()) {// same with intersection part. if the given data is movieData then scores has user names
            	// if not scores and resultScore has movie names
                itemID = movies.get(i+1);
            } else if(data.getN() == userData.getN()) {
                itemID =  "" + (i+1);
            } else {// if the given data neither movieData nor userData then give an error
                System.out.println("error in topMatches method");
                continue;
            }
            //id[i]=i+1;
            if (itemID.equals(item) ) {// itemID holds the item name in the loop. If the itemID is the item then continue with the other one
                continue;
            }
            else {
                //id[i]=i+1;
                double sim = sim_pearson(data, item, itemID);//use sim_pearson method in order to calculate similarty with these two item
                scores.put(itemID, sim);// then put the item with the similarty after that we will be use it
                //id[i]=i+1;
                //temp[i] = sim;
            }
        }

        temp = Heap.sort(scores);// first data is item and the similarity. We are sorting it in order to find Top n movie or person
        // but it is from small to bigger that we do not want it
        for(int i=0;i<n;i++) {
            //System.out.println(id[id.length-i-1] + " "+ temp[id.length-i-1]);
            //int itemID = id[id.length-i-1];
            resultScore.put((String)temp[temp.length-1-i],scores.get((String)temp[temp.length-1-i]));
            /* Now , temp is object array but we need Strings . Therefore we need to change when we put it.
             * Start puttin the temp's object from the last one in order reverse array.
             * Getting scores rating also taking from the last one 
             */
        }

        return resultScore;
    }


    public static SeparateChainingHashST<String,Double> getRecommendation(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, String person) {
        SeparateChainingHashST<String, Double> totals = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, Double> simSums = new SeparateChainingHashST<>();

        for(int i=0;i<userData.getN();i++) {//userData.getN is person number 
            String personID = "" + (i + 1);// this holds every person in loop
            double sim = sim_pearson(mySSST,person,personID);// similarty between person with the others
            if (personID.equals(person)) {// if the personID is the person then continue with the other one
                continue;
            } else {

                if(sim<=0) {// sim cannot be minus
                    continue;
                }
                for(int j=0;j<movieData.getN();j++) {// movieData.getN() is movie Number
                    String moviesID = movies.get(j+1);// this holds movies in loop
                    if(mySSST.get(person).get(moviesID) == null && mySSST.get(personID).get(moviesID) != null) {// if person does not give point to movie
                    	// and the other person gives
                        double score = mySSST.get(personID).get(moviesID);// then holds the rating in score
                        totals.put(moviesID,0.0);
                        totals.put(moviesID, (totals.get(moviesID) + (sim * score)));// put movie with sim*score number

                        simSums.put(moviesID,0.0);
                        simSums.put(moviesID,simSums.get(moviesID) + sim);// and this summations only similarities
                    }
                }
            }
        }

        SeparateChainingHashST<String, Double> rankings = new SeparateChainingHashST<>();// creating a dic
        Object[] temp = new Object[movies.getN()];// temporary object array with size of movie numer
        //int tempCounter = 0;

        for(int i=0;i<movieData.getN();i++) {

            String moviesID = movies.get(i+1);
            if(totals.get(moviesID) == null) {// if the movie is not in totals then continue with others
                continue;
            } else {
                double tempKey = (totals.get(moviesID) / simSums.get(moviesID));// Creating a temporary double that holds Keys of this.
                rankings.put(moviesID,tempKey);//putting the tempKEy
                //temp[tempCounter] = tempKey;
                //tempCounter++;
            }
        }


        temp = Heap.sort(rankings);// now we are sorting temp Object array from small to bigger.

        SeparateChainingHashST<String,Double> result = new SeparateChainingHashST<>(1);// the data what we are gonna return

        for(int i=0;i<5;i++){

            //System.out.println(temp[temp.length - 1 - i]);
            result.put((String)temp[temp.length - 1 - i],rankings.get((String)temp[temp.length - 1 - i]));
            /*Start putting as String from the last one because we want it from bigger to smaller.
             
             */
        }

        return result;

    }

    // todo \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\|||||///////////////////////////////////////
    // Samething with topMatches but for movies.
    public static SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> calculateSimilarItems(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST, int n) {
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();// the dictionary that it will be returned

        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> itemPrefs = movieData;//transformPrefs(mySSST);

        int c=0;// as phyton code wants as 100/1664
        for (int i=0;i<itemPrefs.getN();i++) {
            String itemID = movies.get(i+1);// this holds movies in the loop
            c++;// every time movie change c is gonna increase
            if (c%10 == 0) {
                System.out.println(c + "/"+ itemPrefs.getN());// in the console 
            }

            SeparateChainingHashST<String,Double> scores = topMatches(itemPrefs,itemID,n);// inner dictionary that uses topMatches
            result.put(itemID,scores);// Put them into result outer dictionary
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
    // Takes the dictionary that takes person as key and returns dictionary that movie as key.
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> transformPrefs (SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST){
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();// dictionary that will be returned
        
        for (int i=0;i<movies.getN();i++) {//Firstly we have to put all the movie as key and put empty values
            result.put(movies.get(i+1),new SeparateChainingHashST<>(16));
        }
        for (int i=0;i<userData.getN();i++){// now we have to determine the inner dictionary as user and its rating
            String personID = "" + (i+1);// this holds all the people in loop
            for (int j=0;j<movies.getN();j++) {
                String moviesID = movies.get(j+1);// this holds all the movies in the loop
                if(mySSST.get(personID).get(moviesID) != null) {// if the rating of the person on this movies is not empty
                    result.get(moviesID).put(personID,mySSST.get(personID).get(moviesID));//then get the value of result and put the user as first parametre
                    // and its rating as second.
                } else {
                    result.get(moviesID).put(personID,null);// if person does not watch the movie then put it as empty.
                }
            }
        }

        return result;// return result
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
