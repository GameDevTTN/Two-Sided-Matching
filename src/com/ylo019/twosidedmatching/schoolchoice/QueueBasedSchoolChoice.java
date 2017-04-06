/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import Main.Observers.System.IO;
import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import Main.Settings.Configurations;
import Main.Settings.Format;
import Main.Settings.Settings;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import UtilityModels.EgalitarianModel;
import UtilityModels.ExponentialModel;
import UtilityModels.RandomUtilityModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ordinalpreferencegenerator.Mallows;
import ordinalpreferencegenerator.iOrdinalIterator;

/**
 *
 * @author ylo019
 */
public class QueueBasedSchoolChoice {
    
    public static void main(String[] args) {
        Settings.PATH = "FINALDATA/060417/SchoolChoice/SHORT1/";
        Configurations.getConfigurations().setOutput(new MessageType[]{/*MessageType.PREFERENCE, /*MessageType.DETAILS,*/ MessageType.PRINT});
        IO.getConsole();
        Configurations.getConfigurations().init();
        int minSize = 100;
        int maxSize = 3000;
        int increment = 100;
        float minMallows = 0.1f;
        float maxMallows = 1.0f;
        int mallowsSplit = 9;
        int runs = 1000;
        int currentSize = minSize;
        //print the heading once
        PostBox.broadcast(MessageType.PRINT, "SUMMARY");
        String sentence = "";
        for (String s : SCORES) {
            sentence += "\t" + s + "\tnormalised " + s;
        }
        sentence += "\tstudentCount\tschoolCount\truns\tstudentMallows\tschoolMallows\texcessCapacity";
        //end hack
        PostBox.broadcast(MessageType.PRINT, sentence);
        while (currentSize <= maxSize) {
            for (int i = 0; i <= mallowsSplit; i++)
                for (int j = 0; j <= mallowsSplit; j++) {
                    for (int k : new int[]{2, (int)(Math.ceil(Math.sqrt(currentSize))), (currentSize < 20 ? 2 : currentSize/10), currentSize})
                        start(currentSize, k, runs, ((double)i)/mallowsSplit * (maxMallows - minMallows) + minMallows, ((double)j)/mallowsSplit * (maxMallows - minMallows) + minMallows, 0);
                }
            currentSize += increment;
        }
        
        IO.getConsole().close();
    }
    private static String[] ALGORITHMS = {"GS", "NB", "AB", "HA_Util_L", "HA_Util_R", "HA_Util_B", "HA_Egal_L", "HA_Egal_R", "HA_Egal_B", "Random"};
    private static String[] SCORES = {"L_Util", "L_Egal", "L_Nash", "R_Util", "R_Egal", "R_Nash", "Util", "Egal", "Nash", "Blocking_Pair"};
    public static void start(int studentCount, int schoolCount, int runs, double leftMallows, double rightMallows, int overCapacity) {
        //PostBox.broadcast(MessageType.PRINT, String.format("Params: StudentCount %d, SchoolCount %d, Runs %d, Mallows %s %s, ExcessCapacity %d", studentCount, schoolCount, runs, Format.Format(leftMallows), Format.Format(rightMallows), overCapacity));
        String params = String.format("\t%d\t%d\t%d\t%s\t%s\t%d", studentCount, schoolCount, runs, Format.Format(leftMallows), Format.Format(rightMallows), overCapacity);
        //generate profiles
        String[] studentNames = new String[studentCount];
        String[] schoolNames = new String[schoolCount];
        for (int i = 0; i < studentNames.length; i++) {
            studentNames[i] = "Student " + (i + 1);
        }
        for (int i = 0; i < schoolNames.length; i++) {
            schoolNames[i] = "School " + (i + 1);
        }

        iOrdinalIterator students = new Mallows(runs, studentCount, schoolCount, leftMallows);
        iOrdinalIterator schools = new Mallows(runs, schoolCount, studentCount, rightMallows);
        //initialise collators
        double[][] results = new double[ALGORITHMS.length][SCORES.length];
        double[][] normalizedResults = new double[ALGORITHMS.length][SCORES.length];
        //while(profile has next)
        int count = 0;
        while (students.hasNext() && schools.hasNext()) {
            System.out.println(++count);
            int[] schoolCapacities = new int[schoolCount];
            Arrays.fill(schoolCapacities, 1);
            int totalCapacity = studentCount + overCapacity;
            Set<Integer> cuts = new HashSet<>();
            for (int i = 1; i < schoolCount; i++) { //schoolCount - 1 cuts
                while (!cuts.add((int)(Math.random() * (totalCapacity - 1)) + 1)) {}
            }
            Integer[] sortedCuttingPoint = cuts.toArray(new Integer[0]);
            Arrays.sort(sortedCuttingPoint);
            int lastIndex = 0;
            int lastValue = 0;
            for (Integer i : sortedCuttingPoint) {
                schoolCapacities[lastIndex++] = (i - lastValue);
                lastValue = i;
            }
            schoolCapacities[lastIndex] = (totalCapacity - lastValue);
            PostBox.broadcast(MessageType.PREFERENCE, "Capacities:" + Arrays.toString(schoolCapacities));
            double[][] tempResults = new double[ALGORITHMS.length][SCORES.length];
            PreferenceProfile student = students.getNext();
            PreferenceProfile school = schools.getNext();
            PostBox.broadcast(MessageType.PREFERENCE, student);
            PostBox.broadcast(MessageType.PREFERENCE, school);
        //GS
        //NB
        //AB
            for (int i = 0; i < 3; i++) {
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                iProfileIterator stPref = student.getIterator();
                iProfileIterator scPref = school.getIterator();
                Student[] studentList = new Student[studentCount];
                School[] schoolList = new School[schoolCount];

                for (int j = 0; j < studentList.length; j++) {
                    switch (i) {
                        case 0:
                        case 1:
                            studentList[j] = new Student(studentNames[j]);
                            break;
                        default:
                            studentList[j] = new AdaptiveStudent(studentNames[j]);
                    }
                }
                for (int j = 0; j < schoolList.length; j++) {
                    switch (i) {
                        case 0:
                            schoolList[j] = new PrioritySchool(schoolNames[j], schoolCapacities[j]);
                            break;
                        default:
                            schoolList[j] = new ImmediateAcceptanceSchool(schoolNames[j], schoolCapacities[j]);
                    }
                            
                }
                for (int j = 0; j < schoolList.length; j++) {
                    Student[] stList = new Student[studentList.length];
                    for (int k = 0; k < studentList.length; k++) {
                        int index = scPref.getNext(j + 1) - 1;
                        stList[k] = studentList[index];
                    }
                    schoolList[j].setPreference(stList);
                }
                for (int j = 0; j < studentList.length; j++) {
                    School[] scList = new School[schoolList.length];
                    for (int k = 0; k < schoolList.length; k++) {
                        int index = stPref.getNext(j + 1) - 1;
                        scList[k] = schoolList[index];
                    }
                    studentList[j].setPreference(scList);
                }
                System.out.println(ALGORITHMS[i]);
                double[] output = (new QueueBasedSchoolChoice().start(studentList, schoolList));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    tempResults[i][j] = output[j];
                }
            }
        
        //Hungarian

            for (int i = 3; i < 6; i++) {
                System.out.println(ALGORITHMS[i]);
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                double[] output = (new HungarianAlgorithmWrapper().solve(student, school, schoolCapacities, 
                        ExponentialModel.BORDA, true,
                        (i % 3 == 0 ? HungarianAlgorithmWrapper.DIRECTION.LEFT : 
                                (i % 3 == 1 ? HungarianAlgorithmWrapper.DIRECTION.RIGHT : HungarianAlgorithmWrapper.DIRECTION.BOTH))));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    tempResults[i][j] = output[j];
                }
            }
            
