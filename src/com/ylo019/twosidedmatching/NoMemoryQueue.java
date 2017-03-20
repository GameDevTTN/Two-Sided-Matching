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
public class NoMemoryQueue extends GenericNoMemoryTwoSided {

    @Override
    protected boolean isStack() {
        return false;
    }

    @Override
    public String getName() {
        return "No Memory Queue";
    }
    
}
