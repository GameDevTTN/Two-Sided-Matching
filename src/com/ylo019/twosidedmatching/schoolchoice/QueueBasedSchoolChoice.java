/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import java.util.Arrays;
import ordinalpreferencegenerator.Mallows;

/**
 *
 * @author ylo019
 */
public class QueueBasedSchoolChoice {
    
    public static void main(String[] args) {
        init();
        new QueueBasedSchoolChoice().start(students, schools);
    }
    
    private static Student[] students; private static School[] schools;
    
    private static void init() {
        String[] studentNames = {"Albert", "Bill", "Clark", "David", "Elle", "Flora", "Gloria", "Harmony"};
        String[] schoolNames = {"Sunny Hills Primary", "Farm Cove Intermediate", "Pakuranga College", "University of Auckland"};
        int[] schoolCapacities = {2, 2, 2, 2};
//        int[][] studentPref = {     {1, 2, 0, 3},
//                                    {2, 0, 1, 3},
//                                    {2, 3, 1, 0},
//                                    {2, 0, 3, 1},
//                                    {3, 1, 0, 2},
//                                    {3, 0, 2, 1},
//                                    {1, 3, 2, 0},
//                                    {1, 2, 0, 3}};
        iProfileIterator stPref = new Mallows(1, studentNames.length, schoolNames.length, 0.5d).getNext().getIterator();
//        int[][] schoolPref = {  {0, 2, 4, 6, 1, 3, 5, 7},
//                                {1, 2, 3, 0, 4, 5, 7, 6},
//                                {3, 1, 2, 4, 0, 6, 5, 7},
//                                {4, 5, 6, 7, 0, 1, 2, 3}};
        iProfileIterator scPref = new Mallows(1, schoolNames.length, studentNames.length, 0.2d).getNext().getIterator();
        students = new Student[studentNames.length];
        schools = new School[schoolNames.length];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(studentNames[i]);
        }
        for (int i = 0; i < schools.length; i++) {
            schools[i] = new NoMemoryFIFOSchool(schoolNames[i], schoolCapacities[i]);
        }
        for (int i = 0; i < schools.length; i++) {
            Student[] stList = new Student[students.length];
            for (int j = 0; j < students.length; j++) {
                int index = scPref.getNext(i + 1) - 1;
                System.out.print(index + " ");
                stList[j] = students[index];
            }
            schools[i].setPreference(stList);
            System.out.println();
        }
        for (int i = 0; i < students.length; i++) {
            School[] scList = new School[schools.length];
            for (int j = 0; j < schools.length; j++) {
                int index = stPref.getNext(i + 1) - 1;
                System.out.print(index + " ");
                scList[j] = schools[index];
            }
            students[i].setPreference(scList);
            System.out.println();
        }
    }
        
    public void start(Student[] students, School[] schools) {
        boolean exit;
        do {
            exit = true;
            for (Student s : students) {
                if (s.isFree()) {
                    s.makeProposal();
                    exit = false;
                }
            }
            for (School s : schools) {
                s.newRound();
            }
        } while (!exit);
        for (Student s : students) {
            System.out.println(s.getName() + " is enrolled in " + s.getPartners());
        }
    }
    
}
