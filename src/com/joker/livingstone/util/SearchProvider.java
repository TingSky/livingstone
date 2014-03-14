package com.joker.livingstone.util;

import android.content.SearchRecentSuggestionsProvider;

public class SearchProvider extends SearchRecentSuggestionsProvider {
	
	public final static String AUTHORITY="com.android.search.SearchSuggestionSampleProvider";  
    public final static int MODE=DATABASE_MODE_QUERIES;  
      
    public SearchProvider(){  
        super();  
        setupSuggestions(AUTHORITY, MODE);  
    }  
}
