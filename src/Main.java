import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This class provides an interface to test and use HMMs and Viterbi models
 * 
 * @author Jonah Bard, Daniel Katz
 */

public class Main {

    /**
     * run the UI for the HMM model.
     */
    public static void runUI() {
        HMM hmm = new HMM("texts/brown-train-sentences.txt", "texts/brown-train-tags.txt");
        Viterbi v = new Viterbi(hmm);

        Scanner sc = new Scanner(System.in);
        System.out.println("\n---HMM UI---");

        System.out.println("A brown trained model is used for the command line interface");
        System.out.println("Options\n" +
                "l: line test\n" +
                "f: file test\n" +
                "q: quit");

        while (true) {

            System.out.println("please enter your mode selection: ");
            String line = sc.nextLine();
            if (line.equals("q")) break;
            else if (line.equals("l")) {
                System.out.println("please type in a line you would like to get the part-of-speech tags for.");
                String inputLine = sc.nextLine();
                if (inputLine.isBlank()) {
                    System.out.println("Please enter a non-empty line next time");
                    continue;
                }
                System.out.println("parts of speech for your line: \n" + v.calculateMostLikelyPOSSequence(inputLine));
            }
            else if (line.equals("f")) {
                System.out.println("\nplease type in the local project path to a formatted text file you would like to test the model on:");
                String textFile = sc.nextLine();
                System.out.println("please type in the local project path to a corresponding TAGS file you would like to test the model on:");
                String tagFile = sc.nextLine();
                System.out.println("awesome! our model accuracy is: " + v.fileAccuracy(textFile, tagFile));
            }
            else {
                System.out.println("Please try a valid input");
            }
            System.out.println( );
        }
        sc.close();
    }

    /***
     * run tests using the recitation model, simple model, and brown model, and then call runUI().
     */
    public static void main(String[] args) {

        HMM PDHMM = new HMM(PDTransitionsMap(), PDObservationsMap());
        Viterbi PDV = new Viterbi(PDHMM);

        System.out.println("--RECITATION SENTENCE TESTS--");
        // Demonstrate the most likely POS (Part Of Speech) sequence for each sentence
        demoSentence(PDV, "chase watch dog chase watch");
        demoSentence(PDV, "cat and cat and dog and watch chase");
        demoSentence(PDV, "there are none words");


        HMM simpleHMM = new HMM("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");
        Viterbi simpleV = new Viterbi(simpleHMM);

        System.out.println("--SIMPLE LINE TESTS--");
        lineTest(simpleV, "his work is to bark in a cave .", "PRO N V P V P DET N .");
        lineTest(simpleV, "my watch glows in the night .", "PRO N V P DET N .");

        System.out.println("--SIMPLE FILE TEST--");
        fileTest(simpleV, "texts/simple-test-sentences.txt", "texts/simple-test-tags.txt");


        HMM BrownHMM = new HMM("texts/brown-train-sentences.txt", "texts/brown-train-tags.txt");
        Viterbi brownV = new Viterbi(BrownHMM);

        System.out.println("--BROWN LINE TESTS--");
        lineTest(brownV,
                "attorneys for the mayor said that an amicable property settlement has been agreed upon .",
                "N P DET N VD CNJ DET ADJ N N V V VN ADV .");
        lineTest(brownV, "his work is to bark in a cave .", "PRO N V P V P DET N .");
        lineTest(brownV, "Hello , this is a test .", "UH , DET V DET N .");
        lineTest(brownV, "Will eats the fish", "NP V DET N");
        lineTest(brownV, "Jobs wore one color", "NP VD DET N");
        lineTest(brownV, "The jobs were mine", "DET N VD PRO");


        System.out.println("--BROWN FILE TEST--");
        fileTest(brownV, "texts/brown-test-sentences.txt", "texts/brown-test-tags.txt");

        runUI();
    }

    /**
     * apply the viterbi model to a given line and evaluate based on its correct tags.
     * @param v: viterbi model
     * @param words the line we're testing
     * @param POSs the correct tags for the line
     */
    private static void lineTest(Viterbi v, String words, String POSs) {
        System.out.println("line we're testing: " + words);
        System.out.println("Model POS outputs: " + v.calculateMostLikelyPOSSequence(words));
        System.out.println("Correct POS: " + POSs);
        System.out.print("accuracy of the model for this line: " + v.lineAccuracy(words, POSs));
        System.out.println("\n");
    }

