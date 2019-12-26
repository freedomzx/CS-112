import java.util.ArrayList;
import java.util.Iterator;

import app.PartialTreeList;
import structures.Graph;
import structures.PartialTree;

public class Driver {

    public static void main(String[] args) { 
        ArrayList<String> fileNames = new ArrayList<String>();
        fileNames.add("graph1.txt");
        fileNames.add("graph2.txt");
        fileNames.add("manyNodesfewEdges.txt");
        fileNames.add("manyNodesmanyEdges.txt");
        fileNames.add("zeroNodes.txt");
        fileNames.add("oneNode.txt");
        fileNames.add("twoNodes.txt");
        fileNames.add("skipC.txt");
        //fileNames.add("test1.txt");
        for (String fileName : fileNames) {
            System.out.println("Now testing file: " + fileName);
            Graph testGraph = null;
            try { 
                testGraph = new Graph(fileName);
            }
            catch (Exception e) {
                System.out.println("Took an L on file-reading");
            }
            PartialTreeList pt1 = PartialTreeList.initialize(testGraph);
            Iterator<PartialTree> iter = pt1.iterator();
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
            System.out.println("MST: " + PartialTreeList.execute(pt1) + "\n\n\n");
        }
    }

}
