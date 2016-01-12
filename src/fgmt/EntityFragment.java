package fgmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import rdf.EntityMapping;

import lcn.EntityFragmentFields;
import lcn.EntityNameAndScore;
import lcn.SearchInEntityFragments;

public class EntityFragment extends Fragment {
	
	public HashSet<Integer> inEdges = new HashSet<Integer>();
	public HashSet<Integer> outEdges = new HashSet<Integer>();
	public HashSet<Integer> types = new HashSet<Integer>();	
		
	static double thres1 = 0.4;
	static double thres2 = 0.8;
	static int thres3 = 3;
	static int k = 50;
	
	/**
	 * 将一个phrase(已识别为命名实体)，映射到知识库中相应的<entity>
	 * 
	 * 方法:
	 * (1)拿phrase去搜subject域, 按照"规则"来搜索，如果结果中出现"精确匹配"，则不需要进行步骤(2)；否则需要进行步骤(2)
	 * (2)拿phrase去搜literal域，按照"规则"来搜索，注意处理和(1)中重复的情形。
	 * 
	 * 规则：
	 * 每个phrase只取前k个结果，(1)若前k个结果中出现分数低于thres1的，拦腰截断，其后舍去；
	 * (2)若前k个结果中最后一个分数仍高于thres2，则取到thres2为止。
	 * 
	 * 精确匹配：
	 * 首先，Lucene score必须等于1；其次，检查字符串是否完全匹配，(均转换为小写后)编辑距离不超过阈值thres3。
	 * 
	 * 分数：
	 * 直接拿Lucene的分数来用，因为它已经综合考虑多种因素。
	 * 
	 * @param phrase
	 * @return
	 */
	public static HashMap<String, Double> getCandEntityNames2(String phrase) {
				
		HashMap<String, Double> ret = new HashMap<String, Double>();

		// 此处获得subject域score大于等于thres1的匹配
		ArrayList<EntityNameAndScore> list1 = getCandEntityNames_subject(phrase, thres1, thres2, k);
		
		// 是否存在精确匹配，若存在，直接返回精确匹配
		/*HashMap<String, Double> exact = getExactMatchings(list1, phrase);
		if (exact.size() > 0) {
			System.out.println("PHRASE=\"" + phrase + "\" is EXACTLY mapped to the following entities:");
			for(String s : exact.keySet()) {
				System.out.println("\t<" + s + "> " + exact.get(s));
			}
			return exact;
		}*/
		
		if(list1 == null)
			return ret;
		
		// 按照规则，至多只选择前k个
		int iter_size = 0;
		if (list1.size() <= k) {
			iter_size = list1.size();
		}
		else if (list1.size() > k) {
			if (list1.get(k-1).score >= thres2) {
				iter_size = list1.size();
			}
			else {
				iter_size = k;
			}
		}
		
		// （接上面）选择前k个
		for(int i = 0; i < iter_size; i ++) {
			if (i < k) {
				ret.put(list1.get(i).entityName, getScore(phrase, list1.get(i).entityName, list1.get(i).score));
			}
			else if (list1.get(i).score >= thres2) {
				ret.put(list1.get(i).entityName, getScore(phrase, list1.get(i).entityName, list1.get(i).score));
			}
			else {
				break;
			}
		}

//		// ========  为哈要拿实体取匹配literal？好奇怪，先注释掉。。 hs
//		// 注意这里用传入的phrase匹配triple的literal，如果匹配，返回的是这个triple的entity，即找到的entity可能和phrase字面上毫无关系，只是KB中有条边相连。 hs_151021
//		// 此处获得literal域score大于等于thres1的匹配
//		ArrayList<EntityNameAndScore> list2 = getCandEntityNames_literal(phrase, thres1, thres2, k);
//				
//		// 按照规则，至多只选择前k个
//		iter_size = 0;
//		if (list2.size() <= k) {
//			iter_size = list2.size();
//		}
//		else if (list2.size() > k) {
//			if (list2.get(k-1).score >= thres2) {
//				iter_size = list2.size();
//			}
//			else {
//				iter_size = k;
//			}
//		}
//		
//		// （接上面）选择前k个,但还要注意处理重复
//		for(int i = 0; i < iter_size; i ++) {
//			if (i < k) {
//				String s = list2.get(i).entityName;
//				if (ret.containsKey(s)) {
//					double d = getScore(phrase, s, list2.get(i).score);	// TODO 理论上这里不应该计算phrase和s的编辑距离，因为phrase匹配在s的literal上
//					if (d > ret.get(s)) {
//						ret.put(s, d);						
//					}
//				}
//				else {
//					ret.put(s, getScore(phrase, s, list2.get(i).score));	
//				}
//			}
//			else if (list2.get(i).score >= thres2) {
//				String s = list2.get(i).entityName;
//				if (ret.containsKey(s)) {
//					double d = getScore(phrase, s, list2.get(i).score);
//					if (d > ret.get(s)) {
//						ret.put(s, d);						
//					}
//				}
//				else {
//					ret.put(s, getScore(phrase, s, list2.get(i).score));	
//				}
//			}
//			else {
//				break;
//			}
//		}
		
		/*
		System.out.println("PHRASE=\"" + phrase + "\" is mapped to the following "+ret.size()+" entities:");
		for(String s : ret.keySet()) {
			System.out.println("\t<" + s + "> " + ret.get(s));
		}
		*/

		return ret;
	}
	
