package com.ylo019.twosidedmatching;

import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import MatchingAlgorithm.iAlgorithm;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ylo019
 */
public interface iTwoSidedAlgorithm extends iAlgorithm{
    
    public iProbabilityMatrix solve(PreferenceProfile proposer, PreferenceProfile proposee);
    
}