    /**
     * apply the viterbi model to a given file and evaluate based on its correct tags.
     * @param v viterbi model
     * @param wordsFile the file whose line's we're tagging
     * @param POSFile the file containing the correct tags for the wordsFile
     */
    private static void fileTest(Viterbi v, String wordsFile, String POSFile) {
        System.out.println("file we're testing: " + wordsFile);
        System.out.println("accuracy of the model for this file: " + v.fileAccuracy(wordsFile, POSFile));
        System.out.println("\n");
    }

    /**
     * Prints the tested sentence and the most likely parts of speech sequence as calculated by the Viterbi algorithm.
     * @param v The Viterbi algorithm instance.
     * @param sentence The sentence to be tested.
     */
    private static void demoSentence(Viterbi v, String sentence) {
        System.out.println("Testing sentence: " + sentence);
        // Calculate and print the most likely POS sequence for the given sentence
        String output = v.calculateMostLikelyPOSSequence(sentence);
        System.out.println("Most likely sequence: " + output);
        System.out.println();
    }


    /**
     * Creates a hardcoded observation probability distribution for a HMM.
     * @return A map representing the observation probabilities of different parts of speech.
     */
    private static Map<String, Map<String, Double>> PDObservationsMap() {
        // Initialize observation probability map
        Map<String, Map<String, Double>> observations = new HashMap<>();

        // Define observation probabilities for the start of a sentence
        Map<String, Double> start = new HashMap<>();
        observations.put("#", start); // '#' represents the start of a sentence

        // Define observation probabilities for nouns phrases (NP)
        Map<String, Double> NP = new HashMap<>();
        observations.put("NP", NP);
        NP.put("chase", 10.0);

        // Define observation probabilities for nouns (N)
        Map<String, Double> N = new HashMap<>();
        observations.put("N", N);
        N.put("cat", 4.0);
        N.put("dog", 4.0);
        N.put("watch", 2.0);

        // Define observation probabilities for conjunctions (CNJ)
        Map<String, Double> CNJ = new HashMap<>();
        observations.put("CNJ", CNJ);
        CNJ.put("and", 10.0);

        // Define observation probabilities for verbs (V)
        Map<String, Double> V = new HashMap<>();
        observations.put("V", V);
        V.put("get", 1.0);
        V.put("chase", 3.0);
        V.put("watch", 6.0);

        return observations;
    }

    /**
     * Creates a hardcoded transition probability distribution for a HMM.
     * @return A map representing the transition probabilities between different parts of speech.
     */
    private static Map<String, Map<String, Double>> PDTransitionsMap() {
        // Initialize transition probability map
        Map<String, Map<String, Double>> transitions = new HashMap<>();

        // Define transition probabilities from the start of a sentence
        Map<String, Double> startTo = new HashMap<>();
        transitions.put("#", startTo); // '#' represents the start of a sentence
        startTo.put("NP", 3.0);
        startTo.put("N", 7.0);

        // Define transition probabilities from noun phrases (NP)
        Map<String, Double> NPTo = new HashMap<>();
        transitions.put("NP", NPTo);
        NPTo.put("CNJ", 2.0);
        NPTo.put("V", 8.0);

        // Define transition probabilities from verbs (V)
        Map<String, Double> VTo = new HashMap<>();
        transitions.put("V", VTo);
        VTo.put("NP", 4.0);
        VTo.put("CNJ", 2.0);
        VTo.put("N", 4.0);

        // Define transition probabilities from conjunctions (CNJ)
        Map<String, Double> CNJTo = new HashMap<>();
        transitions.put("CNJ", CNJTo);
        CNJTo.put("NP", 2.0);
        CNJTo.put("V", 4.0);
        CNJTo.put("N", 4.0);

        // Define transition probabilities from nouns (N)
        Map<String, Double> NTo = new HashMap<>();
        transitions.put("N", NTo);
        NTo.put("V", 8.0);
        NTo.put("CNJ", 2.0);

        return transitions;
    }

}