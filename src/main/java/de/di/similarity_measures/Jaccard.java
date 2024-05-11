package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.lang.reflect.Array;
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
        double jaccardSimilarity = 0;
        double jaccardSimilarity1=0;
        Set<String> set_string1, set_string2;
        int union_size = 0;
        int intersection_size = 0;

        if (!bagSemantics) { //it means we have set semantics
            set_string1 = new HashSet<>(Arrays.asList(strings1)); //create set so that no duplicates are there
            set_string2 = new HashSet<>(Arrays.asList(strings2));
            Set<String> intersection = new HashSet<>(set_string1); //calculate intersection
            intersection.retainAll(set_string2);
            Set<String> union = new HashSet<>(set_string1); //calculate union
            union.addAll(set_string2);
            intersection_size = intersection.size();
            union_size = union.size();
            if (union_size == 0) {
                jaccardSimilarity1 = 0;
            } else {
                jaccardSimilarity1 = (double) intersection_size / union_size;
            }
        }
        else { //bag semantics

            int intersectionSize = 0;
            int unionSize = 0;

// Calculate intersection
            for (String s : strings1) {
                int countInStrings1 = countOccurrences(strings1, s);
                int countInStrings2 = countOccurrences(strings2, s);
                intersectionSize += Math.min(countInStrings1, countInStrings2);
            }

// Calculate union
            for (String s : strings1) {
                int countInStrings1 = countOccurrences(strings1, s);
                int countInStrings2 = countOccurrences(strings2, s);
                unionSize += Math.max(countInStrings1, countInStrings2);
            }
            for (String s : strings2) {
                int countInStrings1 = countOccurrences(strings1, s);
                int countInStrings2 = countOccurrences(strings2, s);
                unionSize += Math.max(countInStrings1, countInStrings2);
            }

            unionSize=unionSize-intersectionSize;

            if (unionSize == 0) {
                jaccardSimilarity1 = 0;
            } else {
                jaccardSimilarity1 = (double) intersectionSize / unionSize;
            }
        }
        jaccardSimilarity = Math.round(jaccardSimilarity1 * 1000000.0) / 1000000.0;
        return jaccardSimilarity;
    }


    private static int countOccurrences(String[] array, String element) {
        int count = 0;
        for (String s : array) {
            if (s.equals(element)) {
                count++;
            }
        }
        return count;
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                                      DATA INTEGRATION ASSIGNMENT                                           //
// Calculate the Jaccard similarity of the two String arrays. Note that the Jaccard similarity needs to be    //
// calculated differently depending on the token semantics: set semantics remove duplicates while bag         //
// semantics consider them during the calculation. The solution should be able to calculate the Jaccard       //
// similarity either of the two semantics by respecting the inner bagSemantics flag.                          //
//                                                                                                            //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////