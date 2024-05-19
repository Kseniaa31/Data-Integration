package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;
import java.util.*;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     *
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     *
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override

        public double calculate(String[] strings1, String[] strings2) {
            double jaccardSimilarity;

            if (!bagSemantics) { //it means we have set semantics
                int union_size;
                int intersection_size;
                Set<String> set_string1, set_string2;
                set_string1 = new HashSet<>(Arrays.asList(strings1)); //create set so that no duplicates are there
                set_string2 = new HashSet<>(Arrays.asList(strings2));
                Set<String> intersection = new HashSet<>(set_string1); //calculate intersection
                intersection.retainAll(set_string2);
                Set<String> union = new HashSet<>(set_string1); //calculate union
                union.addAll(set_string2);
                intersection_size = intersection.size();
                union_size = union.size();
                if (union_size == 0) {
                    jaccardSimilarity = 0;
                } else {
                    jaccardSimilarity = (double) intersection_size / union_size;
                }
            }
           else {//bag semantics
                Map<String, Integer> tokens1 = get_Tokens_Freq(strings1);
                Map<String, Integer> tokens2 = get_Tokens_Freq(strings2);

                int intersection_size = 0;
                int union_size = 0;

                for (String token : tokens1.keySet()) {
                    intersection_size += Math.min(tokens1.get(token), tokens2.getOrDefault(token, 0)); //get intersection size
                    union_size += tokens1.get(token); //get part of union size
                }

                for (String token : tokens2.keySet()) {
                    union_size += tokens2.get(token); //complete union size with elements from second list
                }

                if (union_size == 0) {
                    jaccardSimilarity = 0;
                } else {
                    jaccardSimilarity = (double) intersection_size / union_size;
                }
            }
            return jaccardSimilarity;
        }

    private Map<String, Integer> get_Tokens_Freq(String[] tokens) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String token : tokens) {
            freqMap.put(token, freqMap.getOrDefault(token, 0) + 1);
        }
        return freqMap;
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                      DATA INTEGRATION ASSIGNMENT                                           //
// Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
// calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
// semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
// similarity either of the two semantics by respecting the inner bagSemantics flag.                          //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////