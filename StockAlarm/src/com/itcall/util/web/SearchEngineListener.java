package com.itcall.util.web;

import java.util.Map;

public interface SearchEngineListener {

	/**
	 * 검색조건에 해당하는 1회 검색을 수행한 후 호출된다.
	 * return 값으로 false를 반환하면. 더이상 반복검색을 하지 않고 종료한다.
	 * @param targetUrl - 검색대상 URL주소. 프로토콜(HTTP/HTTPS)를 포함한 주소를 입력한다.
	 * @param method - GET / POST 를 지정할 수 있다.
	 * @param params - MAP으로 호출인수값을 입력받는다.
	 * @param startStr - 결과에 대한 검색시작값. [검색시작 ~ 검색종료] 사이에 값이 존재하면 검색이 성공한 케이스임.
	 * @param endStr - 결과에 대한 검색종료값. [검색시작 ~ 검색종료] 사이에 값이 존재하면 검색이 성공한 케이스임.
	 * @param searchedResult - 검색 키워드 사이에 존재하는 값을 반환. 검색키워드 자체가 없으면 null로 반환된다. ""(공백) 문자열이 반환되면 검색된 결과가 ""(공백)이란 뜻이다.
	 * @param body - 실제 호출결과에 대한 전체 전문이 StringBuffer로 반환된다. - 최초 호출시에 전달한 StringBuffer 인스턴스를 그대로 사용하므로 반환 시 setLength(0)로 비워줘야 한다.
	 * @return
	 */
	public boolean workStepComplete(String targetUrl, String method, Map<String, Object> params, String startStr, String endStr, String searchedResult, final StringBuffer body);

}
