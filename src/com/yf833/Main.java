package com.yf833;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static ArrayList<String> initclauses = new ArrayList<>();
    public static HashSet<String> initatoms = new HashSet<>();

    public static void main(String[] args) throws FileNotFoundException {

        // initialize clauses and atoms
        getInput(args[0]);

        System.out.println("CLAUSES: ");
        for(String s : initclauses){
            System.out.println(s);
        }
        System.out.println("--------------------\nATOMS: ");
        System.out.println(initatoms.toString());
        System.out.println("--------------------\n");

        System.out.println();
        System.out.println(DavisPutnam(new ArrayList<>(initclauses), new HashSet<>(initatoms)).toString() + "\n\n");
    }

    // given a set of clauses and atoms;
    // return a HashMap of boolean assignments that satisfy all the clauses (or null)
    public static HashMap<String, Boolean> DavisPutnam(ArrayList<String> clauses, HashSet<String> atoms){

        //initialize a hashmap
        HashMap<String, Boolean> assignments = new HashMap<>();
        for(String atom : atoms){
            assignments.put(atom, null);
        }

        return DPL(atoms, clauses, assignments);
    }


    // DPL METHOD (RECURSIVE)
    public static HashMap<String, Boolean> DPL(HashSet<String> atoms, ArrayList<String> clauses, HashMap<String, Boolean> assignments){

        // (1) Determine Success or Failure //
        while(true){
            //base case: success -- all clauses are satisfied
            if(clauses.isEmpty()){
                System.out.println("SUCCESS: all clauses are satisfied\n");
                for(String atom : atoms){
                    if(assignments.get(atom) == null){
                        assignments.put(atom, true);
                        return assignments;
                    }
                }
            }
            //base case: failure -- some clause in the set is empty
            else if(hasEmptyClause(clauses)){
                System.out.println("FAIL: some clause in the set is empty\n");
                return null;
            }

            // (2) Literal Elimination or Forced Assignment //

            // if there is a single literal in the set of clauses (with no negation)
            // remove all instances of that
            else if(getSingleLiteral(clauses) != -1){
                String L = clauses.get(getSingleLiteral(clauses));
                obviousAssign(L, atoms, assignments);
                deleteInstancesContainingL(L, clauses);
            }
            // if there is a clause in the set that contains a single literal
            else if (getSingleLiteral(clauses) != -1){
                String L = clauses.get(getSingleLiteral(clauses));
                obviousAssign(L, atoms, assignments);
                clauses = propagate(L, new ArrayList<>(clauses), new HashMap<>(assignments));
            }
            else{
                break;
            }
        }

        // PICK SOME ATOM AND TRY EACH ASSIGNMENT IN TURN //
        // pick an unbound atom L
        String L = getNextUnassignedAtom(assignments);
        assignments.put(L, true);
        System.out.println("\n--> try " + L + " = true");

        ArrayList<String> clauses_T = propagate(L, new ArrayList<>(clauses), new HashMap<>(assignments));
        HashMap<String, Boolean> newassignments = DPL(atoms, new ArrayList<>(clauses_T), new HashMap<>(assignments));
        if(newassignments != null){
            return newassignments;
        }

        // IF L = TRUE didn't work, try L = FALSE //
        assignments.put(L, false);
        System.out.println("--> try " + L + " = false");
        ArrayList<String> clauses_F = propagate(L, new ArrayList<>(clauses), new HashMap<>(assignments));
        return DPL(atoms, new ArrayList<>(clauses_F), new HashMap<>(assignments));

    }


    // OBVIOUSASSIGN
    public static void obviousAssign(String L, HashSet<String> atoms, HashMap<String, Boolean> assignments){
        if(atoms.contains(L)){ assignments.put(L, true); }
        else{ assignments.put(L, false); }
    }


    //PROPAGATE: return a new set of clauses
    public static ArrayList<String> propagate(String L, ArrayList<String> oldclauses, HashMap<String, Boolean> assignments){

        ArrayList<String> newclauses = new ArrayList<>(oldclauses);
        HashSet<Integer> items_to_remove = new HashSet<>();

        System.out.println("(propagate()): " + assignments.toString());
        System.out.println(newclauses.toString());

        for(int i=0; i<newclauses.size(); i++) {
            if ((newclauses.get(i).contains(" "+L) && assignments.get(L)==true) || (newclauses.get(i).contains("-"+L) && assignments.get(L) == false)){
                System.out.println("====> removing clause " + newclauses.get(i));
                items_to_remove.add(i);
            }
            else if (newclauses.get(i).contains(" "+L) && assignments.get(L) == false){
                System.out.println("====> removing " + L + " from " + newclauses.get(i));
                newclauses.set(i, removeFromClause(L, newclauses.get(i)));
            }
            else if (newclauses.get(i).contains("-"+L) && assignments.get(L) == true){
                System.out.println("====> removing " + "-"+L + " from " + newclauses.get(i));
                newclauses.set(i, removeFromClause("-"+L, newclauses.get(i)));
            }
        }

        //remove all clauses that need to be removed
        for(int i=newclauses.size()-1; i>=0; i--){
            if(items_to_remove.contains(i)){
                newclauses.remove(i);
            }
        }
        return newclauses;
    }


    public static String removeFromClause(String remove_item, String clause){
        return clause.replace(remove_item,"");
    }


    public static boolean hasEmptyClause(ArrayList<String> clauses){
        for(String clause : clauses){
            if (clause.replaceAll("\\s+","").equals("")){
                return true;
            }
        }
        return false;
    }

    public static int getSingleLiteral(ArrayList<String> clauses){
        for(int i=0; i<clauses.size(); i++){
            if (clauses.get(i).replaceAll("\\s+","").length() == 1 && !containsNegative(clauses, clauses.get(i))){
                return i;
            }
        }
        return -1;
    }

    public static boolean containsNegative(ArrayList<String> clauses, String L){
        for(int i=0; i<clauses.size(); i++){
            if (clauses.get(i).replaceAll("\\s+","").equals("-" + L)){
                return true;
            }
        }
        return false;
    }

    public static void deleteInstancesContainingL(String L, ArrayList<String> clauses){
        for(int i=0; i<clauses.size(); i++){
            if(clauses.get(i).contains(" " + L)){
                clauses.remove(i);
            }
        }
    }


    //parse through input file and assign clauses and atoms
    public static void getInput(String inputfile) throws FileNotFoundException {

        File file = new File(inputfile);
        Scanner s = new Scanner(file);

        //initilialize clauses
        while(s.hasNextLine()){
            String nextclause = s.nextLine();
            if(nextclause.replaceAll("\\s+","").equals("0")){
                break;
            }
            initclauses.add(nextclause);
        }

        //initialize atoms (drop negative sign when comparing)
        for(String clause : initclauses){
            for(String atom : clause.split(" +")){
                if(!initatoms.contains(atom) && !initatoms.contains(atom.substring((1)))){
                    initatoms.add(atom);
                }
            }
        }

    }

    //find the next null item in the HashMap and return it
    public static String getNextUnassignedAtom(HashMap<String, Boolean> assignments){

        for (Map.Entry<String, Boolean> entry : assignments.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(value == null){
                return key;
            }
        }
        return null;
    }


}






