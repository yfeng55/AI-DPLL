package com.yf833;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static ArrayList<String> clauses = new ArrayList<>();
    public static HashSet<String> atoms = new HashSet<>();

    public static void main(String[] args) throws FileNotFoundException {

        // initialize clauses and atoms
        getInput(args[0]);

//        System.out.println(clauses.toString());
//        System.out.println(atoms.toString());

        System.out.println();
        System.out.println(DavisPutnam(clauses, atoms).toString() + "\n\n");

    }

    // given a set of clauses and atoms;
    // return a HashMap of boolean assignments that satisfy all the clauses (or null)
    public static HashMap<String, Boolean> DavisPutnam(ArrayList<String> clauses, HashSet<String> atoms){

        //initialize a hashmap
        HashMap<String, Boolean> assignments = new HashMap<>();
        for(String atom : atoms){
            assignments.put(atom, null);
        }
        for(String atom : atoms){
            return DPL(atoms, clauses, assignments);
        }
        return null;
    }


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
                propagate(L, clauses, assignments);
            }
            else{
                break;
            }
        }

        ///* PICK SOME ATOM AND TRY EACH ASSIGNMENT IN TURN */

        // pick atom L such that V[L] == UNBOUND;  /* Try one assignment */
        String L = getNextUnassignedAtom(assignments);
        assignments.put(L, true);
        System.out.println("\n--> try " + L + " = true");

        propagate(L, clauses, assignments);
        HashMap<String, Boolean> newassignments = DPL(atoms, clauses, assignments);
        if(newassignments != null){
            return newassignments;
        }

        ///* IF V[A] := TRUE didn't work, try V[A} := FALSE;
        assignments.put(L, false);
        System.out.println("--> try " + L + " = false");
        propagate(L, clauses, assignments);
        return DPL(atoms, clauses, assignments);

    }


    // OBVIOUSASSIGN
    public static void obviousAssign(String L, HashSet<String> atoms, HashMap<String, Boolean> assignments){
        if(atoms.contains(L)){
            assignments.put(L, true);
        }
        else{
            assignments.put(L, false);
        }
    }


    //PROPAGATE
    public static HashMap<String, Boolean> propagate(String L, ArrayList<String> clauses, HashMap<String, Boolean> assignments){

        System.out.println("(propagate()): " + assignments.toString());
        for(int i=0; i<clauses.size(); i++) {
            if ((clauses.get(i).contains(" "+L) && assignments.get(L)==true) || (clauses.get(i).contains("-"+L) && assignments.get(L) == false)){
                System.out.println("====> removing " + clauses.get(i));
                clauses.remove(i);
            }
            else if (clauses.get(i).contains(" "+L) && assignments.get(L) == false){
                System.out.println("====> removing " + L + " from " + clauses.get(i));
                clauses.set(i, removeFromClause(L, clauses.get(i)));
            }
            else if (clauses.get(i).contains("-"+L) && assignments.get(L) == true){
                System.out.println("====> removing " + "-"+L + " from " + clauses.get(i));
                clauses.set(i, removeFromClause("-"+L, clauses.get(i)));
            }
        }
        return assignments;
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
            clauses.add(nextclause);
        }

        //initialize atoms (drop negative sign when comparing)
        for(String clause : clauses){
            for(String atom : clause.split(" +")){
                if(!atoms.contains(atom) && !atoms.contains(atom.substring((1)))){
                    atoms.add(atom);
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






