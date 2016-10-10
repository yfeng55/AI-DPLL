import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DavisPutnam{

    public static ArrayList<String> initclauses = new ArrayList<>();
    public static HashSet<String> initatoms = new HashSet<>();
    public static HashMap<String, Boolean> solution;
    public static ArrayList<String> ignoredlines = new ArrayList<>();

    public static void main(String[] args) throws IOException {

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

        solution = DavisPutnam(new ArrayList<>(initclauses), new HashSet<>(initatoms));
        System.out.println(solution.toString() + "\n\n");

        printOutput(solution, args[1]);
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
                    if(assignments.get(atom) == null){ assignments.put(atom, true); }
                }

                solution = assignments;
                return assignments;
            }

            //base case: failure -- some clause in the set is empty
            else if(hasEmptyClause(clauses)){
                System.out.println("FAIL: some clause in the set is empty\n");
                return null;
            }
            else{
                break;
            }
        }

        // Pick an unassigned atom and try T/F assignments, then propagating //
        String L = getNextUnassignedAtom(assignments);
        System.out.println("\n--> try " + L + " = true");

        // try setting L to true and propagating //
        HashMap<String, Boolean> assignments_T = new HashMap<>(assignments);
        assignments_T.put(L, true);
        ArrayList<String> clauses_T = propagate(L, new ArrayList<>(clauses), assignments_T);
        HashMap<String, Boolean> newassignments = DPL(atoms, new ArrayList<>(clauses_T), assignments_T);

        if(newassignments != null){
            return newassignments;
        }

        // If L = TRUE didn't work, try L = FALSE //
        HashMap<String, Boolean> assignments_F = new HashMap<>(assignments);
        assignments_F.put(L, false);
        System.out.println("--> try " + L + " = false");
        ArrayList<String> clauses_F = propagate(L, new ArrayList<>(clauses), assignments_F);
        return DPL(atoms, new ArrayList<>(clauses_F), assignments_F);

    }

    //PROPAGATE: return a new set of clauses
    public static ArrayList<String> propagate(String L, ArrayList<String> oldclauses, HashMap<String, Boolean> assignments){

        ArrayList<String> newclauses = new ArrayList<>(oldclauses);
        HashSet<Integer> items_to_remove = new HashSet<>();

        System.out.println("propagate(): " + assignments.toString() + " for " + newclauses.toString());

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
        L = L.replaceAll("\\s+","");
        for(int i=0; i<clauses.size(); i++){
            if(clauses.get(i).contains(" " + L) || clauses.get(i).replaceAll("\\s+","").equals(L)){
                clauses.remove(i);
                System.out.println("====> removing clause " + clauses.get(i));
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
                ignoredlines.add(nextclause);
                break;
            }
            initclauses.add(nextclause);
        }
        //store ignored lines in a list
        while(s.hasNextLine()){
            String nextclause = s.nextLine();
            ignoredlines.add(nextclause);
        }

        //initialize atoms (drop negative sign when comparing)
        for(String clause : initclauses){
            for(String atom : clause.split(" +")){
                if(atom.charAt(0) == '-' && !initatoms.contains(atom.substring(1)) ){
                    initatoms.add(atom.substring(1));
                }
                else if(!initatoms.contains(atom) && !initatoms.contains(atom.substring(1)) ){
                    initatoms.add(atom);
                }
            }
        }

        //add spaces to beginning of clauses (so that negation symbols are detectable consistently
        for(int i=0; i<initclauses.size(); i++){
            if(initclauses.get(i).charAt(0) != '-'){
                initclauses.set(i, " " + initclauses.get(i));
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


    //print assignments to a file
    public static void printOutput(HashMap<String, Boolean> solution, String output) throws IOException {

        ArrayList<String> sortedkeys = new ArrayList<>();
        ArrayList<String> outputcontent = new ArrayList<>();

        if(!solution.isEmpty()){
            //get keys in sorted order
            for(String k : solution.keySet()){
                sortedkeys.add(k);
            }
            Collections.sort(sortedkeys, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (Integer.parseInt(o1) > Integer.parseInt(o2)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            //print atom assignments in sorted order
            for(String key : sortedkeys){
                if(solution.get(key) == true){
                    outputcontent.add(key + " T");
                }else{
                    outputcontent.add(key + " F");
                }
            }
        }


        //print ignored lines at the bottom
        for(String line : ignoredlines){
            outputcontent.add(line);
        }

        //print output to a file
        Path file = Paths.get(output);
        Files.write(file, outputcontent, Charset.forName("UTF-8"));

    }



}