            for (int i = 6; i < 9; i++) {
                System.out.println(ALGORITHMS[i]);
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                double[] output = (new BinarySearchEgalitarian().solve(student, school, schoolCapacities,
                        (i % 3 == 0 ? HungarianAlgorithmWrapper.DIRECTION.LEFT : 
                                (i % 3 == 1 ? HungarianAlgorithmWrapper.DIRECTION.RIGHT : HungarianAlgorithmWrapper.DIRECTION.BOTH))));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    tempResults[i][j] = output[j];
                }
            }
            
            //commented out "Custom" algorithm - RIGHT SIDE EGAL IS NOT WORKING TODO: fix that
            System.out.println(ALGORITHMS[9]);
            PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[9]);
               double[] output = (new HungarianAlgorithmWrapper().solve(student, school, schoolCapacities, new RandomUtilityModel(), true, HungarianAlgorithmWrapper.DIRECTION.BOTH));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[9][j] += output[j];
                    tempResults[9][j] = output[j];
                }
            
            for (int i = 0; i < SCORES.length; i++) {
                double max = 0.0;
                for (int j = 0; j < ALGORITHMS.length; j++) {
                    max = Math.max(max, tempResults[j][i]); //accessing in very inefficient way
                }
//                if (i == 1 && tempResults[9][1] != max) {
//                    PostBox.broadcast(MessageType.PRINT, "ALERT");
//                    System.out.println("ALERT");
//                }
                for (int j = 0; j < ALGORITHMS.length; j++) {
                    tempResults[j][i] = 100.0 * (max == 0 ? 1 : tempResults[j][i]/max);
                    normalizedResults[j][i] += tempResults[j][i];
                }
            }
         //end while
        }
        
        //report collators
        PostBox.broadcast(MessageType.OUTPUT, "SUMMARY");
        String sentence = "";
        for (String s : SCORES) {
            sentence += "\t" + s + "\tnormalised " + s;
        }
        sentence += "\tstudentCount\tschoolCount\truns\tstudentMallows\tschoolMallows\texcessCapacity";
        PostBox.broadcast(MessageType.OUTPUT, sentence);
        for (int i = 0; i < ALGORITHMS.length; i++) {
            String out = ALGORITHMS[i];
            for (int j= 0; j < SCORES.length; j++) {
                out += "\t" + Format.Format(results[i][j]/runs);
                out += "\t" + Format.Format(normalizedResults[i][j]/runs);
            }
            PostBox.broadcast(MessageType.PRINT, out + params);
        }
    }
        
    public double[] start(Student[] students, School[] schools) {
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
        
        
        double leftUtil = 0.0;
        double leftMin = Double.POSITIVE_INFINITY;
        double leftNash = 1.0;
        for (Student s : students) {
            PostBox.broadcast(MessageType.DETAILS, s.getName() + " is enrolled in " + s.getPartners());
            leftUtil += (s.getUtility(ExponentialModel.BORDA)/(schools.length - 1));
            leftMin = Math.min(leftMin, s.getMinUtility(ExponentialModel.BORDA));
            leftNash *= s.getNashUtility(ExponentialModel.BORDA);
        }
        double rightUtil = 0.0;
        double rightMin = Double.POSITIVE_INFINITY;
        double rightNash = 1.0;
        for (School s : schools) {
            rightUtil += (s.getUtility(ExponentialModel.BORDA)/(students.length - 1));
            rightMin = Math.min(rightMin, s.getMinUtility(ExponentialModel.BORDA));
            rightNash *= s.getNashUtility(ExponentialModel.BORDA);
        }
        int blockingPair = 0;
        for (Student s : students) {
            blockingPair += s.howManyBlockingPairs();
        }
        return new double[]{leftUtil/students.length, leftMin, Math.pow(leftNash, (1.0/students.length)), 
            rightUtil/students.length, rightMin, Math.pow(rightNash, (1.0/students.length)), 
            (leftUtil + rightUtil)/(students.length * 2), Math.min(leftMin, rightMin), Math.pow((leftNash * rightNash), (1.0/(students.length * 2))),
            ((double)blockingPair)/(students.length * schools.length)};
    }
    
    public static void print(double[] outputs) {
        for (int i = 0; i < outputs.length; i++) {
            PostBox.broadcast(MessageType.OUTPUT, SCORES[i] + " " + Format.Format(outputs[i]));
        }
    }
    
}
