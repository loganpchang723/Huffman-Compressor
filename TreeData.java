/**
 * Custom object to be used as elements/nodes in BinaryTree for Huffman Encoding
 *
 * @author Logan Chang, CS10, PS3, 20F
 */
public class TreeData {
    private final char value; //char to be held
    private final int freq;   //frequency that char (value) is found in file

    /**
     * Constructor for TreeData object that takes character and its frequency
     *
     * @param value Character found in file
     * @param freq  Frequency that 'value' occurs in the file
     */
    public TreeData(char value, int freq) {
        this.value = value;
        this.freq = freq;
    }

    /**
     * toString for TreeData object
     *
     * @return Returns String in "char: frequency" form
     */
    public String toString() {
        return value + ":" + freq;
    }

    /**
     * Getter for value
     *
     * @return Character being held ('value')
     */
    public char getValue() {
        return value;
    }

    /**
     * Getter for frequency
     *
     * @return Frequency that 'value' occurs in the file as an int
     */
    public int getFreq() {
        return freq;
    }
}
