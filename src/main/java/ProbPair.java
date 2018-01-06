
public class ProbPair {
    private int count;
    private String word;
    private int probabilityIndex;

    public int getEndProbabilityIndex() {
        return endProbabilityIndex;
    }

    public void setEndProbabilityIndex(int endProbabilityIndex) {
        this.endProbabilityIndex = endProbabilityIndex;
    }

    private int endProbabilityIndex;

    public int getProbabilityIndex() {
        return probabilityIndex;
    }

    public void setProbabilityIndex(int probabilityIndex) {
        this.probabilityIndex = probabilityIndex;
    }

    public ProbPair(String word) {
        this.count = 0;
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void add(){
        count++;
    }
}
