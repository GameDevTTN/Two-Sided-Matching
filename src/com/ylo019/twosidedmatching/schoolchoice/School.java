/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import com.ylo019.twosidedmatching.schoolchoiceobjects.Rejectable;
import com.ylo019.twosidedmatching.schoolchoiceobjects.iProposable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ylo019
 */
public abstract class School extends Rejectable {
    
    private String name;
    protected int capacity;
    protected List<iProposable> enrolled = new ArrayList<>();
    
    public School(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    @Override
    public boolean isFree() {
        return capacity == enrolled.size();
    }

    @Override
    public boolean isFreeSinceRound() {
        return capacity == enrolled.size();
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
        String out = "";
        for (iProposable p : enrolled) {
            out += p.getName() + ", ";
        }
        return out.substring(0, out.length() - 2);
    }
    
}
