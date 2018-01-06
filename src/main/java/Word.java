
import com.intellij.vcs.log.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Word {
    private String word,part1,part2;
    private HashMap<String, ProbPair> nextWords;
    private long uses;
    private long nextWordCount;
    private List<ProbPair> nextWordList;

    public long getNextWordCount() {
        return nextWordCount;
    }

    public void setNextWordCount(long nextWordCount) {
        this.nextWordCount = nextWordCount;
    }


    public long getUses() {
        return uses;
    }

    public void setUses(long uses) {
        this.uses = uses;
    }

    public void addUses() {
        uses++;
    }

    public Word(String word) {
        this.word = word;
        nextWords = new HashMap<>();
        uses = 0;
        nextWordList = new ArrayList<>();

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public HashMap<String, ProbPair> getNextWords() {

        return nextWords;
    }

    public void initWordGen() {
        for (Map.Entry<String, ProbPair> entry : nextWords.entrySet()) {
            nextWordList.add(entry.getValue());
        }
        String[] split = word.trim().split("\\s+");
        part1=split[0];
        part2=split[1];
    }

    public Word generateNextWord(HashMap<String, Word> wordHashMap) throws NoInitializationException {
        if (nextWordList.size() == 0) {
            throw new NoInitializationException("You need to call initWordGen() first");
        }
        int probIndex = (int) (Math.random() * nextWordCount);
        //System.out.println("index:" + probIndex);
        //System.out.println("count:"+nextWordList.stream().filter(probPair -> (probIndex>= probPair.getProbabilityIndex()&&probIndex<probPair.getEndProbabilityIndex())).count());
        String wordString = nextWordList.stream().filter(probPair -> (probIndex >= probPair.getProbabilityIndex() && probIndex < probPair.getEndProbabilityIndex())).findFirst().get().getWord();
       // System.out.println(word.split("\\s+")[1]+" "+part1+" "+part2+" "+wordString);
        return wordHashMap.get(part2+" "+wordString);
    }

    @Override
    public String toString() {
        return word;
    }
    public String toStringPart2() {
        return word.trim().split("\\s+")[1];
    }

    public class NoInitializationException extends Exception {
        public NoInitializationException(String message) {
            super(message);
        }
    }
}
