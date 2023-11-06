import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class builds hidden Markov models (HMMs) from various sources.
 * 
 * Used copilot to help generate comments
 * 
 *  @author Jonah Bard, Daniel Katz
 */

public class HMM {

    private Map<String, Map<String, Integer>> observationFreqs;
    private Map<String, Map<String, Integer>> transitionFreqs;

    private Map<String, Map<String, Double>> observationScores;
    private Map<String, Map<String, Double>> transitionScores;


    public Map<String, Map<String, Integer>> getObservationFreqs() {return observationFreqs;}
    public Map<String, Map<String, Integer>> getTransitionFreqs() {return transitionFreqs;}

    public Map<String, Map<String, Double>> getObservationScores() {return observationScores;}
    public Map<String, Map<String, Double>> getTransitionScores() {return transitionScores;}


    /**
     * Builds a frequency map for 'from' and 'to' strings.
     * Adds 'from' and 'to' to the map if they don't exist, then increments the count of 'to'.
     *
     * @param map The frequency map.
     * @param from The key in the outer map.
     * @param to The key in the inner map to increment.
     */
    private static void buildFreqMap(Map<String, Map<String, Integer>> map, String from, String to) {
        // Create map for outer key if it does not exist
        if (!map.containsKey(from)) map.put(from, new HashMap<>());

        // Get inner map
        Map<String, Integer> innerMap = map.get(from);

        // Create a default for the inner value if it does not exist
        if(!innerMap.containsKey(to)) innerMap.put(to, 0);

        innerMap.put(to, innerMap.get(to) + 1);
    }

    /**
     * Reads in a file of words and a file of POS tags, and builds frequency maps for each.
     * @param wordFileName The file containing the words.
     * @param POSFileName The file containing the POS tags.
     */
    public void fileReader(String wordFileName, String POSFileName) {
        observationFreqs = new HashMap<>();
        transitionFreqs = new HashMap<>();

        BufferedReader wordBr = null;
        BufferedReader POSBr = null;

        try {
            wordBr = new BufferedReader(new FileReader(wordFileName));
            POSBr = new BufferedReader(new FileReader(POSFileName));

            String POSLine, wordLine;

            // Read each line in the file
            while ((POSLine = POSBr.readLine()) != null && (wordLine = wordBr.readLine()) != null) {

                // Reset the prevPOS to the start for each new line
                String prevPOS = "#";

                String[] POS = POSLine.split(" ");
                String[] words = wordLine.split(" ");

                // if the number of words and POS tags aren't equal, throw an exception
                if (POS.length != words.length){
                    System.out.println(" ");
                    System.out.println("line: " + wordLine);
                    throw new Exception("Number of words and POS tags not equal");
                }

                // Build the observation and transition frequency maps based on the inputted words
                for (int i = 0; i < POS.length; i++){
                    String curPOS = POS[i];
                    String curWord = words[i].toLowerCase();

                    buildFreqMap(transitionFreqs, prevPOS, curPOS);
                    buildFreqMap(observationFreqs, curPOS, curWord);

                    prevPOS = curPOS;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Close the file readers
            try {
                wordBr.close();
            } catch (Exception e) {
                System.out.println("wordBR couldn't be closed");
            }
            try {
                POSBr.close();
            } catch (Exception e) {
                System.out.println("POSBR couldn't be closed");
            }
        }
    }

    /**
     * Calculates the score each state based on the frequency map.
     * @param freqMap The frequency map to calculate the score for.
     * @return A map of scores for each state.
     */
    private static Map<String, Map<String, Double>> calculateScore(Map<String, Map<String, Integer>> freqMap) {
        Map<String, Map<String, Double>> scoreMap = new HashMap<>();

        // for each POS in the given map
        for (String outerVal: freqMap.keySet()){

            Map<String, Integer> freqInnerMap = freqMap.get(outerVal);

            Map<String, Double> scoreInnerMap = new HashMap<>();
            scoreMap.put(outerVal, scoreInnerMap);

            int totalInstances = 0;

            // add the total number of instances that occur under the outerKey
            for (String innerVal: freqInnerMap.keySet()) {
                totalInstances += freqInnerMap.get(innerVal);
            }

            for (String innerVal: freqMap.get(outerVal).keySet()){
                // calculate the score for each POS or word
                Double freqValue = Math.log((double) freqMap.get(outerVal).get(innerVal) / totalInstances);
                scoreInnerMap.put(innerVal, freqValue);
            }
        }

        return scoreMap;
    }

    /**
     * Calculates the scores for the transition and observation maps.
     */
    private void calculateScores() {
        observationScores = calculateScore(observationFreqs);
        transitionScores = calculateScore(transitionFreqs);
    }

    /**
     * Creates an HMM from a file of words and a file of POS tags.
     * @param wordFileName The file containing the words.
     * @param POSFileName The file containing the POS tags.
     */
    public HMM(String wordFileName, String POSFileName) {
        fileReader(wordFileName, POSFileName);
        calculateScores();
    }

    /**
     * Creates an HMM from a map of transition scores and a map of observation scores.
     * @param transitionScores The map of transition scores.
     * @param observationScores The map of observation scores.
     */
    public HMM(Map<String, Map<String, Double>> transitionScores, Map<String, Map<String, Double>> observationScores) {
        this.transitionScores = transitionScores;
        this.observationScores = observationScores;
    }

    /**
     * Print out the frequency and score maps for manual inspection.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        HMM HMM = new HMM("texts/example-sentences.txt", "texts/example-tags.txt");

        System.out.println("Example Training");

        System.out.println("POS-word frequencies:");
        System.out.println(HMM.getObservationFreqs());
        System.out.println("POS-word keys:");
        System.out.println(HMM.getObservationFreqs().keySet());
        System.out.println("POS-word scores:");
        System.out.println(HMM.getObservationScores());

        System.out.println("POS-POS frequencies:");
        System.out.println(HMM.getTransitionFreqs());
        System.out.println("POS-word keys:");
        System.out.println(HMM.getTransitionFreqs().keySet());
        System.out.println("POS-POS scores:");
        System.out.println(HMM.getTransitionScores());
    }
}
