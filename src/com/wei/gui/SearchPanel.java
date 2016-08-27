package com.wei.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wei.tools.SearchFile;
import com.wei.tools.StringSearch;
import com.wei.tools.TextAreaOutput;

public class SearchPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JTextField searchPath;
	private JTextField saveFilePath;
	private JTextArea textArea;
	
	private TextAreaOutput textAreaOutput;

	public SearchPanel(JFrame jFrame) {
		
		setLayout(new BorderLayout());
		
		initPanel();
		
		textAreaOutput = new TextAreaOutput(textArea);
	}
	
	private void initPanel() {
		
		add(newSelectPanel(), BorderLayout.NORTH);
		
		add(newTextPanel(), BorderLayout.CENTER);
		
		JButton copyAll = new JButton("Search All");
		copyAll.setActionCommand("SearchAll");
		copyAll.addActionListener(this);
		copyAll.setBackground(new Color(0x007e9c));
		
		add(copyAll, BorderLayout.SOUTH);
	}
	
	private JPanel newSelectPanel() {
		
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new GridLayout(2, 1, 3, 3));
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Search Dir|File"));
		
		searchPath = new JTextField(60);
		searchPath.setEditable(false);
		sourcePanel.add(searchPath);
		
		sourcePanel.add(Box.createHorizontalStrut(5));
		
		JButton sourceButton = new JButton("Select");
		sourceButton.setBackground(new Color(0x007e9c));
		sourceButton.setActionCommand("SelectSearchDir");
		sourceButton.addActionListener(this);
		sourcePanel.add(sourceButton);
		
		selectPanel.add(sourcePanel);
		
		JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "SaveResult File"));
		
		saveFilePath = new JTextField(60);
		saveFilePath.setEditable(false);
		targetPanel.add(saveFilePath);
		
		targetPanel.add(Box.createHorizontalStrut(5));
		
		JButton targetButton = new JButton("Select");
		targetButton.setBackground(new Color(0x007e9c));
		targetButton.setActionCommand("SaveResultDir");
		targetButton.addActionListener(this);
		targetPanel.add(targetButton);
		
		selectPanel.add(targetPanel);
		
		return selectPanel;
	}
	
	private JScrollPane newTextPanel() {
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBackground(new Color(0x000000));
		textArea.setForeground(new Color(0xf0f7f9));
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Output Content"));
		
		return scrollPane;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		if ("SelectSearchDir".equals(action)) {
			File sourcePath = selectDirOrFile(searchPath.getText());
			if (sourcePath != null) {
				searchPath.setText(sourcePath.getPath());
			}
		} else if ("SaveResultDir".equals(action)) {
			File targetPath = saveFilePath(saveFilePath.getText());
			if (targetPath != null) {
				String parentPath = targetPath.getParent();
				if (parentPath == null || !new File(parentPath).exists()) {
					textAreaOutput.outputInfoln("SaveResultDir Error!!!");
					return ;
				}
				saveFilePath.setText(targetPath.getPath());
			}
		} else if ("SearchAll".equals(action)) {
			String sourcePath = searchPath.getText();
			String targetPath = saveFilePath.getText();
			if (sourcePath == null || sourcePath.trim().length() <= 0) {
				textAreaOutput.outputInfoln("SelectSearchDirOrFile Error!!!");
				return ;
			}
			if (targetPath == null || targetPath.trim().length() <= 0) {
				textAreaOutput.outputInfoln("SaveResultFile Error!!!");
				return ;
			}
			
			StringSearch stringSearch = new StringSearch();
			SearchFile searchFile = new SearchFile(sourcePath, targetPath, stringSearch);
			searchFile.setEncoding("GBK");
			searchFile.setOutputImp(textAreaOutput);
			searchFile.setFilter(new com.wei.tools.FileNameExtensionFilter("java"));
			if (searchFile.searchAll()) {
				textAreaOutput.outputInfoln("Success...");
			}
		}
	}
	
	private File saveFilePath(String defaultPath) {
		
		JFileChooser jFileChooser = new JFileChooser(defaultPath);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setSelectedFile(new File("SearchResult.xml"));
		
		int returnVal = jFileChooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return jFileChooser.getSelectedFile();
		}
		
		return null;
	}
	
	private File selectDirOrFile(String defaultPath) {
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Select File|Dir (*.java)", "java");
		
		JFileChooser jFileChooser = new JFileChooser(defaultPath);
		jFileChooser.setFileFilter(filter);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int returnVal = jFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dir = jFileChooser.getSelectedFile();
			if (dir != null && dir.exists()
					&& (dir.isDirectory() || dir.isFile())) {
				return dir;
			}
		}
		
		return null;
	}
}
