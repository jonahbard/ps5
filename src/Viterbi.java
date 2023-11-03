import java.util.ArrayList;

public class Viterbi {

    Maps maps;

    public Viterbi(Maps m) {
        maps = m;
    }



    public String calculateMostLikelyPOSSequence(String phrase){
        String out = "";

        // List<Map<CurrentPOS, PreviousPOS>>
        ArrayList<Map<String, >>

        //viterbi alg: add transition score + wordtoPOS score + previous score
        //should make a helper method to do this probebly

        //store that for each possible word in its own variable (should probably a map for each word in the phrase)

        //backtrace based on those maps




        return out
    }

    class PrevStep {
        private Double score;
        private String POS;
    }
}