	/**
	 * 使用前，必须保证list是从大到小有序！
	 * @param list
	 * @param phrase
	 * @return
	 */
	public static HashMap<String, Double> getExactMatchings(ArrayList<EntityNameAndScore> list, String phrase) {
		HashMap<String, Double> ret = new HashMap<String, Double>();
		for (EntityNameAndScore enas : list) {
			if (enas.score < 0.95) {
				break;
			}
			else {
				int ed = calEditDistance(phrase, enas.entityName);
				if (ed<=thres3) {
					// 精确匹配对score进行了修正
					ret.put(enas.entityName, enas.score*((double)enas.entityName.length()-ed)/enas.entityName.length());
				}
			}
		}
		
		return ret;
	}
	
	public static ArrayList<EntityMapping> getEntityMappingList (String n) {
		HashMap<String, Double> map = getCandEntityNames2(n);
		ArrayList<EntityMapping> ret = new ArrayList<EntityMapping>();
		for (String s : map.keySet()) {
			ret.add(new EntityMapping(s, s, map.get(s)));
		}
		Collections.sort(ret);
		return ret;
	}
	
	public static double getScore (String s1, String s2, double luceneScore) {
		//double ret = luceneScore*100.0/(calEditDistance(s1, s2)+1);
		double ret = luceneScore*100.0/(Math.log(calEditDistance(s1, s2)*1.5+1)+1);
		return ret;
	}
	
	/**
	 * 计算编辑距离，不考虑大小写（大小写视为相同）
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int calEditDistance (String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		int d[][];//矩阵 
        int n = s1.length(); 
        int m = s2.length(); 
        int i;    //遍历str1的 
        int j;    //遍历str2的 
        char ch1;    //str1的 
        char ch2;    //str2的 
        int temp;    //记录相同字符,在某个矩阵位置值的增量,不是0就是1 
		
        if(n == 0) { 
            return m; 
        } 
        if(m == 0) { 
            return n; 
        } 

        d = new int[n+1][m+1]; 
        for(i=0; i<=n; i++) {    //初始化第一列 
            d[i][0] = i; 
        } 
        for(j=0; j<=m; j++) {    //初始化第一行 
            d[0][j] = j; 
        } 

        for(i=1; i<=n; i++) {    //遍历str1 
            ch1 = s1.charAt(i-1); 
            //去匹配str2 
            for(j=1; j<=m; j++) { 
                ch2 = s2.charAt(j-1); 
                if(ch1 == ch2) { 
                    temp = 0; 
                } else { 
                    temp = 1; 
                } 
                //左边+1,上边+1, 左上角+temp取最小 
                d[i][j] = min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+temp); 
            } 
        } 

	    return d[n][m]; 
	}
	
	private static int min(int a, int b, int c) {
		int ab = a<b?a:b;
		return ab<c?ab:c;
	}	
	
	public static ArrayList<EntityNameAndScore> getCandEntityNames_subject(String phrase, double thres1, double thres2, int k) {
		SearchInEntityFragments sf = new SearchInEntityFragments();
		
		//System.out.println("EntityFragment.getCandEntityNames_subject() ...");
		
		ArrayList<EntityNameAndScore> ret_sf = null;
		try {
			ret_sf = sf.searchName(phrase, thres1, thres2, k);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret_sf;
	}
	
	public static String getEntityFgmtStringByName(String entityName) {
		SearchInEntityFragments sf = new SearchInEntityFragments();
		EntityFragmentFields eff = null;
		try {
			eff = sf.searchFragment(entityName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (eff != null){
			return eff.fragment;
		}
		else {
			return null;
		}
	}
	
	public EntityFragment(String fgmt) {
		fragmentType = typeEnum.ENTITY_FRAGMENT;
		
		fgmt = fgmt.replace('|', '#');
		String[] fields = fgmt.split("#");
		if(fields[0].length() > 0) {
			String[] nums = fields[0].split(",");
			for(int i = 0; i < nums.length; i ++) {
				if (nums[i].length() > 0) {
					inEdges.add(Integer.parseInt(nums[i]));
				}
			}
		}
		if(fields.length > 1 && fields[1].length() > 0) {
			String[] nums = fields[1].split(",");
			for(int i = 0; i < nums.length; i ++) {
				if (nums[i].length() > 0) {
					outEdges.add(Integer.parseInt(nums[i]));
				}
			}
		}
		if(fields.length > 2 && fields[2].length() > 0) {
			String[] nums = fields[2].split(",");
			for(int i = 0; i < nums.length; i ++) {
				if (nums[i].length() > 0) {
					types.add(Integer.parseInt(nums[i]));
				}
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder("");
		for(Integer p : inEdges) {
			ret.append(p);
			ret.append(',');
		}
		ret.append('|');
		for(Integer p : outEdges) {
			ret.append(p);
			ret.append(',');
		}
		ret.append('|');
		for(Integer t : types) {
			ret.append(t);
			ret.append(',');
		}
		return ret.toString();
	}
}
