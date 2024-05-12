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
            Set<String> set_string1, set_string2;
    //        int union_size = 0;
    //        int intersection_size = 0;

            if (!bagSemantics) { //it means we have set semantics
                int union_size = 0;
                int intersection_size = 0;
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

                if (strings1.length > 1 && strings2.length > 1) {
                    int union_size;
                    int intersection_size = 0;
                    int[] count1 = countOccurrences(strings1);
                    int[] count2 = countOccurrences(strings2);
                    int maxSize = Math.max(count1.length, count2.length);
                    count1 = Arrays.copyOf(count1, maxSize);
                    count2 = Arrays.copyOf(count2, maxSize);
                    for (int i = 0; i < count1.length; i++) {
                        intersection_size += Math.min(count1[i], count2[i]);
                    }
                    List<String> union = new ArrayList<>();
                    union.addAll(Arrays.asList(strings1));
                    union.addAll(Arrays.asList(strings2));
                    union_size = union.size();
                    if (union_size == 0) {
                        jaccardSimilarity = 0;
                    } else {
                        jaccardSimilarity = (double) intersection_size / union_size;
                    }
                }

                else {
                    int union_size = 0;
                    int intersection_size = 0;
                    Set<String> intersection = new HashSet<>(Arrays.asList(strings1)); //calculate intersection
                    intersection.retainAll(Arrays.asList(strings2));
                    intersection_size = intersection.size();
                    List<String> union = new ArrayList<>();
                    union.addAll(Arrays.asList(strings1));
                    union.addAll(Arrays.asList(strings2));
                    union_size = union.size();
                    if (union_size == 0) {
                        jaccardSimilarity = 0;
                    } else {
                        jaccardSimilarity = (double) intersection_size / union_size;
                    }
                }
            }
            return jaccardSimilarity;
        }

    private int[] countOccurrences(String[] strings) {
        // Find the maximum character value to determine the size of the count array
        int maxChar = 0;
        for (String str : strings) {
            for (char c : str.toCharArray()) {
                maxChar = Math.max(maxChar, c);
            }
        }
        int[] count = new int[maxChar + 1]; // Adjust size based on maximum character value
        for (String str : strings) {
            for (char c : str.toCharArray()) {
                count[c]++;
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