package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.IND;

import java.util.*;
import java.util.stream.Collectors;

public class INDProfiler {

    /**
     * Discovers all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     * @param relations The relations that should be profiled for inclusion dependencies.
     * @return The list of all non-trivial unary (and n-ary) inclusion dependencies in the provided relations.
     */
    public List<IND> profile(List<Relation> relations, boolean discoverNary) {
        List<IND> inclusionDependencies = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all inclusion dependencies and return them in inclusion dependencies list. The boolean flag       //
        // discoverNary indicates, whether only unary or both unary and n-ary INDs should be discovered. To solve     //
        // this assignment, only unary INDs need to be discovered. Discovering also n-ary INDs is optional.           //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (discoverNary)
            throw new RuntimeException("Sorry, n-ary IND discovery is not supported by this solution.");

        for (int i = 0; i < relations.size(); i++) {
            Relation relation1 = relations.get(i);
            List<Set<String>> sets1 = convertToSets(relation1.getColumns());

            for (int j = 0; j < relations.size(); j++) {
                Relation relation2 = relations.get(j);
                List<Set<String>> sets2 = convertToSets(relation2.getColumns());

                for (int col1 = 0; col1 < sets1.size(); col1++) {
                    Set<String> values1 = sets1.get(col1);

                    for (int col2 = 0; col2 < sets2.size(); col2++) {
                        if (i == j && col1 == col2) {
                            continue;
                        }
                        Set<String> values2 = sets2.get(col2);
                        if (values2.containsAll(values1)) {
                            IND ind = new IND(relation1, col1, relation2, col2);
                            inclusionDependencies.add(ind);
                        }
                    }
                }
            }
        }

        return inclusionDependencies;
    }

    private List<Set<String>> convertToSets(String[][] columns) {
        return Arrays.stream(columns)
                .map(column -> new HashSet<>(new ArrayList<>(List.of(column))))
                .collect(Collectors.toList());
    }
}