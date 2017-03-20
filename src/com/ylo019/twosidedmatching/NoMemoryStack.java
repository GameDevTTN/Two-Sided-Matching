/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

/**
 *
 * @author ylo019
 */
public class NoMemoryStack extends GenericNoMemoryTwoSided {
    
    @Override
    protected boolean isStack() {
        return true;
    }
    
    @Override
    public String getName() {
        return "No Memory Stack";
    }
    
}
