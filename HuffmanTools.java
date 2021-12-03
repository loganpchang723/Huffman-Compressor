import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Class that entails all relevant operations and data to to prepare file for compression/decompression
 * Does so by creating Huffman Encoding Tree and bit codes for each distinct character in the file
 *
 * @author Logan Chang, CS10, PS3, 20F
 */
public class HuffmanTools {
    private HashMap<Character, Integer> charCounts; //map that holds the frequency of each character in the file in char:frequency form
    private BufferedReader input;   //input file reader
    private PriorityQueue<BinaryTree<TreeData>> singleCharPQ;   //Priority Queue that holds BinaryTree representations of each character and its frequency treating minimum character frequency as highest priority (min priority queue)
    private BinaryTree<TreeData> combinedTree;  //final Huffman Encoding Tree that for compression/decompression purposes

    /**
     * TreeComparator class to compare BinaryTree nodes for Priority Queue prioritization
     */
    static class TreeComparator implements Comparator<BinaryTree<TreeData>> {
        /**
         * Implementation of compare that will sort Priority Queue in increasing order of each character's fequency in the file
         *
         * @param b1 First BinaryTree containing TreeData elements to have its frequency compared
         * @param b2 Other BinaryTree containing TreeData elements to have its frequency compared
         * @return -1 if b1's char frequency < b2"s char frequency, 0 if equal, 1 otherwise
         */
        @Override
        public int compare(BinaryTree<TreeData> b1, BinaryTree<TreeData> b2) {
            return Integer.compare(b1.getData().getFreq(), b2.getData().getFreq());
        }
    }

