/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoiceobjects;

import UtilityModels.iUtilitiesModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ylo019
 */
public abstract class Rejectable implements iRejectable {
    
    protected List<iProposable> proposable = new ArrayList<>();
    
    public void setPreference(iProposable[] proposable) {
        this.proposable.addAll(Arrays.asList(proposable));
    }
    
    @Override
    public double getNashUtility(iUtilitiesModel ium) {
        return getUtility(ium);
    }
    
    @Override
    public double getMinUtility(iUtilitiesModel ium) {
        return getUtility(ium)/(proposable.size() - 1);
    }
    
    
}
