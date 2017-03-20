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
import MatchingAlgorithm.Auxiliary.Restrictions.RestrictionFactoryAdaptor;
import MatchingAlgorithm.Auxiliary.Restrictions.iRestrictionFactory;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import java.util.Arrays;

/**
 *
 * @author ylo019
 */
public class GeneralisedImmediateAndDeferredAcceptance implements iTwoSidedAlgorithm{
    
    private int param;
    private boolean isInfinity;
    private iRestrictionFactory factory;
    
    public GeneralisedImmediateAndDeferredAcceptance(int param) {
        this(param, new RestrictionFactoryAdaptor());
    }
    
    public GeneralisedImmediateAndDeferredAcceptance(int param, iRestrictionFactory factory) {
        if (param <= 0) {
            isInfinity = true;
            param = 1;
        } else {
            isInfinity = false;
            this.param = param;
        }
            this.factory = factory;
    }
    
    public GeneralisedImmediateAndDeferredAcceptance() {
        this(new RestrictionFactoryAdaptor());
    }
    
    public GeneralisedImmediateAndDeferredAcceptance(iRestrictionFactory factory) {
        isInfinity = true;
        param = 1;
        this.factory = factory;
    }

    @Override
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee) {
        factory.clearRestriction(proposer.size(), proposee.size());
        iProfileIterator proposerIter = proposer.getIterator();
        int[] proposerMatch = new int[proposer.size()];
        int[] proposeeMatch = new int[proposee.size()]; //proposee's pref
        boolean[] proposeeLocked = new boolean[proposee.size()];
        int unmatchedCounts = proposer.size();
        int round = 0;
        while (unmatchedCounts > 0) {
            round++;
            if (!isInfinity && round % param == 0) {
                for (int i = 0; i < proposeeMatch.length; i++) {
                    if (proposeeMatch[i] != 0) {
                        proposeeLocked[i] = true;
                    }
                }
            }
            for (int i = 0; i < proposeeMatch.length; i++) {
                int actor = i + 1;
                if (proposerMatch[actor - 1] != 0) {
                    continue;
                }
                int target = proposerIter.getNext(actor);
                //System.out.printf("Actor %d Target %d: %b\n", actor, target, checkRestriction(actor, target, matchings[target - 1]));
                if (!factory.checkRestriction(actor, target, proposeeMatch[target - 1])) {
                    continue;
                }
                iProfileIterator proposeeIter = proposee.getIterator();
                while (true) {
                    if (proposeeLocked[target - 1]) {
                        break;
                    }
                    int proposeeCurrent = proposeeIter.getNext(target);
                    if (proposeeCurrent == actor) {
                        factory.updateRestriction(actor, target, proposeeMatch[target - 1]);
                        if (proposeeMatch[target - 1] == 0) {
                            unmatchedCounts -= 1;
                        } else {
                            proposerMatch[proposeeMatch[target - 1] - 1] = 0;
                        }
                        proposerMatch[actor - 1] = target;
                        proposeeMatch[target - 1] = actor;
                        break;
                    } else if (proposeeCurrent == proposeeMatch[target - 1]) {
                        break;
                    }
                }
            }
        }
        ProbabilityMatrix matrix = new ProbabilityMatrix(proposer.size(), proposee.size());
        try {
            matrix.addMatching(new Permutation(proposer.size(), proposee.size(), proposerMatch));
        } catch (InvalidPreferenceException ex) {
            System.out.println("GI&DA: fail to add matching");
        }
        return matrix;
    }

    @Override
    public String getName() {
        if (isInfinity) {
            return "Gale Shapley Queue";
        }
        if (param == 1) {
            return "Boston Mechanism";
        } else {
            return "Shanghai Mechanism " + param;
        }
    }
    
}