    /**
     * HuffmanTools object with takes in a relative file path and attempts to create a BufferedReader for this file
     *
     * @param fileName Name of file (i.e. 'WarAndPeace')
     */
    public HuffmanTools(String fileName) {
        //open file, if possible
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
        }
    }


    /**
     * Set charCounts to a map of (char:frequency of char in file) entries for file
     *
     * @throws IOException Possible IOException when reading file
     */
    public void setCharCounts() throws IOException{
        HashMap<Character, Integer> map = new HashMap<>();  //map to hold char:frequency entries
        int ascii;  //holds input character's ASCII value
        //reading file
        try {
            //read each char
            while ((ascii = input.read()) != -1) {
                char c = (char) ascii;
                //update frequency map
                //increment char's frequency if we've encountered this char before
                if (map.containsKey(c)) {
                    map.put(c, map.get(c) + 1);
                } else {
                    //otherwise, put in a new entry for a previously unseen char with frequency 1 (we just saw it)
                    map.put(c, 1);
                }
            }
        }
       finally {
            //try closing file
            try {
                input.close();
                System.out.println("File is now closed");
            } catch (IOException e) {
                System.out.println("Cannot close file.\n" + e.getMessage());
            }
        }
        charCounts = map;
//        System.out.println(charCounts);
    }

    /**
     * Sets singleCharPQ to a min Priority Queue of BinaryTrees of each unique character and its frequency, using frequency as the sorting metric
     */
    public void setSingleCharPQ() {
        assert charCounts != null;
        //create TreeComparator that sorts BinaryTree<TreeData> by non-decreasing char frequency
        Comparator<BinaryTree<TreeData>> comparator = new TreeComparator();
        singleCharPQ = new PriorityQueue<>(comparator);
        //add each initial single-character tree to PriorityQueue singleCharPQ
        for (char c : charCounts.keySet()) {
            TreeData curr = new TreeData(c, charCounts.get(c));
            BinaryTree<TreeData> singleCharTree = new BinaryTree<>(curr);
            singleCharPQ.add(singleCharTree);
        }
        System.out.println("Single Char PQ: "+singleCharPQ);
    }

    /**
     * Getter for charCounts (map of char:frequency of char)
     *
     * @return Map of char:frequency of char for each distinct character in file
     */
    public HashMap<Character, Integer> getCharCounts() {
        return charCounts;
    }

    /**
     * Getter for singleCharPQ (Priority Queue of singular character Binary Trees sorted by character's frequency)
     *
     * @return Priority Queue of distinct character's singular Binary Trees sorted by character's frequency
     */
    public PriorityQueue<BinaryTree<TreeData>> getSingleCharPQ() {
        return singleCharPQ;
    }

    /**
     * Creates the final Huffman Encoding Tree for this file and stores it in combinedTree. Also returns said Tree
     *
     * @return Final Huffman Encoding Tree for the entire file
     */
    public BinaryTree<TreeData> createTree() throws IOException{
        //create the single-character trees and enqueue them in the PriorityQueue
        try {
            setCharCounts();
        } catch(IOException e){
            charCounts = getCharCounts();
        }
        setSingleCharPQ();
        //create reference to the PriorityQueue so we don't mess up its content
        PriorityQueue<BinaryTree<TreeData>> temp = getSingleCharPQ();
        //edge if there is only 1 distinct character in the file
        if (temp.size() == 1) {
            //put the single character as the left child of some arbitrary root so it is accessible for decompression
            combinedTree = new BinaryTree<>(new TreeData('~', 0), temp.poll(), null);
        }
        //more than 1 distinct character in the file
        else {
            //combine the two Trees with the lowest frequency values until there is one tree left (the Huffman Encoding Tree)
            while (temp.size() > 1) {
                //remove the top 2 tress from the PQ (those with the lowest character frequencies)
                BinaryTree<TreeData> bt1 = temp.poll();
                BinaryTree<TreeData> bt2 = temp.poll();
                //make them the left and right children, respectively, of a new tree with a dummy char ('~') whose frequency is the sum of the frequencies of b1 and b2 (for PQ sorting purposes)
                BinaryTree<TreeData> combined = new BinaryTree<>(new TreeData('~', bt1.getData().getFreq() + bt2.getData().getFreq()), bt1, bt2);
                //put this new tree into the PriorityQueue
                temp.add(combined);
            }
            //the last remaining tree is the final HuffmanEncoding tree (stored in combinedTree)
            combinedTree = temp.poll();
        }
        System.out.println("combined Tree: "+combinedTree);
        return combinedTree;
    }

    /**
     * Returns Map of (char:huffman encoded bit sequence) according to combinedTree(Huffman Encoding Tree)
     *
     * @return Map of char:bit code sequence (as a String) for each distinct char in the file
     */
    public HashMap<Character, String> retrieveCodes() {
        HashMap<Character, String> map = new HashMap<>();   //map to hold char:bitCode (bitCode as a String) entries
        if (combinedTree != null) {
            //edge if there is only 1 distinct character in the file
            if (combinedTree.size() == 1) map.put(combinedTree.getData().getValue(), "0");
            //otherwise, call the helper method
            else codeRetrieveHelper(combinedTree, map, "");
        }
//        System.out.println(map);
        return map;
    }

    /**
     * Utility function for retrieveCode to create each bitcode sequence for each char and store the char:bitCode pair in a map
     *
     * @param tree Binary Tree of Tree Data elements to be traversed
     * @param map  Map containing char:bit code sequence (as a String) for each distinct char in the file
     * @param code Bit code sequence created for the given character thus far
     */
    public void codeRetrieveHelper(BinaryTree<TreeData> tree, HashMap<Character, String> map, String code) {
        //BASE CASE
        //if the current Node is a leaf, add its character and bitCode sequence to the map
        if (tree.isLeaf()) {
            map.put(tree.getData().getValue(), code);
            return;
        }
        //otherwise, recur on the left child and add a '0' to the bitCode sequence
        if (tree.hasLeft()) codeRetrieveHelper(tree.getLeft(), map, code + "0");
        //recur on the right child and add a '1' to the bitCode sequence
        if (tree.hasRight()) codeRetrieveHelper(tree.getRight(), map, code + "1");
    }

    public static void main(String[] args) throws IOException{
        HuffmanTools ht = new HuffmanTools("inputs/blankTest.txt");
        ht.setCharCounts();
        ht.setSingleCharPQ();
        ht.createTree();

    }
}
