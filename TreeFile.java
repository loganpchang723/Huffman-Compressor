import java.io.*;
import java.util.Arrays;
import java.util.Stack;

/**
 * Attempt at EC. Writes tree to 'fileName'_treeFile.txt and can reconstruct the tree
 */
public class TreeFile {
    private BinaryTree<TreeData> codeTree;
    private HuffmanTools ht;
    private String filename;
    private int size;
    private static String[] nodes;
    private static String[] isLeaf;
    private Stack<BinaryTree<TreeData>> stack;
    int iter;



    public TreeFile(String filename) {
        this.filename = filename;
        //create a HuffmanTools object for the given file
        ht = new HuffmanTools(this.filename);
        stack = new Stack<>();
        iter = 0;
    }

    public void setTree() throws IOException{
        codeTree = ht.createTree();
    }

    public void setSize(){
        size = codeTree.size();
        nodes = new String[size];
        isLeaf = new String[size];
    }

    public void preorder() throws IOException{
        setTree();
        setSize();
        stack.push(codeTree);
        while(!stack.isEmpty()){
            BinaryTree<TreeData> currNode = stack.pop();
            TreeData curr = currNode.getData();
            nodes[iter] = curr.toString();
            if(currNode.isLeaf()) isLeaf[iter] = "L";
            else isLeaf[iter] = "N";
            if(currNode.hasRight()) stack.push(currNode.getRight());
            if(currNode.hasLeft()) stack.push(currNode.getLeft());
            iter++;
        }
    }

    public void inorder() throws IOException{
        setTree();
        setSize();
        BinaryTree<TreeData> curr = codeTree;
        while(curr!= null || !stack.isEmpty()){
            while(curr != null){
                stack.push(curr);
                curr = curr.getLeft();
            }
            curr = stack.pop();
            //do smth
            if(curr.isLeaf()) isLeaf[iter] = "L";
            else isLeaf[iter] = "N";

            nodes[iter] = curr.getData().toString();

            curr = curr.getRight();
            iter++;
        }

    }

    public void writeToFile() throws IOException{
        String shortName = filename.substring(0,filename.length()-4);
        String newFile = shortName+"_treeFile.txt";
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(newFile));
            for (int i = 0; i < size - 1; i++) {
                out.write(nodes[i] + " ");
            }
            out.write(nodes[size - 1] + "\n");
            for (int i = 0; i < size - 1; i++) {
                out.write(isLeaf[i] + " ");
            }
            out.write(isLeaf[size - 1] + "\n");
        }
        finally{
            out.close();
        }
    }

    public BinaryTree<TreeData> constructTree() throws IOException {
        String shortName = filename.substring(0,filename.length()-4);
        String newFile = shortName+"_treeFile.txt";
        BufferedReader in = new BufferedReader(new FileReader(newFile));
        String[] nodeData = in.readLine().split(" ");
        String[] leafData = in.readLine().split(" ");
        BinaryTree<TreeData> tree = new BinaryTree<>(null);
        return constructTreeUtil(nodeData, leafData,nodeData.length);

    }
    int index_ptr = 0;
    BinaryTree<TreeData> constructTreeUtil(String pre[], String preLN[], int n) {
        int index = index_ptr;
        // Base Case: All nodes are constructed
        if (index == n)
            return null;

        // Allocate memory for this node and increment index for
        // subsequent recursive calls
//        temp.data = getData(pre[index]);
        BinaryTree<TreeData> temp = new BinaryTree<>(getData(pre[index]));
        index_ptr++;

        // If this is an internal node, construct left and right subtrees
        // and link the subtrees
//        System.out.println(index_ptr);
        if (preLN[index].equals("N")) {
            temp.setLeft(constructTreeUtil(pre, preLN, n));
            temp.setRight(constructTreeUtil(pre, preLN, n));
        }

        return temp;
    }

    public TreeData getData(String str){
        String[] split = str.split(":");
        TreeData node = new TreeData(split[0].charAt(0), Integer.parseInt(split[1]));
        return node;
    }

    public BinaryTree<TreeData> getTree() throws IOException{
        preorder();
        writeToFile();
        return constructTree();
    }

    public static void main(String[] args) throws IOException{
        TreeFile tf = new TreeFile("inputs/helloTest.txt");
//        tf.preorder();
        tf.inorder();
        System.out.println(Arrays.toString(nodes));
        System.out.println(Arrays.toString(isLeaf));
//        tf.writeToFile();
//        System.out.println(tf.constructTree().toString());
    }


}
