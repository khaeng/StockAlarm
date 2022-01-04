package com.itcall.util.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.itcall.util.ComUtil;

public class SearchEngine implements Runnable {

	private static final String thisIsNotParamKey = "__paramsEncodedData";
//	private int beforeCount = 0;
//	private String processBar = " ■";
//	private String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

	private String url;
	private String method;
	private Map<String, Object> params;
	private String startStr;
	private String endStr;
	private final StringBuffer resultSb;
	private final SearchEngineListener listener;

	private ScheduledFuture<?> scheduledFuture;
	private String targetUrl;
	private StringBuilder paramsData;

	public SearchEngine(String url, String method, Map<String, Object> params, String startStr, String endStr)
			throws UnsupportedEncodingException {
		this(url, method, params, startStr, endStr, null, null);
	}

	public SearchEngine(String url, String method, Map<String, Object> params, String startStr, String endStr,
			final StringBuffer resultSb, final SearchEngineListener listener) throws UnsupportedEncodingException {
		this.targetUrl = this.url = url;
		this.method = method;
		this.params = params;
		this.startStr = startStr;
		this.endStr = endStr;
		this.resultSb = resultSb;
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			String searchedValue = searchToUrl(this, resultSb);
			params.put(thisIsNotParamKey, this.paramsData);
			if (listener != null) {
				boolean isNext = listener.workStepComplete(this.targetUrl, method, params, startStr, endStr, searchedValue,
						resultSb);
				if (isNext == false) {
					this.scheduledFuture.cancel(false);
				}
			} else {
				this.scheduledFuture.cancel(false);
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void searchToUrl(String url, String method, Map<String, Object> params, String startStr,
			String endStr, int delaySec, final StringBuffer result, final SearchEngineListener listener)
			throws UnsupportedEncodingException {
		SearchEngine searchEngine = new SearchEngine(url, method, params, startStr, endStr, result, listener);
		searchEngine.scheduledFuture = ComUtil.getScheduler().scheduleWithFixedDelay(searchEngine, 1/*delaySec*/, delaySec, TimeUnit.SECONDS);
	}

	public static String searchToUrl(SearchEngine engine/***, String url, String method, Map<String, Object> params, String startStr, String endStr ***/,final StringBuffer result) throws UnknownHostException, UnsupportedEncodingException, IOException {

		if (result == null) {
			throw new NullPointerException("결과값을 전달받을 StringBuffer(result) 인수가 null입니다.");
		}

		// StringBuffer에 기존 값이 존재하면 신규 수집한 데이터에서만 검색할 수 있도록 시작위치를 기억한다.
		int startPos = result.length();

		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {

			// 인수정리...
			engine.paramsData = new StringBuilder();
			if (engine.params != null && engine.params.size() > 0) {
				engine.params.remove(thisIsNotParamKey);
				for (Map.Entry<String, Object> param : engine.params.entrySet()) {
					if (engine.paramsData.length() != 0)
						engine.paramsData.append('&');
					engine.paramsData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					engine.paramsData.append('=');
					engine.paramsData.append(URLEncoder.encode(ComUtil.switchFormatToDateTime(String.valueOf(param.getValue())), "UTF-8"));
				}
			}

			if (engine.method.equalsIgnoreCase("GET")) {
				if (engine.params != null && engine.params.size() > 0) {
					StringBuffer getUrl = new StringBuffer(engine.url);
					if (engine.url.contains("?") && engine.url.endsWith("?") == false) {
						getUrl.append("&");
					} else if (engine.url.contains("?") == false) {
						getUrl.append("?");
					}
//					for (String key : params.keySet()) {
//						getUrl.append(key).append("=").append(params.get(key)).append("&");
//					}
//					getUrl.setLength(getUrl.length() - 1); // 마지막에 추가된 "&"를 제거한다.
					getUrl.append(engine.paramsData.toString());
					engine.targetUrl = getUrl.toString();
				}
				conn = (HttpURLConnection) new URL(engine.targetUrl).openConnection();
			} else if (engine.method.equalsIgnoreCase("POST")) {
				byte[] postDataBytes = engine.paramsData.toString().getBytes("UTF-8");
				conn = (HttpURLConnection) new URL(engine.targetUrl = engine.url).openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				conn.setDoOutput(true);
				conn.getOutputStream().write(postDataBytes); // POST 호출
				conn.getOutputStream().flush();
			}
			if(System.getProperty("log.level", "info").equalsIgnoreCase("debug")) {
				System.out.println("\n"+engine.targetUrl);
			}

			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				result.append(readLine).append("\n");
			}

			// 검색어 찾기
			if (engine.startStr == null && engine.endStr == null) {
				return result.substring(startPos);
			}
			startPos = result.indexOf(engine.startStr, startPos);
			if (startPos >= 0) {
				if (engine.endStr == null) {
					return result.substring(startPos);
				}
				int endPos = result.indexOf(engine.endStr, startPos += engine.startStr.length());
				if (endPos > 0) {
					return result.substring(startPos, endPos);
				}
			}

//		} catch (IOException e) {
//			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

}
