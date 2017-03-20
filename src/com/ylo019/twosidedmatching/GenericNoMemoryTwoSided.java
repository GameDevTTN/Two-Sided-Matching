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
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import Pair.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ylo019
 */
public abstract class GenericNoMemoryTwoSided extends SymmetrisedTwoSidedAlgorithm {

    @Override
    public Permutation solve(Permutation order, PreferenceProfile proposer, PreferenceProfile proposee) {
        int proposerSize = proposer.size();
        int proposeeSize = proposee.size();
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
                    lastProposeeMatch[j] = proposeeMatch[j]; //update the last matched agent (this is a round reset)
                }
                continue;
            }
            if (proposeeMatch[proposeeNum - 1] == lastProposeeMatch[proposeeNum - 1]) {
                proposerMatch[proposeeMatch[proposeeNum - 1] - 1] = 0; //unmatch the last agent
                if (isStack())
                    proposalPriority.add(0, proposeeMatch[proposeeNum - 1]); //add the agent back to stack
                else 
                    proposalPriority.add(proposeeMatch[proposeeNum - 1]);
                proposerMatch[proposerNum - 1] = proposeeNum;
                proposeeMatch[proposeeNum - 1] = proposerNum;
                continue;
            }
            if (proposeeMatch[proposeeNum - 1] == proposerNum) {
                proposalPriority.add(0, proposerNum); //add the agent back to stack, already matched, it shouldn't happen
                System.out.println("GenericNoMemoryTwoSided: Impossible path: agent already matched yet has a turn");
                continue;
            }
            iProfileIterator proposeeIter = proposee.getIterator();
            while (true) {
                int index = proposeeIter.getNext(proposeeNum);
                if (index == proposerNum) {
                    proposerMatch[proposeeMatch[proposeeNum - 1] - 1] = 0; //unmatch the last agent
                    if (isStack())
                        proposalPriority.add(0, proposeeMatch[proposeeNum - 1]); //add the agent back to stack
                    else
                        proposalPriority.add(proposeeMatch[proposeeNum - 1]); //add the agent back to queue
                    proposerMatch[proposerNum - 1] = proposeeNum;
                    proposeeMatch[proposeeNum - 1] = proposerNum;
                    break;
                }
                if (index == proposeeMatch[proposeeNum - 1]) {
                    if (isStack())
                        proposalPriority.add(0, proposerNum); //add the agent back to stack
                    else
                        proposalPriority.add(proposerNum); //add the agent back to queue
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
    
    protected abstract boolean isStack();
    
    protected boolean isQueue() {
        return !isStack();
    }
    
}
