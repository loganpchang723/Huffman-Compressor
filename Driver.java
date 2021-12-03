import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Driver application that runs file compression and decompression
 * Will also check if decompressed file and original file have equivalent content
 *
 * @author Logan Chang, CS10, PS3, 20F
 * @author Ashna Kumar, CS10, PS3, 20F
 */
public class Driver {
    /**
     * Driver code to run the file compression and decompression
     * Uses user input to read in the original txt file
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file name: ");
        String fname = scan.nextLine().strip().replaceAll("\\s+", "");
        if (fname.endsWith(".txt")) fname = fname.substring(0, fname.length() - 4);
        if(fname.startsWith("inputs/")) fname = fname.substring(7);
        String filePath = "inputs/" + fname + ".txt";
        compressAndDecompress(filePath);
    }

    /**
     * Checks equality of contents between two files
     *
     * @param f1 Path object of relative path for one file
     * @param f2 Path object of relative path for other file
     * @return true if files have identical contents, else false
     */
    public static boolean isEqual(Path f1, Path f2) {
        try {
            //if files are different size, then for sure not equal
            if (Files.size(f1) != Files.size(f2)) return false;
            //otherwise, compare the bytes in each file and check for array equality
            byte[] firstFile = Files.readAllBytes(f1);
            byte[] secondFile = Files.readAllBytes(f2);
            return Arrays.equals(firstFile, secondFile);
        } catch (IOException e) {  //possible file reading error when reading bytes
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Compresses and Decompresses given file
     *
     * @param filePath Relative path of original file as a String
     */
    public static void compressAndDecompress(String filePath) {
        //create Compressor object to do compression/decompression
        Compressor compressor = new Compressor(filePath);
        //create file name for decompressed file
        String shortName = filePath.substring(0, filePath.length() - 4);
        String outputFile = shortName + "_decompressed.txt";
        //try compressing file
        try {
            compressor.compressFile(filePath);
        } catch (IOException e) {
            System.err.println("IO Error compressing file: " + filePath + "\n" + e.getMessage());
        }
        //try decompressing file
        try {
            compressor.decompressFile(filePath);
        } catch (IOException e) {
            System.err.println("IO Error decompressing file: " + filePath + "\n" + e.getMessage());
        }
        //check that the decompressed and original files have identical content
        if (isEqual(new File(filePath).toPath(), new File(outputFile).toPath())) {
            System.out.println("\nOriginal and Decompressed are the same!");
        } else {
            System.out.println("\nOriginal and Decompressed are not  the same :(");
        }

    }
}
