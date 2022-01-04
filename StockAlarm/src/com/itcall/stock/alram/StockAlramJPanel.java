package com.itcall.stock.alram;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.itcall.util.ComUtil;
import com.itcall.util.web.SearchEngine;
import com.itcall.util.web.SearchEngineListener;

public class StockAlramJPanel extends JPanel {

	private final int J_X_TERM = 5;
	private final int J_Y_TERM = 7;
	private final int J_HEIGHT = 25;
	private final int TXT_AREA_BUF_SIZE = 1024; // 4096
	private int iGap = 1;
	private long chkCount = 0;
	private int jPaneId;
	private Properties properties;
	
	private JLabel status;
	private JTextField jTxtUrl;
	private JTextField jTxtParams;
	private JTextField jTxtKeyword;
	private JTextField jTxtDocNm;
	private JTextField jTxtCorpNm;
	String[] choices = { "주요사항보고","정기공시","발행공시","지분공시","기타공시","외부감사관련","펀드공시","자산유동화","거래소공시","공정위공시","전체선택"};
	String[] choicesType = { "B"    ,"A"     ,"C"    ,"D"     ,"E"     ,"F"       ,"G"     ,"H"      ,"I"      ,"J"       ,"ALL"};
	private JComboBox<String> jComboType;
	private JComboBox<Integer> jComboDelayMinites;
	private JButton jBtnLaucher;
	private JButton jBtnGoUrl;
	private JScrollPane scrollPaneClientRev;
	private JTextArea jTxea;
	private String targetUrl;
	private String responseBody;
	
