package com.wei.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wei.bean.SimpleApkInfo;
import com.wei.imp.AppImp;
import com.wei.tools.TextAreaOutput;
import com.wei.tools.Tools;

public class ContentPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1223610312151764478L;

	private AppImp appImp;
	
	private JTextField sourcePath;
	
	private JTextField packageName;
	private JButton alterButton;
	
	private JTextField versionCodeText;
	private JTextField versionNameText;
	
	private JTextArea textArea;
	
	private JButton compile;
	
	private SimpleApkInfo formerApkInfo;
	private SimpleApkInfo alterApkInfo;
	
	private TextAreaOutput textAreaOutput;
	
	public ContentPanel(AppImp appImp) {
		
		this.appImp = appImp;
		
		setLayout(new BorderLayout());
		
		initPanel();
		
		this.setTransferHandler(new ImportTransferHandler());
	}
	
	public void setTextOutput(TextAreaOutput textAreaOutput) {
		
		this.textAreaOutput = textAreaOutput;
		textAreaOutput.setTextOutput(textArea);
	}
	
	/**
	 * 初始化整个内容面板的控件
	 */
	private void initPanel() {
		
		add(newAlterPanel(), BorderLayout.NORTH);
		
		add(newTextPanel(), BorderLayout.CENTER);
		
		compile = new JButton("编译Apk");
		compile.setEnabled(false);
		compile.setActionCommand("CompileApk");
		compile.setToolTipText("编译Apk到输出的目录");
		compile.addActionListener(this);
		
		add(compile, BorderLayout.SOUTH);
	}
	
	/**
	 * 构建一个新的修改包名的面板控件
	 * @return JPanel
	 */
	private JPanel newAlterPanel() {
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(3, 1, 3, 3));
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Apk文件路径"));
		
		sourcePath = new JTextField(45);
		sourcePath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					selectAction(new File(sourcePath.getText()));
				}
			}
		});
		sourcePanel.add(sourcePath);
		
		sourcePanel.add(Box.createHorizontalStrut(5));
		
		JButton sourceButton = new JButton("选择");
		sourceButton.setActionCommand("SelectSource");
		sourceButton.setToolTipText("选择要修改的Apk文件");
		sourceButton.addActionListener(this);
		sourcePanel.add(sourceButton);
		
		contentPanel.add(sourcePanel);
		
		JPanel packageNameInfo = new JPanel();
		packageNameInfo.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Apk包名"));
		
		packageName = new JTextField(41);
		packageName.setEditable(false);
		packageNameInfo.add(packageName);
		
		packageNameInfo.add(Box.createHorizontalStrut(5));
		
		alterButton = new JButton("修改Apk信息");
		alterButton.setActionCommand("AlterName");
		alterButton.setToolTipText("修改Apk的相关信息");
		alterButton.setEnabled(false);
		alterButton.addActionListener(this);
		packageNameInfo.add(alterButton);
		
		contentPanel.add(packageNameInfo);
		
		
		JPanel versionInfo = new JPanel();
		versionInfo.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "版本信息"));
		
		JLabel versionCode = new JLabel("版本号:");
		versionInfo.add(versionCode);
		
		versionCodeText = new JTextField(16);
		versionCodeText.setEditable(false);
		versionInfo.add(versionCodeText);
		
		versionInfo.add(Box.createHorizontalStrut(95));
		
		JLabel versionName = new JLabel("版本名:");
		versionInfo.add(versionName);
		
		versionNameText = new JTextField(16);
		versionNameText.setEditable(false);
		versionInfo.add(versionNameText);
		
		contentPanel.add(versionInfo);
		
		return contentPanel;
	}
	
	/**
	 * 构建一个新的输出信息的面板
	 * @return JPanel
	 */
	private JPanel newTextPanel() {
		
		JPanel textPanel = new JPanel(new BorderLayout());
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBackground(new Color(0x000000));
		textArea.setForeground(new Color(0xf0f7f9));
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		textPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "输出的信息"));
		
		return textPanel;
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
		
		int returnVal = jFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File apkFile = jFileChooser.getSelectedFile();
			if (apkFile != null && apkFile.exists() && apkFile.isFile()) {
				return apkFile;
			}
		}
		
		return null;
	}
	
	/**
	 * 设置修改包名的相关控件激活状态
	 * @param enabled
	 */
	private void setAlterEnabled(boolean enabled) {
		
		packageName.setEditable(enabled);
		alterButton.setEnabled(enabled);
		versionCodeText.setEditable(enabled);
		versionNameText.setEditable(enabled);
	}
	
	/**
	 * 设置apk的相关控件的信息
	 * @param simpleApkInfo
	 */
	private void setAlterInfo(SimpleApkInfo simpleApkInfo) {
		
		if (simpleApkInfo == null) {
			packageName.setText("");
			versionCodeText.setText("");
			versionNameText.setText("");
			
			packageName.setToolTipText("");
			versionCodeText.setToolTipText("");
			versionNameText.setToolTipText("");
			return ;
		}
		
		packageName.setText(simpleApkInfo.getPackageName());
		versionCodeText.setText(simpleApkInfo.getVersionCode());
		versionNameText.setText(simpleApkInfo.getVersionName());
		
		packageName.setToolTipText("原包名:" + formerApkInfo.getPackageName());
		versionCodeText.setToolTipText("原版本号:" + formerApkInfo.getVersionCode());
		versionNameText.setToolTipText("原版本名:" + formerApkInfo.getVersionName());
	}
	
	/**
	 * 获取控件中的apk相关信息
	 * @return SimpleApkInfo
	 */
	private SimpleApkInfo getAlterInfo() {
		
		SimpleApkInfo info = new SimpleApkInfo();
		info.setPackageName(packageName.getText());
		info.setVersionCode(versionCodeText.getText());
		info.setVersionName(versionNameText.getText());
		
		return info;
	}
	
	/**
	 * 检查修改apk信息的完整性
	 * @param simpleApkInfo 修改后的apk信息
	 * @return true:没有问题,false:有问题
	 */
	private boolean checking(SimpleApkInfo simpleApkInfo) {
		
		if (simpleApkInfo == null
				|| simpleApkInfo.getPackageName() == null
				|| simpleApkInfo.getPackageName().trim().length() <= 0
				|| simpleApkInfo.getVersionCode() == null
				|| simpleApkInfo.getVersionCode().trim().length() <= 0
				|| simpleApkInfo.getVersionName() == null
				|| simpleApkInfo.getVersionName().trim().length() <= 0) {
			return false;
		}
		
		if (simpleApkInfo.equals(this.formerApkInfo)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 处理对应的apk文件,并反编译apk文件
	 * @param sourceApkFile 处理的apk文件
	 * @return true:反编译apk文件成功,false:反编译失败
	 */
	public boolean selectAction(File sourceApkFile) {
		
		if (sourceApkFile == null || !sourceApkFile.exists()
				|| !sourceApkFile.getName().toLowerCase().endsWith(".apk")) {
			Tools.outputInfoln(textAreaOutput, "无效的Apk文件!");
			return false;
		}

		// 设置当前反编译的Apk路径
		sourcePath.setText(sourceApkFile.getPath());
		
		// 设置控件信息并禁用相关控件
		setAlterInfo(null);
		setAlterEnabled(false);
		
		// 反编译apk文件,并返回当前apk的相关信息
		formerApkInfo = appImp.open(sourceApkFile);
		
		if (formerApkInfo == null) {
			compile.setEnabled(false);
			Tools.outputInfoln(textAreaOutput, "获取Apk信息失败!");
			return false;
		}
		
		// 设置获取到的apk信息
		setAlterInfo(formerApkInfo);
		compile.setEnabled(true);
		setAlterEnabled(true);
		
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("SelectSource".equals(action)) {
			selectAction(selectApkFile(sourcePath.getText()));
		} else if ("AlterName".equals(action)) {
			SimpleApkInfo simpleApkInfo = getAlterInfo();
			if (!checking(simpleApkInfo)) {
				Tools.outputInfoln(textAreaOutput, "修改包名异常!\n" + simpleApkInfo);
				return ;
			}
			this.alterApkInfo = simpleApkInfo;
			appImp.alterPackageName(formerApkInfo, alterApkInfo);
		} else if ("CompileApk".equals(action)) {
			if (alterApkInfo == null) {
				Tools.outputInfoln(textAreaOutput, "编译Apk异常!\nAlterApkInfo: " + alterApkInfo);
				return ;
			}
			appImp.compileApk(alterApkInfo);
		}
	}
	
	/**
	 * 对鼠标拖入文件进行处理
	 * @author jingcai.wei
	 *
	 */
	class ImportTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(JComponent comp,DataFlavor[] transferFlavors) {
			
			return true;		/* 必须要返回true,不然不可以拖东西到面板中来 */
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			
			if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				try {
					boolean isSuccess = true;
					List<?> list = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
					if (list != null && list.size() > 0) {
						File file = (File)list.get(0);
						isSuccess = selectAction(file);
					}
					if (!isSuccess) {
						JOptionPane.showMessageDialog(null, "请拖入正确的(.apk)文件!","错误!", JOptionPane.ERROR_MESSAGE);
					}
					return true;
				} catch (UnsupportedFlavorException e) {
					Tools.log.error("UnsupportedFlavorException!", e);
				} catch (IOException e) {
					Tools.log.error("IOException!", e);
				}
			}
			
			return super.importData(comp, t);
		}
	}
}
