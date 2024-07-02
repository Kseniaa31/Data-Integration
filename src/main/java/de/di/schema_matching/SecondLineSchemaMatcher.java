package de.di.schema_matching;

import de.di.schema_matching.structures.CorrespondenceMatrix;
import de.di.schema_matching.structures.SimilarityMatrix;
import java.util.Arrays;

public class SecondLineSchemaMatcher {

    /**
     * Translates the provided similarity matrix into a binary correspondence matrix by selecting possibly optimal
     * attribute correspondences from the similarities.
     * @param similarityMatrix A matrix of pair-wise attribute similarities.
     * @return A CorrespondenceMatrix of pair-wise attribute correspondences.
     */
    public CorrespondenceMatrix match(SimilarityMatrix similarityMatrix) {
        double[][] simMatrix = similarityMatrix.getMatrix();
        int[][] corrMatrix = null;

        // Stable Marriage algorithm
        int[] assignments = stableMarriage(simMatrix);
        corrMatrix = assignmentArray2correlationMatrix(assignments, simMatrix);
        return new CorrespondenceMatrix(corrMatrix, similarityMatrix.getSourceRelation(), similarityMatrix.getTargetRelation());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Translate the similarity matrix into a binary correlation matrix by implementing either the StableMarriage //
        // algorithm or the Hungarian method.                                                                         //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    /**
     * Translate an array of source assignments into a correlation matrix. For example, [0,3,2] maps 0->1, 1->3, 2->2
     * and, therefore, translates into [[1,0,0,0][0,0,0,1][0,0,1,0]].
     * @param sourceAssignments The list of source assignments.
     * @param simMatrix The original similarity matrix; just used to determine the number of source and target attributes.
     * @return The correlation matrix extracted form the source assignments.
     */
    private int[][] assignmentArray2correlationMatrix(int[] sourceAssignments, double[][] simMatrix) {
        int[][] corrMatrix = new int[simMatrix.length][];
        for (int i = 0; i < simMatrix.length; i++) {
            corrMatrix[i] = new int[simMatrix[i].length];
            for (int j = 0; j < simMatrix[i].length; j++)
                corrMatrix[i][j] = 0;
        }
        for (int i = 0; i < sourceAssignments.length; i++)
            if (sourceAssignments[i] >= 0)
                corrMatrix[i][sourceAssignments[i]] = 1;
        return corrMatrix;
    }

    private int[] stableMarriage(double[][] preference_matrix) {
        int n = preference_matrix.length;
        int m = preference_matrix[0].length;

        // Conversion of similarity to ranking matrix
        int[][] ranking_matrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            int[] sorted_preference = new int[m];
            for (int j = 0; j < m; j++) {
                sorted_preference[j] = j;
            }
            
            for (int j = 0; j < m; j++) {
                for (int k = j + 1; k < m; k++) {
                    if (preference_matrix[i][sorted_preference[j]] < preference_matrix[i][sorted_preference[k]]) {
                        int temp = sorted_preference[j];
                        sorted_preference[j] = sorted_preference[k];
                        sorted_preference[k] = temp;
                    }
                }
            }
            for (int j = 0; j < m; j++) {
                ranking_matrix[i][sorted_preference[j]] = j;
            }
        }

        int[] proposer_match = new int[n];
        int[] receiver_match = new int[m];
        Arrays.fill(proposer_match, -1);
        Arrays.fill(receiver_match, -1);

        boolean[] proposer_free = new boolean[n];
        Arrays.fill(proposer_free, true);

        int count_free = n;
        while (count_free > 0) {
            int proposer = -1;
            for (int i = 0; i < n; i++) {
                if (proposer_free[i]) {
                    proposer = i;
                    break;
                }
            }

            int[] sorted_preference = new int[m];
            for (int j = 0; j < m; j++) {
                sorted_preference[j] = j;
            }

            for (int j = 0; j < m; j++) {
                for (int k = j + 1; k < m; k++) {
                    if (preference_matrix[proposer][sorted_preference[j]] < preference_matrix[proposer][sorted_preference[k]]) {
                        int temp = sorted_preference[j];
                        sorted_preference[j] = sorted_preference[k];
                        sorted_preference[k] = temp;
                    }
                }
            }

            for (int j = 0; j < m && proposer_free[proposer]; j++) {
                int receiver = sorted_preference[j];
                if (receiver_match[receiver] == -1) {
                    receiver_match[receiver] = proposer;
                    proposer_match[proposer] = receiver;
                    proposer_free[proposer] = false;
                    count_free--;
                } else {
                    int current_proposer = receiver_match[receiver];
                    if (ranking_matrix[proposer][receiver] < ranking_matrix[current_proposer][receiver]) {
                        receiver_match[receiver] = proposer;
                        proposer_match[proposer] = receiver;
                        proposer_free[proposer] = false;
                        proposer_free[current_proposer] = true;
                    }
                }
            }
        }
        return proposer_match;
    }
}