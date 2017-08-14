package com.ylo019.marriageproblem.algorithms;

import java.util.List;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;

public class DeferredAcceptanceWithCircuitBreaker extends AlgorithmAdapter {
	
	private int proposalCount;
	private int agentCount;
	
	public DeferredAcceptanceWithCircuitBreaker(int numberOfProposals) {
		this(numberOfProposals, 1);
	}
	
	public DeferredAcceptanceWithCircuitBreaker(int numberOfProposals, int numberOfAgents) {
		proposalCount = numberOfProposals;
		agentCount = numberOfAgents;
	}

	@Override
	public int[] solve(MallowsPreferencesContainer left, MallowsPreferencesContainer right) {
		// TODO Auto-generated method stub
		int[] matching = new int[left.size()];
		int[] reverseMatching = new int[right.size()];
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		
		int[] proposerPointers = new int[left.size()];
		int[] numberOfProposalsByProposer = new int[left.size()];
		boolean[] reverseIsFinalMatching = new boolean[right.size()];
		
		for (int i = 0; i < matching.length; i++) {
			int proposer = i + 1;
			while (proposer != 0) { //breaks only when proposer matches to an unmatched proposee
				int proposee = leftPref.get(proposer - 1).get(proposerPointers[proposer - 1]++);
				if (reverseIsFinalMatching[proposee - 1]) {
					//if proposee is not in O, no proposal made. (i.e. proposee has been removed from the set of available items)
					continue;
				}
				numberOfProposalsByProposer[proposer - 1]++;
				//if proposee if unmatched
				if (reverseMatching[proposee - 1] == 0) {
					matching[proposer - 1] = proposee;
					reverseMatching[proposee - 1] = proposer;
					proposer = 0; //break
				} else {
					if (rightPref.get(proposee - 1).doesPrefers(proposer, reverseMatching[proposee - 1])) {
						int nextAgent = reverseMatching[proposee - 1];
						matching[reverseMatching[proposee - 1] - 1] = 0;
						reverseMatching[proposee - 1] = proposer;
						matching[proposer - 1] = proposee;
						proposer = nextAgent;
					}
				}
				
				int exceedCount = 0;
				for (int j = 0; j < numberOfProposalsByProposer.length; j++) {
					if (numberOfProposalsByProposer[j] >= proposalCount) {
						exceedCount++;
					}
				}
				if (exceedCount >= agentCount) { //finalise the matchings
					for (int kIndex = 0; kIndex < numberOfProposalsByProposer.length; kIndex++) {
						if (numberOfProposalsByProposer[kIndex] >= proposalCount) {
							numberOfProposalsByProposer[kIndex] = 0;
						}
					}
					for(int j = 0; j < reverseMatching.length; j++) {
						reverseIsFinalMatching[j] = (reverseMatching[j] != 0);
					}
				}
			}
		}
		
		return matching;
	}
	
	@Override
	public String toString() {
		return (proposalCount == 1 && agentCount == 1 ? "SD" : ("DACB k = " + proposalCount + (agentCount != 1 ? " j = " + agentCount : "")));
	}

}
