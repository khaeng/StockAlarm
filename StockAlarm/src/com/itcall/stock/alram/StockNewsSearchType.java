package com.itcall.stock.alram;

import java.util.HashMap;
import java.util.Map;

public class StockNewsSearchType {

	public static final String DART_SEARCH = "https://dart.fss.or.kr/dsab007/search.ax"; // "http://dart.fss.or.kr/dsab007/search.ax";
	// http://dart.fss.or.kr/dsab007/search.ax?startDate=${yyyyMMdd}&endDate=${yyyyMMdd}&dspType=B
	//                     &keyword=무상증자
	public static final String INFOSTOCK_SEARCH = "http://www.infostockdaily.co.kr/news/articleList.html";
	// http://www.infostockdaily.co.kr/news/articleList.html?page=1&total=10&sc_section_code=&sc_sub_section_code=&sc_serial_code=&sc_area=A&sc_level=&sc_article_type=&sc_view_level=&sc_sdate=${yyyyMMdd}&sc_edate=${yyyyMMdd}&sc_serial_number=
	//                     &sc_word=세계 최초

	public static final String[] SEARCH_TYPEs = new String[] {DART_SEARCH, INFOSTOCK_SEARCH};

	public static final Map<String, String> SEARCH_START_KEY_MAP = new HashMap<String, String>(){
		{
			put(DART_SEARCH, " : ");
			put(INFOSTOCK_SEARCH, "<section class=\"article-list-content text-left\">");
		}
	};

	public static final Map<String, String> SEARCH_END_KEY_MAP = new HashMap<String, String>(){
		{
			put(DART_SEARCH, "</h4>"); // "\n");
			put(INFOSTOCK_SEARCH, "</section>");
		}
	};

	public static final Map<String, String> SEARCH_KEY_NAME_MAP = new HashMap<String, String>(){
		{
			put(DART_SEARCH, "keyword");
			put(INFOSTOCK_SEARCH, "sc_word");
		}
	};








}

