import java.io.FileInputStream;
import java.util.Scanner;

/**
 * @author ibrahim
 * @author kadir
 * @author ilker
 */
public class Main {

    public static LinearProbingHashST<String,LinearProbingHashST<String,Double>> SSST;

    public static LinearProbingHashST<String,Double> temp;

    public static String[] movies = {
            "Star Wars", "Gladiator","LOTR"};

    public static String[] persons = {
            "ilker","kadir","ibrahim"
    };
    public static void main(String[] args) {
        SSST = new LinearProbingHashST<>();
        temp = new LinearProbingHashST<>();
        temp.put("Star Wars",1.0);
        temp.put("Gladiator",5.0);
        temp.put("LOTR",1.0);

        SSST.put("ilker",temp);

        LinearProbingHashST<String,Double> temp2 = new LinearProbingHashST<>();
        temp = new LinearProbingHashST<>();
        temp.put("Star Wars",4.0);
        temp.put("Gladiator",3.5);
        temp.put("LOTR",5.0);

        SSST.put("kadir",temp);
        temp = new LinearProbingHashST<>();
        temp.put("Star Wars",4.6);
        temp.put("Gladiator",2.9);

        SSST.put("ibrahim",temp);


        //System.out.println(score(SSST,"ibrahim","LOTR"));
        //System.out.println(similarity(SSST,"ibrahim","ilker"));
        System.out.println(sim_pearson(SSST,"ilker","kadir"));

    }

    public static String[][] readData() {

        String[][] tempDataTable = new String[100000][4];
        try {
            String[] items = new String[100000];
            Scanner scan = new Scanner(new FileInputStream("u.data"));
            for (int i=0;scan.hasNextLine();i++) {
                items[i] = scan.nextLine();
                tempDataTable[i] = items[i].split("\t");
            }
            System.out.println(items.length);
        } catch (Exception e){
            e.printStackTrace();
        }

        return tempDataTable;

    }

    public static double similarity(LinearProbingHashST<String, LinearProbingHashST<String, Double> > data, String person1, String person2){
        double distance = 0.0;
        String[] common = intersection(data,person1,person2);
        if(common.length == 0) { return 0; }
        for (int i = 0; common[i] != null; i++) {
            try {
                distance +=dist(data,person1,person2,common[i]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1.0/ (1.0 + Math.pow(distance,0.5));
    }

    public static double dist(LinearProbingHashST<String, LinearProbingHashST<String, Double> > data, String person1, String person2, String movie){

        double score1 = data.get(person1).get(movie);
        double score2 = data.get(person2).get(movie);

        return Math.pow((score1 - score2),2);
    }

    public static String[] intersection (LinearProbingHashST<String, LinearProbingHashST<String, Double> > data, String person1, String person2) {
        String[] tempSi = new String[movies.length];
        int counter = 0;
        for(int i=0;i<movies.length;i++) {
            if(data.get(person1).get(movies[i]) != null && data.get(person2).get(movies[i]) != null) {
                tempSi[counter] = movies[i];
                counter++;
            }
        }
        String[] si = new String[counter];
        for(int i=0;i<si.length;i++) {
            si[i] = tempSi[i];
        }
        return si;
    }

    public static double sim_pearson(LinearProbingHashST<String,LinearProbingHashST<String,Double>> mySSST, String person1, String person2) {
        String[] si = intersection(mySSST,person1,person2);
        int n = si.length;
        double sum1 = 0;
        double sum2 = 0;
        double sum1sq = 0;
        double sum2sq = 0;
        double pSum = 0;

        if(n == 0) { return 0; }

        for(int i=0;i<si.length;i++) {
            sum1 += mySSST.get(person1).get(si[i]);
            sum2 += mySSST.get(person2).get(si[i]);
            sum1sq += Math.pow(mySSST.get(person1).get(si[i]),2);
            sum2sq += Math.pow(mySSST.get(person2).get(si[i]),2);
            pSum += mySSST.get(person1).get(si[i]) * mySSST.get(person2).get(si[i]);
        }
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.pow((sum1sq - Math.pow(sum1,2)/n) * (sum2sq - Math.pow(sum2,2)/n),0.5);

        if(den == 0) {
            return 0;
        }

        return num/den;


    }



    public static double score(LinearProbingHashST<String,LinearProbingHashST<String,Double>> mySSST, String person, String movie) {

        double totalSim = 0.0;
        double totalSim2 = 0.0;
        for(int i=0;i<persons.length;i++) {
            if(persons[i].equals(person)) {
                continue;
            } else {
                totalSim += similarity(mySSST,person,persons[i]) * mySSST.get(persons[i]).get(movie);
                totalSim2 += similarity(mySSST,person,persons[i]);
            }
        }

        return totalSim/totalSim2;
    }
}
