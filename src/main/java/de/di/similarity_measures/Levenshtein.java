package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // The choice of whether Levenshtein or DamerauLevenshtein should be calculated.
    private final boolean withDamerau;

    /**
     * Calculates the Levenshtein similarity of the two input strings.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The (Damerau) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String string1, final String string2) {
        double levenshteinSimilarity;
        int levenshteinDistance;

        int[] upperupperLine = new int[string1.length() + 1];   // line for Damerau lookups
        int[] upperLine = new int[string1.length() + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[string1.length() + 1];        // line to be filled next by the algorithm

        if (!withDamerau) {
            for (int i = 0; i <= string1.length(); i++)
                upperLine[i] = i; //initialize first row of table (number of edits needed to transform an empty string to string1)
            for (int i = 1; i <= string2.length(); i++) {
                lowerLine[0] = i; //initialize lowerLine for each row of table (number of edits needed to transform an empty string to string2)
                for (int j = 1; j <= string1.length(); j++) {
                    int cost = (string1.charAt(j - 1) == string2.charAt(i - 1)) ? 0 : 1; //if the characters at given position match --> cost=0, else--> cost=1
                    lowerLine[j] = Math.min(upperLine[j - 1] + cost, Math.min(upperLine[j] + 1, lowerLine[j - 1] + 1));
                    //lowerLine value is the minimum of 3 operations (insertion, deletion, substitution)
                }
                int[] c = upperLine;
                upperLine = lowerLine;
                lowerLine = c;
                // Swap the arrays to use in the next iteration to avoid copying them
            }
        }
        else {
            for (int i = 0; i <= string1.length(); i++) {
                upperupperLine[i] = i;
                upperLine[i] = i;
            }
            for (int i = 0; i <= string1.length(); i++)
                upperLine[i] = i;
            for (int i = 1; i <= string2.length(); i++) {
                lowerLine[0] = i;
                for (int j = 1; j <= string1.length(); j++) {
                    int cost = (string1.charAt(j - 1) == string2.charAt(i - 1)) ? 0 : 1;
                    lowerLine[j] = Math.min(upperLine[j - 1] + cost, Math.min(upperLine[j] + 1, lowerLine[j - 1] + 1));
                    if (i > 1 && j > 1 && string1.charAt(j - 1) == string2.charAt(i - 2) && string1.charAt(j - 2) == string2.charAt(i - 1)) {
                        //Damerau: check for the possibility of transposition
                        lowerLine[j] = Math.min(lowerLine[j], upperupperLine[j - 2] + cost);
                        //check for the lowest cost considering the cost found before and the transposition
                    }
                }
                int[] c = upperupperLine;
                upperupperLine = upperLine;
                upperLine = lowerLine;
                lowerLine = c;
            }
        }
        levenshteinDistance = upperLine[string1.length()]; //the value found in the last column of the last row

        if (string1.isEmpty() || string2.isEmpty()) {
            levenshteinSimilarity = 0;
        } else {
            levenshteinSimilarity = 1.0 - (double) levenshteinDistance / Math.max(string1.length(), string2.length());
        }

        return levenshteinSimilarity;
    }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm here first, then copy the code   //
        // to the String tuple function and adjust it a bit to work on the arrays - the algorithm is the same.        //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Calculates the Levenshtein similarity of the two input string lists.
     * The Levenshtein similarity is defined as "1 - normalized Levenshtein distance".
     * For string lists, we consider each list as an ordered list of tokens and calculate the distance as the number of
     * token insertions, deletions, replacements (and swaps) that transform one list into the other.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The (multiset) Levenshtein similarity of the two arguments.
     */
    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity;
        int levenshteinDistance;

        int[] upperupperLine = new int[strings1.length + 1];   // line for Damerau lookups
        int[] upperLine = new int[strings1.length + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[strings1.length + 1];        // line to be filled next by the algorithm

        if(!withDamerau) {
            for (int i = 0; i <= strings1.length; i++)
                upperLine[i] = i;
            for (int i = 1; i <= strings2.length; i++) {
                lowerLine[0] = i;
                for (int j = 1; j <= strings1.length; j++) {
                    int cost = (Objects.equals(strings1[j - 1], strings2[i - 1])) ? 0 : 1;
                    lowerLine[j] = Math.min(upperLine[j - 1] + cost, Math.min(upperLine[j] + 1, lowerLine[j - 1] + 1));
                }
                int[] temp = upperLine;
                upperLine = lowerLine;
                lowerLine = temp;
            }

        }
        else {
            for (int i = 0; i <= strings1.length; i++) {
                upperupperLine[i] = i;
                upperLine[i] = i;
            }
            for (int i = 0; i <= strings1.length; i++)
                upperLine[i] = i;
            for (int i = 1; i <= strings2.length; i++) {
                lowerLine[0] = i;
                for (int j = 1; j <= strings1.length; j++) {
                    int cost = (strings1[j - 1].equals(strings2[i - 1])) ? 0 : 1;
                    lowerLine[j] = Math.min(upperLine[j - 1] + cost, Math.min(upperLine[j] + 1, lowerLine[j - 1] + 1));
                    // Damerau extra condition
                    if (i > 1 && j > 1 && strings1[j - 1].equals(strings2[i - 2]) && strings1[j - 2].equals(strings2[i - 1])) {
                        lowerLine[j] = Math.min(lowerLine[j], upperupperLine[j - 2] + cost);
                    }
                }
                int[] temp = upperupperLine;
                upperupperLine = upperLine;
                upperLine = lowerLine;
                lowerLine = temp;
            }
        }
        levenshteinDistance = upperLine[strings1.length];
        if (strings1.length == 0 || strings2.length == 0) {
                     levenshteinSimilarity = 0;
                 } else {
                     levenshteinSimilarity = 1.0 - (double) levenshteinDistance / Math.max(strings1.length, strings2.length);
                 }
                 return levenshteinSimilarity;
             }
}
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Use the three provided lines to successively calculate the Levenshtein matrix with the dynamic programming //
        // algorithm. Depending on whether the inner flag withDamerau is set, the Damerau extension rule should be    //
        // used during calculation or not. Hint: Implement the Levenshtein algorithm above first, then copy the code  //
        // to this function and adjust it a bit to work on the arrays - the algorithm is the same.                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////