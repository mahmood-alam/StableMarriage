/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching marriage) {
        /* TODO implement this function */
        ArrayList<Integer> matches = marriage.getResidentMatching();
        for(int i=0;i<matches.size();i++){
            for(int j=0;j<matches.size(); j++){
                int h1 = matches.get(i);
                int h2 = matches.get(j);
                if(h2==-1 && h1!=-1) {
                    ArrayList<Integer> hospital1 = marriage.getHospitalPreference().get(h1);
                    if(hospital1.contains(i)&&hospital1.contains(j)){
                        if (hospital1.indexOf(j) < hospital1.indexOf(i)) {
                            return false;
                        }
                    }
                }
                if(h2!=-1 && h1!=-1){
                    ArrayList<Integer> hospital1 = marriage.getHospitalPreference().get(h1);
                    ArrayList<Integer> hospital2 = marriage.getHospitalPreference().get(h2);
                    if(hospital1.contains(i)&&hospital1.contains(j)&&hospital2.contains(i)&&hospital2.contains(j)){
                        if(hospital1.indexOf(j)<hospital1.indexOf(i) && hospital2.indexOf(i)<hospital1.indexOf(j)){
                            return false;
                        }
                    }

                }


            }
        }
        ArrayList<Integer> slots = marriage.getHospitalSlots();
        int total = totalSlots(slots);
        for(Integer hospital:marriage.getResidentMatching()){
            if(hospital!=-1){
                total--;
            }
        }
        if(total>0){
            return false;
        }
        return true; /* TODO remove this line */
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageBruteForce_residentoptimal(Matching marriage) {
        /* TODO implement this function */
        int n = marriage.getResidentCount();
        int slots = marriage.totalHospitalSlots();

        Permutation p = new Permutation(n, slots);
        ArrayList<Matching> list = new ArrayList<Matching>();
        ArrayList<Integer> listNum = new ArrayList<Integer>();
        Matching matching;
        while ((matching = p.getNextMatching(marriage)) != null) {
            if (isStableMatching(matching)) {
                list.add(matching);
                listNum.add(calculatePreference(matching));
            }
        }
        int minIndex = listNum.indexOf(Collections.min(listNum));
        return new Matching(marriage,list.get(minIndex).getResidentMatching());

    }

    public static int calculatePreference(Matching marriage){
        int total=0;
        for(int i=0; i<marriage.getResidentMatching().size(); i++){
            if(marriage.getResidentMatching().get(i)!=-1){
                int j=0;
                while(marriage.getResidentPreference().get(i).get(j)!=marriage.getResidentMatching().get(i)){
                    total++;
                    j++;
                }
            }
        }
        return total;
    }

    /**
     * Determines a resident optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_residentoptimal(Matching marriage) {
        /* TODO implement this function */
        int unmarried = marriage.getResidentCount();
        ArrayList<Integer> resident_matching = new ArrayList<Integer>(marriage.getResidentCount());
        for(int i=0; i<marriage.getResidentCount(); i++){
            resident_matching.add(-1);
        }

        ArrayList<Integer> slots = marriage.getHospitalSlots();

        while(unmarried>0 && totalSlots(slots)>0){
            for(int i=0;i<marriage.getResidentCount(); i++){
                if(unmarried==0 || totalSlots(slots)==0){
                    break;
                }
                for(int j=0; j<marriage.getResidentPreference().get(i).size(); j++){
                    int entry = marriage.getResidentPreference().get(i).get(j);
                    if(resident_matching.contains(entry)){
                        int resident = resident_matching.indexOf(entry);
                        if(slots.get(entry)>0){
                            resident_matching.set(i, entry);
                            unmarried--;
                            int hospital = slots.get(entry);
                            hospital--;
                            slots.set(entry,hospital);
                            j=marriage.getResidentPreference().get(i).size();
                        }
                        else if(marriage.getHospitalPreference().get(entry).indexOf(i)<marriage.getHospitalPreference().get(entry).indexOf(resident)){
                            resident_matching.set(i,entry);

                            resident_matching.set(resident,i);
                            j=marriage.getResidentPreference().get(i).size();
                        }
                    } else{
                        int hospital = slots.get(entry);
                        if(hospital>0){
                            resident_matching.set(i, entry);
                            unmarried--;
                            hospital--;
                            slots.set(resident_matching.get(i),hospital);
                            j=marriage.getResidentPreference().get(i).size();
                        }

                    }
                }
            }
        }
        Matching newMarriage = new Matching(marriage, resident_matching);
        return newMarriage; /* TODO remove this line */
    }

//    public static boolean notOptimal(Matching marriage){
//        for(Integer hospital:marriage.getResidentMatching()){
//            for(int i=0; i<marriage.getHospitalPreference().get(hospital).size(); i++){
//                if(marriage.getHospitalPreference().get(hospital).get(i)==marriage.getResidentMatching().indexOf(hospital)){
//                    break;
//                }
//                if(marriage.getResidentMatching().get(marriage.getHospitalPreference().get(hospital).get(i))==-1){
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    /**
     * Determines a hospital optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_hospitaloptimal(Matching marriage) {
        /* TODO implement this function */

        ArrayList<Integer> resident_matching = new ArrayList<Integer>(marriage.getResidentCount());
        for(int i=0; i<marriage.getResidentCount(); i++) {
            resident_matching.add(-1);
        }
        ArrayList<Integer> slots = marriage.getHospitalSlots();
        int unmarried = totalSlots(slots);

        while(unmarried>0){
            for(int i=0;i<marriage.getHospitalCount(); i++){
                if(unmarried==0){
                    break;
                }
                for(int j=0; j<marriage.getHospitalPreference().get(i).size(); j++){
                    int entry = marriage.getHospitalPreference().get(i).get(j);
                    if(resident_matching.contains(i)){
                        int resident = resident_matching.indexOf(entry);
                        if(slots.get(i)>0){
                            resident_matching.set(entry, i);
                            unmarried--;
                            int hospital = slots.get(i);
                            hospital--;
                            slots.set(i,hospital);
                            if(slots.get(i)==0){
                                j=marriage.getHospitalPreference().get(i).size();
                            }
                        }
                        else if(marriage.getResidentPreference().get(i).indexOf(entry)<marriage.getResidentPreference().get(i).indexOf(resident)){
                            resident_matching.set(entry,i);

                            resident_matching.set(i,resident);
                            if(slots.get(i)==0){
                                j=marriage.getHospitalPreference().get(i).size();
                            }
                        }
                    } else{
                        int hospital = slots.get(i);
                        if(hospital>0){
                            resident_matching.set(entry, i);
                            unmarried--;
                            hospital--;
                            slots.set(resident_matching.get(entry),hospital);
                            if(slots.get(resident_matching.get(entry))==0){
                                j=marriage.getHospitalPreference().get(i).size();
                            }

                        }

                    }
                }
            }
        }
        Matching newMarriage = new Matching(marriage, resident_matching);
        return newMarriage; /* TODO remove this line */
    }

    public static int totalSlots(ArrayList<Integer> slots){
        int total=0;
        for(Integer entry:slots){
            total+=entry;
        }
        return total;
    }
}
