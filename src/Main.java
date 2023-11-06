import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {


    /***
     * return array of POS that corresponds to the backtraced viterbi most-likely POS list for given string
     * @param
     * @return
     */

    public static void runUI() throws Exception {

        HMM hmm = new HMM("texts/brown-train-sentences.txt", "texts/brown-train-tags.txt");
        Viterbi v = new Viterbi(hmm);

        Scanner sc = new Scanner(System.in);
        System.out.println("\n---HMM UI---");

        while (true) {
            System.out.println("please type in a line you would like to get the part-of-speech tags for. just press ENTER to exit.");
            String line = sc.nextLine();
            if (line.isEmpty()) break;
            System.out.println("parts of speech for your line: \n" + v.calculateMostLikelyPOSSequence(line));
        }

        sc.close();


//        System.out.println("please type in a line you would like to get the part-of-speech tags for.");
//        String line = sc.nextLine();
//        System.out.println("parts of speech for your line: \n" + v.calculateMostLikelyPOSSequence(line));
//
//        System.out.println("\nplease type in the local project path to a formatted text file you would like to test the model on:");
//        String textFile = sc.nextLine();
//        System.out.println("please type in the local project path to a corresponding TAGS file you would like to test the model on:");
//        String tagFile = sc.nextLine();
//
//        System.out.println("awesome! our model accuracy is: " + v.fileAccuracy(textFile, tagFile));
//
//
//        sc.close();
    }

    public static void main(String[] args) throws Exception {

        HMM simpleHMM = new HMM("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");

        System.out.println("--LINE TEST--");
        System.out.println("observation frequencies keyset:");
        System.out.println(simpleHMM.getObservationFreqs().keySet());

        System.out.println("transition frequencies keyset:");
        System.out.println(simpleHMM.getTransitionFreqs().keySet());
        Viterbi v = new Viterbi(simpleHMM);

        System.out.println("line we're testing:");
        System.out.println(v.calculateMostLikelyPOSSequence("his work is to bark in a cave ."));

        System.out.println("accuracy of the model for this line:");
        System.out.println(v.lineAccuracy("his work is to bark in a cave .", "PRO N V P V P DET N ."));


        System.out.println("\n--FILE TEST--");
        System.out.println("file we're testing: simple-test-sentences.txt");
        System.out.println("accuracy of the model for this file:");
        System.out.println(v.fileAccuracy("texts/simple-test-sentences.txt", "texts/simple-test-tags.txt"));


        HMM BrownHMM = new HMM("texts/brown-train-sentences.txt", "texts/brown-train-tags.txt");

        System.out.println("\nobservation frequencies keyset:");
        System.out.println(BrownHMM.getObservationFreqs().keySet());

        System.out.println("transition frequencies keyset:");
        System.out.println(BrownHMM.getTransitionFreqs().keySet());
        Viterbi v2 = new Viterbi(BrownHMM);

        System.out.println("line we're testing:");
        System.out.println(v2.calculateMostLikelyPOSSequence("his work is to bark in a cave ."));

        System.out.println("accuracy of the model for this line:");
        System.out.println(v2.lineAccuracy("his work is to bark in a cave .", "PRO N V P V P DET N ."));


        System.out.println("\n--FILE TEST--");
        System.out.println("file we're testing: brown-test-sentences.txt");
        System.out.println("accuracy of the model for this file:");
        System.out.println(v2.fileAccuracy("texts/brown-test-sentences.txt", "texts/brown-test-tags.txt"));


        runUI();
    }
}