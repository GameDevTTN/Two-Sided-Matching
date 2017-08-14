package com.ylo019.marriageproblem.algorithms;

import java.util.Arrays;
import java.util.List;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;
import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer.MallowsPreferences;

import Graph.GraphImpl;

public class GraphAddon {
	
	public static boolean hasCycle(MallowsOnlineImplementation.MallowsPreferencesContainer pref, int[] matching) {
		List<MallowsPreferences> prefList = pref.getPreferences();
		GraphImpl gi = new GraphImpl(matching.length);
		for (int j = 0; j < prefList.size(); j++) {
			for (int k = 0; k < prefList.size(); k++) {
				if (matching[j] != prefList.get(j).get(k)) {
					int hasObject = -1;
					for (int l = 0; l < matching.length; l++) {
						if (matching[l] == prefList.get(j).get(k)) {
							hasObject = l;
						}
					}
					gi.addEdge(j, hasObject);
				} else {
					break;
				}
			}
		}
		if (gi.hasCycles()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int paretoImprovableAgents(MallowsOnlineImplementation.MallowsPreferencesContainer pref, int[] matching) {
		List<MallowsPreferences> prefList = pref.getPreferences();
		int improvableCount = 0;
		GraphImpl gi = new GraphImpl(matching.length);
		for (int j = 0; j < prefList.size(); j++) {
			for (int k = 0; k < prefList.size(); k++) {
				if (matching[j] != prefList.get(j).get(k)) {
					int hasObject = -1;
					for (int l = 0; l < matching.length; l++) {
						if (matching[l] == prefList.get(j).get(k)) {
							hasObject = l;
						}
					}
					gi.addEdge(j, hasObject);
				} else {
					break;
				}
			}
		}
		for (int i = 0; i < prefList.size(); i++) {
			if (gi.hasCycleFrom(i)) {
				improvableCount++;
			}
		}
		return improvableCount;
	}
	
	public static int bordaSum(MallowsPreferencesContainer left, int[] js) {
		int sum = 0;
		for (int i = 0; i < js.length; i++) {
			for (int j = 0; j < js.length; j++) {
				if (left.getPreferences().get(i).get(j) == js[i]) {
					sum += (js.length - 1 - j);
				}
			}
		}
		return sum;
	}

	public static int[] topRanks(int size, MallowsPreferencesContainer left, int[] js) {
		// TODO Auto-generated method stub
		int[] output = new int[size];
		List<MallowsPreferences> prefList = left.getPreferences();
		for (int i = 0; i < prefList.size(); i++) {
			for (int j = 0; j < size; j++) {
				if (prefList.get(i).get(j) == js[i]) {
					output[j]++;
				}
			}
		}
		return output;
	}

	public static int blockingPairsCount(MallowsPreferencesContainer left, MallowsPreferencesContainer right,
			int[] solutions) {
		// TODO Auto-generated method stub
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		int count = 0;
		for (int i = 0; i < solutions.length - 1; i++) {
			for (int j = i + 1; j < solutions.length; j++) {
				//does agent i envy j
				if (leftPref.get(i).doesPrefers(solutions[j], solutions[i])) {
					//is it justified?
					if (rightPref.get(solutions[j] - 1).doesPrefers(i + 1, j + 1)) {
						count++;
					}
				}
				//does agent j envy i
					//is it justified?
				if (leftPref.get(j).doesPrefers(solutions[i], solutions[j])) {
					if (rightPref.get(solutions[i] - 1).doesPrefers(j + 1, i + 1)) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public static int enviousAgentCount(MallowsPreferencesContainer left, MallowsPreferencesContainer right,
			int[] solutions) {
		// TODO Auto-generated method stub
		List<MallowsPreferences> leftPref = left.getPreferences();
		List<MallowsPreferences> rightPref = right.getPreferences();
		int count = 0;
		for (int i = 0; i < solutions.length; i++) {
			for (int j = 0; j < solutions.length; j++) {
				if (i == j) {
					continue;
				}
				//does agent i envy j
				if (leftPref.get(i).doesPrefers(solutions[j], solutions[i])) {
					//is it justified?
					if (rightPref.get(solutions[j] - 1).doesPrefers(i + 1, j + 1)) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

}
