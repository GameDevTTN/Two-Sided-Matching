/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.galeshapley;

/**
 *
 * @author ylo019
 */
public class GaleShapley {
    
    public static void main(String[] args) {
        init();
        new GaleShapley().start(males, females);
    }
    
    private static Male[] males; private static Female[] females;
    
    private static void init() {
        String[] maleName = {"Albert", "Bill", "Clark", "David"};
        String[] femaleName = {"Elle", "Flora", "Gloria", "Harmony"};
        int[][] malePref = {    {1, 2, 0, 3},
                                {2, 0, 1, 3},
                                {2, 3, 1, 0},
                                {2, 0, 3, 1}};
        int[][] femalePref = {  {3, 1, 0, 2},
                                {3, 0, 2, 1},
                                {1, 3, 2, 0},
                                {1, 2, 0, 3}};
        males = new Male[4];
        females = new Female[4];
        for (int i = 0; i < males.length; i++) {
            males[i] = new Male(maleName[i]);
            females[i] = new Female(femaleName[i]);
        }
        for (int i = 0; i < males.length; i++) {
            Male[] mList = new Male[males.length];
            Female[] fList = new Female[females.length];
            for (int j = 0; j < males.length; j++) {
                fList[j] = females[malePref[i][j]];
                mList[j] = males[femalePref[i][j]];
            }
            males[i].setPreference(fList);
            females[i].setPreference(mList);
        }
    }
        
    public void start(Male[] males, Female[] females) {
        for (Male m : males) {
            m.makeProposal();
        }
        for (Male m : males) {
            System.out.println(m.getName() + " is engaged to " + m.getPartners());
        }
    }
    
}
