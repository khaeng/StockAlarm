package com.itcall.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ComUtil {

	public static final String DEF_DART_SEARCH_URL = "https://dart.fss.or.kr/dsab007/search.ax";

	private static final String SWITCH_PRE_KEY = "${";
	private static final String SWITCH_POST_KEY = "}";
	private static final int SCHEDULER_POOL_SIZE = 100;
	private static final int THREAD_POOL_SIZE = 100;
	private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(SCHEDULER_POOL_SIZE);
	// private static final Executor executor = Executors.newSingleThreadExecutor();
	private static final Executor EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public static ScheduledExecutorService getScheduler() {
		return SCHEDULER;
	}
	public static Executor getExecutor() {
		return EXECUTOR;
	}
	
	/**
	 * 입력된 데이터 중에 날짜/시간 포멧으로 진입한 데이터를 실제 지금시간으로 변경하여 반환한다. "${yyyyMMdd HHmmss.SSS}"
	 * >>> "20211223 154325.321"
	 * 
	 * @param formatData
	 * @return
	 */
	public static String switchFormatToDateTime(String formatData) {
		if (formatData == null) {
			return formatData;
		}
		StringBuffer sb = new StringBuffer();
		int startPos = formatData.indexOf(SWITCH_PRE_KEY);
		int endPos = formatData.indexOf(SWITCH_POST_KEY, startPos);
		if (startPos >= 0 && endPos > startPos) {
			sb.append(formatData.substring(0, startPos))
					.append(new SimpleDateFormat(formatData.substring(startPos + SWITCH_PRE_KEY.length(), endPos))
							.format(new Date()))
					.append(switchFormatToDateTime(formatData.substring(endPos + SWITCH_POST_KEY.length())));
			return sb.toString();
		} else {
			return formatData;
		}
	}

	/**
	 * Map 데이터를 URL 파라메터로 인코딩한 데이터로 변환하여 반환한다.
	 * {"key":"무상증자", "key2":"Test"} ===> "key=%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90&key2=Test"
	 * @param param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String switchMapToUrlParams(Map<String, Object> param) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"),
					URLEncoder.encode(switchFormatToDateTime(String.valueOf(entry.getValue())), "UTF-8")));
		}
		return sb.toString();
	}

	/**
	 * URL 파라메터로 인코딩딘 데이터를 디코딩하여 Map 데이터로 반환한다.
	 * "key=%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90&key2=Test" ===> {"key":"무상증자", "key2":"Test"}
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, Object> switchUrlParamsToMap(String params) throws UnsupportedEncodingException {
		if(params==null) {
			throw new NullPointerException("입력변수가 null입니다. URL인코딩 파라메터들의 집합을 Map으로 변환할 수 없습니다.");
		}
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		if(params.isEmpty()) {
			return paramMap;
		}
		int pos = params.indexOf("&");
		if(pos>0) {
			String param = params.substring(0,pos);
			if(param.split("=",2).length>=2) {
				paramMap.put(param.split("=")[0], URLDecoder.decode(param.split("=",2)[1], "UTF-8"));
			} else {
				paramMap.put(param.split("=")[0], param.indexOf("=")>0 ? "":null);
			}
			paramMap.putAll(switchUrlParamsToMap(params.substring(pos+1)));
		} else {
			if(params.split("=",2).length>=2) {
				paramMap.put(params.split("=")[0], URLDecoder.decode(params.split("=",2)[1], "UTF-8"));
			} else {
				paramMap.put(params.split("=")[0], params.indexOf("=")>0 ? "":null);
			}
		}
		return paramMap;
	}

}
