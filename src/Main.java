import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLOutput;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {


    //don't really need this but could be easier if we use a real graph
    public AdjacencyMapGraph HMM; // graph that represents the HMM, using Map<String,Map<String, Double>> as nodes and Double as edge labels

    /***
     * return array of POS that corresponds to the backtraced viterbi most-likely POS list for given string
     * @param phrase
     * @return
     */
    public String[] viterbiMostLikelyPOSSequence(String phrase){} // need to decide whether we're doing this with HMM graph or not

    public static Map<Map<String, String>, Double> calculateTransitionProbabilities(String POSfile) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("simple-train-tags.txt"));
        Map<String, Set<String>> nextWords = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null){ // assemble map of words to set of words that follow them
            String[] words = line.split(" ");
            for (int i = 1; i < words.length; i++){
                String prevWord = words[i-1];
                String currentWord = words[i];
                Set<String> set;
                if (nextWords.containsKey(words[i-1])){
                    set = nextWords.get(prevWord);
                } else {
                    set = new HashSet<>();
                }
                set.add(currentWord);
                nextWords.put(prevWord, set);
            }

        }
        Map<Map<String, String>, Double> frequencies = new HashMap<>(); // make this hashmap from the other hashmap

        return new HashMap<Map<String, String>, Double>();
    }


    public static Map<String, Map<String, Double>> calculateWordPOSProbabilities(String wordsFile, String POSFile) throws Exception {
        // can either map POS to <word, probability> or map word to <pos, probability>. first option probably better

        Map<String, Map<String, Double>> wordPOSProbabilities = new HashMap<>();
        BufferedReader wordFileReader = new BufferedReader(new FileReader(wordsFile));
        BufferedReader POSFileReader = new BufferedReader(new FileReader(POSFile));

        String wordLine;
        String POSLine;
        while ((POSLine = POSFileReader.readLine()) != null && (wordLine = wordFileReader.readLine()) != null){
            //until period, read the line and add to wordPOSprobs by <POS, <word, int-frequency>>

        }
        //normalize frequencies in-place
        //log normalized frequencies in-place

        return wordPOSProbabilities;
    }

    public void assembleHMM(){} // assemble the HMM from the calculation methods

    public void testHMM(String testWordsFile, String testPOSfile){}

    public static void main(String[] args) throws Exception {
        calculateTransitionProbabilities("simple-train-sentences.txt");
        calculateWordPOSProbabilities("simple-train-sentences.txt", "simple-train-tags.txt");

    }
}