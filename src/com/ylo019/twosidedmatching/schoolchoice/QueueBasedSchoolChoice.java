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
import MatchingAlgorithm.Auxiliary.Permutation;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.iProfileIterator;
import UtilityModels.EgalitarianModel;
import UtilityModels.ExponentialModel;
import UtilityModels.NashModel;
import UtilityModels.RandomUtilityModel;
import UtilityModels.TopKChoiceModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import ordinalpreferencegenerator.Mallows;
import ordinalpreferencegenerator.iOrdinalIterator;

/**
 *
 * @author ylo019
 */
public class QueueBasedSchoolChoice {
    
    public static void main(String[] args) {
    	System.out.println(LocalDateTime.now());
        //Settings.PATH = "GOODDATA/300617/MARRIAGEPROBLEM/SET1";
    	Settings.PATH = "TESTDATA/060717/";
        Configurations.getConfigurations().setOutput(new MessageType[]{/*MessageType.PREFERENCE, /*MessageType.DETAILS,*/ MessageType.PRINT});
        IO.getConsole();
        Configurations.getConfigurations().init();
        int minSize = 5;
        int maxSize = 50;
        int increment = 5;
        float minMallows = 0.2f; //do not change
        float maxMallows = 1.0f; //do not change
        int mallowsSplit = 1; //do not change
        int runs = 1000;
        int currentSize = minSize;
        //print the heading once
        PostBox.broadcast(MessageType.PRINT, "SUMMARY");
        String sentence = "";
        for (String s : SCORES) {
            sentence += "\t" + s + "\t" + s + "_std_dev\tnormalised_" + s;
        }
        sentence += "\tstudentCount\tschoolCount\truns\tstudentMallows\tschoolMallows\texcessCapacity";
        //end hack
        PostBox.broadcast(MessageType.PRINT, sentence);
        while (currentSize <= maxSize) {
            for (int i = 0; i <= mallowsSplit; i++)
                for (int j = 0; j <= mallowsSplit; j++) {
                	System.out.printf("Size: %d MaxSize: %d studentSplit: %d of %d schoolSplit: %d of %d\n",currentSize,maxSize,i+1,mallowsSplit+1,j+1,mallowsSplit+1);
                    for (int k : new int[]{/*3, (int)(Math.ceil(Math.sqrt(currentSize))), (currentSize < 20 ? 2 : currentSize/10), */currentSize})
                        start(currentSize, k, runs, ((double)i)/mallowsSplit * (maxMallows - minMallows) + minMallows, ((double)j)/mallowsSplit * (maxMallows - minMallows) + minMallows, 0);
                }
            currentSize += (currentSize < 50? increment : increment * 2);
        }
        
        IO.getConsole().close();
        System.out.println(LocalDateTime.now());
    }
    private static String[] ALGORITHMS = {"GS_L", "GS_R", "NB_L", "NB_R", "AB_L", "AB_R", "SD_L", "SD_R", "TTC_L", "TTC_R", "HA_Util_L", "HA_Util_R", "HA_Util_B", "HA_Egal_L", "HA_Egal_R", "HA_Egal_B", "Random"};
    private static final int NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS = 10;
    private static String[] SCORES = {"L_Util", "L_Egal", "L_Plur", "R_Util", "R_Egal", "R_Plur", "Util", "Egal", "Plur", "Blocking_Pair"/*, "J_Envious_L", "J_Envious_R", "J_Envious"*/};
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
        double[][] resultsSquared = new double[ALGORITHMS.length][SCORES.length];
        double[][] normalizedResults = new double[ALGORITHMS.length][SCORES.length];
        //while(profile has next)
        int count = 0;
        while (students.hasNext() && schools.hasNext()) {
        	System.out.printf("StC: %d ScC: %d lM: %f rM: %f run: %d\n", studentCount, schoolCount, leftMallows, rightMallows, ++count);
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
            //System.out.println(Format.Format(student));
            //System.out.println(Format.Format(school));
            PostBox.broadcast(MessageType.PREFERENCE, student);
            PostBox.broadcast(MessageType.PREFERENCE, school);
        //GS
        //NB
        //AB
        //SD
        //TTC (L&R should be identical)
            //Left & Right
            for (int i = 0; i < NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS; i++) {
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                iProfileIterator stPref = student.getIterator();
                iProfileIterator scPref = school.getIterator();
                Student[] studentList = new Student[studentCount];
                School[] schoolList = new School[schoolCount];

                for (int j = 0; j < studentList.length; j++) {
                    switch (i) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            studentList[j] = new Student(studentNames[j]);
                            break;
                        case 8:
                        case 9:
                        	studentList[j] = new TTCStudent(studentNames[j]);
                        	break;
                        default: //case 4-7
                            studentList[j] = new AdaptiveStudent(studentNames[j]);
                    }
                }
                for (int j = 0; j < schoolList.length; j++) {
                    switch (i) {
                        case 0:
                        case 1:
                            schoolList[j] = new PrioritySchool(schoolNames[j], 1/*schoolCapacities[j]*/);
                            break;
                        case 8:
                        case 9:
                        	schoolList[j] = new TTCSchool(schoolNames[j], 1);
                        	break;
                        default:
                            schoolList[j] = new ImmediateAcceptanceSchool(schoolNames[j], 1/*schoolCapacities[j]*/);
                    }
                            
                }
                for (int j = 0; j < schoolList.length; j++) {
                    Student[] stList = new Student[studentList.length];
                    for (int k = 0; k < studentList.length; k++) {
                        int index = (i % 2 == 0 ? scPref.getNext(j + 1) - 1 : stPref.getNext(j + 1) - 1);
                        stList[k] = studentList[index];
                    }
                    schoolList[j].setPreference(stList);
                }
                for (int j = 0; j < studentList.length; j++) {
                    School[] scList = new School[schoolList.length];
                    for (int k = 0; k < schoolList.length; k++) {
                        int index = (i % 2 == 0 ? stPref.getNext(j + 1) - 1 : scPref.getNext(j + 1) - 1);
                        scList[k] = schoolList[index];
                    }
                    studentList[j].setPreference(scList);
                }
                //System.out.println(ALGORITHMS[i]);
                double[] output = ((i != 6 && i != 7) ? (new QueueBasedSchoolChoice().start(studentList, schoolList)) : (new StackBasedSchoolChoice().start(studentList, schoolList)));
                print(output);
                if (i % 2 == 1) {
                	//invert direction of L/R of output
                	swap(output, SCORES, "L_Util", "R_Util");
                	swap(output, SCORES, "L_Egal", "R_Egal");
                	swap(output, SCORES, "L_Plur", "R_Plur");
                	//swap(output, SCORES, "J_Envious_L", "J_Envious_R");
                }
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    resultsSquared[i][j] += (output[j] * output[j]);
                    tempResults[i][j] = output[j];
                }
            }
        
        //Hungarian

            for (int i = NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS; i < NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS + 3; i++) {
                //System.out.println(ALGORITHMS[i]);
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                double[] output = (new HungarianAlgorithmWrapper().solve(student, school, schoolCapacities, 
                        ExponentialModel.BORDA, true,
                        (i % 3 == NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS % 3 ? HungarianAlgorithmWrapper.DIRECTION.LEFT : 
                                (i % 3 == (NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS + 1) % 3 ? HungarianAlgorithmWrapper.DIRECTION.RIGHT : HungarianAlgorithmWrapper.DIRECTION.BOTH))));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    resultsSquared[i][j] += (output[j] * output[j]);
                    tempResults[i][j] = output[j];
                }
            }
            
            for (int i = NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS + 3; i < NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS + 6; i++) {
                //System.out.println(ALGORITHMS[i]);
                PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[i]);
                double[] output = (new BinarySearchEgalitarian().solve(student, school, schoolCapacities,
                        (i % 3 == NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS % 3 ? HungarianAlgorithmWrapper.DIRECTION.LEFT : 
                                (i % 3 == (NUMBER_OF_UNIQUE_PROPOSAL_ALGORITHMS + 1) % 3 ? HungarianAlgorithmWrapper.DIRECTION.RIGHT : HungarianAlgorithmWrapper.DIRECTION.BOTH))));
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[i][j] += output[j];
                    resultsSquared[i][j] += (output[j] * output[j]);
                    tempResults[i][j] = output[j];
                }
            }

            //System.out.println(ALGORITHMS[ALGORITHMS.length - 1]); //print "Random"
            PostBox.broadcast(MessageType.OUTPUT, ALGORITHMS[ALGORITHMS.length - 1]);
            
            //Random
            
            int[] matchings = new int[studentCount];
            ArrayList<Integer> pool = new ArrayList<>();
            for (int i = 0; i < schoolCount; i++) {
            	for (int j = 0; j < schoolCapacities[i]; j++) {
            		pool.add(i + 1);
            	}
            }
            for (int i = 0; i < matchings.length; i++) {
            	if (pool.isEmpty()) {
            		System.out.println("Pool Empty: Infinite Pool pending");
            		throw new RuntimeException("Pool Empty");
            	}
            	matchings[i] = pool.remove((int)(Math.random() * pool.size()));
            }
            int blockingPairCount = 0;
            boolean doRandomBlockingPair = true;
            if (doRandomBlockingPair) {
            for (int i = 0; i < studentCount; i++) {
            	for (int j = 0; j < schoolCount; j++) {
            		//if s_i prefers s_j to current partner and vice versa and s_i is not matched to s_j
            		//blockingpaircount++
            		if (matchings[i] == j + 1) {
            			continue;
            		}
            		int[] studentPreference = student.getProfiles()[i].getArray();
            		int[] schoolPreference = school.getProfiles()[j].getArray();

            		int rankOfEnrolledSchool = 0;
            		int rankOfThisSchool = 0;
            		int rankOfWorstStudent = 0;
            		int rankOfThisStudent = 0;
            		for (int sc = 0; sc < studentPreference.length; sc++) {
            			if (studentPreference[sc] == matchings[i]) {
            				rankOfEnrolledSchool = sc+1;
            			}
            			if (studentPreference[sc] == j + 1) {
            				rankOfThisSchool = sc+1;
            			}
            		}
            		for (int st = 0; st < schoolPreference.length; st++) {
            			if (matchings[schoolPreference[st] - 1] == j + 1) {
            				rankOfWorstStudent = st+1;
            			}
            			if (schoolPreference[st] == i + 1) {
            				rankOfThisStudent = st+1;
            			}
            		}
            		if (rankOfThisSchool < rankOfEnrolledSchool && rankOfThisStudent < rankOfWorstStudent) {
            			blockingPairCount++;
            		}
            	}
            }
            }
            double blockingPairPercentage = (double)blockingPairCount/(studentCount * schoolCount);
        	double[] output = Helper.ranksToScore(Helper.matchingToRanks(matchings, student), Helper.invertedMatchingToRanks(matchings, school, schoolCount), blockingPairPercentage);
                print(output);
                for (int j = 0; j < output.length; j++) {
                    results[ALGORITHMS.length - 1][j] += output[j];
                    resultsSquared[ALGORITHMS.length - 1][j] += (output[j] * output[j]);
                    tempResults[ALGORITHMS.length - 1][j] = output[j];
                }

                //Processing Normalised results
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
            sentence += "\t" + s + "\tstd_dev" + s + "\tnormalised " + s;
        }
        sentence += "\tstudentCount\tschoolCount\truns\tstudentMallows\tschoolMallows\texcessCapacity";
        PostBox.broadcast(MessageType.OUTPUT, sentence);
        for (int i = 0; i < ALGORITHMS.length; i++) {
            String out = ALGORITHMS[i];
            for (int j = 0; j < SCORES.length; j++) {
                out += "\t" + Format.Format(results[i][j]/runs);
                out += "\t" + Format.Format((resultsSquared[i][j]/runs - Math.pow(results[i][j]/runs, 2)) * runs/(runs - 1));
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
        int[][] leftRanks = new int[students.length][];
        for (int i = 0; i < students.length; i++) {
        	leftRanks[i] = students[i].getRanks();
        }
        int[][] rightRanks = new int[schools.length][];
        for (int i = 0; i < schools.length; i++) {
        	rightRanks[i] = schools[i].getRanks();
        }
        int blockingPair = 0;
        for (Student s : students) {
            blockingPair += s.howManyBlockingPairs();
        }
        return Helper.ranksToScore(leftRanks, rightRanks, (double)blockingPair/(students.length * schools.length));
//		double[] fromHelper = Helper.ranksToScore(leftRanks, rightRanks, (double)blockingPair/(students.length * schools.length));
//        
//        double leftUtil = 0.0;
//        double leftMin = Double.POSITIVE_INFINITY;
//        double leftNash = 1.0;
//        double leftPlur = 0.0;
//        for (Student s : students) {
//            PostBox.broadcast(MessageType.DETAILS, s.getName() + " is enrolled in " + s.getPartners());
//            leftUtil += (s.getUtility(ExponentialModel.BORDA)/(schools.length - 1));
//            leftMin = Math.min(leftMin, s.getMinUtility(ExponentialModel.BORDA));
//            leftNash *= s.getNashUtility(ExponentialModel.BORDA);
//            leftPlur += s.getUtility(new TopKChoiceModel(1, schools.length));
//        }
//        double rightUtil = 0.0;
//        double rightMin = Double.POSITIVE_INFINITY;
//        double rightNash = 1.0;
//        double rightPlur = 0.0;
//        for (School s : schools) {
//            rightUtil += (s.getUtility(ExponentialModel.BORDA)/(students.length - 1));
//            rightMin = Math.min(rightMin, s.getMinUtility(ExponentialModel.BORDA));
//            rightNash *= s.getNashUtility(ExponentialModel.BORDA);
//            rightPlur += s.getUtility(new TopKChoiceModel(s.capacity, students.length));
//        }
//        double[] fromRaw = new double[]{leftUtil/students.length, leftMin, Math.pow(leftNash, (1.0/students.length)), leftPlur/students.length,
//            rightUtil/students.length, rightMin, Math.pow(rightNash, (1.0/students.length)), rightPlur/students.length,
//            (leftUtil + rightUtil)/(students.length * 2), Math.min(leftMin, rightMin), Math.pow((leftNash * rightNash), (1.0/(students.length * 2))),
//            	(leftPlur + rightPlur)/(students.length * 2),
//            ((double)blockingPair)/(students.length * schools.length)};
//        if (fromHelper.length != fromRaw.length) {
//        	throw new RuntimeException("length mismatch");
//        }
//        for (int i = 0; i < fromHelper.length; i++) {
//        	if (Math.abs(fromHelper[i] - fromRaw[i]) > 0.000001f) {
//        		System.out.println(Arrays.toString(fromHelper));
//        		System.out.println(Arrays.toString(fromRaw));
//        		try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        	}
//        }
//        return fromHelper;
    }
    
    public static void swap(double[] array, String[] ref, String left, String right) {
    	int lIndex = -1; int rIndex = -1;
    	for (int i = 0; i < ref.length; i++) {
    		if (ref[i].equals(left)) {
    			lIndex = i;
    		} else if (ref[i].equals(right)) {
    			rIndex = i;
    		}
    	}
    	if (lIndex >= 0 && lIndex < array.length && rIndex >= 0 && rIndex < array.length) {
    		array[lIndex] += array[rIndex];
    		array[rIndex] = array[lIndex] - array[rIndex];
    		array[lIndex] -= array[rIndex];
    	} else {
    		throw new RuntimeException("swap failed: " + left + " " + right + " " + lIndex + " " + rIndex);
    	}
    }
    
    public static void print(double[] outputs) {
        for (int i = 0; i < outputs.length; i++) {
            PostBox.broadcast(MessageType.OUTPUT, SCORES[i] + " " + Format.Format(outputs[i]));
        }
    }
    
}
