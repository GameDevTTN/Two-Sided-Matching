package com.ylo019.marriageproblem.algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import Main.Settings.Format;

public class TTCFamily extends AlgorithmAdapter {
	
	private int param;
	
	public TTCFamily(int param) {
		this.param = param;
	}
	
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
				if (matching[i] == 0) {
					for (int j = 0; j < right.size(); j++) {
						int school = findKthChoice(leftPref.get(i), reverseMatching, j+1);
						if (school != 0) {
							for (int k = 0; k < param; k++) {
								if (findKthChoice(rightPref.get(school - 1), matching, k + 1) == i + 1) {
									edges[i] = left.size() + school;
								}
							}
							if (edges[i] != 0) {
								break;
							}							
						}
					}
				}
			}
			for (int i = 0; i < right.size(); i++) {
				if (reverseMatching[i] == 0)
					edges[left.size() + i] = findKthChoice(rightPref.get(i), matching, 1);
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
	
	private int findKthChoice(MallowsPreferences preference, int[] matching, int k) {
		return preference.find(matching, k);
	}
	
	@Override
	public String toString() {
		return "TTCFamily p = " + Format.Format(param);
	}

}
