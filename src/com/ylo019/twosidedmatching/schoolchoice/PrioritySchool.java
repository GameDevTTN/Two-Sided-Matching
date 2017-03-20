/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import com.ylo019.twosidedmatching.schoolchoiceobjects.iProposable;

/**
 *
 * @author ylo019
 */
public class PrioritySchool extends School {

    public PrioritySchool(String name, int capacity) {
        super(name, capacity);
    }

    @Override
    public void receivesProposal(iProposable proposer) {
        enrolled.add(proposer);
        proposer.isEngaged(this);
        if (enrolled.size() > capacity) {
            //linear search on lowest priority
            iProposable lowestPriority = null;
            for (iProposable p : enrolled) {
                if (lowestPriority == null || proposable.indexOf(p) > proposable.indexOf(lowestPriority)) {
                    lowestPriority = p;
                }
            }
            if (lowestPriority != null) {
                enrolled.remove(lowestPriority);
                lowestPriority.isRejected(this);
            }
        }
    }
    
}
