package com.yf833;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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






