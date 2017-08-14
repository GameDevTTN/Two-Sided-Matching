package com.ylo019.marriageproblem.algorithms;

import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;

public abstract class AlgorithmAdapter implements iAlgorithm {
	
//	protected int[][] convertPreferenceProfile(PreferenceProfile profile) {
//		int[][] output = new int[profile.size()][profile.objectSize()];
//		Permutation[] permutations = profile.getProfiles();
//		if (output.length != permutations.length) {
//			throw new RuntimeException("AlgorithmAdapter, convertPreferenceProfile: output and permutation length does not match");
//		}
//		for (int i = 0; i < output.length; i++) {
//			output[i] = permutations[i].getArray();
//		}
//		return output;
//	}

	
	protected boolean arrayHasZeroes(int[] array) {
		for (int i : array) {
			if (i == 0) {
				return true;
			}
		}
		return false;
	}
}
