import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class HMM {

    private Map<String, Map<String, Integer>> POSToWordFreq;
    private Map<String, Map<String, Integer>> POSToPOSFreq;

    private Map<String, Map<String, Double>> POSToWordScore;
    private Map<String, Map<String, Double>> POSToPOSScore;


    public Map<String, Map<String, Integer>> getPOSToWordFreq() {return POSToWordFreq;}
    public Map<String, Map<String, Integer>> getPOSToPOSFreq() {return POSToPOSFreq;}

    public Map<String, Map<String, Double>> getPOSToWordScore() {return POSToWordScore;}
    public Map<String, Map<String, Double>> getPOSToPOSScore() {return POSToPOSScore;}


    private void buildToPOSToWordFreq(String POS, String word) {
        if (!POSToWordFreq.containsKey(POS)) POSToWordFreq.put(POS, new HashMap<>());
        Map<String, Integer> wordToFreqForPOS = POSToWordFreq.get(POS);

        if (!wordToFreqForPOS.containsKey(word)) wordToFreqForPOS.put(word, 0);
        wordToFreqForPOS.put(word, wordToFreqForPOS.get(word) + 1);

    }

    private void buildToPOSToPOSFreq(String prevPOS, String curPOS) {
        if (!POSToPOSFreq.containsKey(prevPOS)) POSToPOSFreq.put(prevPOS, new HashMap<>());
        Map<String, Integer> curPOSToFreqForPrevPOS = POSToPOSFreq.get(prevPOS);
        if (!curPOSToFreqForPrevPOS.containsKey(curPOS)) curPOSToFreqForPrevPOS.put(curPOS, 0);
        curPOSToFreqForPrevPOS.put(curPOS, curPOSToFreqForPrevPOS.get(curPOS) + 1);
    }

    public void fileReader(String wordFileName, String POSFileName) {
        POSToWordFreq = new HashMap<>();
        POSToPOSFreq = new HashMap<>();

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

                    buildToPOSToWordFreq(curPOS, curWord);
                    buildToPOSToPOSFreq(prevPOS, curPOS);

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
        POSToWordScore = calculateScore(POSToWordFreq);
        POSToPOSScore = calculateScore(POSToPOSFreq);
    }

    public HMM(String wordFileName, String POSFileName) {

        fileReader(wordFileName, POSFileName);
        calculateScores();
    }

    public static void main(String[] args) {
        HMM HMM = new HMM("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");
        System.out.println("POS-word frequencies:");
        System.out.println(HMM.getPOSToWordFreq());
        System.out.println("POS-word keys:");
        System.out.println(HMM.getPOSToWordFreq().keySet());
        System.out.println("POS-word scores:");
        System.out.println(HMM.getPOSToWordScore());

        System.out.println("POS-POS frequencies:");
        System.out.println(HMM.getPOSToPOSFreq());
        System.out.println("POS-word keys:");
        System.out.println(HMM.getPOSToPOSFreq().keySet());
        System.out.println("POS-POS scores:");
        System.out.println(HMM.getPOSToPOSScore());
    }
}
