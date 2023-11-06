import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class HMM {

    private Map<String, Map<String, Integer>> observationFreqs;
    private Map<String, Map<String, Integer>> transitionFreqs;

    private Map<String, Map<String, Double>> observationScores;
    private Map<String, Map<String, Double>> transitionScores;


    public Map<String, Map<String, Integer>> getObservationFreqs() {return observationFreqs;}
    public Map<String, Map<String, Integer>> getTransitionFreqs() {return transitionFreqs;}

    public Map<String, Map<String, Double>> getObservationScores() {return observationScores;}
    public Map<String, Map<String, Double>> getTransitionScores() {return transitionScores;}


    private static void buildFreqMap(Map<String, Map<String, Integer>> map, String from, String to) {
        if (!map.containsKey(from)) map.put(from, new HashMap<>());
        Map<String, Integer> innerMap = map.get(from);

        if(!innerMap.containsKey(to)) innerMap.put(to, 0);
        innerMap.put(to, innerMap.get(to) + 1);
    }

    public void fileReader(String wordFileName, String POSFileName) {
        observationFreqs = new HashMap<>();
        transitionFreqs = new HashMap<>();

        BufferedReader wordBr;
        BufferedReader POSBr;

        try {
            wordBr = new BufferedReader(new FileReader(wordFileName));
            POSBr = new BufferedReader(new FileReader(POSFileName));


            String POSLine, wordLine;
            while ((POSLine = POSBr.readLine()) != null && (wordLine = wordBr.readLine()) != null) {
                String prevPOS = "#";
                String[] POS = POSLine.split(" ");
                String[] words = wordLine.split(" ");

                if (POS.length != words.length){
                    System.out.println(" ");
                    System.out.println("line: " + wordLine);
                    throw new Exception();
                }

                for (int i = 0; i < POS.length; i++){
                    String curPOS = POS[i];
                    String curWord = words[i].toLowerCase();

                    buildFreqMap(transitionFreqs, prevPOS, curPOS);
                    buildFreqMap(observationFreqs, curPOS, curWord);

//                    buildToPOSToWordFreq(curPOS, curWord);
//                    buildToPOSToPOSFreq(prevPOS, curPOS);

                    prevPOS = curPOS;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Map<String, Double>> calculateScore(Map<String, Map<String, Integer>> freqMap) {
        Map<String, Map<String, Double>> scoreMap = new HashMap<>();

        // for each POS in the given map
        for (String outerVal: freqMap.keySet()){

            Map<String, Integer> freqInnerMap = freqMap.get(outerVal);

            Map<String, Double> scoreInnerMap = new HashMap<>();
            scoreMap.put(outerVal, scoreInnerMap);

//            Map<String, Integer> innerMap = freqMap.get(outerVal);

            int totalInstances = 0;
            // for each either POS or word in given map, depending on whether it's POStoPOS or POStoWord
            for (String innerVal: freqInnerMap.keySet()){
                totalInstances += freqInnerMap.get(innerVal);
            }

            for (String innerVal: freqMap.get(outerVal).keySet()){
                Double freqValue = Math.log(freqMap.get(outerVal).get(innerVal) / (double) totalInstances);
                scoreInnerMap.put(innerVal, freqValue);
            }
        }

        return scoreMap;
    }

    private void calculateScores() {
        observationScores = calculateScore(observationFreqs);
        transitionScores = calculateScore(transitionFreqs);
    }

    public HMM(String wordFileName, String POSFileName) {

        fileReader(wordFileName, POSFileName);
        calculateScores();
    }

    public static void main(String[] args) {
        HMM HMM = new HMM("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");
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
