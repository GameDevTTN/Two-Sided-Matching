package com.ylo019.marriageproblem.algorithms;

import java.util.Arrays;
import java.util.List;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import Main.Settings.Format;

public class EfficiencyAdjustedDeferredAcceptance extends AlgorithmAdapter {

	@Override
	public int[] solve(MallowsPreferencesContainer left, MallowsPreferencesContainer right) {
		// TODO Auto-generated method stub
		int[] matching = new int[left.size()];
		boolean[] finalisedStudents = new boolean[left.size()];
		while (arrayHasZeroes(matching)) {
			iteration(left, right, matching);
		//remove non-final matching
			removeCompetitiveItems(left, matching, finalisedStudents);
		}
		
		return matching;
	}
	
	protected void iteration(MallowsPreferencesContainer left, MallowsPreferencesContainer right, int[] partialMatching) {
		int[] reverseMatching = new int[right.size()];
		
		for (int i = 0; i < partialMatching.length; i++) {
			if (partialMatching[i] != 0) {
				reverseMatching[partialMatching[i] - 1] = i + 1;
			}
		}
		
		int[] proposerPointers = new int[left.size()];
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		
		boolean proposalMade = false;
		do {
			proposalMade = false;
			for (int proposer = 0; proposer < left.size(); proposer++) {
				if (partialMatching[proposer] == 0) {
					proposalMade = true;
					int proposee = leftPref.get(proposer).get(proposerPointers[proposer]++);
					if (reverseMatching[proposee - 1] == 0) {
						partialMatching[proposer] = proposee;
						reverseMatching[proposee - 1] = proposer + 1;
					} else {
						if (rightPref.get(proposee - 1).doesPrefers(proposer + 1, reverseMatching[proposee - 1])) {
							partialMatching[reverseMatching[proposee - 1] - 1] = 0;
							reverseMatching[proposee - 1] = proposer + 1;
							partialMatching[proposer] = proposee;
						}
					}
				}
			}
		} while (proposalMade);
	}
	
	protected void removeCompetitiveItems(MallowsPreferencesContainer left, int[] matching, boolean[] finalisedStudents) {
		List<MallowsPreferences> leftPref = left.getPreferences();
		boolean[] notFinalised = new boolean[finalisedStudents.length];
		for (int i = 0; i < matching.length; i++) {
			if (finalisedStudents[i] == true) {
				continue;
			}
			for (int j = 0; j < matching.length; j++) {
				if (finalisedStudents[j] == true) {
					continue;
				}
				if (i == j) {
					continue;
				}
				if (leftPref.get(i).doesPrefers(matching[j], matching[i])) {
					notFinalised[j] = true;
				}
			}
		}
		for (int i = 0; i < finalisedStudents.length; i++) {
			if (finalisedStudents[i]) {
				continue;
			}
			if (notFinalised[i]) {
				matching[i] = 0;
			} else {
				finalisedStudents[i] = true;
			}
		}
	}
	
	@Override
	public String toString() {
		return "EADA";
	}

}
