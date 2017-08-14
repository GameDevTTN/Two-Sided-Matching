/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import UtilityModels.iUtilitiesModel;

import java.util.Arrays;

import com.ylo019.twosidedmatching.schoolchoiceobjects.Proposable;
import com.ylo019.twosidedmatching.schoolchoiceobjects.iRejectable;

/**
 *
 * @author ylo019
 */
public class Student extends Proposable {
    
    private String name;
    private iRejectable school = null;
    
    public Student(String name) {
        this.name = name;
    }

    @Override
    public void makeProposal() {
        if (iterator.hasNext())
            iterator.next().receivesProposal(this);
    }

    @Override
    public void isEngaged(iRejectable rejectable) {
        school = rejectable;
    }

    @Override
    public void isRejected(iRejectable rejectable) {
        if (school == rejectable)
            school = null;
    }

    @Override
    public boolean isFree() {
        return school == null;
    }

    @Override
    public boolean isFreeSinceRound() {
        return school == null; //not supported
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
        return school.getName();
    }
    
    public int[] getRanks() {
    	int[] ranks = new int[1];
		ranks[0] = rejectables.indexOf(school) + 1;
		return ranks;
    }

    @Override
    public double getUtility(iUtilitiesModel ium) {
        return (school == null ? 0 : ium.getUtilities(rejectables.size())[rejectables.indexOf(school)]);
    }
    
    @Override
    public int howManyBlockingPairs() {
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
    }
}
