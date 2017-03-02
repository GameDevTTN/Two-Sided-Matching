/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import MatchingAlgorithm.Auxiliary.InvalidPreferenceException;
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.ProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import Pair.Pair;
import ordinalpreferencegenerator.StaticFunctions;

/**
 *
 * @author ylo019
 */
public abstract class SymmetrisedTwoSidedAlgorithm implements iTwoSidedAlgorithm {

    @Override
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee) {
        int proposerSize = proposer.size();
        int proposeeSize = proposee.size();
        ProbabilityMatrix ipm = new ProbabilityMatrix(proposerSize, proposeeSize);
        Permutation[] priority = null;
        boolean fixOrder = false;
        if (fixOrder) {
            priority = new Permutation[1];
            int[] order = new int[proposerSize];
            for (int i = 0; i < order.length ; i++) {
                order[i] = i + 1;
            }
            try {
                priority[0] = new Permutation(proposerSize, proposeeSize, order);
            } catch (InvalidPreferenceException ex) {
                System.out.println("NoMemoryStack: solve: order is wrong");
            }
        } else {
            priority = StaticFunctions.permutations(proposerSize);
        }
        //    ----//
        for (Permutation p : priority) {
            PostBox.broadcast(MessageType.PROCESS, new Pair<>("Priority", p));
            Permutation matching = solve(p, proposer, proposee);
            ipm.addMatching(matching);
            PostBox.broadcast(MessageType.PROCESS, new Pair<>("Matching", matching));
        }
        return ipm;
    }
    
    protected abstract Permutation solve(Permutation order, PreferenceProfile proposer, PreferenceProfile proprosee);

}
