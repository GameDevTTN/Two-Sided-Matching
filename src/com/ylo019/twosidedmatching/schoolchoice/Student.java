/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

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
    
}
