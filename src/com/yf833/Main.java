package com.yf833;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Main {

    public static ArrayList<String> clauses = new ArrayList<>();
    public static HashSet<String> atoms = new HashSet<>();

    public static void main(String[] args) throws FileNotFoundException {

        // initialize clauses and atoms
        getInput(args[0]);

        System.out.println(clauses.toString());
        System.out.println(atoms.toString());


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

        //base case: success -- all clauses are satisfied
        if(clauses.isEmpty()){
            for(String atom : atoms){
                if(assignments.get(atom) == null){
                    assignments.put(atom, true);
                    return assignments;
                }
            }
        }
        //base case: failure -- some clause in S is empty
        else if(hasEmptyClause(clauses)){
            return null;
        }

        // (2) Literal Elimination or Forced Assignment //

        // if there is a single literal in the set of clauses (with no negation)
        // remove all instances of that
        else if(getSingleLiteral(clauses) != -1){

        }
//        then { V := obvious_assign(L,V);
//            delete every clause containing L from S;
//        }
//        else if (there exists a clause C in S       /* Forced assignment */
//        containing a single literal L)
//        then { V := obvious_assign(L,V)
//            S := propagate(atom(L), S, V);
//        }
//        else exitloop;  /* No easy cases found */
//    }   /* endloop */


    }




//
//    /*
//    in  ATOMS: set of propositional atoms;
//        S : Set of propositional formulas in CNF (clauses)
//        V : assignments
//
//    return either a valuation on ATOMS satisfying S or NIL if none exists.
//
//    var V : array[ATOMS];
//    { for (A in ATOMS) do V[A] := UNBOUND;
//        return dp1(ATOMS,S,V) }
//    end dp.
//
//    dp1(ATOMS,S,V){
//
//    loop {
//
///*  BASE OF THE RECURSION: SUCCESS OR FAILURE */
//        if (S is empty)   /*  Success: All clauses are satisfied */
//        { for (A in ATOMS)
//            if V[A] == UNBOUND then assign V[A] either TRUE or FALSE;
//            return(V);
//        }
//        else if (some clause in S is empty) /* Failure: Some clause */
//        then return(NIL);             /* is unsatisfiable under V */;
//
///* EASY CASES: PURE LITERAL ELIMINATION AND FORCED ASSIGNMENT */
//        else if (there exists a literal L in S /* Pure literal elimination */
//        such that the negation of L does not appear in S)
//        then { V := obvious_assign(L,V);
//            delete every clause containing L from S;
//        }
//        else if (there exists a clause C in S       /* Forced assignment */
//        containing a single literal L)
//        then { V := obvious_assign(L,V)
//            S := propagate(atom(L), S, V);
//        }
//        else exitloop;  /* No easy cases found */
//    }   /* endloop */
//
//
///* PICK SOME ATOM AND TRY EACH ASSIGNMENT IN TURN */
//        pick atom A such that V[A] == UNBOUND;  /* Try one assignment */
//        V[A] := TRUE;
//        S1 := propagate(A, S, V);
//        VNEW := dp1(ATOMS,S1,V);
//        if (VNEW != NIL) then return(VNEW);
//
///* IF V[A] := TRUE didn't work, try V[A} := FALSE;
//V[A] := FALSE;
//S1 := propagate(A, S, V);
//return(dp1(ATOMS,S1,V));
//} end dp1
//
//propagate(A,S,V)
//{ for each clause C in S do
//     if ((A in C and V[A]=TRUE) or (~A in C and V[A]==FALSE))
//      then delete C from S
//     else if (A in C and V[A]==FALSE) then delete A from C
//     else if (~A in C and V[A]==TRUE) then delete ~A from C;
//  return S;
//}
//end propagate.
//
//obvious_assign(L,V) {
//  if (L is an atom A) then V[A] := TRUE;
//  else if (L has the form ~A) then V[A] := FALSE;
//}
//
//*/


    public static void obviousAssign(String L, HashSet<String> atoms, HashMap<String, Boolean> assignments){
        if(atoms.contains(L)){
            assignments.put(L, true);
        }
        else{
            assignments.put(L, false);
        }
    }


    public static HashMap<String, Boolean> propagate(String L, ArrayList<String> clauses, HashMap<String, Boolean> assignments){
        for(int i=0; i<clauses.size(); i++) {
            if (clauses.get(i).contains(L) && assignments.get(L)==true || clauses.get(i).contains("-"+L) && assignments.get(L) == false){
                clauses.remove(i);
            }
            else if (clauses.get(i).contains(L) && assignments.get(L) == false){
                clauses.set(i, removeFromClause(L, clauses.get(i)));
            }
            else if (clauses.get(i).contains("-"+L) && assignments.get(L) == true){
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


}






