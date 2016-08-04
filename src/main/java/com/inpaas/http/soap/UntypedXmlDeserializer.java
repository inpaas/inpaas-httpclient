package com.inpaas.http.soap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

public class UntypedXmlDeserializer extends UntypedObjectDeserializer {

	@Override
	@SuppressWarnings("unchecked")
	protected Object mapObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        String firstKey;

        JsonToken t = p.getCurrentToken();
        
        if (t == JsonToken.START_OBJECT) {
        	firstKey = p.nextFieldName();
        } else if (t == JsonToken.FIELD_NAME) {
        	firstKey = p.getCurrentName();
        } else {
            if (t != JsonToken.END_OBJECT) {
                throw ctxt.mappingException(handledType(), p.getCurrentToken());
            }
            firstKey = null;
        }
        
        // empty map might work; but caller may want to modify... so better just give small modifiable
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<String, Object>(2);
        if (firstKey == null) return resultMap; 
        
        p.nextToken();
        resultMap.put(firstKey, deserialize(p, ctxt));
        
        
        // 03-Aug-2016, jpvarandas: handle next objects and create an array
        
        /*
        ArrayList<LinkedHashMap<String, Object>> resultList = null;

        String nextKey;
        while((nextKey = p.nextFieldName()) != null) {	        	        	
	        if (resultMap.containsKey(nextKey)) { 
            	if (resultList == null) { 
            		resultList = new ArrayList<>();
            		resultList.add(resultMap);
            	}
            	
            	resultMap = new LinkedHashMap<>();	   
            	resultList.add(resultMap);
	        }
	        
	        p.nextToken();
	        resultMap.put(nextKey, deserialize(p, ctxt));
        }    
        
        return resultList != null ? resultList : resultMap;
        */
        
        Set<String> listKeys = new LinkedHashSet<>();
        
        String nextKey;
        while((nextKey = p.nextFieldName()) != null) {	        	        	
	        p.nextToken();
        	if (resultMap.containsKey(nextKey)) {
	        	Object listObject = resultMap.get(nextKey);
	        	
	        	if (!(listObject instanceof List)) {
	        		listObject = new ArrayList<>();
	        		((List) listObject).add(resultMap.get(nextKey));
	        		
	        		resultMap.put(nextKey, listObject);
	        	}
	        	
	            ((List) listObject).add(deserialize(p, ctxt));
	            
	            listKeys.add(nextKey);
	            
	        } else {
	        	resultMap.put(nextKey, deserialize(p, ctxt));
	        	
	        }
        }    
        
        /*
        // if we have more than 1 element type in this root, it
        // makes sense (semantically) to fix the object name to a nList
        if (resultMap.keySet().size() > 1 && !listKeys.isEmpty()) {
        	for(String key: listKeys) {
        		Object data = resultMap.get(key);        		
        		resultMap.remove(key);
        		
        		resultMap.put(key+"List", data);
        	}
        }
        */
        
        return resultMap;
        
        
	}

}