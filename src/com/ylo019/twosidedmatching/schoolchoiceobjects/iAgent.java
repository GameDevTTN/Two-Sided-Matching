/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoiceobjects;

import UtilityModels.iUtilitiesModel;

/**
 *
 * @author ylo019
 */
public interface iAgent {
    
    public boolean isFree();
    public boolean isFreeSinceRound();
    public void newRound();
    public String getName();
    public String getPartners();
    
    public int[] getRanks();
    
    public double getUtility(iUtilitiesModel ium);
    public double getNashUtility(iUtilitiesModel ium);
    public double getMinUtility(iUtilitiesModel ium);
    
}
