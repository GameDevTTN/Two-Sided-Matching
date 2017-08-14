package com.ylo019.twosidedmatching.schoolchoice;

import java.util.Arrays;
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;

public class Helper {
	
	public static int[][] matchingToRanks(int[] matchings, PreferenceProfile pp) {
		Permutation[] profiles = pp.getProfiles();
		int[][] out = new int[matchings.length][1];
		for (int i = 0; i < matchings.length; i++) {
			int[] preferences = profiles[i].getArray();
			for (int j = 0; j < preferences.length; j++) {
				if (preferences[j] == matchings[i]) {
					out[i][0] = j + 1;
				}
			}
		}
		return out;
	}
	
	public static int[][] invertedMatchingToRanks(int[] matchings, PreferenceProfile pp, int size) {
		Permutation[] profiles = pp.getProfiles();
		int[][] out = new int[size][0];
		for (int i = 0; i < matchings.length; i++) {
			out[matchings[i] - 1] = Arrays.copyOf(out[matchings[i] - 1], out[matchings[i] - 1].length + 1);
			int[] preferences = profiles[matchings[i] - 1].getArray();
			for (int j = 0; j < preferences.length; j++) {
				if (preferences[j] == i + 1) {
					out[matchings[i] - 1][out[matchings[i] - 1].length - 1] = j + 1;
				}
			}
		}
		return out;
	}
	
	public static double[] ranksToScore(int[][] leftRanks, int[][] rightRanks, double blockingPairPercentage) {
		return new double[]{util(leftRanks, rightRanks.length), egal(leftRanks, rightRanks.length), plur(leftRanks, rightRanks.length),
				util(rightRanks, leftRanks.length), egal(rightRanks, leftRanks.length), plur(rightRanks, leftRanks.length),
				(util(leftRanks, rightRanks.length) + util(rightRanks, leftRanks.length))/2, 
				Math.min(egal(leftRanks, rightRanks.length), egal(rightRanks, leftRanks.length)), 
				(plur(leftRanks, rightRanks.length) + plur(rightRanks, leftRanks.length))/2,
				blockingPairPercentage};
	}
	
	private static double util(int[][] ranks, int size) {
		double runningTotal = 0;
		double runningCount = 0;
		for (int[] arr : ranks) {
			for (int rank : arr) {
				runningTotal += (size - rank);
				runningCount++;
			}
		}
		return runningTotal/(size - 1)/runningCount;
	}
	
	private static double egal(int[][] ranks, int size) {
		double maxRank = 0;
		double runningCount = 0;
		for (int[] arr : ranks) {
			for (int rank : arr) {
				maxRank = Math.max(maxRank, rank);
				runningCount++;
			}
		}
		return (size - maxRank)/(size - 1);
	}
	
	private static double nash(int[][] ranks, int size) {
		double runningProduct = 1;
		double runningCount = 0;
		for (int[] arr : ranks) {
			for (int rank : arr) {
				runningProduct *= (size - rank);
				runningCount++;
			}
		}
		return Math.pow(runningProduct, 1.0/runningCount);
	}
	
	private static double plur(int[][] ranks, int size) {
		double count = 0;
		double runningCount = 0;
		for (int[] arr : ranks) {
			for (int rank : arr) {
				if (rank <= arr.length) {
					count++;
				}
				runningCount++;
			}
		}
//		System.out.println(Arrays.deepToString(ranks));
//		System.out.println(count + " " + runningCount);
		return count/runningCount;
	}

}