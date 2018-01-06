
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.vcs.log.Hash;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;
import java.util.stream.Collectors;

public class Util {
    public static void analyze(String data, HashMap<String, Word> wordHashMap, GeneralData generalData) {
        int wordCount = 0;
        int sentenceCount = 0;

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

        iterator.setText(data);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String line = data.substring(start, end);
            //System.out.println(line);
            sentenceCount++;
            String[] wordArray = line.split("\\W+");
            for (int i = 0; i < wordArray.length - 2; i++) {

                String currentWord = wordArray[i].toLowerCase();
                if (currentWord.equals("")) {
                    break;
                }
                String nextWord = wordArray[i + 1].toLowerCase();
                String nextNextWord = wordArray[i+2].toLowerCase();
                if (!wordHashMap.containsKey(currentWord+" "+nextWord)) {
                    wordHashMap.put(currentWord+" "+nextWord, new Word(currentWord+" "+nextWord));
                }
                Word currentWordObj = wordHashMap.get(currentWord+" "+nextWord);
                currentWordObj.addUses();
                if (!currentWordObj.getNextWords().containsKey(nextNextWord)) {
                    currentWordObj.getNextWords().put(nextNextWord, new ProbPair(nextNextWord));
                }
                currentWordObj.getNextWords().get(nextNextWord).add();
                if(i== wordArray.length-3){
                    if (!wordHashMap.containsKey(nextNextWord)) {
                        wordHashMap.put(nextNextWord, new Word("."));
                    }
                    Word nextWordObj = wordHashMap.get(nextNextWord);
                    if (!nextWordObj.getNextWords().containsKey(".")) {
                        nextWordObj.getNextWords().put(".", new ProbPair("."));
                    }
                    nextWordObj.getNextWords().get(".").add();
                }
            }

            wordCount += wordArray.length;
        }
        generalData.setTotalSentenceCount(sentenceCount);
        generalData.setTotalWordCount(wordCount);
        System.out.println("words: " + generalData.getTotalWordCount() + "; sentences: " + generalData.getTotalSentenceCount());
    }

    public static void storeData(String fileName, String fileName2, HashMap<String, Word> wordHashMap, GeneralData generalData) {
        FileOutputStream fos =
                null;
        try {
            File f = new File("src/main/java/data/" + fileName + ".json");
            if (f.isFile()) {
                Scanner p = new Scanner(System.in);
                System.out.println("File" + fileName + ".json exists. Do you want to overwrite (y/n)");
                String answer = p.next().toLowerCase();
                if (!answer.equals("y")) {
                    return;
                }
            }


            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Word>>() {
            }.getType();
            String json = gson.toJson(wordHashMap, type);


            List<String> lines = Arrays.asList(json);
            Path file = Paths.get("src/main/java/data/" + fileName + ".json");
            Files.write(file, lines, Charset.forName("UTF-8"));

            System.out.println(json);


            File f2 = new File("src/main/java/data/" + fileName2 + ".json");
            if (f2.isFile()) {
                Scanner p = new Scanner(System.in);
                System.out.println("File" + fileName2 + ".json exists. Do you want to overwrite (y/n)");
                String answer = p.next().toLowerCase();
                if (!answer.equals("y")) {
                    return;
                }
            }


            Type type2 = new TypeToken<GeneralData>() {
            }.getType();
            String json2 = gson.toJson(generalData, type2);


            List<String> lines2 = Arrays.asList(json2);
            Path file2 = Paths.get("src/main/java/data/" + fileName2 + ".json");
            Files.write(file2, lines2, Charset.forName("UTF-8"));

            System.out.println(json2);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static HashMap<String, Word> readWordData(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/java/data/" + fileName + ".json"));
        StringBuilder sb = new StringBuilder(200000000);
        String data = "";
        for (String line : lines) {
            sb.append(line);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Word>>() {
        }.getType();
        return gson.fromJson(sb.toString(), type);
    }

    public static GeneralData readGeneralData(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/java/data/" + fileName + ".json"));
        StringBuilder sb = new StringBuilder(200000000);

        for (String line : lines) {
            sb.append(line);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<GeneralData>() {
        }.getType();
        return gson.fromJson(sb.toString(), type);
    }

    public static void setProbabilities(HashMap<String, Word> wordHashMap) {

        for (Map.Entry<String, Word> entry : wordHashMap.entrySet()) {
            int currentProb = 0;
            Set<Map.Entry<String, ProbPair>> set = entry.getValue().getNextWords().entrySet();
            for (Map.Entry<String, ProbPair> nextEntry : set) {
                ProbPair pair = nextEntry.getValue();
                pair.setProbabilityIndex(currentProb);
                currentProb += pair.getCount();
                pair.setEndProbabilityIndex(currentProb);
            }
            entry.getValue().setNextWordCount(set.size());
        }
    }
    public static void generateText(HashMap<String,Word> words, int numSentences){

        Random generator = new Random();

        Collection wordCollection = words.values().stream().filter(word1 -> !word1.getWord().equals(".")).collect(Collectors.toList());
        Object[] wordArray = wordCollection.toArray();
        Word word = (Word) wordArray[generator.nextInt(wordArray.length)];
        System.out.print(word+" ");
        for (int i = 0; i < numSentences; i++) {
            int j2 = 0;
            while(!word.getWord().equals(".")){


                try {
                    word.initWordGen();
                    word = word.generateNextWord(words);
                    if(word==null){
                        break;
                    }
                } catch (Word.NoInitializationException e) {
                    e.printStackTrace();
                }
                if(j2%numSentences==0){
                    System.out.print(word+" ");
                }else {
                    System.out.print(word.toStringPart2() + " ");
                }
                j2++;
            }
            System.out.println(".");
            word = (Word) wordArray[generator.nextInt(wordArray.length)];
        }




    }
    public static void generateText(HashMap<String,Word> words, int numSentences, int sentenceLength){

        Random generator = new Random();

        Collection wordCollection = words.values().stream().filter(word1 -> !word1.getWord().equals(".")).collect(Collectors.toList());
        Object[] wordArray = wordCollection.toArray();
        Word word = (Word) wordArray[generator.nextInt(wordArray.length)];
        System.out.print(word+" ");
        for (int i = 0; i < numSentences; i++) {

                for (int j = 0; j < sentenceLength; j++) {

                try {
                    word.initWordGen();
                    word = word.generateNextWord(words);
                    if(word==null){
                        break;
                    }
                } catch (Word.NoInitializationException e) {
                    e.printStackTrace();
                }
                if(j%sentenceLength==0){
                    System.out.print(word+" ");
                }else {
                    System.out.print(word.toStringPart2() + " ");
                }

            }
            System.out.println(".");
            word = (Word) wordArray[generator.nextInt(wordArray.length)];
        }




    }
}


