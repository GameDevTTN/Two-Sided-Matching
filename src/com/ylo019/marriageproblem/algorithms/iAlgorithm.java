package com.ylo019.marriageproblem.algorithms;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;

public interface iAlgorithm {

	public int[] solve(MallowsPreferencesContainer left, MallowsPreferencesContainer right);
	
}
