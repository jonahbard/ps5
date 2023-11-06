import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Viterbi {

    private static final double UNSEEN_SCORE = -100.0;

    HMM hmm;

    public Viterbi(HMM hmm) {
        this.hmm = hmm;
    }


    /**
     * Calculate the most likely POS sequence based on the param and the model passed into the object during creation.
     * @param phrase
     * @return
     */
    public String calculateMostLikelyPOSSequence(String phrase){

        // each instance of this is compared to one specific POS key
        class ViterbiStep {
            public Double score; //the weighted HMM score for the current POS given the previous one
            public String currStep; // the previous step the POS->POS score reflects
            public ViterbiStep(Double score, String currStep) {
                this.score = score;
                this.currStep = currStep;
            }
        }

        Map<String, Map<String, Double>> transitions = hmm.getTransitionScores();
        Map<String, Map<String, Double>> observations = hmm.getObservationScores();

        // list where each element is a map representing 1 step: step<winning next state for each POS<current Viterbi Step>>
        List<Map<String, ViterbiStep>> steps = new ArrayList<>();

        Map<String, ViterbiStep> preStart = new HashMap<>();
        preStart.put("#", new ViterbiStep(0.0, null));
        steps.add(preStart);

        String[] words = phrase.split(" ");

        for (int i = 0; i < words.length; i++){
            String nextWord = words[i].toLowerCase();

            Map<String, ViterbiStep> curSteps = steps.get(steps.size()-1);
            Map<String, ViterbiStep> nextSteps = new HashMap<>();
            steps.add(nextSteps);

            for (String curPOS : curSteps.keySet()) {
                Double curScore = curSteps.get(curPOS).score;


                //  if the curPOS does not exist as a transition key, it is a trap and the current path doesn't work
                if (!transitions.containsKey(curPOS)) continue;

                for(String nextPOS : transitions.get(curPOS).keySet()) {

                    Double observationScore = observations.get(nextPOS).getOrDefault(nextWord, UNSEEN_SCORE);
                    Double newScore = curScore + transitions.get(curPOS).get(nextPOS) + observationScore;

                    if (nextSteps.containsKey(nextPOS)) {
                        double existingNextStepScore = nextSteps.get(nextPOS).score;
                        if (existingNextStepScore < newScore) {
                            nextSteps.put(nextPOS, new ViterbiStep(newScore, curPOS));
                        }
                    }
                    else {
                        nextSteps.put(nextPOS, new ViterbiStep(newScore, curPOS));
                    }

                }
            }
        }


        Map<String, ViterbiStep> lastStep = steps.get(steps.size()-1);

        // Get random POS for last step
        String bestLastPOS = lastStep.keySet().iterator().next();
        double bestLastScore = lastStep.get(bestLastPOS).score;

        for (String POS: lastStep.keySet()){
            if (lastStep.get(POS).score > bestLastScore) {
                bestLastScore = lastStep.get(POS).score;
                bestLastPOS = POS;
            }
        }

        String bestCurrPOS = bestLastPOS;

        String out = "";

        // Stop at 1 because we don't want to add # to the out string
        for (int i = steps.size() - 1; i >= 1; i--){

            out = bestCurrPOS + " " + out;
            bestCurrPOS = steps.get(i).get(bestCurrPOS).currStep;
        }

        return out;
    }

    /***
     * returns the amount of incorrect POS tags the model assigns to a given string
     * @param phrase: the line given to the model to process
     * @param actualPOSString: the line representing the correct tags for the given line
     * @return
     */
    public int incorrectPOS(String phrase, String actualPOSString){
        String[] viterbiResult = calculateMostLikelyPOSSequence(phrase).split(" ");
        String[] actualPOSArray = actualPOSString.split(" ");

        if (viterbiResult.length != actualPOSArray.length) {
            System.out.println("incorrectPOS: uh oh, POS array lengths are different!");
            return -1;
        }

        int incorrectPOS = 0;
        for (int i = 0; i < actualPOSArray.length; i++){
            if (!viterbiResult[i].equals(actualPOSArray[i])){
                incorrectPOS++;
            }
        }
        return incorrectPOS;
    }


    /***
     * return proportion of tags our model gets correct for a given line and its actual tags
     * @param phrase: given line to process
     * @param actualPOSString: correct POS tags for line
     * @return
     */
    public double lineAccuracy(String phrase, String actualPOSString){
        String[] actualPOSArray = actualPOSString.split(" ");

        int incorrectPOS = incorrectPOS(phrase, actualPOSString);

        //accuracy = (total - incorrect) / total
        return (double) (actualPOSArray.length - incorrectPOS) / actualPOSArray.length;
    }

    //it's 1-line / 1-use but just makes things more coherent
    public int getNumberOfWords(String line){
        return line.split(" ").length;
    }
    /***
     * returns the accuracy of the model's POS predictions for an entire file of formatted text.
     * @param textFileName
     * @param actualPOSFileName
     * @return
     * @throws Exception
     */
    public double fileAccuracy(String textFileName, String actualPOSFileName) {
        int totalWords = 0;
        int totalIncorrect = 0;

        BufferedReader textFileReader = null;
        BufferedReader tagFileReader = null;


        int lines = 0;
        try {
            textFileReader = new BufferedReader(new FileReader(textFileName));
            tagFileReader = new BufferedReader(new FileReader(actualPOSFileName));

            String textLine;
            String tagLine;

            while ((tagLine = tagFileReader.readLine()) != null) {
                textLine = textFileReader.readLine();
                totalWords += getNumberOfWords(textLine); // some debate here as to whether to count from tag/text
                totalIncorrect += incorrectPOS(textLine, tagLine);
                lines += 1;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during file reading");
        }
        finally {
            try { textFileReader.close(); }
            catch (Exception e) {}

            try { tagFileReader.close(); }
            catch (Exception e) {}
        }

        System.out.println("total words: " + totalWords);
        System.out.println("total lines: " + lines);

        System.out.println("total incorrect: " + totalIncorrect);

        return (double) (totalWords - totalIncorrect) / (totalWords);
    }
}
