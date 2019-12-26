package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList L = new PartialTreeList(); // step 1
		for(int i = 0; i < graph.vertices.length; i++) { // step 2
			PartialTree T = new PartialTree(graph.vertices[i]); // part 2a
			graph.vertices[i].parent = T.getRoot(); // part 2b
			MinHeap<Arc> P = T.getArcs(); // part 2c
			Vertex.Neighbor current = graph.vertices[i].neighbors;
			while(current != null) {
				P.insert(new Arc(graph.vertices[i], current.vertex, current.weight));
				current = current.next;
			} // part 2d
			L.append(T); // part 2e
		}
		return L;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		ArrayList<Arc> toReturn = new ArrayList<Arc>(); // list of arcs that make up the MST
		while(ptlist.size() > 1) { //while loop condition = step 9
			PartialTree PTX = ptlist.remove(); 
			MinHeap<Arc> PQX = PTX.getArcs(); // step 3
			Arc a = PQX.deleteMin();
			Vertex v1 = a.getv1();
			Vertex v2 = a.getv2();
			while(v2.getRoot() == v1.getRoot()) {
				a = PQX.deleteMin();
				v2 = a.getv2();
			} // step 4/5/6
			PartialTree PTY = ptlist.removeTreeContaining(v2); // step 7
			PTX.merge(PTY);
			ptlist.append(PTX); // step 8
			toReturn.add(a);
		}
		return toReturn;
	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	if(vertex == null) throw new NoSuchElementException("Null vertex");
    	if(size < 1) throw new NoSuchElementException("No tree");
    	Node current = rear.next;
    	Node previous = rear;
    	PartialTree toReturn = null;
    	do { 
    		PartialTree currentTree = current.tree;
    		if(currentTree.getRoot() == vertex.getRoot()) {
    			if(size == 1) { //just 1
    				rear = null;
    				size--;
    				toReturn = currentTree;
    				break;
    			}
    			else if(rear.tree.getRoot() == vertex.getRoot()) { //rear's tree
    				previous.next = current.next;
    				rear = previous;
    				size--;
    				toReturn = currentTree;
    				break;
    			}
    			previous.next = current.next; //regular case
    			size--;
    			toReturn = currentTree;
    			break;
    		}
    		current = current.next;
    		previous = previous.next;
    	} while(current != rear.next);
    	if(toReturn == null) throw new NoSuchElementException("Couldn't find it brah");
    	return toReturn;
     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}


