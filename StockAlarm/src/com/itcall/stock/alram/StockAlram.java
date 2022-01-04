package com.itcall.stock.alram;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.itcall.util.ComUtil;
import com.itcall.util.web.SearchEngine;
import com.itcall.util.web.SearchEngineListener;

public class StockAlram {

	public int beforeCount = 0;
	public static String processBar = " ■";
	public String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		PlayAlramSound.playAlramSound(15);
		if(true) return ;
		String test = "key=&key2&key3=&key4=무상증자";
		Map<String, Object> testMap = ComUtil.switchUrlParamsToMap(test);
		System.out.println(testMap.toString());
		System.out.println(ComUtil.switchMapToUrlParams(testMap));
		System.out.println(ComUtil.switchUrlParamsToMap(test).toString());
		if(true) return;
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		// url+="?keyword="+encKeyword+"&startDate="+startDate+"&endDate="+endDate+"&dspType=B";
		params.put("keyword", "무상증자");
		params.put("startDate", "${yyyyMMdd}");
		params.put("endDate", "${yyyyMMdd}");
		params.put("dspType", "B");
		String crpCikCode;
		try {
			crpCikCode = SearchEngine.searchToUrl(new SearchEngine("http://dart.fss.or.kr/corp/searchExistAll.ax?textCrpNm=제노코", "GET", null, "", " "), new StringBuffer());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("textCrpCik", crpCikCode );
		StringBuffer result = new StringBuffer();
		SearchEngine.searchToUrl("http://dart.fss.or.kr/dsab007/search.ax", "GET", params , " : ", "\n", 10, result, 
				new SearchEngineListener() {
			public int beforeCount = 0;
			@Override
			public boolean workStepComplete(String targetUrl, String method, Map<String, Object> params, String startStr, String endStr, String searchedResult, StringBuffer body) {
//				System.out.println(targetUrl);
//				System.out.println(searchedResult);
//				System.out.println(body.toString());
//				return false;
				
				
				
				for (int i = 0; i < 100; i++) {
					System.out.printf("\b"); // System.out.print("\b");
				}
				System.out.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				System.out.print(" : [" + params.get("keyword") + "] " + searchedResult);
				int resultCount = Integer.valueOf(searchedResult);
				
				System.out.print(" ====> " + resultCount);
				System.out.print(processBar);
				System.out.print("                    ");
				processBar+="■";
				if(processBar.length()>10) {
					processBar = " ■";
				}
				if(resultCount > beforeCount) {
					// 실행 IE to Page...
					try {
						System.out.println();
						System.out.println(targetUrl);
						System.out.println("새로운 검색건수가 존재합니다. ====> " + resultCount);
						Desktop.getDesktop().browse(new URI(targetUrl));
						beforeCount = resultCount; // 다음 검색에서 동일한 카운트이므로 웹페이지를 안띄우게 한다.
					} catch (URISyntaxException | IOException e) {
						System.out.println();
						e.printStackTrace();
					}
				}
				body.setLength(0);
				return true;
			}
		});
		
		if(true)
			return;
		StockAlram stockAlram = new StockAlram();
		
		String url = "http://dart.fss.or.kr/dsab007/search.ax";
		String keyword = "무상증자"; // URLEncoder.encode("무상증자", Charset.forName("EUC-KR").name());
		// "%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90"
		// "%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90"
		// System.out.println(URLEncoder.encode(keyword));
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					stockAlram.checkDartStockIssue(url, "GET", keyword, null);
				} catch (MalformedURLException e) {
					System.out.println();
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println();
					e.printStackTrace();
				}
			}
		}, 10, 10 * 1000);
		
	}
	
	
	
	public void checkDartStockIssue(String url, String method, String keyword, Map<String, String> params) throws MalformedURLException, IOException {
		if(searchDate.equals(new SimpleDateFormat("yyyyMMdd").format(new Date())) == false) {
			beforeCount = 0; // 검색일자가 바뀌면 검색카운트를 초기화 한다.
			searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
		String encKeyword = URLEncoder.encode(keyword, "UTF-8"); // "무상증자"; // "%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90"; // "무상증자"; // URLEncoder.encode("무상증자", Charset.forName("EUC-KR").name());
		String startDate = new SimpleDateFormat("yyyyMMdd").format(new Date()); // "20210323";
		String endDate = new SimpleDateFormat("yyyyMMdd").format(new Date()); // "20210323"; 
		url+="?keyword="+encKeyword+"&startDate="+startDate+"&endDate="+endDate+"&dspType=B";
		URLConnection conn = new URL(url).openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

		String temp = null;
		while((temp = br.readLine() ) != null){
			if(temp.contains("검색건수 : ")) {
				// System.out.println(url);
				for (int i = 0; i < 100; i++) {
					System.out.printf("\b"); // System.out.print("\b");
				}
				System.out.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				System.out.print(" : [" + keyword + "] " + temp.trim());
				int resultCount = Integer.valueOf(temp.substring(temp.indexOf(" : ") + 3));
				
				System.out.print(" ====> " + resultCount);
				System.out.print(processBar);
				System.out.print("                    ");
				processBar+="■";
				if(processBar.length()>10) {
					processBar = " ■";
				}
				if(resultCount > beforeCount) {
					// 실행 IE to Page...
					try {
						System.out.println();
						System.out.println(url);
						System.out.println("새로운 검색건수가 존재합니다. ====> " + resultCount);
						Desktop.getDesktop().browse(new URI(url));
						beforeCount = resultCount; // 다음 검색에서 동일한 카운트이므로 웹페이지를 안띄우게 한다.
					} catch (URISyntaxException e) {
						System.out.println();
						e.printStackTrace();
						break;
					}
				}
			} else {
				// System.out.println(temp);
			}
		}
		br.close();
	}
}
