/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import Main.Observers.Auxiliary.PreferenceType;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import MatchingAlgorithm.Taxonomy.GenericImplementation;

/**
 *
 * @author ylo019
 */
public class TwoSidedTaxonomy implements iTwoSidedAlgorithm {
    
    private GenericImplementation underlyingAlgorithm;
    private boolean isLeft;
    
    public TwoSidedTaxonomy(GenericImplementation algo, PreferenceType side, boolean fixedOrder) {
        if (PreferenceType.ONE_SIDED == side || side == null) {
            System.out.println("TwoSidedTaxonomy(): side is One_Sided or null");
            return;
        }
        if (algo != null) {
            algo.setFixInitialOrder(fixedOrder);
            underlyingAlgorithm = algo;
        }
        if (PreferenceType.TWO_SIDED_PROPOSER == side) {
            isLeft = true;
        } else if (PreferenceType.TWO_SIDED_PROPOSEE == side) {
            isLeft = false;
        }
    }

    @Override
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee) {
        if (isLeft) {
            return underlyingAlgorithm.solve(proposer, proposer.size(), proposer.objectSize());
        } else {
            iProbabilityMatrix result = underlyingAlgorithm.solve(proposee, proposee.size(), proposee.objectSize());
            return result.invert();
        }
    }

    @Override
    public String getName() {
        return underlyingAlgorithm.getName() + "(" + (isLeft? "Left" : "Right") + ")";
    }
    
    
}