	private SearchEngineListener listener = new SearchEngineListener() {
		public int beforeCount = 0;
		public String toDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		@Override
		public boolean workStepComplete(String targetUrl, String method, Map<String, Object> params, String startStr, String endStr, String searchedResult, StringBuffer body) {
			
			int resultCount = Integer.valueOf(searchedResult);
			
			StringBuffer log = new StringBuffer(String.format("> 감시[%d] Try[%d] : ", jPaneId, ++chkCount));
			log.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
					.append(" : [" + params.get("keyword") + "] " + searchedResult)
					.append(" ====> " + resultCount);
			
			for (int i = 0; i < 100; i++) {
				System.out.printf("\b"); // System.out.print("\b");
			}
			System.out.print(log.toString());
			System.out.println("          ");
			
			if(resultCount > beforeCount) {
				// 실행 IE to Page...
				try {
					log.append("\n").append(targetUrl);
					log.append("\n").append("새로운 검색건수가 존재합니다. ====> " + resultCount);
					responseBody = body.toString();
					StockAlramJPanel.this.targetUrl=targetUrl;
					jBtnGoUrl.setEnabled(true);
					jBtnGoUrl.setBackground(new Color(255, 75, 25));
					Desktop.getDesktop().browse(new URI(targetUrl));
					beforeCount = resultCount; // 다음 검색에서 동일한 카운트이므로 웹페이지를 안띄우게 한다.
					PlayAlramSound.playAlramSound(15);
				} catch (URISyntaxException | IOException e) {
					e.printStackTrace();
				}
			} else if(toDay.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) == false || resultCount < beforeCount){
				toDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				beforeCount = resultCount; // 날짜선이 변경되어 초기화 된 경우...
				chkCount = 0; // 체크카운트를 되돌린다.
			}
			if(jTxea.getText().length() < TXT_AREA_BUF_SIZE) {
				log.append("\n").append(jTxea.getText());
			} else {
				log.append("\n").append(jTxea.getText().substring(0, TXT_AREA_BUF_SIZE));
			}
			jTxea.setText(log.toString());
			body.setLength(0); // 읽은 정보를 클리어한다.
			if(jBtnLaucher.getText().equals("감시")) {
				ComUtil.getExecutor().execute(()->jBtnLaucher.setEnabled(true));
				String message = String.format("\nID[%d] 감시 종료 : ", jPaneId);
				System.out.println(message);
				jTxea.setText(message + "\n" + jTxea.getText());
				scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
				return false; // 감시주기를 멈춘다.
			}
			scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
			return true;
		}
	};
	
	public StockAlramJPanel(int jPaneId) throws FileNotFoundException, IOException {
		this.jPaneId = jPaneId;
		// this.iGap = (jPaneId - iGap) * 5;
		this.properties = loadConfFile("StockAlram-" + jPaneId + ".properties");
	}

	public void initialize() {
		status = new JLabel(String.format("ID[%d] 감시주소", jPaneId));
		// _statusLabel = status;
//		status.setHorizontalAlignment(JLabel.LEFT);
//		status.setSize(35, J_HEIGHT);
//		setBorder(BorderFactory.createEtchedBorder());
//		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		status.setBounds(J_X_TERM, J_Y_TERM, 70, J_HEIGHT);
		add(status);
		
		jTxtUrl = new JTextField(properties.getProperty("jTxtUrl", ComUtil.DEF_DART_SEARCH_URL)); // "http://dart.fss.or.kr/dsab007/search.ax"));
		jTxtUrl.setToolTipText("검색URL주소");
		// jTxtUrl.setSize(150, J_HEIGHT);
		jTxtUrl.setBounds(status.getX()+status.getWidth()+J_X_TERM, J_Y_TERM, 250, J_HEIGHT);
		add(jTxtUrl);
		
		jTxtParams = new JTextField(properties.getProperty("jTxtParams", "startDate=${yyyyMMdd}&endDate=${yyyyMMdd}"));
		jTxtParams.setToolTipText("기본적인 파라메터");
		// jTxtParams.setSize(120, J_HEIGHT);
		jTxtParams.setBounds(jTxtUrl.getX()+jTxtUrl.getWidth()+J_X_TERM, J_Y_TERM, 220, J_HEIGHT);
		add(jTxtParams);
		
		jTxtKeyword = new JTextField(properties.getProperty("jTxtKeyword", "무상증자"));
		jTxtKeyword.setToolTipText("검색할 한개의 단어를 입력하세요.");
		// jTxtKeyword.setSize(100, J_HEIGHT);
		jTxtKeyword.setBounds(jTxtParams.getX()+jTxtParams.getWidth()+J_X_TERM, J_Y_TERM, 100, J_HEIGHT);
		add(jTxtKeyword);
		
		jTxtDocNm = new JTextField(properties.getProperty("jTxtDocNm", ""));
		jTxtDocNm.setToolTipText("보고서명칭을 입력하세요. 예) 주요사항보고서(무상증자결정)");
		// jTxtDocNm.setSize(100, J_HEIGHT);
		jTxtDocNm.setBounds(jTxtKeyword.getX()+jTxtKeyword.getWidth()+J_X_TERM, J_Y_TERM, 100, J_HEIGHT);
		add(jTxtDocNm);
		
		jTxtCorpNm = new JTextField(properties.getProperty("jTxtCorpNm", ""));
		jTxtCorpNm.setToolTipText("회사명칭 또는 주식코드을 입력하세요. 예) 한국항우주 또는 047810");
		// jTxtCorpNm.setSize(100, J_HEIGHT);
		jTxtCorpNm.setBounds(jTxtDocNm.getX()+jTxtDocNm.getWidth()+J_X_TERM, J_Y_TERM, 100, J_HEIGHT);
		add(jTxtCorpNm);
		
		jComboType = new JComboBox<String>(choices); // jComboType.setVisible(true);
		jComboType.setSelectedIndex(Integer.valueOf(properties.getProperty("jComboType", "0")));
		jComboType.setBounds(jTxtCorpNm.getX()+jTxtCorpNm.getWidth()+J_X_TERM, J_Y_TERM, 100, J_HEIGHT);
		add(jComboType);
		
		jComboDelayMinites = new JComboBox<Integer>(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
		jComboDelayMinites.setSelectedIndex(Integer.valueOf(properties.getProperty("jComboDelayMinites", "0")));
		jComboDelayMinites.setBounds(jComboType.getX()+jComboType.getWidth()+J_X_TERM, J_Y_TERM, 50, J_HEIGHT);
		add(jComboDelayMinites);
		
		jBtnLaucher = new JButton("감시");
		jBtnLaucher.setToolTipText("감시를 시작하거나 멈춥니다.");
		jBtnLaucher.setBounds(jComboDelayMinites.getX()+jComboDelayMinites.getWidth()+J_X_TERM, J_Y_TERM, 85, J_HEIGHT);
		add(jBtnLaucher);
		
		jBtnGoUrl = new JButton("결과보기");
		jBtnGoUrl.setToolTipText("결과가 검색되면 활성화 됩니다.");
		jBtnGoUrl.setBounds(jBtnLaucher.getX()+jBtnLaucher.getWidth()+J_X_TERM, J_Y_TERM, 95, J_HEIGHT);
		jBtnGoUrl.setEnabled(false);
		add(jBtnGoUrl);
		
		jTxea = new JTextArea("검색결과 표시영역");
		jTxea.setEditable(false);
		// jTxea.setRows(5);
		// jTxea.setBounds(J_TERM*2, J_HEIGHT*2+J_TERM, 1000, J_HEIGHT*5);
		// add(jTxea);
		scrollPaneClientRev = new JScrollPane(jTxea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneClientRev.setBounds(J_X_TERM*2, J_HEIGHT*1+J_Y_TERM, 1210, (int)(J_HEIGHT*2.3));
		// scrollPaneClientRev.setAutoscrolls(true);
		add(scrollPaneClientRev);
		// scrollPaneClientRev.setViewportView(jTxea);
		scrollPaneClientRev.setVisible(true);
		
		jBtnLaucher.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton jBtn = ((JButton)e.getSource());
				if(jBtn.getText().equals("감시")) {
					// 감시를 시작합니다.
					try {
						Map<String, Object> params = ComUtil.switchUrlParamsToMap(jTxtParams.getText()); // url+="?keyword="+encKeyword+"&startDate="+startDate+"&endDate="+endDate+"&dspType=B";
						if(jTxtKeyword.getText().isEmpty() == false) {
							params.put("keyword", jTxtKeyword.getText()); // params.put("startDate", "${yyyyMMdd}"); params.put("endDate", "${yyyyMMdd}");
						}
						if(jTxtDocNm.getText().isEmpty() == false) {
							params.put("reportName", jTxtDocNm.getText());
						}
						if(jTxtCorpNm.getText().isEmpty() == false) {
							params.put("textCrpNm", jTxtCorpNm.getText());
							String dartCrpCik = SearchEngine.searchToUrl(new SearchEngine(String.format("http://dart.fss.or.kr/corp/searchExistAll.ax?textCrpNm=%s", URLEncoder.encode(jTxtCorpNm.getText(), "UTF-8")), "GET", null, "", " "), new StringBuffer());
							if(dartCrpCik==null || dartCrpCik.trim().isEmpty() || dartCrpCik.indexOf("null") >= 0) {
								JOptionPane.showMessageDialog(StockAlramJPanel.this, String.format("입력하신[%s] 회사의 코드값을 찾을 수 없습니다.\n회사의 증권코드번호로 입력하세요.\n\n찾은 값 : %s", jTxtCorpNm.getText(), dartCrpCik), "NOT FOUND", JOptionPane.ERROR_MESSAGE);
								return;
							}
							params.put("textCrpCik", dartCrpCik);
						}
						if(choicesType[jComboType.getSelectedIndex()].equalsIgnoreCase("ALL") == false) {
							params.put("dspType", choicesType[jComboType.getSelectedIndex()]);
						}
						
						final StringBuffer result = new StringBuffer();
						
						if(jTxtUrl.getText().trim().equals(StockNewsSearchType.DART_SEARCH)) {
							// currentPage=1&maxResults=15&maxLinks=10&sort=DATE&sortType=desc&textCrpCik=&lateKeyword=&flrCik=&dspTypeTab=&isSort=false&isTab=false&tocSrch=&b_textCrpCik=&b_flrCik=&b_keyword=%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90&b_docType=&b_textPresenterNm=&b_reportName=&b_startDate=20220104&b_endDate=20220104&b_dspType=B&b_synonym=&b_reSearch=&reportNamePopYn=Y&autoSearch=N&option=contents&keyword=%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90&textCrpNm=&textPresenterNm=&startDate=20220104&endDate=20220104&docType=&reportName=&dspType=B
//							params.put("currentPage", "1");
//							params.put("maxResults", "15");
//							params.put("maxLinks", "10");
//							params.put("sort", "DATE");
//							params.put("sortType", "desc");
//							params.put("textCrpCik", "");
//							params.put("lateKeyword", "");
//							params.put("flrCik", "");
//							params.put("dspTypeTab", "");
//							params.put("isSort", "false");
//							params.put("isTab", "false");
//							params.put("tocSrch", "");
//							params.put("b_textCrpCik", "");
//							params.put("b_flrCik", "");
//							params.put("b_keyword", params.getOrDefault("keyword", ""));
//							params.put("b_docType", "");
//							params.put("b_textPresenterNm", "");
//							params.put("b_reportName", "");
//							params.put("b_startDate", params.getOrDefault("startDate", ""));
//							params.put("b_endDate", params.getOrDefault("endDate", ""));
//							params.put("b_dspType", "B");
//							params.put("b_synonym", "");
//							params.put("b_reSearch", "");
//							params.put("reportNamePopYn", "Y");
//							params.put("autoSearch", "N");
//							params.put("option", "contents");
//							// params.put("keyword", "%EB%AC%B4%EC%83%81%EC%A6%9D%EC%9E%90");
//							params.put("textCrpNm", "");
//							params.put("textPresenterNm", "");
//							// params.put("startDate", "20220104");
//							// params.put("endDate", "20220104");
//							params.put("docType", "");
//							params.put("reportName", "");
//							params.put("dspType", "B");
							SearchEngine.searchToUrl(jTxtUrl.getText(), "GET", params // "GET", "POST"
									, StockNewsSearchType.SEARCH_START_KEY_MAP.get(StockNewsSearchType.DART_SEARCH)
									, StockNewsSearchType.SEARCH_END_KEY_MAP.get(StockNewsSearchType.DART_SEARCH)
									, (jComboDelayMinites.getSelectedIndex() + 1) * 60
									, result, DART_LISTENER );
						} else if(jTxtUrl.getText().trim().equals(StockNewsSearchType.INFOSTOCK_SEARCH)) {
							params = ComUtil.switchUrlParamsToMap(jTxtParams.getText());
							if(jTxtKeyword.getText().isEmpty() == false) {
								params.put("sc_word", jTxtKeyword.getText()); // sc_word=세계 최초, params.put("sc_sdate", "${yyyyMMdd}"); params.put("sc_edate", "${yyyyMMdd}");
							}
							SearchEngine.searchToUrl(jTxtUrl.getText(), "GET", params 
									, StockNewsSearchType.SEARCH_START_KEY_MAP.get(StockNewsSearchType.INFOSTOCK_SEARCH)
									, StockNewsSearchType.SEARCH_END_KEY_MAP.get(StockNewsSearchType.INFOSTOCK_SEARCH)
									, (jComboDelayMinites.getSelectedIndex() + 1) * 60
									, result, INFOSTOCK_LISTENER );
						} else {
							SearchEngine.searchToUrl(jTxtUrl.getText()/* "http://dart.fss.or.kr/dsab007/search.ax" */, "GET", params
								, " : ", "\n", (jComboDelayMinites.getSelectedIndex() + 1) * 60, result
								, listener);
						}
						
						jBtn.setText("감시중");
						String message = String.format("\nID[%d] 감시 중... : ", jPaneId);
						System.out.println(message);
						jTxea.setText(message + "\n" + jTxea.getText());
						saveProperties("StockAlram-" + jPaneId + ".properties");
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("설정된 조사값은 저장하지 못했지만... 감시는 정상으로 처리 중...");
					}
				} else {
					// 감시를 멈춥니다.
					jBtn.setText("감시");
					String message = String.format("\nID[%d] 감시멈춤요청...", jPaneId);
					System.out.println(message);
					jTxea.setText(message + "\n" + jTxea.getText());
					jBtn.setEnabled(false);
				}
				scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
			}
		});
		
		jBtnGoUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(targetUrl));
					jBtnGoUrl.setBackground(jBtnLaucher.getBackground());
				} catch (IOException | URISyntaxException ex) {
					JOptionPane.showMessageDialog(StockAlramJPanel.this, String.format("다음과 같은 사유로 웹페이지를 실해하지 못했습니다.\n\n%s", ex.getMessage()), "에러", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});
		
	}
	
	public Properties loadConfFile(String fileName) throws FileNotFoundException, IOException {
		// load to saved data...
		Properties properties = new Properties();
		if(new File(fileName).exists()) {
			properties.load(new FileInputStream(fileName));
		}
		return properties;
	}
	
	protected void saveProperties(String fileName) throws FileNotFoundException, IOException {
		// save to properties file
		properties.setProperty("jTxtUrl", jTxtUrl.getText());
		properties.setProperty("jTxtParams", jTxtParams.getText());
		properties.setProperty("jTxtKeyword", jTxtKeyword.getText());
		properties.setProperty("jTxtDocNm", jTxtDocNm.getText());
		properties.setProperty("jTxtCorpNm", jTxtCorpNm.getText());
		properties.setProperty("jComboType", jComboType.getSelectedIndex()+"");
		properties.setProperty("jComboDelayMinites", jComboDelayMinites.getSelectedIndex()+"");
		
		properties.store(new FileOutputStream(fileName), null);
	}








	private SearchEngineListener DART_LISTENER = new SearchEngineListener() {
		public int beforeCount = 0;
		public String toDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		@Override
		public boolean workStepComplete(String targetUrl, String method, Map<String, Object> params, String startStr, String endStr, String searchedResult, StringBuffer body) {
			
			int resultCount = Integer.valueOf(searchedResult);
			
			StringBuffer log = new StringBuffer(String.format("> 감시[%d] Try[%d] : ", jPaneId, ++chkCount));
			log.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
					.append(" : [" + params.get("keyword") + "] " + searchedResult)
					.append(" ====> " + resultCount);
			
			for (int i = 0; i < 100; i++) {
				System.out.printf("\b"); // System.out.print("\b");
			}
			System.out.print(log.toString());
			System.out.println("          ");
			
			if(resultCount > beforeCount) {
				// 실행 IE to Page...
				try {
					log.append("\n").append(targetUrl);
					log.append("\n").append("새로운 검색건수가 존재합니다. ====> " + resultCount);
					responseBody = body.toString();
					StockAlramJPanel.this.targetUrl=targetUrl;
					jBtnGoUrl.setEnabled(true);
					jBtnGoUrl.setBackground(new Color(255, 75, 25));
					Desktop.getDesktop().browse(new URI(targetUrl));
					beforeCount = resultCount; // 다음 검색에서 동일한 카운트이므로 웹페이지를 안띄우게 한다.
					PlayAlramSound.playAlramSound(15);
				} catch (URISyntaxException | IOException e) {
					e.printStackTrace();
				}
			} else if(toDay.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date())) == false || resultCount < beforeCount){
				toDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				beforeCount = resultCount; // 날짜선이 변경되어 초기화 된 경우...
				chkCount = 0; // 체크카운트를 되돌린다.
			}
			if(jTxea.getText().length() < TXT_AREA_BUF_SIZE) {
				log.append("\n").append(jTxea.getText());
			} else {
				log.append("\n").append(jTxea.getText().substring(0, TXT_AREA_BUF_SIZE));
			}
			jTxea.setText(log.toString());
			body.setLength(0); // 읽은 정보를 클리어한다.
			if(jBtnLaucher.getText().equals("감시")) {
				ComUtil.getExecutor().execute(()->jBtnLaucher.setEnabled(true));
				String message = String.format("\nID[%d] 감시 종료 : ", jPaneId);
				System.out.println(message);
				jTxea.setText(message + "\n" + jTxea.getText());
				scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
				return false; // 감시주기를 멈춘다.
			}
			scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
			return true;
		}
	};

	private SearchEngineListener INFOSTOCK_LISTENER = new SearchEngineListener() {
		public int beforeCount = 0;
		public String toDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 날짜가 변경되지 않는이상 계속 고정됨.
		@Override
		public boolean workStepComplete(String targetUrl, String method, Map<String, Object> params, String startStr, String endStr, String searchedResult, StringBuffer body) {
			
			String nowDay = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 호출시마다 날짜를 새로 가져옴.
			int resultCount = 0;
			int start = 0;
			while (searchedResult!=null) {
				start = searchedResult.indexOf(nowDay, start);
				if(start>=0) {
					++resultCount;
					start+=nowDay.length();
				} else {
					break;
				}
			}
			
			StringBuffer log = new StringBuffer(String.format("> 감시[%d] Try[%d] : ", jPaneId, ++chkCount));
			log.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
					.append(" : [" + params.get("sc_word") + "] " + searchedResult)
					.append(" ====> " + resultCount);
			
			for (int i = 0; i < 100; i++) {
				System.out.printf("\b"); // System.out.print("\b");
			}
			System.out.print(log.toString());
			System.out.println("          ");
			
			if(resultCount > beforeCount) {
				// 실행 IE to Page...
				try {
					log.append("\n").append(targetUrl);
					log.append("\n").append("새로운 검색건수가 존재합니다. ====> " + resultCount);
					responseBody = body.toString();
					StockAlramJPanel.this.targetUrl=targetUrl;
					jBtnGoUrl.setEnabled(true);
					jBtnGoUrl.setBackground(new Color(255, 75, 25));
					Desktop.getDesktop().browse(new URI(targetUrl));
					beforeCount = resultCount; // 다음 검색에서 동일한 카운트이므로 웹페이지를 안띄우게 한다.
					PlayAlramSound.playAlramSound(15);
				} catch (URISyntaxException | IOException e) {
					e.printStackTrace();
				}
			} else if(toDay.equals(nowDay) == false || resultCount < beforeCount){
				toDay = nowDay;
				beforeCount = resultCount; // 날짜선이 변경되어 초기화 된 경우...
				chkCount = 0; // 체크카운트를 되돌린다.
			}
			if(jTxea.getText().length() < TXT_AREA_BUF_SIZE) {
				log.append("\n").append(jTxea.getText());
			} else {
				log.append("\n").append(jTxea.getText().substring(0, TXT_AREA_BUF_SIZE));
			}
			jTxea.setText(log.toString());
			body.setLength(0); // 읽은 정보를 클리어한다.
			if(jBtnLaucher.getText().equals("감시")) {
				ComUtil.getExecutor().execute(()->jBtnLaucher.setEnabled(true));
				String message = String.format("\nID[%d] 감시 종료 : ", jPaneId);
				System.out.println(message);
				jTxea.setText(message + "\n" + jTxea.getText());
				scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
				return false; // 감시주기를 멈춘다.
			}
			scrollPaneClientRev.getVerticalScrollBar().setValue(0); // 가장 앞으로 이동.
			return true;
		}
	};

}
