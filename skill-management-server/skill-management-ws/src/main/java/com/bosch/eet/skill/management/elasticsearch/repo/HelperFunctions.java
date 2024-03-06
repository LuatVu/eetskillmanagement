package com.bosch.eet.skill.management.elasticsearch.repo;

import java.util.regex.Matcher;

public class HelperFunctions {
	
	public static String buildMultiIndexMatchBody(String query, Integer size, Integer from) {
		query = handleQueryString(query);
		
		if (from != null && size != null) {
	        return "{\r\n"
	        		+ "    \"query\": {\r\n"
	        		+ "        \"query_string\": {\r\n"
	        		+ "      \"query\":      \"*" + query + "*\"\n"
	        		+ "        }\r\n"
	        		+ "    },\r\n"
	        		+ "      \"size\":      " + size + ",\n"
	        		+ "      \"from\":      " + from + ",\n"
	        		+ "    \"sort\": []\r\n"
	        		+ "}";
		} else {
	        return "{\r\n"
	        		+ "    \"query\": {\r\n"
	        		+ "        \"query_string\": {\r\n"
	        		+ "      \"query\":      \"*" + query + "*\"\n"
	        		+ "        }\r\n"
	        		+ "    },\r\n"
	        		+ "    \"size\": 10,\r\n"
	        		+ "    \"from\": 0,\r\n"
	        		+ "    \"sort\": []\r\n"
	        		+ "}";

		}
		
    }
	
	public static String buildSearchPersonalByNameOrNtidBody(String query, Integer size, Integer from) {
		query = handleQueryString(query);

		if (from != null && size != null) {
	        return "{\r\n"
	        		+ "    \"query\": {\r\n"
	        		+ "			\"query_string\": {\r\n"
	        		+ "      		\"query\":      \"*" + query + "*\",\n"
					+ "				\"fields\": [\"displayName\", \"personalCode\", \"displayName.keyword\", \"personalCode.keyword\"]"
	        		+ "        }\r\n"
	        		+ "    },\r\n"
	        		+ "      \"size\":      " + size + ",\n"
	        		+ "      \"from\":      " + from + ",\n"
	        		+ "    \"sort\": []\r\n"
	        		+ "}";
		} else {
	        return "{\r\n"
	        		+ "    \"query\": {\r\n"
	        		+ "        \"query_string\": {\r\n"
	        		+ "      		\"query\":      \"*" + query + "*\",\n"
					+ "				\"fields\": [\"displayName\", \"personalCode\", \"displayName.keyword\", \"personalCode.keyword\"]"
					+ "        }\r\n"
	        		+ "    },\r\n"
	        		+ "    \"size\": 10,\r\n"
	        		+ "    \"from\": 0,\r\n"
	        		+ "    \"sort\": []\r\n"
	        		+ "}";

		}
		
    }

    public static String buildSearchUri(String elasticSearchUri,
                                        String elasticSearchIndex,
                                        String elasticSearchSearch) {
        return elasticSearchUri + elasticSearchIndex + elasticSearchSearch;
    }
    
    private static String handleQueryString(String query) {
    	query = query.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"));
    	query = query.replaceAll("[<>]", "");
    	query = query.replaceAll("[-+=\\&|!(){}\\[\\]^\\\"~*?:/]", " " + Matcher.quoteReplacement("\\\\") + "$0 ");
    	query = query.trim();
		if(query.contains(" ")){
			String[] tempArr = query.split("\\s+");
			query = String.join("* && *", tempArr);
		}
		return query;
    }
}