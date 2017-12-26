import java.io.FileInputStream;
import java.util.Scanner;

/**
 * @author ibrahim
 * @author kadir
 * @author ilker
 */
public class Main {

    public static SeparateChainingHashST<String,SeparateChainingHashST<String,Double>> userData;

    public static SeparateChainingHashST<String,Double> temp;

    public static String[] movies = {
            "Star Wars", "Gladiator","LOTR"};

    public static String[] persons = {
            "ilker","kadir","ibrahim"
    };


    public static void main(String[] args) {
        userData = readData();
        /*temp = new SeparateChainingHashST<>(8);
        temp.put("Star Wars",1.0);
        temp.put("Gladiator",5.0);
        temp.put("LOTR",1.0);

        userData.put("ilker",temp);

        SeparateChainingHashST<String,Double> temp2 = new SeparateChainingHashST<>(8);
        temp = new SeparateChainingHashST<>(8);
        temp.put("Star Wars",4.0);
        temp.put("Gladiator",3.5);
        temp.put("LOTR",5.0);

        userData.put("kadir",temp);
        temp = new SeparateChainingHashST<>(8);
        temp.put("Star Wars",4.6);
        temp.put("Gladiator",2.9);

        userData.put("ibrahim",new SeparateChainingHashST<>());*/


        //System.out.println(score(userData,"ibrahim","LOTR"));
        //System.out.println(similarity(userData,"ibrahim","ilker"));
        //System.out.println(sim_pearson(userData,"ilker","kadir"));
        //System.out.println(userData.get("ibo") == null);
        //userData.get("ibo").show();

        System.out.println(sim_pearson(userData,"136","200"));
        System.out.println(dist(userData,"299","162","1047"));
    }


    //bu dosyayi tarama methodu nerdeyse bitti biraz daha degisiklik yapmaliyiz
    //todo movilerin idlerini isimleriyle degistirme ve movie isimlerinden bir array olusturma
    public static SeparateChainingHashST<String,SeparateChainingHashST<String,Double>> readData() {

        SeparateChainingHashST<String,SeparateChainingHashST<String,Double>> tempUserData = new SeparateChainingHashST<>(32);
        String[][] tempDataTable = new String[100000][4];
        try {
            String[] items = new String[100000];
            Scanner scan = new Scanner(new FileInputStream("u.data"));
            for (int i=0;scan.hasNextLine();i++) {
                items[i] = scan.nextLine();
                tempDataTable[i] = items[i].split("\t");
            }

            for(int i=0;i<tempDataTable.length;i++) {
                if(tempUserData.get(tempDataTable[i][0]) == null) {
                    SeparateChainingHashST<String, Double> tempST = new SeparateChainingHashST<>(32);
                    tempUserData.put(tempDataTable[i][0],tempST);
                } else {
                    double myDouble = Double.parseDouble(tempDataTable[i][2]);
                    tempUserData.get(tempDataTable[i][0]).put(tempDataTable[i][1],myDouble);
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

        return tempUserData;

    }

    public static double similarity(SeparateChainingHashST<String, SeparateChainingHashST<String, Double>> data, String person1, String person2){
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

    public static double dist(SeparateChainingHashST<String, SeparateChainingHashST<String, Double> > data, String person1, String person2, String movie){

        double score1 = data.get(person1).get(movie);
        double score2 = data.get(person2).get(movie);

        return Math.pow((score1 - score2),2);
    }

    //intersection methodunda degisiklikler yaptim alinan verileri kullanabilmek icin
    //todo suanda movilerin idlerine gore bakiyor bu id leri normal isimlerine cevirdigimizde bu method calismiyicak
    private static int interCounter = 0;
    public static String[] intersection (SeparateChainingHashST<String, SeparateChainingHashST<String, Double> > data, String person1, String person2) {
        String[] tempSi = new String[1682];
        for(int i=0;i<tempSi.length;i++) {
            String movieID = "" + (i+1);
            if(data.get(person1).get(movieID) != null && data.get(person2).get(movieID) != null) {
                tempSi[interCounter] = movieID;
                interCounter++;
            }
        }
        String[] si = new String[interCounter];
        for(int i=0;i<si.length;i++) {
            si[i] = tempSi[i];
        }
        return si;
    }

    public static double sim_pearson(SeparateChainingHashST<String, SeparateChainingHashST<String,Double>> mySSST, String person1, String person2) {
        String[] si = intersection(mySSST,person1,person2);
        int n = si.length;
        double sum1 = 0;
        double sum2 = 0;
        double sum1sq = 0;
        double sum2sq = 0;
        double pSum = 0;

        if(n == 0) {
            System.out.println("n = 0");
            return 0; }

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
            System.out.println("den = 0");
            return 0;
        }

        return num/den;


    }


    public static double score(SeparateChainingHashST<String, SeparateChainingHashST<String,Double>> mySSST, String person, String movie) {

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
