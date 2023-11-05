import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Viterbi {

    HMM hmm;

    public Viterbi(HMM hmm) {
        this.hmm = hmm;
    }


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
        ArrayList<Map<String, ViterbiStep>> steps = new ArrayList<>();

        Map<String, ViterbiStep> preStart = new HashMap<>();
        preStart.put("#", new ViterbiStep(0.0, null));
        steps.add(preStart);

        String[] words = phrase.split(" ");

        for (int i = 0; i < words.length; i++){
            String nextWord = words[i];

            Map<String, ViterbiStep> curSteps = steps.get(steps.size()-1);
            Map<String, ViterbiStep> nextSteps = new HashMap<>();
            steps.add(nextSteps);

            for (String curPOS : curSteps.keySet()) {
                Double curScore = curSteps.get(curPOS).score;

                System.out.println(curPOS);

                //  if the curPOS does not exist as a transition key, it is a trap and the current processing must be
                //    done according to the model
                if (!transitions.containsKey(curPOS)) continue;

                for(String nextPOS : transitions.get(curPOS).keySet()) {

                    Double observationScore = observations.get(nextPOS).getOrDefault(nextWord, -5.0);
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

    public double viterbiAccuracy(String phrase, String actualPOSString){
        String[] viterbiResult = calculateMostLikelyPOSSequence(phrase).split(" ");
        String[] actualPOSArray = actualPOSString.split(" ");

        if (viterbiResult.length != actualPOSArray.length) {
            System.out.println("uh oh, POS array lengths are different!");
            return 0.0;
        }

        int incorrectPOS = 0;
        for (int i = 0; i < actualPOSArray.length; i++){
            if (!viterbiResult[i].equals(actualPOSArray[i])){
                incorrectPOS++;
            }
        }

        //accuracy = (total - incorrect) / total
        return (double) (actualPOSArray.length - incorrectPOS) / actualPOSArray.length;
    }
}
