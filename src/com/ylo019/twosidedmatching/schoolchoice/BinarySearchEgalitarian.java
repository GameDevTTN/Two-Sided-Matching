/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import UtilityModels.TopKChoiceModel;
import com.ylo019.twosidedmatching.schoolchoice.HungarianAlgorithmWrapper.DIRECTION;
import java.util.Arrays;

/**
 *
 * @author ylo019
 */
public class BinarySearchEgalitarian {
    
    public double[] solve(PreferenceProfile proposer, PreferenceProfile proposee, int[] capacity, DIRECTION direction) {
        int size = capacity.length;
        double[] highestSuccess = null;
        if (direction != DIRECTION.RIGHT) {
            highestSuccess = leftSolve(proposer, proposee, capacity, (direction == DIRECTION.BOTH));
        }
        if (direction != DIRECTION.LEFT) {
            if (highestSuccess == null) {
                highestSuccess = rightSolve(proposer, proposee, capacity, (direction == DIRECTION.BOTH));
            } else {
                double[] temp = rightSolve(proposer, proposee, capacity, (direction == DIRECTION.BOTH));
                if (highestSuccess[getIndexOfOutput(DIRECTION.BOTH)] < temp[getIndexOfOutput(DIRECTION.BOTH)]) {
                    highestSuccess = temp;
                }
            }
        }
        //System.out.println("Output: " + Arrays.toString(highestSuccess));
        return highestSuccess;
    }
    
    private double[] leftSolve(PreferenceProfile proposer, PreferenceProfile proposee, int[] capacity, boolean isBoth) {
        double[] lastSucc = null;
        int maxFail = 0; int minSucc = proposee.size();
        int pivot = minSucc;
        do {
            double[] out = new HungarianAlgorithmWrapper().solve(proposer, proposee, capacity, new TopKChoiceModel(pivot, proposee.size()), true, (isBoth ? DIRECTION.BOTH : DIRECTION.LEFT));
            //System.out.print("PIVOT: " + pivot + " " + Arrays.toString(out));
            if (lastSucc == null || out[getIndexOfOutput(isBoth ? DIRECTION.BOTH : DIRECTION.LEFT)] > 
                    1.0 - (pivot - 1.0)/(proposee.size() - 1) - 0.000000001f) { //0.00000001f = error rate
                lastSucc = out;
                minSucc = pivot;
                //System.out.println("SUCC");
            } else {
                maxFail = pivot;
                //System.out.println("FAIL");
            }
            pivot = (maxFail + minSucc)/2;
        } while (minSucc - maxFail > 1);
        //System.out.println(Arrays.toString(lastSucc));
        return lastSucc;
    }
    
    private double[] rightSolve(PreferenceProfile proposer, PreferenceProfile proposee, int[] capacity, boolean isBoth) {
        double[] lastSucc = null;
        int maxFail = 0; int minSucc = proposer.size();
        int pivot = minSucc;
        do {
            double[] out = new HungarianAlgorithmWrapper().solve(proposer, proposee, capacity, new TopKChoiceModel(pivot, proposer.size()), true, (isBoth ? DIRECTION.BOTH : DIRECTION.RIGHT));
            //System.out.print("PIVOT: " + pivot + " " + Arrays.toString(out));
            if (lastSucc == null || out[getIndexOfOutput(isBoth ? DIRECTION.BOTH : DIRECTION.RIGHT)] > 
                    1.0 - (pivot - 1.0)/(proposer.size() - 1) - 0.000000001f) { //0.00000001f = error rate
                lastSucc = out;
                minSucc = pivot;
                //System.out.println("SUCC");
            } else {
                maxFail = pivot;
                //System.out.println("FAIL");
            }
            pivot = (maxFail + minSucc)/2;
        } while (minSucc - maxFail > 1);
        //System.out.println(Arrays.toString(lastSucc));
        return lastSucc;
    }
//        
//        double[] highestSuccess = new HungarianAlgorithmWrapper().solve(proposer, proposee, capacity, new TopKChoiceModel(size, (direction != DIRECTION.RIGHT ? size : proposer.size())), true, direction);
//        int maxSuccess = size;
//        int lowestFail = 0;
//        int pivot = size/2;
//        do {
//            double[] out = new HungarianAlgorithmWrapper().solve(proposer, proposee, capacity, new TopKChoiceModel(pivot, (direction != DIRECTION.RIGHT ? size : proposer.size())), true, direction);
//            int index = getIndexOfOutput(direction);
////            System.out.println("out: " + Arrays.toString(out));
////            System.out.println("out[index]: " + out[index]);
////            System.out.println("out[index]: " + (1.0 - ((pivot - 1.0)/((direction != DIRECTION.RIGHT ? size : proposer.size()) - 1)) - out[index]));
//            if (1.0 - ((pivot - 1.0)/((direction != DIRECTION.RIGHT ? size : proposer.size()) - 1)) - out[index] > 0.000001d) { //error rate = 0.000001
//                lowestFail = pivot;
//            } else {
//                maxSuccess = pivot;
//                highestSuccess = out;
//            }
//            pivot = (maxSuccess + lowestFail)/2;
////            System.out.println("\t" + String.format("LowestFail: %d HighestSuccess: %d", lowestFail, maxSuccess));
////            System.out.println("\t" + Arrays.toString(highestSuccess));
//        }while (maxSuccess - lowestFail > 1);
////        System.out.println(String.format("LowestFail: %d HighestSuccess: %d", lowestFail, maxSuccess));
////        System.out.println(Arrays.toString(highestSuccess));
//        
//        //check for school values in betweens
//        if (direction == DIRECTION.BOTH) {
//            int sMaxSuccess = (int)Math.ceil(maxSuccess * (double)proposer.size()/size);
//            int sLowestFail = (int)(lowestFail * (double)proposer.size()/size);
//            while (sMaxSuccess - sLowestFail > 1) {
////                System.out.println(String.format("sMaxSucces %d sLowestFail %d", sMaxSuccess, sLowestFail));
////                System.out.println(String.format("Before: %s", Arrays.toString(highestSuccess)));
//                int sPivot = (sMaxSuccess + sLowestFail)/2;
//                double[] out = new HungarianAlgorithmWrapper().solve(proposer, proposee, capacity, new TopKChoiceModel(pivot, proposer.size()), true, DIRECTION.BOTH);
//                int index = getIndexOfOutput(direction);
//                if (1.0 - ((sPivot - 1.0)/(proposer.size() - 1)) - out[index] > 0.000001d) { //error rate = 0.000001
//                    sLowestFail = sPivot;
//                } else {
//                    sMaxSuccess = sPivot;
//                    highestSuccess = out;
//                }
////                System.out.println(String.format("Out: %s", Arrays.toString(out)));
////                System.out.println(String.format("After: %s", Arrays.toString(highestSuccess)));
//            }
//        }
//        return highestSuccess;
    
    private int getIndexOfOutput(DIRECTION direction) {
        switch (direction) {
            case LEFT:
                return 1;
            case RIGHT:
                return 4;
            case BOTH:
                return 7;
                default:
                return -1;
        }
    }
    
    
}
