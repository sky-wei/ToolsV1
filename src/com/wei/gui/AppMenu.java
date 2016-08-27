package com.wei.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wei.imp.AppImp;
import com.wei.main.AppMain;

public class AppMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = 8733691563697940118L;

	private File lastSignerFile;
	private File batchSignerFile;
	
	private AppImp appImp;
	
	public AppMenu(AppImp appImp) {
		
		this.appImp = appImp;
		
		initMenuBar();
	}
	
	private void initMenuBar() {
		
		JMenu menu = new JMenu("菜单(M)");
		menu.setMnemonic(KeyEvent.VK_M);
		add(menu);
		
		JMenuItem open = new JMenuItem("打开");
		open.setActionCommand("Open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menu.add(open);
		open.addActionListener(this);
		
		menu.addSeparator();
		
		JMenuItem quit = new JMenuItem("退出");
		quit.setActionCommand("Quit");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		menu.add(quit);
		quit.addActionListener(this);
		
		JMenu function = new JMenu("功能(F)");
		function.setMnemonic(KeyEvent.VK_F);
		add(function);
		
		JMenuItem decompileFile = new JMenuItem("反编译");
		decompileFile.setActionCommand("DecompileFile");
		decompileFile.addActionListener(this);
		function.add(decompileFile);
		
		JMenuItem compileFile = new JMenuItem("回编译");
		compileFile.setActionCommand("CompileFile");
		compileFile.addActionListener(this);
		function.add(compileFile);
		
		function.addSeparator();
		
		JMenuItem signerFile = new JMenuItem("签名文件");
		signerFile.setActionCommand("SignerFile");
		signerFile.addActionListener(this);
		function.add(signerFile);
		
		JMenuItem batchSignerFile = new JMenuItem("批量签名文件");
		batchSignerFile.setActionCommand("BatchSignerFile");
		batchSignerFile.addActionListener(this);
		function.add(batchSignerFile);
		
		JMenuItem verifySigner = new JMenuItem("验证签名");
		verifySigner.setActionCommand("VerifySigner");
		verifySigner.addActionListener(this);
		function.add(verifySigner);
		
		function.addSeparator();
		
		JMenuItem signerManage = new JMenuItem("签名管理");
		signerManage.setActionCommand("SignerManage");
		signerManage.addActionListener(this);
		function.add(signerManage);
		
		JMenu settings = new JMenu("设置(S)");
		settings.setMnemonic(KeyEvent.VK_S);
		add(settings);
		
		JMenu alterMode = new JMenu("修改方式");
		settings.add(alterMode);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		JRadioButtonMenuItem simpleMode = new JRadioButtonMenuItem("简单");
		simpleMode.setActionCommand("SimpleMode");
		simpleMode.addActionListener(this);
		buttonGroup.add(simpleMode);
		
		JRadioButtonMenuItem complexMode = new JRadioButtonMenuItem("复杂");
		complexMode.setActionCommand("ComplexMode");
		complexMode.setSelected(true);
		complexMode.addActionListener(this);
		buttonGroup.add(complexMode);
		
		alterMode.add(simpleMode);
		alterMode.add(complexMode);
		
		JMenuItem keySettings = new JMenuItem("签名证书");
		keySettings.setActionCommand("KeySettings");
		keySettings.addActionListener(this);
		settings.add(keySettings);
		
		JMenuItem pathSettings = new JMenuItem("输出路径");
		pathSettings.setActionCommand("PathSettings");
		pathSettings.addActionListener(this);
		settings.add(pathSettings);
		
		settings.addSeparator();
		
		JMenuItem themeSettings = new JMenuItem("界面设置");
		themeSettings.setActionCommand("ThemeSettings");
		themeSettings.addActionListener(this);
		settings.add(themeSettings);
		
		JMenu help = new JMenu("帮助(H)");
		help.setMnemonic(KeyEvent.VK_H);
		add(help);
		
		JMenuItem about = new JMenuItem("关于");
		about.setActionCommand("About");
		about.addActionListener(this);
		help.add(about);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("Open".equals(action)) {
			JOptionPane.showMessageDialog(appImp.getCurFrame(), "该功能暂未实现!还需等待...", "提示!", JOptionPane.INFORMATION_MESSAGE);
		} else if ("Quit".equals(action)) {
			appImp.quit();
		} else if ("KeySettings".equals(action)) {
			appImp.keystoreSettings();
		} else if ("PathSettings".equals(action)) {
			JOptionPane.showMessageDialog(appImp.getCurFrame(), "该功能暂未实现!还需等待...", "提示!", JOptionPane.INFORMATION_MESSAGE);
		} else if ("ThemeSettings".equals(action)) {
			UiSettings uiSettings = new UiSettings(appImp, appImp.loadUiProperty());
			uiSettings.setVisible(true);
			
			if (!uiSettings.isCancel()) {
				appImp.saveUiProperty(uiSettings.getUiProperty());
			}
		} else if ("About".equals(action)) {
			AboutDialog aboutDialog = new AboutDialog(appImp.getCurFrame());
			aboutDialog.setVisible(true);
		} else if ("SimpleMode".equals(action)) {
			appImp.setAlterMode(AppMain.SIMPLE_MODE);
		} else if ("ComplexMode".equals(action)) {
			appImp.setAlterMode(AppMain.COMPLEX_MODE);
		} else if ("SignerFile".equals(action)) {
			File selectFile = selectApkFile(
					lastSignerFile == null ? null : lastSignerFile.getPath());
			if (selectFile != null) {
				lastSignerFile = selectFile;
				appImp.signerFile(selectFile);
			}
		} else if ("BatchSignerFile".equals(action)) {
			File selectDir = selectApkDir(
					batchSignerFile == null ? null : batchSignerFile.getPath());
			if (selectDir != null) {
				batchSignerFile = selectDir;
				appImp.batchSignerFile(selectDir);
			}
		} else if ("VerifySigner".equals(action)) {
			File selectFile = selectApkFile(null);
			if (selectFile != null) {
				appImp.verifySigner(selectFile);
			}
		} else if ("SignerManage".equals(action)) {
			SignerManage signerManage = new SignerManage(appImp);
			signerManage.setVisible(true);
		} else if ("DecompileFile".equals(action)) {
			File selectFile = selectApkFile(null);
			if (selectFile != null) {
				appImp.decompileFile(selectFile);
			}
		} else if ("CompileFile".equals(action)) {
			File selectFile = selectApkDir(null);
			if (selectFile != null) {
				appImp.compileFile(selectFile);
			}
		}
	}
	
	/**
	 * 选择.apk文件,并返回选择的apk文件的路径
	 * @param defaultPath 默认的文件路径
	 * @return
	 */
	private File selectApkFile(String defaultPath) {
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("选择Apk文件(*.apk)", "apk");
		
		JFileChooser jFileChooser = new JFileChooser(defaultPath);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileFilter(filter);
		
		int returnVal = jFileChooser.showOpenDialog(appImp.getCurFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File apkFile = jFileChooser.getSelectedFile();
			if (apkFile != null && apkFile.exists() && apkFile.isFile()) {
				return apkFile;
			}
		}
		
		return null;
	}
	
	private File selectApkDir(String defaultPath) {
		
		JFileChooser jFileChooser = new JFileChooser(defaultPath);
		jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		
		int returnVal = jFileChooser.showOpenDialog(appImp.getCurFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dirFile = jFileChooser.getSelectedFile();
			if (dirFile != null && dirFile.exists() && dirFile.isDirectory()) {
				return dirFile;
			}
		}
		
		return null;
	}
}
