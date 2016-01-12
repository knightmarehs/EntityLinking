package global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import nlp.tool.CoreNLP;
import nlp.tool.StanfordParser;
import nlp.tool.StopWordsList;

public class Globals {
	// nlp tools
	public static CoreNLP coreNLP;
	public static StanfordParser stanfordParser;
	public static StopWordsList stopWordsList;
	
	public static String localPath="D:/husen/gAnswer/";
	
	public static void init () {
		System.out.println("====== gAnswer over DBpedia ======");

		long t1, t2, t3, t4, t5, t6, t7, t8, t9;
		
		t1 = System.currentTimeMillis();
		coreNLP = new CoreNLP();
		
		t2 = System.currentTimeMillis();
		stanfordParser = new StanfordParser();
		
		t5 = System.currentTimeMillis();
		stopWordsList = new StopWordsList();
	}
	
	/**
	 * Use as system("pause") in C
	 */
	public static void systemPause () {
		System.out.println("System pause ...");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
