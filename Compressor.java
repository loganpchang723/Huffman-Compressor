import java.io.*;
import java.util.HashMap;

/**
 * Handles immediate file compression/decompression based on Huffman Encoding Data
 * NOTE: all files are taken from 'inputs' directory of IntelliJ directory
 *
 * @author Logan Chang, CS10, PS3, 20F
 */
public class Compressor {
    private final HuffmanTools ht;
    private final String filename;
    private BinaryTree<TreeData> codeTree;

    /**
     * Constructs Compressor object
     *
     * @param filename Name of the original file as a String
     */
    public Compressor(String filename) {
        this.filename = filename;
        //create a HuffmanTools object for the given file
        ht = new HuffmanTools(this.filename);
    }

    /**
     * Gets the map of (char:bit code sequence) for each distinct character in the file
     *
     * @return Map of char:bit code sequence for each distinct charter in the file
     */
    public HashMap<Character, String> getCodeMap() throws IOException{
        //set codeTree to the Huffman Encoding Map
        codeTree = ht.createTree();
        //return HuffmanTools Map of char:bit code sequence
        return ht.retrieveCodes();
    }

    /**
     * Gets the Huffman Encoding Tree for the file
     *
     * @return Huffman Encoding Tree for the file as a Binary Tree of TreeData elements
     */
    public BinaryTree<TreeData> getCodeTree() {
        return codeTree;
    }

    /**
     * Performs file compression on original ('fileName'.txt) file and stores compressed file in ('fileName'_compressed.txt)
     *
     * @param filePath Relative file path as a String
     * @throws IOException Possible Exception when opening/reading/closing files
     */
    public void compressFile(String filePath) throws IOException {
        //create the name for the compressed file
        String shortName = filePath.substring(0, filePath.length() - 4);
        String outputFile = shortName + "_compressed.txt";
        //compressed file requires reading from a plain txt file and writing bits to compressed file
        BufferedReader input = null;    //plain txt file reader
        BufferedBitWriter bitOutput = new BufferedBitWriter(outputFile);    //bit writer to compressed file
        //try opening original file
        try {
            input = new BufferedReader(new FileReader(this.filename));
            System.out.println("Opened original file");
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open original file.\n" + e.getMessage());
        }
        int ascii;  //holds ASCII value of each character read in the original txt file
        HashMap<Character, String> codeMap = getCodeMap();  //store the map of char:bit code sequence
        //reading file
        try {
            //read each char, while there is one in the file
            while ((ascii = input.read()) != -1) {
                String charCode = codeMap.get((char) ascii);    //get the bit code sequence from codeMap for read character
                for (char c : charCode.toCharArray()) {
                    System.out.println("writing..."+c);
                    bitOutput.writeBit(c != '0');   //write that character's bit code sequence to the compressed file
                }
            }
        }
        finally {
            //try closing input file (compressed file)
            try {
                input.close();
                System.out.println("Original file is now closed");
            }
            catch (IOException e) {
                System.err.println("Cannot close original file.\n" + e.getMessage());
            }
            //try closing output file (compressed file)
            try {
                bitOutput.close();
                System.out.println("Compressed file is now closed");
            }
            catch (IOException e) {
                System.err.println("Cannot close compressed file.\n" + e.getMessage());
            }
        }
    }

    /**
     * Performs decompression on compressed file ('fileName'_compressed.txt) and writes decompressed text to ('fileName'_decompressed.txt)
     *
     * @param filePath Relative file path as a String
     * @throws IOException Possible Exception when opening/reading/closing files
     */
    public void decompressFile(String filePath) throws IOException {
        //create file names for compressed and decompressed file
        String shortName = filePath.substring(0, filePath.length() - 4);
        String inputFile = shortName + "_compressed.txt";
        String outputFile = shortName + "_decompressed.txt";
        //decompression requires reading bits from the compressed file and writing plain text to the decompressed file
        BufferedBitReader bitInput;  //bit code reader from the compressed file
        BufferedWriter output = new BufferedWriter(new FileWriter(outputFile)); //plain text writing to the decompressed file
        //try opening compressed file
        try {
            bitInput = new BufferedBitReader(inputFile);
            System.out.println("Opened compressed file");
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open compressed file.\n" + e.getMessage());
            return;
        }
        //reading compressed file
        try {
            //begin traversal from root of the Huffman Encoding Tree
            BinaryTree<TreeData> traverser = getCodeTree();
            //read each bit from compressed file
            while (bitInput.hasNext()) {
                boolean bit = bitInput.readBit();
                System.out.println("bit: "+bit);
                //if the bit is true (1), we traverse right in the tree, else we traverse left
                if (bit) traverser = traverser.getRight();  //bit == 1
                else traverser = traverser.getLeft(); //bit == 0
                //if we reach leaf, write the corresponding char at that leaf to the decompressed file and start traversing from the root of the Huffman Encoding Tree again
                if (traverser.isLeaf()) {
                    output.write(traverser.getData().getValue());
                    traverser = getCodeTree();
                }
            }
        }
        finally {
            //try closing input file (compressed file)
            try {
                bitInput.close();
                System.out.println("Compressed file is now closed");
            }
            catch (IOException e) {
                System.err.println("Cannot close compressed file.\n" + e.getMessage());
            }
            //try closing output file (decompressed file)
            try {
                output.close();
                System.out.println("Decompressed file is now closed");
            }
            catch (IOException e) {
                System.err.println("Cannot close decompressed file.\n" + e.getMessage());
            }
        }
    }

    /**
     * Hardcoded Driver to create and test file compression/decompression
     *
     * @param args Command Line arguments (not used)
     * @throws IOException compressFile and decompressFile could raise IOException
     */
    public static void main(String[] args) throws IOException {
        String fileName = "inputs/helloTest.txt";
        Compressor comp = new Compressor(fileName);
        comp.compressFile(fileName);
        comp.decompressFile(fileName);
    }
}

