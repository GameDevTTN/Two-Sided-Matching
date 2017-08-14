package com.ylo019.marriageproblem.algorithms;

import java.util.Arrays;
import java.util.List;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;

public abstract class DeferredAcceptanceWithRounds extends AlgorithmAdapter { //for DA & Shanghai/Boston
	
	private int roundNumber = 0;
	
	protected final void incrementRoundCounter() {
		roundNumber++;
	}
	
	protected final int getRoundNumber() {
		return roundNumber;
	}
	
	protected abstract boolean isNewRound();

	@Override
	public int[] solve(MallowsPreferencesContainer left, MallowsPreferencesContainer right) {
		// TODO Auto-generated method stub
		roundNumber = 0;
		int[] matching = new int[left.size()];
		int[] reverseMatching = new int[right.size()];
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		
		int[] proposerPointers = new int[left.size()];
		boolean[] reverseIsFinalMatching = new boolean[right.size()];
		
		while (arrayHasZeroes(matching)) {
			if (isNewRound()) {
				for (int i = 0; i < reverseMatching.length; i++) {
					reverseIsFinalMatching[i] = (reverseMatching[i] != 0);
				}
			}
			incrementRoundCounter();
			boolean proposalMade = false;
			do {
				proposalMade = false;
				for (int proposer = 0; proposer < left.size(); proposer++) {
					if (matching[proposer] == 0 && proposerPointers[proposer] < getRoundNumber()) {
						proposalMade = true;
						int proposee = leftPref.get(proposer).get(proposerPointers[proposer]++);
						if (reverseMatching[proposee - 1] == 0) {
							matching[proposer] = proposee;
							reverseMatching[proposee - 1] = proposer + 1;
						} else {
							if (!reverseIsFinalMatching[proposee - 1] && rightPref.get(proposee - 1).doesPrefers(proposer + 1, reverseMatching[proposee - 1])) {
								matching[reverseMatching[proposee - 1] - 1] = 0;
								reverseMatching[proposee - 1] = proposer + 1;
								matching[proposer] = proposee;
							}
						}
					}
				}
			} while (proposalMade);

			//increment round number
			//check if tentative become final
		}
		
		return matching;
	}

}
