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
import MatchingAlgorithm.Auxiliary.iIterator;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import UtilityModels.ExponentialModel;
import UtilityModels.iUtilitiesModel;
import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;
import java.util.Arrays;

/**
 *
 * @author ylo019
 */
public class TwoSidedHungarianWrapper implements iTwoSidedAlgorithm {
    
    private iUtilitiesModel model = new ExponentialModel(0.0);

    @Override
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee) {
        double[][] preferences = new double[proposer.size()][proposee.size()];
        Permutation[] profiles = proposer.getProfiles();
        for (int i = 0; i < proposer.size(); i++) {
            iIterator iter = profiles[i].getIterator();
            applyWeights(preferences[i], iter);
        }
        profiles = proposee.getProfiles();
        double[][] itemPref = new double[proposee.size()][proposer.size()];
        for (int i = 0; i < proposee.size(); i++) {
            iIterator iter = profiles[i].getIterator();
            applyWeights(itemPref[i], iter);
        }
        for (int i = 0; i < itemPref.length; i++) {
            for (int j = 0; j < itemPref[i].length; j++) {
                preferences[j][i] += itemPref[i][j];
            }
        }
        ProbabilityMatrix pm = new ProbabilityMatrix(proposer.size(), proposee.size());
        try {
            pm.addMatching(new Permutation(proposer.size(), proposee.size(), new HungarianAlgorithm(preferences).execute()));
        } catch (InvalidPreferenceException ex) {
            throw new RuntimeException("HungarianAlgorithmWrapper: solve(): matching generated is invalid");
        }
        return pm;
    }
    
    protected void applyWeights(double[] pref, iIterator iter) {
        double[] utilities = model.getUtilities(iter.size());
        int index = 0;
        while (iter.hasNext()) {
            pref[iter.getNext() - 1] += (utilities[0] - utilities[index++]); //the Hungarian Algorithm look for min cost - I need a matching that generate max borda
        }
    }

    @Override
    public String getName() {
        return "HA (sum)";
    }
    
}
