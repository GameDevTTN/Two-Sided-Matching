/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.galeshapley;

import UtilityModels.iUtilitiesModel;
import com.ylo019.twosidedmatching.schoolchoiceobjects.Proposable;
import com.ylo019.twosidedmatching.schoolchoiceobjects.iRejectable;

/**
 *
 * @author ylo019
 */
public class Male extends Proposable {
    
    private String name;
    private iRejectable partner = null;
    
    public Male(String name) {
        this.name = name;
    }

    @Override
    public void makeProposal() {
        if (iterator.hasNext())
            iterator.next().receivesProposal(this);
    }

    @Override
    public void isEngaged(iRejectable female) {
        partner = female;
    }

    @Override
    public void isRejected(iRejectable female) {
        if (partner == female) {
            partner = null;
        }
        makeProposal();
    }

    @Override
    public boolean isFree() {
        return partner == null;
    }

    @Override
    public boolean isFreeSinceRound() {
        return partner == null;
    }

    @Override
    public void newRound() {
        
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPartners() {
        return (partner == null ? "null" : partner.getName());
    }

    @Override
    public double getUtility(iUtilitiesModel ium) {
        return (partner == null ? 0 : ium.getUtilities(rejectables.size())[rejectables.indexOf(partner)]);
    }

    @Override
    public int howManyBlockingPairs() {
        int count = 0;
        for (iRejectable r : rejectables) {
            if (r == partner) {
                return count;
            }
            if (r.isMyEnvyJustified(this)) {
                count++;
            }
        }
        return count;
    }
    
}
