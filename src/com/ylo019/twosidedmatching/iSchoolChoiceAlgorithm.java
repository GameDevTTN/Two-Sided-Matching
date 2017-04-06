/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.iAlgorithm;

/**
 *
 * @author ylo019
 */
public interface iSchoolChoiceAlgorithm extends iAlgorithm {
    
    public int[] solve(PreferenceProfile left, PreferenceProfile right, int[] rightCapacity);
    
}
