/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import ordinalpreferencegenerator.*;

/**
 *
 * @author ylo019
 */
public class TwoSidedMatching {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new TwoSidedMatching().start();
    }
    private int size = 5;
    public void start() {
        iOrdinalIterator proposerPref = new Mallows(3, size, size, 0.7f);
        int i = 0;
        while (proposerPref.hasNext()) {
            PreferenceProfile proposer = proposerPref.getNext();
            iOrdinalIterator proposeePref = new Mallows(3, size, size, 0.7f);
            while (proposeePref.hasNext()) {
                PreferenceProfile proposee = proposeePref.getNext();
                iTwoSidedAlgorithm algo = new NoMemoryStack();
//                iTwoSidedAlgorithm boston = new iTwoSidedAlgorithm() {
//
//                    @Override
//                    public Permutation solve(PreferenceProfile proposer, PreferenceProfile proposee) {
//                        List<iRestriction> restrictions = new ArrayList<>();
//                        restrictions.add(new LimitedByItemHeldBy(size, 10));
//                        iProfileIterator proposerPref = proposer.getIterator();
//                        Permutation[] proposeePref = proposee.getProfiles();
//                        int[] matching = new int[size];
//                        List<Integer> order = new ArrayList<>();
//                        Integer[] numbers = new Integer[size];
//                        for (int i = 0; i < size; i++) {
//                            numbers[i] = i + 1;
//                        }
//                        order.addAll(Arrays.asList(numbers));
//                        while (!order.isEmpty()) {
//                            int proposerIndex = order.remove(0);
//                            int proposeeIndex = proposerPref.getNext(proposerIndex);
//                            //proposes
//                            if (matching[proposeeIndex - 1] == 0) {
//                                matching[proposeeIndex - 1] = proposerIndex;
//                                updateRestriction(restrictions, proposerIndex, proposeeIndex, 0);
//                            } else if (proposeePref[proposeeIndex - 1].isBefore(proposerIndex, matching[proposeeIndex - 1]) && checkRestriction(restrictions, proposerIndex, proposeeIndex, matching[proposeeIndex - 1])) {
//                                order.add(matching[proposeeIndex - 1]);
//                                int temp = matching[proposeeIndex - 1];
//                                matching[proposeeIndex - 1] = proposerIndex;
//                                updateRestriction(restrictions, proposerIndex, proposeeIndex, temp);
//                            } else {
//                                order.add(proposerIndex);
//                            }
//                        }
//                        try {
//                            Permutation p =  new Permutation(size, size, matching);
//                            p = p.inverse();
//                            return p;
//                        } catch (Exception e) {
//                            throw new RuntimeException("e");
//                        }
//                    }
//                    
//                    private boolean checkRestriction(List<iRestriction> restriction, int actingAgent, int item, int currentAgent) {
//                        for (iRestriction r : restriction) {
//                            if (!r.attemptToTake(actingAgent, item, currentAgent)) {
//                                return false;
//                            }
//                        }
//                        return true;
//                    }
//                    
//                    private void updateRestriction(List<iRestriction> restriction, int actingAgent, int item, int currentAgent) {
//                        for (iRestriction r : restriction) {
//                            r.take(actingAgent, item, currentAgent);
//                        }
//                    }
//                    
//                };
                System.out.println (++i + "\n");
                System.out.println(proposer + "\n");
                System.out.println(proposee + "\n");
                System.out.println(algo.solve(proposer, proposee) + "\n"); //not quite boston in that it is not simulatenous
                
            }
        }
    }
    
    
    
}
