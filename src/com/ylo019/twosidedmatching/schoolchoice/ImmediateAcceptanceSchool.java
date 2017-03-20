/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import com.ylo019.twosidedmatching.schoolchoiceobjects.iProposable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ylo019
 */
public class ImmediateAcceptanceSchool extends School {
    
    protected List<iProposable> shortListed = new ArrayList<>();

    public ImmediateAcceptanceSchool(String name, int capacity) {
        super(name, capacity);
    }

    @Override
    public void receivesProposal(iProposable proposer) {
        shortListed.add(proposer);
        proposer.isEngaged(this);
        if (enrolled.size() + shortListed.size() > capacity) {
            //linear search on lowest priority
            iProposable lowestPriority = null;
            for (iProposable p : shortListed) {
                if (lowestPriority == null || proposable.indexOf(p) > proposable.indexOf(lowestPriority)) {
                    lowestPriority = p;
                }
            }
            if (lowestPriority != null) {
                shortListed.remove(lowestPriority);
                lowestPriority.isRejected(this);
            }
        }
    }
    
    @Override
    public void newRound() {
        enrolled.addAll(shortListed);
        shortListed.clear();
    }
    
}
