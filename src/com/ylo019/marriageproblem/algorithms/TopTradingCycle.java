package com.ylo019.marriageproblem.algorithms;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import Graph.GraphImpl;

public class TopTradingCycle extends AlgorithmAdapter {
	
	public static void main(String[] args) {
		//debug
		System.out.println(LocalDateTime.now());
		int[] paretoInefficencyCount = new int[7];
		//int[] bordaScore;
		int[] blockingPairs = new int[7];
		int[][] rankDistribution = new int[7][4];
		for (int i = 0; i < 1; i++) {
			if (i % 10 == 9 || true) {
				System.out.println(i + 1);
			}
		MallowsPreferencesContainer left = MallowsOnlineImplementation.factory(30, 30, 0.2d);
		MallowsPreferencesContainer right = MallowsOnlineImplementation.factory(30, 30, 0.2d);
		int[] solutionTTC = new TopTradingCycle().solve(left, right);
		int[] solutionDA = new DeferredAcceptance().solve(left, right);
		int[] solutionNB = new ChineseParallel(1).solve(left, right);
		int[] solutionSH = new ChineseParallel(2).solve(left, right);
		int[] solutionSD = new DeferredAcceptanceWithCircuitBreaker(1).solve(left, right);
		int[] solutionDACB = new DeferredAcceptanceWithCircuitBreaker(2).solve(left, right);
		int[] solutionEDACB = new DeferredAcceptanceWithCircuitBreaker(2, 10).solve(left, right);
		int[][] solutions = {solutionTTC, solutionDA, solutionNB, solutionSH, solutionSD, solutionDACB, solutionEDACB};
		for (int j = 0; j < solutions.length; j++) {
			if (GraphAddon.hasCycle(left, solutions[j])) {
				paretoInefficencyCount[j]++;
			}
			
			blockingPairs[j] += GraphAddon.blockingPairsCount(left, right, solutions[j]);
			
			int[] ranks = GraphAddon.topRanks(4, left, solutions[j]);
			for (int k = 0; k < 4; k++) {
				rankDistribution[j][k] += ranks[k];
			}
		}
		System.out.println(left.toString());
		System.out.println(right.toString());
//		System.out.println(Arrays.toString(solutionTTC));
//		System.out.println(Arrays.toString(solutionDA));
		System.out.println(Arrays.toString(solutionNB));
//		System.out.println(Arrays.toString(solutionSH));
//		System.out.println(Arrays.toString(solutionSD));
//		System.out.println(Arrays.toString(solutionDACB));
//		System.out.println(Arrays.toString(solutionEDACB));
		}
//		System.out.println(Arrays.deepToString(rankDistribution));
//		System.out.println(Arrays.toString(blockingPairs));
//		System.out.println(Arrays.toString(paretoInefficencyCount));
    	System.out.println(LocalDateTime.now());
	}

	//only does square case
	
	@Override
	public int[] solve(MallowsPreferencesContainer left, MallowsPreferencesContainer right) {
		// TODO Auto-generated method stub
		int[] matching = new int[left.size()];
		int[] reverseMatching = new int[right.size()];
//		int[][] leftPref = convertPreferenceProfile(left);		
//		int[][] rightPref = convertPreferenceProfile(right);
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		while (arrayHasZeroes(matching)) {
			int[] edges = new int[left.size() + right.size()]; //points to the item, 0 for removed
			for (int i = 0; i < left.size(); i++) {
				if (matching[i] == 0)
					edges[i] = left.size() + findTopChoice(leftPref.get(i), reverseMatching);
			}
			for (int i = 0; i < right.size(); i++) {
				if (reverseMatching[i] == 0)
					edges[left.size() + i] = findTopChoice(rightPref.get(i), matching);
			}
			boolean[] nodeVisited = new boolean[left.size()];
			for (int i = 0; i < left.size(); i++) {
				if (!nodeVisited[i]) {
					int currentNode = i;
					Set<Integer> currentTree = new HashSet<>();
					while (currentNode != -1 && !currentTree.contains(currentNode)) {
						//keep following the tree until I find a cycle or a back edge
						currentTree.add(currentNode);
						currentNode = edges[currentNode] - 1;
					}
					if (currentNode >= left.size())
						currentNode = edges[currentNode] - 1;
					if (currentNode != -1) {
						//the cycle contains currentNode
						int cycleNode = currentNode;
						do {
							matching[cycleNode] = edges[cycleNode] - left.size();
							reverseMatching[edges[cycleNode] - left.size() - 1] = cycleNode + 1;
							cycleNode = edges[edges[cycleNode] - 1] - 1;
						} while (cycleNode != currentNode);
					}
					//make all nodes in the tree as visited
					for (Integer ii : currentTree) {
						if (ii < left.size())
							nodeVisited[ii] = true;
					}
				}
			}
		}
		return matching;
	}
	
	private int findTopChoice(MallowsPreferences preference, int[] matching) { //returns the agent or item number
		for (int i = 0; i < preference.size(); i++) {
			if (matching[preference.get(i) - 1] == 0) {
				return preference.get(i);
			}
		}
		throw new RuntimeException("TTC, findTopChoice: cannot find unallocated item");
	}
	
	@Override
	public String toString() {
		return "TTC";
	}

}
