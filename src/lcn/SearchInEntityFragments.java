package lcn;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import global.Globals;


public class SearchInEntityFragments {
	public EntityFragmentFields searchFragment(String entityname) throws IOException{
		Hits hits = null;
		String queryString = null;
		Query query = null;
		//IndexSearcher searcher = new IndexSearcher(Globals.localPath+"data\\DBpedia\\lucene\\entity_fragment_index");
		IndexSearcher searcher = new IndexSearcher(Globals.localPath+"data/DBpedia3.9/lucene/entity_fragment_index");
		//ArrayList<String> result = new ArrayList<String>(); 
		//System.out.println("hello");

		queryString = entityname;
		queryString = queryString.replace('?', ' ');	//好像Lucene一碰到问号就会出错 // 好像还没解决，不知道那个出错的?是什么东西，测试用例"Berlin"
		queryString = queryString.replace('!', ' ');
		
		Analyzer analyzer = new StandardAnalyzer();
		try
		{
			QueryParser qp = new QueryParser("EntityName", analyzer);			
			query = qp.parse(queryString);
		} catch (ParseException e)
		{
			System.err.println("error search string: " + queryString);
			e.printStackTrace();
			return null;
		}
		
		if (searcher != null)
		{
			hits = searcher.search(query);
			//System.out.println("search for entity fragment hits.length=" + hits.length());
			if (hits.length() > 0) 
			{
				//System.out.println("find " + hits.length() + " result!");
				for (int i=0; i<hits.length(); i++) {
					/*
					if (hits.score(i) > 0.8)
				    System.out.println(i+": "+hits.doc(i).get("EntityName") +";"
				    		  +hits.doc(i).get("EntityFragment") 
				    		  + "; Score: " + hits.score(i));  
				    */  
					if (hits.score(i) > 0.7 && hits.doc(i).get("EntityName").equals(queryString)) {	// 必须是精确匹配！
						return new EntityFragmentFields(hits.doc(i).get("EntityName"), hits.score(i), hits.doc(i).get("EntityFragment"));  
					}
				}				    	  
			}				
		}
		return null;
	}

	public ArrayList<EntityNameAndScore> searchName(String literal, double thres1, double thres2, int k) throws IOException {
		Hits hits = null;
		String queryString = null;
		Query query = null;
		//IndexSearcher searcher = new IndexSearcher(Globals.localPath+"data\\DBpedia\\lucene\\entity_fragment_index");
		IndexSearcher searcher = new IndexSearcher(Globals.localPath+"data/DBpedia3.9/lucene/entity_fragment_index");
		
		ArrayList<EntityNameAndScore> result = new ArrayList<EntityNameAndScore>(); 
		//System.out.println("hello");

		queryString = literal;
		
		Analyzer analyzer = new StandardAnalyzer();
		try
		{
			QueryParser qp = new QueryParser("EntityName", analyzer);
			query = qp.parse(queryString);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		if (searcher != null)
		{
			hits = searcher.search(query);
			//System.out.println("search for entity fragment hits.length=" + hits.length());
			if (hits.length() > 0) 
			{
				//System.out.println("find " + hits.length() + " result!");
				for (int i=0; i<hits.length(); i++) {
				    //System.out.println(i+": <"+hits.doc(i).get("EntityName") +">;"
				    //		  +hits.doc(i).get("EntityFragment")
				    //		  + "; Score: " + hits.score(i)
				    //		  + "; Score2: " + hits.score(i)*(literalLength/hits.doc(i).get("EntityName").length()));    
				    if(i<k) {
				    	if (hits.score(i) >= thres1) {
					    	String en = hits.doc(i).get("EntityName");
					    	result.add(new EntityNameAndScore(en, hits.score(i)));
				    	}
				    	else {
				    		break;
				    	}
				    }
				    else {
				    	if (hits.score(i) >= thres2) {
					    	String en = hits.doc(i).get("EntityName");
					    	result.add(new EntityNameAndScore(en, hits.score(i)));
				    	}
				    	else {
				    		break;
				    	}
				    }
				}				    	  
			}				
		}
		
		//Collections.sort(result);
		return result;

	}
}
