import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Viterbi {

    Maps maps;

    public Viterbi(Maps m) {
        maps = m;
    }


    public String calculateMostLikelyPOSSequence(String phrase){

        // each instance of this is compared to one specific POS key
        class ViterbiStep {
            public Double score; //the weighted HMM score for the current POS given the previous one
            public String prevStep; // the previous step the POS->POS score reflects
            public ViterbiStep(Double score, String prevStep) {
                this.score = score;
                this.prevStep = prevStep;
            }
        }

        // map representing the transitions part of HMM graph
        Map<String, Map<String, Double>> transitions = maps.getPOSToPOSScore();
        // map representing the observations part of HMM graph
        Map<String, Map<String, Double>> observations = maps.getPOSToWordScore();

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


        //should make a helper method to do this probebly

        //store that for each possible word in its own variable (should probably a map for each word in the phrase)

        //backtrace based on those maps

        Map<String, ViterbiStep> lastStep = steps.get(steps.size()-1);
        String bestLastPOS = lastStep.keySet().iterator().next();
        double bestLastScore = lastStep.get(bestLastPOS).score;

        for (String POS: lastStep.keySet()){
            if (lastStep.get(POS).score > bestLastScore) {
                bestLastScore = lastStep.get(POS).score;
                bestLastPOS = POS;
            }
        }

        String out = "";

        for (int i = steps.size() - 1; i >= 0; i--){

            out = bestLastPOS + " " + out;
            bestLastPOS = steps.get(i).get(bestLastPOS).prevStep;
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
