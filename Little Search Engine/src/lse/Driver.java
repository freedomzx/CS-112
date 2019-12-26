package lse;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.io.*;
public class Driver {
	public static void main(String[] args) throws FileNotFoundException{
		//preliminary
		LittleSearchEngine engi = new LittleSearchEngine();
		Scanner sc = new Scanner(System.in);
		System.out.print("Name of document: ");
		String file = sc.next();
		
		//noise words
		Scanner noiseScan = new Scanner(new File("noisewords.txt"));
		while (noiseScan.hasNext()) {
			String word = noiseScan.next();
			engi.noiseWords.add(word);
		}
		//trying to load
		/*try {
			engi.mergeKeywords(engi.loadKeywordsFromDocument(file));
		}
		catch(FileNotFoundException e) {
			System.out.println("u suk lol");
		}*/
		
		//merge/insertlast check
		engi.mergeKeywords(engi.loadKeywordsFromDocument("AliceCh1.txt"));
		engi.mergeKeywords(engi.loadKeywordsFromDocument("WowCh1.txt"));
		engi.mergeKeywords(engi.loadKeywordsFromDocument("beemovie.txt"));
		engi.mergeKeywords(engi.loadKeywordsFromDocument("Shrek.txt"));
		System.out.println(engi.keywordsIndex.size());
		System.out.println(engi.keywordsIndex.get("really"));
		System.out.println(engi.keywordsIndex.get("something"));
		System.out.println(engi.keywordsIndex.get("saw"));
		System.out.println(engi.keywordsIndex.get("nice"));
		System.out.println(engi.keywordsIndex.get("shrek"));
		sc.close();
		noiseScan.close();
	}
}
