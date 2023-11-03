import java.util.Map;

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
    public String[] viterbiMostLikelyPOSSequence(String phrase){}

    public Map<Map<String, String>, Double> calculateTransitionProbabilities(String POSfile){}

    public Map<String, Map<String, Double>> calculateWordPOSProbabilities(String wordsFile, String POSfile){}

    public void assembleHMM(){} // assemble the HMM from the calculation methods

    public void testHMM(String wordsFile, String POSfile){}

    public static void main(String[] args) {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        // Press Ctrl+R or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Ctrl+D to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Cmd+F8.
            System.out.println("i = " + i);
        }
    }
}