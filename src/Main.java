import java.util.Scanner;


public class Main {
    public static void runUI() {

        HMM hmm = new HMM("texts/brown-train-sentences.txt", "texts/brown-train-tags.txt");
        Viterbi v = new Viterbi(hmm);

        Scanner sc = new Scanner(System.in);
        System.out.println("\n---HMM UI---");

        System.out.println("Option\n" +
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
            System.out.println("\n");
        }
        sc.close();
    }

    public static void main(String[] args) throws Exception {

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

        System.out.println("--BROWN FILE TEST--");
        fileTest(brownV, "texts/brown-test-sentences.txt", "texts/brown-test-tags.txt");

        runUI();
    }


    private static void lineTest(Viterbi v, String words, String POSs) {
        System.out.println("line we're testing: " + words);
        System.out.println("Model POS outputs: " + v.calculateMostLikelyPOSSequence(words));
        System.out.println("Correct POS: " + POSs);
        System.out.print("accuracy of the model for this line: " + v.lineAccuracy(words, POSs));
        System.out.println("\n");
    }

    private static void fileTest(Viterbi v, String wordsFile, String POSFile) {
        System.out.println("file we're testing: " + wordsFile);
        System.out.println("accuracy of the model for this file: " + v.fileAccuracy(wordsFile, POSFile));
        System.out.println("\n");
    }
}