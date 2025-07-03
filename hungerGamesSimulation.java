package games;

import java.util.ArrayList;

public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    public void setupDistricts (String filename) {

        int n = StdIn.readInt();

        for (int i = 0; i < n; i++){
            District d = new District(StdIn.readInt());
            districts.add(d);
        }
    }

    public void setupPeople (String filename) {
    
        int p = StdIn.readInt();
        ArrayList<String> firstnames = new ArrayList<String>();
        ArrayList<String> lastnames = new ArrayList<String>();
        ArrayList<Integer> birthmonths = new ArrayList<Integer>();
        ArrayList<Integer> ages = new ArrayList<Integer>();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<Integer> effectiveness = new ArrayList<Integer>();

        for (int i = 0; i < p; i++){
            firstnames.add(StdIn.readString());
            lastnames.add(StdIn.readString());

            birthmonths.add(StdIn.readInt());

            ages.add(StdIn.readInt());

            ids.add(StdIn.readInt());

            effectiveness.add(StdIn.readInt());
        }

        for (int i = 0; i < p; i++){
            Person person = new Person(birthmonths.get(i), firstnames.get(i), lastnames.get(i), ages.get(i), ids.get(i), effectiveness.get(i));
            if (ages.get(i) >= 12 && ages.get(i) < 18) person.setTessera(true);

            int did = 0;
            for (int j = 0; j < districts.size(); j++){
                if (districts.get(j).getDistrictID() == person.getDistrictID()) did = j;
            }
            if (person.getBirthMonth() % 2 == 0){
                districts.get(did).addEvenPerson(person);
            }
            else{
                districts.get(did).addOddPerson(person);
            }
        }
    }

    public void addDistrictToGame(TreeNode root, District newDistrict) {

        if (root == null){
            TreeNode t = new TreeNode();
            t.setDistrict(newDistrict);
            root = new TreeNode(t.getDistrict(), t.getLeft(), t.getRight());
            game = root;
            districts.remove(newDistrict);
        }
        TreeNode t = root;

        if (t.getLeft() == null && t.getRight() == null){
            if (t.getDistrict().getDistrictID() < newDistrict.getDistrictID()){
                TreeNode a = new TreeNode();
                a.setDistrict(newDistrict);
                t.setRight(a);
                districts.remove(newDistrict);
            }
            else if (t.getDistrict().getDistrictID() > newDistrict.getDistrictID()){
                TreeNode a = new TreeNode();
                a.setDistrict(newDistrict);
                t.setLeft(a);
                districts.remove(newDistrict);
            }
            else {
                districts.remove(newDistrict);
            }
            return;
        }
        else{
            districts.remove(newDistrict);
        }
        boolean done = false;
        while (done == false){
            if (t.getDistrict().getDistrictID() < newDistrict.getDistrictID()){
                if (t.getRight() == null){
                TreeNode a = new TreeNode();
                a.setDistrict(newDistrict);
                t.setRight(a);
                done = true;
                }
                else {
                    t = t.getRight();
                }
            }
            else if (t.getDistrict().getDistrictID() > newDistrict.getDistrictID()){
                if (t.getLeft() == null){
                    TreeNode a = new TreeNode();
                    a.setDistrict(newDistrict);
                    t.setLeft(a);
                    done = true;
                }
                else{
                    t = t.getLeft();
                }
            }   
        }
    }

    public District findDistrict(int id) {

        TreeNode temp = game;
        while (temp != null){
            
            if (id < temp.getDistrict().getDistrictID()) temp = temp.getLeft();
            else if (id > temp.getDistrict().getDistrictID()) temp = temp.getRight();
            else return temp.getDistrict();

        }


        return null; 
    }

    private Person selectRandomPerson(ArrayList<Person> people) {
        int randomIndex = StdRandom.uniform(people.size());
        return people.get(randomIndex);
    }

    private Person oddtessera(TreeNode t, Person a) {
        if (t == null || a != null) {
            return a;
        }
    
        ArrayList<Person> oddPopulation = t.getDistrict().getOddPopulation();
        if (oddPopulation != null) {
            for (int i = 0; i < oddPopulation.size(); i++) {
                if (oddPopulation.get(i).getTessera()) {
                    a = oddPopulation.get(i);
                    return a;
                }
            }
        }
    
        oddtessera(t.getLeft(), a);
        oddtessera(t.getRight(), a);
    
        return a;
    }
    
    private Person eventessera(TreeNode t, Person b) {
        if (t == null || b != null) {
            return b;
        }
    
        ArrayList<Person> evenPopulation = t.getDistrict().getEvenPopulation();
        if (evenPopulation != null) {
            for (int i = 0; i < evenPopulation.size(); i++) {
                if (evenPopulation.get(i).getTessera()) {
                    b = evenPopulation.get(i);
                    return b;
                }
            }
        }
    
        eventessera(t.getLeft(), b);
        eventessera(t.getRight(), b);
    
        return b;
    }

    

    public DuelPair selectDuelers() {
        
        Person a = null;
        Person b = null;
        TreeNode t = game;

        a = oddtessera(t, a);
        b = eventessera(t, b);
        if (a != null && b != null && b.getDistrictID() == a.getDistrictID()){
            if (t.getLeft() != null){
                b = eventessera(t.getLeft(), b);
            }
            else if (t.getRight() != null){
                b = eventessera(t.getRight(), b);
            }
        }
        t = game;
        if (a == null) a = selectRandomPerson(t.getDistrict().getOddPopulation());
       
        if (b != null && a != null && a.getDistrictID() == b.getDistrictID()){
            TreeNode parent = t;
            if (t.getLeft() != null) t = t.getLeft();
            else if (t.getRight() != null) t= t.getRight();
            else {
                if (parent.getRight() != null) t = parent.getRight();
                else t = parent;
            }
            a = selectRandomPerson(t.getDistrict().getOddPopulation());
        }

        if (b == null) b = selectRandomPerson(t.getDistrict().getEvenPopulation());

        if (a != null && b != null && a.getDistrictID() == b.getDistrictID()){
            TreeNode parent = t;
            if (t.getLeft() != null) t = t.getLeft();
            else if (t.getRight() != null) t= t.getRight();
            else {
                if (parent.getRight() != null) t = parent.getRight();
                else t = parent;
            }
            b = selectRandomPerson(t.getDistrict().getEvenPopulation());
        }

        t = game;
        while (t.getDistrict().getDistrictID() != a.getDistrictID()){
            TreeNode parent = t;
            if (t.getLeft() != null) t = t.getLeft();
            else if (t.getRight() != null) t= t.getRight();
            else {
                if (parent.getRight() != null) t = parent.getRight();
                else t = parent;
            }
        }
        t.getDistrict().getOddPopulation().remove(a);


        t = game;
        while (t.getDistrict().getDistrictID() != b.getDistrictID()){
            TreeNode parent = t;
            if (t.getLeft() != null) t = t.getLeft();
            else if (t.getRight() != null) t= t.getRight();
            else {
                if (parent.getRight() != null) t = parent.getRight();
                else t = parent;
            }
        }

        t.getDistrict().getEvenPopulation().remove(b);
        DuelPair z = new DuelPair(a, b);
        return z; 
    }
    
    public void removeRoot(TreeNode root) {
        if (root == null) {
            System.out.println("Tree is empty.");
            return;
        }

        // Case 1: Root has no children
        if (root.getLeft() == null && root.getRight() == null) {
            root = null;
        }
        // Case 2: Root has one child
        else if (root.getLeft() != null && root.getRight() == null) {
            root = root.getLeft();
        } else if (root.getRight() != null && root.getLeft() == null) {
            root = root.getRight();
        }
        // Case 3: Root has two children
        else {
            // Find the minimum node in the right subtree
            TreeNode minRightSubtree = findMin(root.getRight());

            // Copy the value of the minimum node to the root
            root.setDistrict(minRightSubtree.getDistrict());

            // Remove the minimum node from the right subtree
            root.setRight(removeNode(root.getRight(), minRightSubtree.getDistrict().getDistrictID()));
        }
    }

    private TreeNode findMin(TreeNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private TreeNode removeNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }

        if (key < root.getDistrict().getDistrictID()) {
            root.setLeft(removeNode(root.getLeft(), key));
        } else if (key > root.getDistrict().getDistrictID()) {
            root.setRight(removeNode(root.getRight(), key));
        } else {
            // Node with only one child or no child
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }

            // Node with two children
            root.setDistrict(findMin(root.getRight()).getDistrict());
            root.setRight(removeNode(root.getRight(), root.getDistrict().getDistrictID()));
        }

        return root;
    }

    public void eliminateDistrict(int id) {

        TreeNode t = game;

        t = removeNode(t, id);
        game = t;
    }
    

    public void eliminateDueler(DuelPair pair) {
        
        if (pair.getPerson1() == null && pair.getPerson2() == null){
            return;
        }

        if (pair.getPerson1() == null || pair.getPerson2() == null){
            if (pair.getPerson1() != null){
                districts.get(pair.getPerson1().getDistrictID()).getOddPopulation().add(pair.getPerson1());
            }
            else if (pair.getPerson2() != null){
                districts.get(pair.getPerson2().getDistrictID()).getEvenPopulation().add(pair.getPerson2());
            }
        }
        else {
            if (pair.getPerson1().duel(pair.getPerson2()) == pair.getPerson1()){
                TreeNode t = game;
                while (pair.getPerson1().getDistrictID() != t.getDistrict().getDistrictID()){
                    TreeNode parent = t;
                    if (t.getLeft() != null) t = t.getLeft();
                    else if (t.getRight() != null) t= t.getRight();
                    else {
                        if (parent.getRight() != null) t = parent.getRight();
                        else t = parent;
                    }
                }
                t.getDistrict().addOddPerson(pair.getPerson1());


                //remove loser
                t = game;
                while (pair.getPerson2().getDistrictID() != t.getDistrict().getDistrictID()){
                    if (t.getLeft() != null) t = t.getLeft();
                    else if (t.getRight() != null) t= t.getRight();
                }
                if (t.getDistrict().getEvenPopulation().size()<= 0){
                    eliminateDistrict(t.getDistrict().getDistrictID());
                }
            }
            else if (pair.getPerson1().duel(pair.getPerson2()) == pair.getPerson2()){
                TreeNode t = game;
                while (pair.getPerson2().getDistrictID() != t.getDistrict().getDistrictID()){
                    TreeNode parent = t;
                    if (t.getLeft() != null) t = t.getLeft();
                    else if (t.getRight() != null) t= t.getRight();
                    else {
                        if (parent.getRight() != null) t = parent.getRight();
                        else t = parent;
                    }
                }
                t.getDistrict().addEvenPerson(pair.getPerson2());

                //remove loser

                t = game;
                while (pair.getPerson1().getDistrictID() != t.getDistrict().getDistrictID()){
                    if (t.getLeft() != null) t = t.getLeft();
                    else if (t.getRight() != null) t= t.getRight();
                }
                if (t.getDistrict().getOddPopulation().size()<= 0){
                    eliminateDistrict(t.getDistrict().getDistrictID());
                }
            }
        }


    }

    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    public TreeNode getRoot() {
        return game;
    }
}
