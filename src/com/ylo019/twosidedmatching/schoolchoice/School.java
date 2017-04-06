/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import UtilityModels.iUtilitiesModel;
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
        return capacity > enrolled.size();
    }

    @Override
    public boolean isFreeSinceRound() {
        return capacity > enrolled.size();
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
    
    @Override
    public double getUtility(iUtilitiesModel ium) {
        double sum = 0.0;
        for (iProposable p : enrolled) {
            sum += ium.getUtilities(proposable.size())[proposable.indexOf(p)];
        }
        return sum;
    }
    
    @Override
    public double getNashUtility(iUtilitiesModel ium) {
        double product = 1.0;
        for (iProposable p : enrolled) {
            product *= ium.getUtilities(proposable.size())[proposable.indexOf(p)];
        }
        return product;       
    }
    
    @Override
    public double getMinUtility(iUtilitiesModel ium) {
        double min = Double.POSITIVE_INFINITY;
        for (iProposable p : enrolled) {
            min = Math.min(min, ium.getUtilities(proposable.size())[proposable.indexOf(p)]);
        }
        return min/(proposable.size() - 1);
    }
    
    @Override
    public boolean isMyEnvyJustified(iProposable proposer) {
        for (iProposable p : enrolled) {
            if (proposable.indexOf(proposer) < proposable.indexOf(p)) {
                return true;
            }
        }
        return false;
    }
    
}
