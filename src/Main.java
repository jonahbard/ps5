import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.FileReader;
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

    public static Map<String, Map<String, Double>> calculateTransitionScores(String POSfile) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("simple-train-tags.txt"));
        Map<String, Set<String>> nextPOS = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null){ // assemble map of words to set of words that follow them
            String[] POS = line.split(" ");
            for (int i = 1; i < POS.length; i++){
                String prevPOS = POS[i-1];
                String currentPOS = POS[i];
                Set<String> set;
                if (nextPOS.containsKey(POS[i-1])){
                    set = nextPOS.get(prevPOS);
                } else {
                    set = new HashSet<>();
                }
                set.add(currentPOS);
                nextPOS.put(currentPOS, set);
            }

        }
        Map<String, Map<String, Double>> frequencies = new HashMap<>(); // make this hashmap from the other hashmap

        //normalize frequencies in-place.

        //take log of normalizations in-place


        return new HashMap<String, Map<String, Double>>();
    }


    public static Map<String, Map<String, Double>> calculateWordPOSScores(String wordsFile, String POSFile) throws Exception {
        // can either map POS to <word, score> or map word to <pos, score>. first option probably better

        Map<String, Map<String, Double>> wordPOSScores = new HashMap<>(); //<POS, <word, score>>
        BufferedReader wordFileReader = new BufferedReader(new FileReader(wordsFile));
        BufferedReader POSFileReader = new BufferedReader(new FileReader(POSFile));

        String wordLine;
        String POSLine;
        while ((POSLine = POSFileReader.readLine()) != null && (wordLine = wordFileReader.readLine()) != null){
            String[] POS = POSLine.split(" ");
            String[] words = wordLine.split(" ");

            if (POS.length != words.length){
                System.out.println(" uh oh POS length != words length");
                System.out.println("line: " + wordLine);
            }

            for (int i = 0; i < POS.length; i++){
                Map<String, Double> wordAndScore;
                String curPOS = POS[i];
                String curWord = words[i];

//                if (!wordPOSScores.containsKey(curPOS)) {
//                    wordPOSScores.put(curPOS, new HashMap<>());
//                }

                if (wordPOSScores.containsKey(curPOS)) {
                    wordAndScore = wordPOSScores.get(curPOS);
                    if (wordAndScore.containsKey(curWord)) {
                        wordAndScore.put(curWord, wordAndScore.get(curWord)+1);
                    } else {
                        wordAndScore.put(curWord, 1.0);
                    }
                } else {
                    wordAndScore = new HashMap<>();
                    wordAndScore.put(curWord, 1.0);
                }
                wordPOSScores.put(POS[i], wordAndScore);
            }

            //until period, read the line and add to wordPOSScores by <POS, <word, int-frequency>>

        }


        //normalize frequencies in-place

        //log normalized frequencies in-place

        return wordPOSScores;
    }

    public void assembleHMM(){} // assemble the HMM from the calculation methods

    public void testHMM(String testWordsFile, String testPOSfile){}

    public static void main(String[] args) throws Exception {
        calculateTransitionScores("simple-train-sentences.txt");
        calculateWordPOSScores("simple-train-sentences.txt", "simple-train-tags.txt");

    }
}