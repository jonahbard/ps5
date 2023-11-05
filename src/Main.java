// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {


    /***
     * return array of POS that corresponds to the backtraced viterbi most-likely POS list for given string
     * @param
     * @return
     */

    public void assembleHMM(){} // assemble the HMM from the calculation methods

    public void testViterbi(String testWordsFile, String testPOSfile){}

    public static void main(String[] args) throws Exception {
        HMM HMM = new HMM("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");
//        System.out.println("POS-word frequencies:");
//        System.out.println(maps.getPOSToWordFreq());
//        System.out.println("POS-word keys:");
        System.out.println(HMM.getObservationFreqs().keySet());
//        System.out.println("POS-word scores:");
//        System.out.println(maps.getPOSToWordScore());
//
//        System.out.println("POS-POS frequencies:");
//        System.out.println(maps.getPOSToPOSFreq());
//        System.out.println("POS-word keys:");
        System.out.println(HMM.getTransitionFreqs().keySet());
//        System.out.println("POS-POS scores:");
//        System.out.println(maps.getPOSToPOSScore());


        Viterbi v = new Viterbi(HMM);
        System.out.println(v.calculateMostLikelyPOSSequence("my watch glows in the night ."));
    }
}