package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
		throws FileNotFoundException {    
			HashMap<String,Occurrence> toReturn = new HashMap<String,Occurrence>();
		    Scanner sc = new Scanner(new File(docFile));
		    while(sc.hasNext()) {
		        String token = sc.next();
		        String keyword = this.getKeyword(token);
		        if(keyword == null) continue;
		        if(toReturn.containsKey(keyword)) {
		            toReturn.get(keyword).frequency++;
		        }
		        else {
		            Occurrence toPut = new Occurrence(docFile, 1);
		            toReturn.put(keyword, toPut);
		        }
		    }
		    System.out.println(toReturn.size());
		    sc.close();
		    return toReturn;
	 }
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		kws.forEach((String key, Occurrence occ)->{
			if(keywordsIndex.containsKey(key)) {
				keywordsIndex.get(key).add(occ);
				this.insertLastOccurrence(keywordsIndex.get(key));
			}
			else {
				ArrayList<Occurrence> toPut = new ArrayList<Occurrence>();
				toPut.add(kws.get(key));
				keywordsIndex.put(key, toPut);
			}
		});
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		Scanner sc = new Scanner(System.in);
		if(word == null) {
			sc.close();
			return null;
		}//empty word
		
		String temp = word.toLowerCase();
		if(temp.length() <= 1) {
			sc.close();
			return null;
		}//smol string
		
		while(isPunct(temp.charAt(temp.length()-1))) {
			if(temp.length() <= 1) {
				sc.close();
				return null;
			}
			temp = temp.substring(0, temp.length()-1);
		} //removing trailing punct
		
		for(int i = 0; i < temp.length(); i++) {
			if(!isLetter(temp.charAt(i))) {
				sc.close();
				return null;
			}
		}//seeing if its an illegal word
		
		for(String s : noiseWords) {
			if(s.toLowerCase().equals(temp)) {
				sc.close();
				return null;
			}
		}
		sc.close();
		return temp; //a winner is you
	}
	private boolean isPunct(char c) {
		if(c == '.' || c == ',' || c == '?' || c == ':' || c == ';' || c == '!') return true;
		return false;
	}
	private static boolean isLetter(char c) {
    	if((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) return true;
    	return false;
    }
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() == 1) return null;
		Occurrence toInsert = occs.get(occs.size()-1);
		occs.remove(occs.size()-1);
		ArrayList<Integer> mids = new ArrayList<Integer>();
		int low = 0;
		int high = occs.size()-1;
		int mid = (low + high)/2;
		while(low <= high) {
			mid = (low + high)/2;
			mids.add(mid);
			if(toInsert.frequency == occs.get(mid).frequency) break;
			else if(toInsert.frequency > occs.get(mid).frequency) high = mid - 1;
			else {
				low = mid + 1;
				if(high <= mid) mid++;
			}
		}
		occs.add(mid, toInsert);
		return mids;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> toReturn = new ArrayList<String>();
		ArrayList<Occurrence> kw1occs = new ArrayList<Occurrence>();
		if(this.keywordsIndex.containsKey(kw1)) kw1occs.addAll(keywordsIndex.get(kw1));
		ArrayList<Occurrence> kw2occs = new ArrayList<Occurrence>(); 
		if(this.keywordsIndex.containsKey(kw2)) kw2occs.addAll(keywordsIndex.get(kw2));
		ArrayList<Occurrence> kwlist = new ArrayList<Occurrence>(); 
		kwlist.addAll(kw1occs); kwlist.addAll(kw2occs);
		if(!kw1occs.isEmpty() && !kw2occs.isEmpty()) {
			for(int i = 0; i < kwlist.size()-1; i++) {
				for(int j = 1; j < kwlist.size(); j++) {
					if(kwlist.get(j-1).frequency < kwlist.get(j).frequency) {
						Occurrence toAdd = kwlist.get(j-1);
						kwlist.set(j-1, kwlist.get(j));
						kwlist.set(j, toAdd);
					}
				}
			}
			for(int i = 0; i < kwlist.size()-1; i++) {
				for(int j = i+1; j < kwlist.size(); j++) {
					if(kwlist.get(i).document.contentEquals(kwlist.get(j).document)) kwlist.remove(j);
				}
			}
		}
		while(kwlist.size() > 5) kwlist.remove(kwlist.size()-1);
		for(Occurrence occurrence : kwlist) toReturn.add(occurrence.document);
		return toReturn;
	}
}
