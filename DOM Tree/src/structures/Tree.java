package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		String base = sc.nextLine();
		root = this.recBuild(base);
	}
	private TagNode recBuild(String cTag) {
		TagNode tag = null;
		if(cTag == null) return tag;
		if(cTag.indexOf("</") != -1) return null; //closing tag idc
		else if(cTag.indexOf("<") != -1) {
			tag = new TagNode(cTag.substring(1, cTag.length()-1), this.recBuild(sc.nextLine()), null);
			if(!sc.hasNext()) return tag; //there can only be siblings if theres still more shit
			tag.sibling = this.recBuild(sc.nextLine());
		} 
		else tag = new TagNode(cTag, null, this.recBuild(sc.nextLine())); //last node of the line, cant have any kids
		return tag;
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if(root == null) return;
		traverseAndReplace(root, oldTag, newTag);
	}
	private void traverseAndReplace(TagNode root, String oldTag, String newTag) {
		if(root == null) return;
		if(root.tag.equals(oldTag) && root.firstChild != null) root.tag = newTag;
		traverseAndReplace(root.firstChild, oldTag, newTag); //standard traversal
		traverseAndReplace(root.sibling, oldTag, newTag);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if(root == null) return;
		root = traverseAndBold(root, row);
	}
	private TagNode traverseAndBold(TagNode root, int row) {
		 int counter = 1;
	        TagNode pointerRow = null;
	        TagNode pointer = null;
	        if(root == null) return root;
	        if(root.tag.equals("tr")) {
	            pointerRow = root;
	            while(pointerRow != null && counter != row) {
	                pointerRow = pointerRow.sibling;
	                counter++;
	            }
	            if(pointerRow == null) return root; //row aint here chief
	            else if(counter == row) {
	                pointer = pointerRow.firstChild;
	                while(pointer != null && pointer.tag.equals("td")) {
	                    if(!pointer.firstChild.tag.equals("b")) pointer.firstChild = new TagNode("b", pointer.firstChild, null);
	                    pointer = pointer.sibling;
	                }
	                return root; //stops the recursion here
	            }
	        }
	        traverseAndBold(root.firstChild, row);
	        traverseAndBold(root.sibling, row);
	        return root;
	 }
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if(root == null) return;
		if(tag.equals("b") || tag.equals("p") || tag.equals("em")) root = deleteFormat(root, tag);
		else if(tag.equals("ol") || tag.equals("ul")) root = deleteLists(root, tag);
		else return;
	}
	private TagNode deleteFormat(TagNode root, String tag) { //em, b, p
		if(root == null) return null;
		if(root.tag.equals(tag)){
			TagNode pointer = root.firstChild;
			while(pointer.sibling != null) pointer = pointer.sibling;
			pointer.sibling = root.sibling;
			root = root.firstChild;
		}
		root.firstChild = this.deleteFormat(root.firstChild, tag);
		root.sibling = this.deleteFormat(root.sibling, tag);
		return root;
	}
	private TagNode deleteLists(TagNode root, String tag) { //ol, ul
        if(root == null) return null;
        if(root.tag.equals(tag)) {
            TagNode pointer = root.firstChild;
            while(pointer != null) {
                if(pointer.tag.equals("li")) pointer.tag = "p";
                pointer = pointer.sibling;
            }
            pointer = root.firstChild;
            while(pointer.sibling != null) pointer = pointer.sibling;
            pointer.sibling = root.sibling;
            root = root.firstChild;
        }
        root.firstChild = this.deleteLists(root.firstChild, tag);
        root.sibling = this.deleteLists(root.sibling, tag);
        return root;
    }
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if(root == null) return;
		root = traverseAndAdd(root, word, tag);
	} //case insensitive, can have ".,?!:;" only as last character
	private TagNode traverseAndAdd(TagNode root, String word, String tag) {
		if(root == null) return null;
		root.firstChild = this.traverseAndAdd(root.firstChild, word, tag);
		root.sibling = this.traverseAndAdd(root.sibling, word, tag);
		TagNode toAdd = null; //this is basically a linked list lol
		String temp = "", toInsert = "", wordTrue = word.toLowerCase(), current = root.tag + " ";
		while(!current.equals("")) {
			int tempIndex = current.indexOf(" ");
			temp = current.substring(0, tempIndex).toLowerCase();
			if(temp.indexOf(wordTrue) == -1) toInsert = toInsert + current.substring(0, tempIndex+1);
			else if(temp.indexOf(wordTrue) != -1 && word.length() >= temp.length()-1) {
				if(temp.length() == word.length()) {
					if(!toInsert.equals("")) toAdd = childBirth(toAdd, toInsert);
					toAdd = childBirth(toAdd, tag);
					traverseKids(toAdd).firstChild = new TagNode(current.substring(0, tempIndex), null, null);
					if(current.substring(current.indexOf(" ")+1).indexOf(" ") == -1) toInsert = "";
					else toInsert = " ";
				}
				else if(word.length()+1 == temp.length() && validPunctuation(temp)) {
					if(!toInsert.equals("")) toAdd = childBirth(toAdd, toInsert);
					toAdd = childBirth(toAdd, tag);
					traverseKids(toAdd).firstChild = new TagNode(current.substring(0, tempIndex), null, null);
					if(current.substring(current.indexOf(" ")+1).indexOf(" ") == -1) toInsert = "";
					else toInsert = " ";
				}
				else toInsert += temp + " ";
			}
			else toInsert += temp + " ";
			current = current.substring(tempIndex+1);
		}
		
		if(toAdd == null) return root;
		if(!toInsert.equals("")) {
			TagNode pointer = toAdd;
			traverseKids(pointer).sibling = new TagNode(toInsert.substring(0, toInsert.length()-1), null, null);
		}
		traverseKids(toAdd).sibling = root.sibling;
		root = toAdd;
		return root;
	}
	private TagNode traverseKids(TagNode root) {
		if(root == null) return null;
		while(root.sibling != null) root = root.sibling;
		return root;
	}
	private TagNode childBirth(TagNode root, String tag) {
		if(root == null) return new TagNode(tag, null, null);
		TagNode pointer = root;
		traverseKids(pointer).sibling = new TagNode(tag, null, null);
		return root;
	}
	private boolean validPunctuation(String tag) {
		if(tag.charAt(tag.length()-1) == '.' || tag.charAt(tag.length()-1) == ',' || tag.charAt(tag.length()-1) == '?' || tag.charAt(tag.length()-1) == '!' || tag.charAt(tag.length()-1) == ':' || tag.charAt(tag.length()-1) == ';') {
			return true;
		}
		else return false;
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}