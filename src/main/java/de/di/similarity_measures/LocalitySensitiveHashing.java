package de.di.similarity_measures;

import de.di.similarity_measures.helper.MinHash;
import de.di.similarity_measures.helper.Tokenizer;

import java.util.*;

public class LocalitySensitiveHashing implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    // The MinHash functions that are used to calculate the LSH signatures.
    private final List<MinHash> minHashFunctions;

    public LocalitySensitiveHashing(final Tokenizer tokenizer, final boolean bagSemantics, final int numHashFunctions) {
        assert (tokenizer.getTokenSize() >= numHashFunctions);

        this.tokenizer = tokenizer;
        this.bagSemantics = bagSemantics;
        this.minHashFunctions = new ArrayList<>(numHashFunctions);
        for (int i = 0; i < numHashFunctions; i++)
            this.minHashFunctions.add(new MinHash(i));
    }

    /**
     * Calculates the LSH similarity of the two input strings.
     * The LHS algorithm calculates the LHS signatures by first tokenizing the input strings and then applying its
     * internal MinHash functions to the tokenized strings. Then, it uses the two signatures to approximate the Jaccard
     * similarity of the two strings with their signatures by simply applying the Jaccard algorithm on the two signatures.
     *
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The LSH similarity (= Jaccard approximation) of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the LSH similarity of the two input string arrays.
     * The LHS algorithm calculates the LHS signatures by applying its internal MinHash functions to the two input string
     * lists. Then, it uses the two signatures to approximate the Jaccard similarity of the two strings with their
     * signatures by simply applying the Jaccard algorithm on the two signatures.
     *
     * @param strings1 The first string argument for the similarity calculation.
     * @param strings2 The second string argument for the similarity calculation.
     * @return The LSH similarity (= Jaccard approximation) of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double lshJaccard;
        String[] signature1 = new String[this.minHashFunctions.size()];
        String[] signature2 = new String[this.minHashFunctions.size()];
        int numHashFunctions = minHashFunctions.size();

        if (strings1.length == 1 && strings2.length == 1) {
            strings1 = tokenizer.tokenize(strings1[0]);
            strings2 = tokenizer.tokenize(strings2[0]);
        }

        // Generate signatures for strings1
        for (int i = 0; i < numHashFunctions; i++) {
            signature1[i] = minHashFunctions.get(i).hash(strings1);
        }

        // Generate signatures for strings2
        for (int i = 0; i < numHashFunctions; i++) {
            signature2[i] = minHashFunctions.get(i).hash(strings2);
        }

        if (bagSemantics) {
            Map<String, Integer> sig1 = get_Tokens_Freq(signature1);
            Map<String, Integer> sig2 = get_Tokens_Freq(signature2);

            int intersection_size = 0;
            int union_size = 0;

            for (String token : sig1.keySet()) {
                intersection_size += Math.min(sig1.get(token), sig2.getOrDefault(token, 0)); //get intersection size
                union_size += sig1.get(token); //get part of union size
            }

            for (String token : sig2.keySet()) {
                union_size += sig2.get(token); //complete union size with elements from second list
            }

            if (union_size == 0) {
                lshJaccard = 0;
            } else {
                lshJaccard = (double) intersection_size / union_size;
            }
        }
        else {
            int intersection ;
            int union;

            // Set semantics: Use sets to calculate intersection and union
            Set<String> set1 = new HashSet<>(Arrays.asList(signature1));
            Set<String> set2 = new HashSet<>(Arrays.asList(signature2));

            // Calculate intersection
            Set<String> intersectionSet = new HashSet<>(set1);
            intersectionSet.retainAll(set2);
            intersection = intersectionSet.size();

            // Calculate union
            Set<String> unionSet = new HashSet<>(set1);
            unionSet.addAll(set2);
            union = unionSet.size();

            // Calculate Jaccard similarity
            lshJaccard = (double) intersection / union;
        }
        return lshJaccard;
    }

    private Map<String, Integer> get_Tokens_Freq(String[] tokens) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String token : tokens) {
            freqMap.put(token, freqMap.getOrDefault(token, 0) + 1);
        }
        return freqMap;
    }
}
/////////////////////// /////////////////////////////////////////////////////////////////////////////////////////
//                                      DATA INTEGRATION ASSIGNMENT                                           //
// Calculate the two signatures by using the internal MinHash functions. Then, use the signatures to          //
// approximate the Jaccard similarity.                                                                        //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////