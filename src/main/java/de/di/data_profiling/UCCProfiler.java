package de.di.data_profiling;

import de.di.Relation;
import de.di.data_profiling.structures.AttributeList;
import de.di.data_profiling.structures.PositionListIndex;
import de.di.data_profiling.structures.UCC;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UCCProfiler {

    /**
     * Discovers all minimal, non-trivial unique column combinations in the provided relation.
     * @param relation The relation that should be profiled for unique column combinations.
     * @return The list of all minimal, non-trivial unique column combinations in ths provided relation.
     */
    public List<UCC> profile(Relation relation) {
        int numAttributes = relation.getAttributes().length;
        List<UCC> uniques = new ArrayList<>();
        List<PositionListIndex> currentNonUniques = new ArrayList<>();

//      Calculate all unary UCCs and unary non-UCCs
        for (int attribute = 0; attribute < numAttributes; attribute++) {
            AttributeList attributes = new AttributeList(attribute);
            PositionListIndex pli = new PositionListIndex(attributes, relation.getColumns()[attribute]);
            if (pli.isUnique())
                uniques.add(new UCC(relation, attributes));
            else
                currentNonUniques.add(pli);
        }

        int level = 1; //lattice traversal
        while (!currentNonUniques.isEmpty() && level < numAttributes) {
            List<PositionListIndex> nonUniqueNext = new ArrayList<>();
            Set<AttributeList> candidate_unique = new HashSet<>();

            for (int i = 0; i < currentNonUniques.size(); i++) {
                for (int j = i + 1; j < currentNonUniques.size(); j++) {
                    PositionListIndex pli1 = currentNonUniques.get(i);
                    PositionListIndex pli2 = currentNonUniques.get(j);
                    if (pli1.getAttributes().samePrefixAs(pli2.getAttributes())) {
                        AttributeList attributes_combined = pli1.getAttributes().union(pli2.getAttributes());

                        if (attributes_combined.size() == level + 1 && !has_subset(candidate_unique, attributes_combined)) {
                            PositionListIndex PLI_combined = pli1.intersect(pli2);
                            if (PLI_combined.isUnique()) {
                                if (is_minimal(uniques, attributes_combined)) {
                                    uniques.add(new UCC(relation, attributes_combined));
                                    candidate_unique.add(attributes_combined);
                                }
                            } else {
                                nonUniqueNext.add(PLI_combined);
                            }
                        }
                    }
                }
            }
            currentNonUniques = nonUniqueNext;
            level++;
        }
        return uniques;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                      DATA INTEGRATION ASSIGNMENT                                           //
        // Discover all unique column combinations of size n>1 by traversing the lattice level-wise. Make sure to     //
        // generate only minimal candidates while moving upwards and to prune non-minimal ones. Hint: The class       //
        // AttributeList offers some helpful functions to test for sub- and superset relationships. Use PLI           //
        // intersection to validate the candidates in every lattice level. Advances techniques, such as random walks, //
        // hybrid search strategies, or hitting set reasoning can be used, but are mandatory to pass the assignment.  //
        //                                                                                                            //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private boolean has_subset(Set<AttributeList> uniqueCandidates, AttributeList attributes) {
        for (AttributeList candidate : uniqueCandidates) {
            if (candidate.subsetOf(attributes)) {
                return true;
            }
        }
        return false;
    }

    private boolean is_minimal(List<UCC> uniques, AttributeList candidate) {
        for (UCC ucc : uniques) {
            if (ucc.getAttributeList().subsetOf(candidate)) {
                return false;
            }
        }
        return true;
    }
}