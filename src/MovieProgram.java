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

    private static String[] movieNames;
    private static SeparateChainingHashST<Integer,String> movies;
    private static SeparateChainingHashST<Integer,String> people;


    public static void main(String[] args) {

        StopWatch time2 = new StopWatch();
        /* TOY DATA CREATING BEGINNING
        movieNames = new String[3];
        movieNames[0] = "Star Wars";
        movieNames[1] = "Amelie";
        movieNames[2] = "Gladiator";
        movies = new SeparateChainingHashST<>();
        movies.put(1,movieNames[0]);
        movies.put(2,movieNames[1]);
        movies.put(3,movieNames[2]);
        userData = new SeparateChainingHashST<>();
        SeparateChainingHashST<String,Double> temp = new SeparateChainingHashST<>();
        temp.put(movieNames[0],1.0);
        temp.put(movieNames[1],5.0);
        temp.put(movieNames[2],1.0);
        userData.put("1",temp);
        temp = new SeparateChainingHashST<>();
        temp.put(movieNames[0],5.0);
        temp.put(movieNames[1],3.0);
        temp.put(movieNames[2],4.0);
        userData.put("2",temp);
        temp = new SeparateChainingHashST<>();
        temp.put(movieNames[0],4.0);
        temp.put(movieNames[1],3.5);
        temp.put(movieNames[2],5.0);
        userData.put("3",temp);
        temp = new SeparateChainingHashST<>();
        temp.put(movieNames[2],5.0);
        userData.put("4",temp);

        movieData = transformPrefs(userData);

        //TOY DATA CREATING END*/

        readData();
        System.out.println("Read data time = (mils) " + time2.elapsedTimeInMilSec());

        StopWatch time1 = new StopWatch();
        topMatches(userData,"4",3).show();

        System.out.println("Top Matches time = (mils) " +time1.elapsedTimeInMilSec() + "\n");

        StopWatch time3 = new StopWatch();
        SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> itemSimilarity = calculateSimilarItems(movieData);
        System.out.println("Calculate Similar Items = (Sec) " + time3.elapsedTime());
        Object[] itemSim = itemSimilarity.getKeys();

        /*for (int i=0;i<itemSim.length;i++) {
            System.out.printf(itemSim[i] + " : ");
            itemSimilarity.get((String)itemSim[i]).show();
        }*/

        //System.out.println(dist(userData,"1","2","Star Wars"));
        //System.out.println(dist(userData,"1","2","Amelie"));
        //System.out.println(intersection(userData,"1","2").length);

        //System.out.println(sim_distance(userData,"4","3"));
        //System.out.println(sim_distance(userData,"4","1"));
        //System.out.println(sim_distance(userData,"4","2"));

        //topMatches(movieData,"Gladiator",2).show();

        //getRecommendation(movieData,"Amelie").show();

        //System.out.println(score(movieData,"Star Wars","4"));

        //System.out.println(score(userData,"4","Amelie"));

        //getRecommendation(movieData)

        //SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> itemMatch = calculateSimilarItems(movieData,5);

        System.out.println("Get Recommendations for 87th person:\n");

        getRecommendation(userData,"87").show();

        System.out.println("Item-based recommendations for Movielens Dataset\n" + "for 87th person\n\n");
        getRecommendedItems(userData,itemSimilarity,"87",10).show();




    }

    /**
     *
     */
    public static void readData() {

        movies = new SeparateChainingHashST<>();
        people = new SeparateChainingHashST<>();

        userData = new SeparateChainingHashST<>();
        movieData = new SeparateChainingHashST<>();


        String[][] tempDataTable = new String[100000][4];
        String[][] tempMoviesTable = new String[1682][3];
        movieNames = new String[1682];
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
                movieNames[i] = tempMoviesTable[i][1];
            }

            for (int i = 0; i < tempDataTable.length; i++) {
                if (userData.get(tempDataTable[i][0]) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>();
                    userData.put(tempDataTable[i][0], tempST);
                    people.put(i+1,tempDataTable[i][0]);
                }
                if (movieData.get(movies.get(Integer.parseInt(tempDataTable[i][1]))) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>();
                    String movieName = movies.get(Integer.parseInt(tempDataTable[i][1]));
                    movieData.put(movieName, tempST);
                }
                double myDouble = Double.parseDouble(tempDataTable[i][2]);
                userData.get(tempDataTable[i][0]).put(movies.get(Integer.parseInt(tempDataTable[i][1])), myDouble);
                movieData.get(movies.get(Integer.parseInt(tempDataTable[i][1]))).put(tempDataTable[i][0], myDouble);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Calculates distance of the scores for a movie between 2 people
     * @param data stores data
     * @param person1 first person
     * @param person2
     * @param movie
     * @return distance if 2 score
     */
    public static double dist(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2, String movie) {

        double score1 = data.get(person1).get(movie);// What is the rating that person1 gives to this movie
        double score2 = data.get(person2).get(movie);// What is the rating that person2 gives to this movie

        return Math.pow((score1 - score2), 2);// in order not to return a minus we give square of result	
    }

    /**
     *
     * @param data
     * @param item1
     * @param item2
     * @return
     */
    // It takes 2 ıtem and returns string array that holds cammon movie or user.
    public static String[] intersection(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item1, String item2) {

        String[] tempSi;
        if(data.getN() == movieNames.length) {//getN method returns data's all value in separateChainning class. If we are dealing with movies then length of
        	// string array will be length of values in data which is 943
        	tempSi = new String[userData.getN()];
        } else {// if not , length will be movies number in the u.item which is 1682.
            tempSi = new String[movieData.getN()];
        }
        int interCounter = 0;
        for (int i = 0; i < tempSi.length; i++) {
            String itemID;// we need to get movie or person in this variable
            if (data.getN() == movieData.getN()) {// if we call movie then
                itemID = "" + (i + 1);// we are getting person in here string type
            } else if (data.getN() == userData.getN()) {
                itemID = movieNames[i];
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

        String[] si = new String[interCounter];
        for (int i = 0; i < si.length; i++) {// This is the one will be returned. Array length will be interCounter that holds how many movie or person that cammon.
            si[i] = tempSi[i];// we could not return tempSi array because of its size

        }
        return si;

    }

    /**
     *
     * @param mySSST
     * @param person1
     * @param person2
     * @return
     */
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
            sum1sq += Math.pow(mySSST.get(person1).get(si[i]), 2);
            sum2sq += Math.pow(mySSST.get(person2).get(si[i]), 2);
            pSum += mySSST.get(person1).get(si[i]) * mySSST.get(person2).get(si[i]);// multiply these two person's rating
        }
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.pow((sum1sq - Math.pow(sum1, 2) / n) * (sum2sq - Math.pow(sum2, 2) / n), 0.5);

        if (den == 0) {
            return 0.0;
        }

        return num / den;

    }

    /**
     *
     * @param data
     * @param thing1
     * @param thing2
     * @return
     */
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

    /**
     *
     * @param data
     * @param item
     * @param n
     * @return
     */
    //Take a item and returns a most n similar item like it 
    public static SeparateChainingHashST<String,Double> topMatches(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item, int n) {

        Object[] temp;

        SeparateChainingHashST<String,Double> scores = new SeparateChainingHashST<>();
        SeparateChainingHashST<String,Double> resultScore = new SeparateChainingHashST<>(1);
        for (int i = 0; i<data.getN(); i++) {
            String itemID;
            if(data.getN() == movieData.getN()) {// same with intersection part. if the given data is movieData then scores has user names
            	// if not scores and resultScore has movie names
                itemID = movieNames[i];//movies.get(i+1);
            } else if(data.getN() == userData.getN()) {
                itemID =  "" + (i+1);
            } else {
                System.out.println("error in topMatches method");// if the given data neither movieData nor userData then give an error
                continue;
            }
            if (itemID.equals(item) ) {// itemID holds the item name in the loop. If the itemID is the item then continue with the other one
                continue;
            }
            else {
                double sim = sim_distance(data, item, itemID);//use sim_pearson method in order to calculate similarty with these two item

                scores.put(itemID, sim);// then put the item with the similarty after that we will be use it
            }
        }

        temp = Heap.sort(scores);// first data is item and the similarity. We are sorting it in order to find Top n movie or person
        // but it is from small to bigger that we do not want it

        int N;
        if(n>temp.length) N=temp.length;
        else N=n;
        for(int i=0;i<N;i++) {
            //resultScore.put((String)temp[temp.length-1-i],scores.get((String)temp[temp.length-1-i]));
            resultScore.put((String)temp[i+(temp.length-N)],scores.get((String)temp[i+(temp.length-N)]));
            /* Now , temp is object array but we need Strings . Therefore we need to change when we put it.
             * Start puttin the temp's object from the last one in order reverse array.
             * Getting scores rating also taking from the last one 
             */
            

        }

        return resultScore;
    }

    /**
     *
     * @param data
     * @param item
     * @return
     */
    public static SeparateChainingHashST<String,Double> getRecommendation(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item) {
        SeparateChainingHashST<String, Double> totals = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, Double> simSums = new SeparateChainingHashST<>();

        //TODO add movie based running

        SeparateChainingHashST<String, Double> rankings = new SeparateChainingHashST<>();
        Object[] temp;
        int NN;
        if(data.getN()==userData.getN()) NN=movieData.getN();
        else NN=userData.getN();

        for(int i=0;i<NN;i++) {//it depends on what we give in the method. userData or movieData 
            String itemID;// this holds every person or movie in loop
            if (NN==movieData.getN()) {// if movieData given in method then itemID will holds the movies
                itemID = movieNames[i];
            } else {// if not it holds people
                itemID = "" + (i+1);
            }
            if(data.get(item).get(itemID) != null) continue;// if it not null then continue with the other items
            double tempKey = score(data,item,itemID);// we are using score method in order calculate sim's
            rankings.put(itemID,tempKey);// then put it what we calculate
        }

        temp = Heap.sort(rankings);// sort it from smaller to bigger but we dont want it like it

        SeparateChainingHashST<String,Double> result = new SeparateChainingHashST<>(1);

        int N = 5;
        if(N>temp.length) N=temp.length;
        for(int i=0;i<N;i++){// we have to do it bigger to smaller so we are start putting from the last one
            result.put((String)temp[i+(temp.length-N)],rankings.get((String)temp[i+(temp.length-N)]));
        }

        return result;

    }
    /**
     *
     * @param data
     * @param item
     * @param innerItem
     * @return
     */
    public static double score(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String item, String innerItem) {

        double totalSim = 0.0;
        double totalSim2 = 0.0;
        for (int i = 0; i < data.getN(); i++) {
            String personID;
            if(userData.getN() == data.getN()) {
                personID = "" + (i+1);
            } else {
                personID = movieNames[i];
            }
            if (personID.equals(item)) {// When the personID -which holds all the person in the loop - is the person, then continue with the other one
                continue;
            } else if (data.get(personID).get(innerItem) != null) {
                totalSim += sim_distance(data, item, personID) * data.get(personID).get(innerItem);
                totalSim2 += sim_distance(data, item, personID);
            }
        }

        return totalSim / totalSim2;
    }


    /**
     *
     * @param mySSST
     * @return
     */
 // Samething with topMatches but for movies.
    public static SeparateChainingHashST<String,SeparateChainingHashST<String, Double>> calculateSimilarItems(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> mySSST) {
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();

        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> itemPrefs = movieData;//transformPrefs(mySSST);

        int c=0;// as phyton code wants as 100/1664
        for (int i=0;i<itemPrefs.getN();i++) {
            String itemID = movieNames[i];// this holds movies in the loop
            c++;// every time movie change c is gonna increase
            if (c%100 == 0 || c==itemPrefs.getN()) {
                System.out.println(c + "/"+ itemPrefs.getN() + "    " + (int)((double)c/(double)itemPrefs.getN()*100) + " %");// in the console 
            }

            SeparateChainingHashST<String,Double> scores = topMatches(itemPrefs,itemID,itemPrefs.getN());;// inner dictionary that uses topMatches
            result.put(itemID,scores);// Put them into result outer dictionary
        }

        return result;
    }

    /**
     * Gets recommendations as item-based
     * @param data data which is
     * @param itemMatch
     * @param person
     * @param n
     * @return
     */
    public static SeparateChainingHashST<String,Double> getRecommendedItems(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data,
                                                                            SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> itemMatch,String person,int n) {
        SeparateChainingHashST<String, Double> userRatings = data.get(person);// userRatings holds inner dictionary that person's rating and movie
        Object[] userRatingKeys = userRatings.getKeys();//This holds Keys of person
        SeparateChainingHashST<String, Double> scores = new SeparateChainingHashST<>();
        SeparateChainingHashST<String, Double> totalSim = new SeparateChainingHashST<>();
        Object[] itemMatchKeys = itemMatch.getKeys();//given outer dictioner's keys
        for(int i=0;i<userRatingKeys.length;i++) {
            String itemID = (String)userRatingKeys[i];// this holds items in the loop
            for(int j=0;j<itemMatchKeys.length;j++) {
                String innerItemID = (String)itemMatchKeys[j];// this holds inner items in the loop
                SeparateChainingHashST<String, Double> temp = itemMatch.get(itemID);// temporary inner dictionary holds given itemMatch's movie and it rating
                

                if(userRatings.get(innerItemID) != null) continue;//if the person give a point to movie then continue with the other one

                if(scores.get(innerItemID) == null) scores.put(innerItemID,0.0);//in order to determine the item put it as null then 
                scores.put(innerItemID,scores.get(innerItemID) + (temp.get(innerItemID) * userRatings.get(itemID)));// we will determine the key of it from temp object array

                if(totalSim.get(innerItemID) == null) totalSim.put(innerItemID,0.0);//same thing with scores
                totalSim.put(innerItemID,totalSim.get(innerItemID) + temp.get(innerItemID));
            }
        }
        SeparateChainingHashST<String,Double> result = new SeparateChainingHashST<>();

        Object[] keys = scores.getKeys();// holds scores keys
        for(int i=0;i<keys.length;i++) {
            String item = (String)keys[i];// this holds scores keys in every loop
            result.put(item,scores.get(item)/totalSim.get(item));// and put it to result
        }
        Object[] sortedKeys = Heap.sort(result);// sort it

        SeparateChainingHashST<String,Double> actualResult = new SeparateChainingHashST<>(1);// the dict which we are gonna return

        int N;
        if (n==0) N=sortedKeys.length;
        else if (n>sortedKeys.length) N=sortedKeys.length;
        else N=n;
        for(int i=0;i<N;i++) {
            actualResult.put((String)sortedKeys[i+(sortedKeys.length-N)],result.get((String)sortedKeys[i+(sortedKeys.length-N)]));
            // we have return bigger to smaller and null keys are not gonna return so we use another dictionary
        }


        return actualResult;
    }

    /**
     * Creates transformed data of parameter data
     * it exchange places of the keys and inner keys
     * @param data Hash Symbol Table which will be transformed
     * @return transformed data
     */
    public static SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> transformPrefs (SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data){
        SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> result = new SeparateChainingHashST<>();// dictionary that will be returned
        

        //create SCHST that has movie name as key and default hast table as value
        for (int i=0;i<movies.getN();i++) {//Firstly we have to put all the movie as key and put empty values
            result.put(movieNames[i],new SeparateChainingHashST<>());
        }
        for (int i=0;i<userData.getN();i++){// now we have to determine the inner dictionary as user and its rating
            String personID = "" + (i+1);// this holds all the people in loop
            for (int j=0;j<movies.getN();j++) {
                String moviesID = movieNames[j];// this holds all the movies in the loop
                if(data.get(personID).get(moviesID) != null) { // if the rating of the person on this movies is not empty
                    result.get(moviesID).put(personID,data.get(personID).get(moviesID));//then get the value of result and put the user as first parametre
                    // and its rating as second.
                } else {
                    result.get(moviesID).put(personID,null);// if person does not watch the movie then put it as empty.
                }
            }
        }

        return result;
    }

}
