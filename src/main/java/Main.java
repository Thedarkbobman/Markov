

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Type the filename (no extension)");
        Scanner p = new Scanner(System.in);
        String textSource = p.nextLine();
        String data1 = textSource+"data1";
         String data2 = textSource+"data2";
       // String data1 = "newdota1";
       // String data2 = "newdota2";
        System.out.println("1 to rebuild hash or any other number to generate");

        HashMap<String,Word> words = new HashMap<>();
        GeneralData generalData = new GeneralData();
        if(p.nextInt()==1){
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/java/source/"+textSource+".txt"),"utf-8"));;
          /*  List<String> lines = Files.readAllLines(Paths.get("src/main/java/essaydata.txt"));
            StringBuilder sb = new StringBuilder(200000000);
            String data = "";
            for(String line:lines){
                sb.append(line);
            }
            data = sb.toString();*/
            String data="";
            StringBuilder sb = new StringBuilder(200000000);
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line=reader.readLine();
            }
            data = sb.toString();
            reader.close();
            Util.analyze(data,words,generalData);
            Util.setProbabilities(words);
            Util.storeData(data1,data2,words,generalData);

        }
        else {
            words = Util.readWordData(data1);
            System.out.println(words.entrySet().size());
            generalData = Util.readGeneralData(data2);
            System.out.println("sentences:"+generalData.getTotalSentenceCount()+" words:"+generalData.getTotalWordCount());
            System.out.println("Type how many sentences you want");
            int numSentences = p.nextInt();
            System.out.println("Type if you want the sentences to go up to a period(true/false)");
            boolean period = p.nextBoolean();

            if(period){
                Util.generateText(words,numSentences);
            }
            else{
                System.out.println("Type what you want the sentence limit to be.");
                int numWords = p.nextInt();
                Util.generateText(words,numSentences,numWords);
            }

          // int avgWords= (int)(((double)generalData.getTotalWordCount())/generalData.getTotalSentenceCount());
          // System.out.println("avgwords:"+avgWords);

//
        }
        /*String data2 = "Until you wake into a dream where you are at once a pen flying over vellum and the vellum itself with the touch of ink tickling your surface.";
        for (String s : data2.split("\\W+")) {
            System.out.println("word:"+s);
        }*/
       /*for(ProbPair probPair: words.get("the").getNextWords().values()){
           System.out.println(probPair.getWord()+": "+probPair.getCount());
       }*/
    }
}
