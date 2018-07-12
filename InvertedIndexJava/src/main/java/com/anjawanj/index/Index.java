package com.anjawanj.index;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Index {
	
	static Map<String, List<Posting>> index = null;
	
	public static Map<String, List<Posting>> getInstance(){
        if(index == null){
        	index = new ConcurrentHashMap<String, List<Posting>>();
        }
        return index;
    }

}
