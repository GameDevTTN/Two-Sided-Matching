/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoiceobjects;

import UtilityModels.iUtilitiesModel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ylo019
 */
public abstract class Proposable implements iProposable {
    
    protected List<iRejectable> rejectables = new LinkedList<>();
    protected Iterator<iRejectable> iterator;
    
    public void setPreference(iRejectable[] rejectables) {
        this.rejectables.addAll(Arrays.asList(rejectables));
        iterator = this.rejectables.listIterator();
    }
    
    @Override
    public double getNashUtility(iUtilitiesModel ium) {
        return getUtility(ium);
    }
        
    @Override
    public double getMinUtility(iUtilitiesModel ium) {
        return getUtility(ium)/(rejectables.size() - 1);
    }
    
}
