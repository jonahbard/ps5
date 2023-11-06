import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that runs Viterbi algorithim and related tests on given HMM
 * 
 * @author Jonah Bard, Daniel Katz
 */

public class Viterbi {

    private static final double UNSEEN_SCORE = -10.0;

    HMM hmm;

    /**
     * Constructor for Viterbi object
     * @param hmm
     */
    public Viterbi(HMM hmm) {
        this.hmm = hmm;
    }


    /**
     * Calculate the most likely POS sequence based on the param and the model passed into the object during creation.
     * @param phrase
     * @return
     */
    public String calculateMostLikelyPOSSequence(String phrase) {

        // Get the backtrace steps
        List<Map<String, ViterbiStep>> steps = createBackTraceSteps(phrase);

        // get the best POS at the last step
        String bestCurrPOS = bestPOSForStep(steps.get(steps.size() - 1));

        String out = "";

        // Stop at 1 because we don't want to add # to the out string
        for (int i = steps.size() - 1; i >= 1; i--){
            // add the best POS for the current step to the output string
            out = bestCurrPOS + " " + out;
            bestCurrPOS = steps.get(i).get(bestCurrPOS).currStep;
        }

        return out;
    }

    /**
     * Create the backtrace steps for the given phrase
     * @param phrase
     * @return
     */
    private List<Map<String, ViterbiStep>> createBackTraceSteps(String phrase) {
        Map<String, Map<String, Double>> transitions = hmm.getTransitionScores();
        Map<String, Map<String, Double>> observations = hmm.getObservationScores();

        // list where each element is a map representing 1 step: step<winning next state for each POS<current Viterbi Step>>
        List<Map<String, ViterbiStep>> steps = new ArrayList<>();

        // add the pre-start step
        Map<String, ViterbiStep> preStart = new HashMap<>();
        preStart.put("#", new ViterbiStep(0.0, null));
        steps.add(preStart);

        String[] words = phrase.split(" ");

        for (int i = 0; i < words.length; i++){
            String nextWord = words[i].toLowerCase();

            Map<String, ViterbiStep> curSteps = steps.get(steps.size() - 1);
            Map<String, ViterbiStep> nextSteps = new HashMap<>();
            steps.add(nextSteps);

            // go over each POS in the current step
            for (String curPOS : curSteps.keySet()) {
                Double curScore = curSteps.get(curPOS).score;


                //  if the curPOS does not exist as a transition key, it is a trap and the current path doesn't work
                if (!transitions.containsKey(curPOS)) continue;

                // go over each POS that the current POS can transition to
                for(String nextPOS : transitions.get(curPOS).keySet()) {

                    // calculate the score for the next POS
                    Double observationScore = observations.get(nextPOS).getOrDefault(nextWord, UNSEEN_SCORE);
                    Double newScore = curScore + transitions.get(curPOS).get(nextPOS) + observationScore;

                    // if the next POS already exists in the nextSteps map, compare the scores and keep the higher one
                    if (nextSteps.containsKey(nextPOS)) {
                        double existingNextStepScore = nextSteps.get(nextPOS).score;
                        if (existingNextStepScore < newScore) {
                            nextSteps.put(nextPOS, new ViterbiStep(newScore, curPOS));
                        }
                    }

                    // if the next POS does not exist in the nextSteps map, add it
                    else {
                        nextSteps.put(nextPOS, new ViterbiStep(newScore, curPOS));
                    }
                }
            }
        }

        return steps;
    }

    /**
     * Get the next POS with the best score for the given step
     * @param step
     * @return
     */
    private String bestPOSForStep(Map<String, ViterbiStep> step) {
        // Get random POS for last step
        String bestLastPOS = step.keySet().iterator().next();
        double bestLastScore = step.get(bestLastPOS).score;

        // find the POS with the highest score for the last step
        for (String POS: step.keySet()){
            if (step.get(POS).score > bestLastScore) {
                bestLastScore = step.get(POS).score;
                bestLastPOS = POS;
            }
        }

        return bestLastPOS;
    }

    /***
     * returns the amount of incorrect POS tags the model assigns to a given string
     * @param phrase the line given to the model to process
     * @param actualPOSString the line representing the correct tags for the given line
     * @return
     */
    public int incorrectPOS(String phrase, String actualPOSString) {
        String[] viterbiResult = calculateMostLikelyPOSSequence(phrase).split(" ");
        String[] actualPOSArray = actualPOSString.split(" ");

        // If the lengths are wrong, something is not right and the user has to fix the inputs
        if (viterbiResult.length != actualPOSArray.length) {
            System.out.println("incorrectPOS: uh oh, POS array lengths are different! Check the inputs");
            return -1;
        }

        // Check each generated POS with the inputted POS
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
     * @param phrase given line to process
     * @param actualPOSString correct POS tags for line
     * @return
     */
    public double lineAccuracy(String phrase, String actualPOSString) {
        String[] actualPOSArray = actualPOSString.split(" ");

        int incorrectPOS = incorrectPOS(phrase, actualPOSString);

        //accuracy = (total - incorrect) / total
        return (double) (actualPOSArray.length - incorrectPOS) / actualPOSArray.length;
    }

    /**
     * returns the number of words in a given line
     *  
     * it's 1-line / 1-use but just makes things more coherent
     * 
     * @param line
     * @return
     */
    public int getNumberOfWords(String line){
        return line.split(" ").length;
    }

    /***
     * returns the accuracy of the model's POS predictions for an entire file of formatted text.
     * @param textFileName
     * @param actualPOSFileName
     * @return
     */
    public double fileAccuracy(String textFileName, String actualPOSFileName) {
        int totalWords = 0;
        int totalIncorrect = 0;

        BufferedReader textFileReader = null;
        BufferedReader tagFileReader = null;

        try {
            textFileReader = new BufferedReader(new FileReader(textFileName));
            tagFileReader = new BufferedReader(new FileReader(actualPOSFileName));

            String textLine, tagLine;

            // Calculate the number of words and the number of incorrect POS tags for each line
            while ((tagLine = tagFileReader.readLine()) != null && (textLine = textFileReader.readLine()) != null) {
                totalWords += getNumberOfWords(textLine);
                totalIncorrect += incorrectPOS(textLine, tagLine);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during file reading");
        }
        finally {
            // Close the file readers
            try { textFileReader.close(); }
            catch (Exception e) {}

            try { tagFileReader.close(); }
            catch (Exception e) {}
        }

        // accuracy = (total - incorrect) / total
        return (double) (totalWords - totalIncorrect) / (totalWords);
    }
}

class ViterbiStep {
    public final Double score; //the weighted HMM score for the current POS given the previous one
    public final String currStep; // the previous step the POS->POS score reflects
    public ViterbiStep(Double score, String currStep) {
        this.score = score;
        this.currStep = currStep;
    }
}
