import java.util.HashMap;
import java.util.Map;


public class HardCodedTestDriver {
    public static void main(String[] args) {
        Map<String, Map<String, Double>> transitions = PDTransitions();
        Map<String, Map<String, Double>> observations = PDObservations();

        HMM hmm = new HMM(transitions, observations);

        Viterbi v = new Viterbi(hmm);

        System.out.println(v.calculateMostLikelyPOSSequence("chase watch dog chase watch"));
    }


    public static Map<String, Map<String, Double>> PDObservations() {
        Map<String, Map<String, Double>> observations = new HashMap<>();

        Map<String, Double> start = new HashMap<>();
        observations.put("#", start);

        Map<String, Double> NP = new HashMap<>();
        observations.put("NP", NP);
        NP.put("chase", 10.0);

        Map<String, Double> N = new HashMap<>();
        observations.put("N", N);
        N.put("cat", 4.0);
        N.put("dog", 4.0);
        N.put("watch", 2.0);

        Map<String, Double> CNJ = new HashMap<>();
        observations.put("CNJ", CNJ);
        CNJ.put("and", 10.0);

        Map<String, Double> V = new HashMap<>();
        observations.put("V", V);
        V.put("get", 1.0);
        V.put("chase", 3.0);
        V.put("watch", 6.0);

        return observations;
    }

    public static Map<String, Map<String, Double>> PDTransitions() {
        Map<String, Map<String, Double>> transitions = new HashMap<>();

        Map<String, Double> startTo = new HashMap<>();
        transitions.put("#", startTo);
        startTo.put("NP", 3.0);
        startTo.put("N", 7.0);

        Map<String, Double> NPTo = new HashMap<>();
        transitions.put("NP", NPTo);
        NPTo.put("CNJ", 2.0);
        NPTo.put("V", 8.0);

        Map<String, Double> VTo = new HashMap<>();
        transitions.put("V", VTo);
        VTo.put("NP", 4.0);
        VTo.put("CNJ", 2.0);
        VTo.put("N", 4.0);

        Map<String, Double> CNJTo = new HashMap<>();
        transitions.put("CNJ", CNJTo);
        CNJTo.put("NP", 2.0);
        CNJTo.put("V", 4.0);
        CNJTo.put("N", 4.0);

        Map<String, Double> NTo = new HashMap<>();
        transitions.put("N", NTo);
        NTo.put("V", 8.0);
        NTo.put("CNJ", 2.0);

        return transitions;
    }
}
