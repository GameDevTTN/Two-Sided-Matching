/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import MatchingAlgorithm.Auxiliary.InvalidPreferenceException;
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.ProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ylo019
 */
public class GaleShapley implements iTwoSidedAlgorithm {

    @Override
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee) {
        iProfileIterator proposerIter = proposer.getIterator();
        int[] matchings = new int[proposer.size()]; //proposee's pref
        for (int i = 0; i < matchings.length; i++) {
            int actor = i + 1;
            do {
                int target = proposerIter.getNext(actor);
                iProfileIterator proposeeIter = proposee.getIterator();
                while (true) {
                    int proposeeCurrent = proposeeIter.getNext(target);
                    if (proposeeCurrent == actor) {
                        int temp = actor;
                        actor = matchings[target - 1];
                        matchings[target - 1] = temp;
                        break;
                    } else if (proposeeCurrent == matchings[target - 1]) {
                        break;
                    }
                }
            }while (actor != 0);
        }
        //invert the matchings
        int[] out = new int[proposer.size()];
        for (int i = 0; i < out.length; i++) {
            out[matchings[i] - 1] = i + 1;
        }
        ProbabilityMatrix matrix = new ProbabilityMatrix(proposer.size(), proposee.size());
        try {
            matrix.addMatching(new Permutation(proposer.size(), proposee.size(), out));
        } catch (InvalidPreferenceException ex) {
            System.out.println("GaleShapley: fail to add matching");
        }
        return matrix;
    }

    @Override
    public String getName() {
        return "Gale Shapley";
    }
    
}
