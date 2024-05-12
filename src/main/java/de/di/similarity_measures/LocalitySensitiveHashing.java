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
        if (strings1 == null || strings2 == null) {
            return Double.NaN; // Return NaN if any of the input arrays is null
        }

        double lshJaccard = 0;
        String[] signature1 = new String[this.minHashFunctions.size()];
        String[] signature2 = new String[this.minHashFunctions.size()];

        // Generate LSH signatures for strings1 and strings2
        for (int i = 0; i < this.minHashFunctions.size(); i++) {
//            signature1[i] = this.minHashFunctions.get(i).MinHash(strings1);
//            signature2[i] = this.minHashFunctions.get(i).MinHash(strings2);
        }

        // Compute Jaccard similarity between signatures
        lshJaccard = jaccardSimilarity(signature1, signature2);

        return lshJaccard;
    }

    // Compute Jaccard similarity between two sets represented as arrays of strings
    private double jaccardSimilarity(String[] set1, String[] set2) {
        Set<String> intersection = new HashSet<>();
        Set<String> union = new HashSet<>();

        // Add elements from set1 to union
        for (String s : set1)
            union.add(s);

        // Add elements from set2 to union, and count intersection
        for (String s : set2) {
            if (!union.add(s)) // If already in union, it's an intersection
                intersection.add(s);
        }

        // Calculate Jaccard similarity
        double jaccardSimilarity;
        if (bagSemantics) {
            jaccardSimilarity = (double) intersection.size() / union.size();
        } else {
            int intersectionSize = intersection.size();
            int unionSize = set1.length + set2.length - intersectionSize;
            jaccardSimilarity = (double) intersectionSize / unionSize;
        }
        return jaccardSimilarity;
    }

    // Assume MinHash class exists with a minHash method
    private static class MinHash {
        private final int hashIndex; // Add a field to hold the hash index
         public MinHash(int hashIndex) {
            this.hashIndex = hashIndex; // Initialize the hash index
        }
        

        // Rest of the class...
    }

}

/////////////////////// /////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Calculate the two signatures by using the internal MinHash functions. Then, use the signatures to          //
        // approximate the Jaccard similarity.                                                                        //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////