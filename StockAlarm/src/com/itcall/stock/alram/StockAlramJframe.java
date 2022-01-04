package com.itcall.stock.alram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class StockAlramJframe extends JFrame {

	private static final int MAX_STOCK_ALRAM = 10;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
//		String test = "한국항공우주";
//		String encode = Charset.defaultCharset().name();
//		System.out.println(encode + " : " + URLEncoder.encode(test, encode));
//		encode = "EUC-KR";
//		System.out.println(encode + " : " + URLEncoder.encode(test, encode));
//		encode = "UTF-8";
//		System.out.println(encode + " : " + URLEncoder.encode(test, encode));
//		encode = "MS949";
//		System.out.println(encode + " : " + URLEncoder.encode(test, encode));
//		encode = Charset.defaultCharset().displayName();
//		System.out.println(encode + " : " + URLEncoder.encode(test, encode));
		
		new StockAlramJframe().initialize();
	}

	public void initialize() throws FileNotFoundException, IOException {
		setTitle("감시 테스트 창...");
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1245, 950);
		// setLocation(250, 100);
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		
		StockAlramJPanel[] stockAlramJPanels = new StockAlramJPanel[MAX_STOCK_ALRAM];
		for (int i = 0; i < stockAlramJPanels.length; i++) {
			stockAlramJPanels[i] = new StockAlramJPanel(i+1);
			stockAlramJPanels[i].initialize();
			stockAlramJPanels[i].setLayout(null);
			stockAlramJPanels[i].setVisible(true);
			jPanel.add(stockAlramJPanels[i]);
		}
		add(jPanel);
		
////		JPanel jPanel = new JPanel();
////		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
////		jPanel.add(jPanel1);
////		jPanel.add(jPanel2);
////		jPanel.add(jPanel3);
////		jPanel.add(jPanel4);
////		jPanel.add(jPanel5);
////		jPanel.add(jPanel6);
//		
////		// add(jPanel, BorderLayout.CENTER);
////		JScrollPane scrollPane = new JScrollPane(jPanel);
////		add(scrollPane, BorderLayout.CENTER);
//		
//		
//		final JScrollPane scrollSetup = new JScrollPane();
//		scrollSetup.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
//		scrollSetup.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		scrollSetup.setBounds(10, 10, 745, 433);
//		add(scrollSetup, BorderLayout.CENTER);
//		
//		JPanel panelSetup = new JPanel();
//		panelSetup.setBackground(new Color(255, 255, 240));
//		scrollSetup.setViewportView(panelSetup);
//		
////		panelSetup.setLayout(null);
////		panelSetup.setLayout( new GridBagLayout());
//		panelSetup.setLayout(new BoxLayout(panelSetup, BoxLayout.Y_AXIS));
//		panelSetup.add(jPanel1);
//		panelSetup.add(jPanel2);
//		panelSetup.add(jPanel3);
//		panelSetup.add(jPanel4);
//		panelSetup.add(jPanel5);
//		panelSetup.add(jPanel6);
		
		
		
		
		setVisible(true);
	}

}
