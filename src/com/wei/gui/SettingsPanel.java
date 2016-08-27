package com.wei.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.wei.bean.ConfigInfo;
import com.wei.tools.Tools;

public class SettingsPanel extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -5970503415299811493L;
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 320;
	
	private JTextField keystorePassword;
	private JTextField name;
	private JTextField password;
	private JTextField path;
	private ConfigInfo oldConfigInfo;
	private ConfigInfo resultConfigInfo;

	public SettingsPanel(JFrame jFrame, ConfigInfo configInfo) {
		
		super(jFrame, true);
		
		this.oldConfigInfo = configInfo;
		
		setTitle("设置");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, false);
		
		setDefaultContent();
		
		pack();
	}
	
	private void initPanel() {
		
		setLayout(null);
		
		add(newContentPanel());
		
		JButton okButton = new JButton("确定");
		okButton.setActionCommand("OK");
		okButton.setBounds(WIDTH - 160, HEIGHT - 59, 65, 25);
		okButton.addActionListener(this);
		add(okButton);
		
		JButton cancelButton = new JButton("取消");
		cancelButton.setActionCommand("Cancel");
		cancelButton.setBounds(WIDTH - 83, HEIGHT - 59, 65, 25);
		cancelButton.addActionListener(this);
		add(cancelButton);
	}
	
	private JPanel newContentPanel() {
		
		JPanel contentPanel = new JPanel(null);
		contentPanel.setBounds(10, 10, WIDTH - 26, HEIGHT - 75);
		contentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "签名文件设置"));
		
		JLabel keystoreLable = new JLabel("证书密码:");
		keystoreLable.setBounds(30, 35, 60, 25);
		contentPanel.add(keystoreLable);
		
		keystorePassword = new JTextField();
		keystorePassword.setBounds(100, 35, 240, 25);
		contentPanel.add(keystorePassword);
		
		JLabel nameLable = new JLabel("名称:");
		nameLable.setBounds(30, 75, 60, 25);
		contentPanel.add(nameLable);
		
		name = new JTextField();
		name.setBounds(100, 75, 240, 25);
		contentPanel.add(name);
		
		JLabel passwordLable = new JLabel("密码:");
		passwordLable.setBounds(30, 115, 60, 25);
		contentPanel.add(passwordLable);
		
		password = new JTextField();
		password.setBounds(100, 115, 240, 25);
		contentPanel.add(password);
		
		JLabel pathLable = new JLabel("路径:");
		pathLable.setBounds(30, 155, 60, 25);
		contentPanel.add(pathLable);
		
		path = new JTextField();
		path.setBounds(100, 155, 240, 25);
		contentPanel.add(path);
		
		JButton select = new JButton("选择");
		select.setActionCommand("Select");
		select.setBounds(275, 200, 65, 25);
		select.addActionListener(this);
		contentPanel.add(select);
		
		JButton defaults = new JButton("恢复默认");
		defaults.setActionCommand("RestoreDefaults");
		defaults.setBounds(145, 200, 115, 25);
		defaults.addActionListener(this);
		contentPanel.add(defaults);
		
		return contentPanel;
	}
	
	private void setDefaultContent() {
		
		if (oldConfigInfo == null) {
			return ;
		}
		
		keystorePassword.setText(oldConfigInfo.getKeystorePassword());
		name.setText(oldConfigInfo.getName());
		password.setText(oldConfigInfo.getPassword());
		path.setText(oldConfigInfo.getKeystoreFile().getPath());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("OK".equals(action)) {
			okAction();
		} else if ("Cancel".equals(action)) {
			this.dispose();
		} else if ("Select".equals(action)) {
			File file = selectKeystoreFile(path.getText());
			if (file != null) {
				path.setText(file.getPath());
			}
		} else if ("RestoreDefaults".equals(action)) {
			setDefaultContent();
		}
	}
	
	private void okAction() {
		
		String keystorePassword = this.keystorePassword.getText();
		if (keystorePassword == null || keystorePassword.trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "证书密码不能为Null!", "警告!", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String name = this.name.getText();
		if (name == null || name.trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "名称不能为Null!", "警告!", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String password = this.password.getText();
		if (password == null || password.trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "密码不能为Null!", "警告!", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String pathText = this.path.getText();
		if (pathText == null || pathText.trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "文件路径不能为Null!", "警告!", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		File keyFile = new File(pathText);
		if (!keyFile.exists() || !keyFile.isFile()
				|| !keyFile.getName().toLowerCase().endsWith(".keystore")) {
			JOptionPane.showMessageDialog(this, "当前选择的文件无效!", "警告!", JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		ConfigInfo newConfigInfo = new ConfigInfo();
		newConfigInfo.setKeystorePassword(keystorePassword);
		newConfigInfo.setName(name);
		newConfigInfo.setPassword(password);
		newConfigInfo.setKeystoreFile(keyFile);
		resultConfigInfo = newConfigInfo;
		
		this.dispose();
	}
	
	private File selectKeystoreFile(String defaultPath) {
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("选择签名文件 (*.keystore)", "keystore");
		
		JFileChooser jFileChooser = new JFileChooser(defaultPath);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileFilter(filter);
		
		int returnVal = jFileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File keystoreFile = jFileChooser.getSelectedFile();
			if (keystoreFile != null && keystoreFile.exists() && keystoreFile.isFile()) {
				return keystoreFile;
			}
		}
		
		return null;
	}

	public ConfigInfo getResultConfigInfo() {
		return resultConfigInfo;
	}
}
