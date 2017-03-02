/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import MatchingAlgorithm.Auxiliary.InvalidPreferenceException;
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.ProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProbabilityMatrix;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import Pair.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ordinalpreferencegenerator.StaticFunctions;

/**
 *
 * @author ylo019
 */
public class NoMemoryStack extends SymmetrisedTwoSidedAlgorithm {

    @Override
    public Permutation solve(Permutation order, PreferenceProfile proposer, PreferenceProfile proposee) {
        int proposerSize = proposer.size();
        int proposeeSize = proposee.size();
        ProbabilityMatrix ipm = new ProbabilityMatrix(proposerSize, proposeeSize);
        PostBox.broadcast(MessageType.PROCESS, new Pair<>("Priority", order));
        List<Integer> proposalPriority = new ArrayList<>();
        for (int agent : order.getArray()) {
            proposalPriority.add(agent);
        }
        int[] proposerMatch = new int[proposerSize]; //constant update
        int[] proposeeMatch = new int[proposeeSize]; //constant update
        int[] lastProposeeMatch = new int[proposeeSize]; //only update on new match
        iProfileIterator iProposer = proposer.getIterator();
        while (!proposalPriority.isEmpty()) {
            //who is proposing to who
            int proposerNum = proposalPriority.remove(0);
            int proposeeNum = iProposer.getNext(proposerNum);
            //logic: if proposee is unmatched, accept
            //if proposee hasn't accept a new match, accept
            //if proposee was matched to proposer, reject
            //otherwise compare the two agents
            if (proposeeMatch[proposeeNum - 1] == 0) {
                proposerMatch[proposerNum - 1] = proposeeNum;
                proposeeMatch[proposeeNum - 1] = proposerNum;
                iProposer = proposer.getIterator();
                for (int j = 0; j < proposeeMatch.length; j++) {
                    lastProposeeMatch[j] = proposeeMatch[j];
                }
                continue;
            }
            if (proposeeMatch[proposeeNum - 1] == lastProposeeMatch[proposeeNum - 1]) {
                proposerMatch[proposeeMatch[proposeeNum - 1] - 1] = 0; //unmatch the last agent
                proposalPriority.add(0, proposeeMatch[proposeeNum - 1]); //add the agent back to stack
                proposerMatch[proposerNum - 1] = proposeeNum;
                proposeeMatch[proposeeNum - 1] = proposerNum;
                continue;
            }
            if (proposeeMatch[proposeeNum - 1] == proposerNum) {
                proposalPriority.add(0, proposerNum); //add the agent back to stack
                continue;
            }
            iProfileIterator proposeeIter = proposee.getIterator();
            while (true) {
                int index = proposeeIter.getNext(proposeeNum);
                if (index == proposerNum) {
                    proposerMatch[proposeeMatch[proposeeNum - 1] - 1] = 0; //unmatch the last agent
                    proposalPriority.add(0, proposeeMatch[proposeeNum - 1]); //add the agent back to stack
                    proposerMatch[proposerNum - 1] = proposeeNum;
                    proposeeMatch[proposeeNum - 1] = proposerNum;
                    break;
                }
                if (index == proposeeMatch[proposeeNum - 1]) {
                    proposalPriority.add(0, proposerNum); //add the agent back to stack
                    break;
                }
            }
        }
        Permutation matching;
        try {
            matching = new Permutation(proposerSize, proposeeSize, proposerMatch);
        } catch (InvalidPreferenceException ex) {
            System.out.println("NoMemoryStack error");
            return null;
        }
        return matching;
    }

    @Override
    public String getName() {
        return "NoMemoryStack";
    }
    
}
