/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import MatchingAlgorithm.Auxiliary.InvalidPreferenceException;
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.ProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iIterator;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;
import UtilityModels.ExponentialModel;
import UtilityModels.iUtilitiesModel;
import java.util.Arrays;

/**
 *
 * @author ylo019
 */
public class HungarianAlgorithmWrapper {
    
    public static enum DIRECTION {
        LEFT, RIGHT, BOTH;
    }

    public double[] solve(PreferenceProfile proposer, PreferenceProfile proposee, int[] capacity, iUtilitiesModel model, boolean isUtil, DIRECTION direction) {
        int totalCapacity = 0;
        for (int c : capacity) {
            totalCapacity += c;
        }
        double[] weight = model.getUtilities(proposer.size());
        double[] scWeight = model.getUtilities(proposee.size());
        double[][] weights = new double[proposer.size()][totalCapacity];
        int[] buckets = new int[totalCapacity];
        int index = 0;
        for (int i = 0; i < capacity.length; i++) {
            for (int j = 0; j < capacity[i]; j++) {
                buckets[index++] = i;
            }
        }
        int[][] inverseBucket = new int[capacity.length][];
        index = 0;
        for (int i = 0; i < capacity.length; i++) {
            int[] clones = new int[capacity[i]];
            for (int j = 0; j < clones.length; j++) {
                clones[j] = index++;
            }
            inverseBucket[i] = clones;
        }
        Permutation[] proposers = proposer.getProfiles();
        if (direction != DIRECTION.RIGHT) {
            for (int agentIndex = 0; agentIndex < proposers.length; agentIndex++) {
                int[] pref = proposers[agentIndex].getArray();
                for (int schoolIndex = 0; schoolIndex < pref.length; schoolIndex++) {
                    for (int k : inverseBucket[pref[schoolIndex] - 1]) {
                        if (isUtil)
                            weights[agentIndex][k] += (scWeight[0] - scWeight[schoolIndex]);
                        else
                            weights[agentIndex][k] = Math.max(weights[agentIndex][k], (scWeight[0] - scWeight[schoolIndex]));
                    }
                }
            }
        }
        Permutation[] proposees = proposee.getProfiles();
        if (direction != DIRECTION.LEFT) {
            for (int bucketIndex = 0; bucketIndex < buckets.length; bucketIndex++) {
                int[] pref = proposees[buckets[bucketIndex]].getArray();
                for (int agentIndex = 0; agentIndex < pref.length; agentIndex++) {
                    if (isUtil)
                        weights[pref[agentIndex] - 1][bucketIndex] += (weight[0] - weight[agentIndex]) * (scWeight[0]/weight[0]);
                    else
                        weights[pref[agentIndex] - 1][bucketIndex] = Math.max(weights[pref[agentIndex] - 1][bucketIndex], (weight[0] - weight[agentIndex]) * (scWeight[scWeight.length - 1]/weight[weight.length - 1]));                }
            }
        }
//        System.out.println(Arrays.toString(weight));
        //System.out.println(Arrays.deepToString(weights));
        int[] result = new HungarianAlgorithm(weights).execute();
        
        //System.out.println(Arrays.toString(result));
        
        double[] borda = ExponentialModel.BORDA.getUtilities(proposer.size());
        double[] scBorda = ExponentialModel.BORDA.getUtilities(proposee.size());
        for (int i = 0; i < result.length; i++)
            PostBox.broadcast(MessageType.DETAILS, "Student " + (i + 1) + " is enrolled in " + "School " + (buckets[result[i] - 1] + 1));
        double leftUtil = 0.0;
        double leftEgal = Double.POSITIVE_INFINITY;
        double leftNash = 1.0;
        double rightUtil = 0.0;
        double rightEgal = Double.POSITIVE_INFINITY;
        double rightNash = 1.0;
        for (int i = 0; i < result.length; i++) {
            int[] proposerPref = proposers[i].getArray();
            for (int j = 0; j < proposerPref.length; j++) {
                if (proposerPref[j] == buckets[result[i] - 1] + 1) {
                    leftUtil += scBorda[j]/(proposee.size() - 1);
                    leftEgal = Math.min(leftEgal, scBorda[j]/(proposee.size() - 1));
                    leftNash *= scBorda[j];
                }
            }
            int[] proposeePref = proposees[buckets[result[i] - 1]].getArray();
            for (int j = 0; j < proposeePref.length; j++) {
                if (proposeePref[j] == (i + 1)) {
                    rightUtil += borda[j]/(proposer.size() - 1);
                    rightEgal = Math.min(rightEgal, borda[j]/(proposer.size() - 1));
                    rightNash *= borda[j];
                }
            }
        }
        int blockingPair = 0;
        for (int proposerIndex = 0; proposerIndex < proposer.size(); proposerIndex++) {
            int currentMatch = buckets[result[proposerIndex] - 1] + 1; //make it 1 based
            for (int proposerPrefList : proposers[proposerIndex].getArray()) {
                if (proposerPrefList == currentMatch)
                    break;
                for (int proposeePrefList : proposees[currentMatch - 1].getArray())
                    if (proposeePrefList - 1 == proposerIndex)
                        blockingPair++;
            }
        }
        /*
        int count = 0;
        for (iRejectable r : rejectables) {
            if (r == school) {
                return count;
            }
            if (r.isMyEnvyJustified(this)) {
                count++;
            }
        }
        return count;
        */
        return new double[]{leftUtil/proposer.size(), leftEgal, Math.pow(leftNash, 1.0/proposer.size()),
            rightUtil/proposer.size(), rightEgal, Math.pow(rightNash, 1.0/proposer.size())/proposer.size(),
            (leftUtil + rightUtil)/(proposer.size() * 2), Math.min(leftEgal, rightEgal), Math.pow(leftNash * rightNash, 1.0/(proposer.size() * 2)),
            (double)blockingPair/(proposer.size() * proposee.size())}; //not support blocking pairs
    }
    
}
