/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.galeshapley;

import com.ylo019.twosidedmatching.schoolchoiceobjects.Rejectable;
import com.ylo019.twosidedmatching.schoolchoiceobjects.iProposable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ylo019
 */
public class Female extends Rejectable {
    
    private String name;
    private iProposable partner = null;
    private boolean isFreeSinceRound = true;
    
    public Female(String name) {
        this.name = name;
    }
    
    @Override
    public void setPreference(iProposable[] males) {
        super.setPreference(males);
    }

    @Override
    public void receivesProposal(iProposable proposer) {
        if (partner == null || (proposable.indexOf(proposer) < proposable.indexOf(partner))) {
            if (partner != null) {
                partner.isRejected(this);
            }
            partner = proposer;
            partner.isEngaged(this);
        } else {
            proposer.isRejected(this);
        }
    }

    @Override
    public boolean isFree() {
        return partner == null;
    }

    @Override
    public boolean isFreeSinceRound() {
        return isFreeSinceRound;
    }

    @Override
    public void newRound() {
        isFreeSinceRound = (partner == null);
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getPartners() {
        return (partner == null ? "null" : partner.getName());
    }
    
}
