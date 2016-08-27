package com.wei.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wei.bean.SignerInfo;
import com.wei.imp.AppImp;
import com.wei.tools.Tools;

public class CreateSignerPanel extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 390;
	private static final int HEIGHT = 470;
	
	private JTextField keystorePassword;
	private JTextField termText;
	private JTextField keyName;
	private JTextField keyPassword;
	private JTextField nameText;
	private JTextField organizationText;
	private JTextField cityText;
	private JTextField provinceText;
	private JTextField codeText;
	
	private JLabel hintLabel;
	private JLabel showInfo;
	
	private boolean cancel;
	private SignerInfo signerInfo;
	
	public CreateSignerPanel(AppImp appImp) {
		
		super(appImp.getCurFrame(), true);
		
		this.cancel = true;
		
		setTitle("创建签名文件");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, false);
		
		pack();
	}
	
	private void initPanel() {
		
		setLayout(null);
		
		add(newContentPanel());
		
		hintLabel = new JLabel();
		hintLabel.setBounds(WIDTH - 375, HEIGHT - 58, 200, 25);
		hintLabel.setForeground(Color.RED);
		add(hintLabel);
		
		JButton okButton = new JButton("确定");
		okButton.setActionCommand("OK");
		okButton.setToolTipText("创建签名文件");
		okButton.setBounds(WIDTH - 160, HEIGHT - 59, 65, 25);
		okButton.addActionListener(this);
		add(okButton);
		
		JButton cancelButton = new JButton("取消");
		cancelButton.setActionCommand("Cancel");
		cancelButton.setToolTipText("取消创建签名文件");
		cancelButton.setBounds(WIDTH - 83, HEIGHT - 59, 65, 25);
		cancelButton.addActionListener(this);
		add(cancelButton);
	}
	
	private JPanel newContentPanel() {
		
		JPanel contentPanel = new JPanel(null);
		contentPanel.setBounds(10, 10, WIDTH - 26, HEIGHT - 75);
		contentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "签名信息"));
		
		JLabel keystoreLable = new JLabel("证书密码:");
		keystoreLable.setBounds(30, 35, 60, 25);
		contentPanel.add(keystoreLable);
		
		keystorePassword = new JTextField();
		keystorePassword.setBounds(100, 35, 230, 25);
		keystorePassword.setToolTipText("证书的密码与下面的密码有区别");
		contentPanel.add(keystorePassword);
		
		JLabel termLable = new JLabel("期限(年):");
		termLable.setBounds(30, 70, 60, 25);
		contentPanel.add(termLable);
		
		termText = new JTextField();
		termText.setBounds(100, 70, 230, 25);
		termText.setToolTipText("签名证书的有效期限(单位:年)");
		contentPanel.add(termText);
		
		JLabel aliasLable = new JLabel("名称:");
		aliasLable.setBounds(30, 105, 60, 25);
		contentPanel.add(aliasLable);
		
		keyName = new JTextField();
		keyName.setBounds(100, 105, 230, 25);
		keyName.setToolTipText("签名用户的名称");
		contentPanel.add(keyName);
		
		JLabel passwordLable = new JLabel("密码:");
		passwordLable.setBounds(30, 140, 60, 25);
		contentPanel.add(passwordLable);
		
		keyPassword = new JTextField();
		keyPassword.setBounds(100, 140, 230, 25);
		keyPassword.setToolTipText("签名用户的密码");
		contentPanel.add(keyPassword);
		
		JLabel nameLable = new JLabel("名字:");
		nameLable.setBounds(30, 175, 60, 25);
		contentPanel.add(nameLable);
		
		nameText = new JTextField();
		nameText.setBounds(100, 175, 230, 25);
		nameText.setToolTipText("签名用户的名字(非真实制)");
		contentPanel.add(nameText);
		
		JLabel organizationLable = new JLabel("组织单位:");
		organizationLable.setBounds(30, 210, 60, 25);
		contentPanel.add(organizationLable);
		
		organizationText = new JTextField();
		organizationText.setBounds(100, 210, 230, 25);
		organizationText.setToolTipText("签名用户的组织单位");
		contentPanel.add(organizationText);
		
		JLabel cityLable = new JLabel("所在城市:");
		cityLable.setBounds(30, 245, 60, 25);
		contentPanel.add(cityLable);
		
		cityText = new JTextField();
		cityText.setBounds(100, 245, 230, 25);
		cityText.setToolTipText("签名用户所在城市");
		contentPanel.add(cityText);
		
		JLabel provinceLable = new JLabel("所在省份:");
		provinceLable.setBounds(30, 280, 60, 25);
		contentPanel.add(provinceLable);
		
		provinceText = new JTextField();
		provinceText.setBounds(100, 280, 230, 25);
		provinceText.setToolTipText("签名用户所在省份");
		contentPanel.add(provinceText);
		
		JLabel codeLable = new JLabel("国家代码:");
		codeLable.setBounds(30, 315, 60, 25);
		contentPanel.add(codeLable);
		
		codeText = new JTextField();
		codeText.setBounds(100, 315, 230, 25);
		codeText.setToolTipText("签名用户的国家代码");
		contentPanel.add(codeText);
		
		JLabel operationLabel = new JLabel("附加功能:");
		operationLabel.setBounds(30, 350, 60, 25);
		contentPanel.add(operationLabel);
		
		showInfo = new JLabel("人品: 0(快求人品吧!)");
		showInfo.setBounds(100, 350, 130, 25);
		showInfo.setForeground(Color.RED);
		contentPanel.add(showInfo);
		
		JButton demandButton = new JButton("求人品!");
		demandButton.setBounds(247, 350, 80, 25);
		demandButton.setActionCommand("DemandButton");
		demandButton.setToolTipText("选择保存签名文件的路径");
		demandButton.addActionListener(this);
		contentPanel.add(demandButton);
		
		return contentPanel;
	}
	
	private int randomCharacter() {
		
		int value = Tools.RANDOM.nextInt(10);
		
		if (Tools.RANDOM.nextInt(10) < 5) {
			return -value;
		}
		
		return value;
	}

	public boolean isCancel() {
		return cancel;
	}
	
	public SignerInfo getSignerInfo() {
		return signerInfo;
	}

	private SignerInfo verifySignerInfo() {
		
		String keystorePassword = this.keystorePassword.getText();
		if (isNull(keystorePassword)) {
			hint("签名证书的密码不能为NULL!");
			return null;
		}
		
		String term = this.termText.getText();
		if (isNull(term)) {
			hint("签名的期限不能为NULL!");
			return null;
		}
		
		try {
			Integer.parseInt(term);
		} catch (NumberFormatException e) {
			hint("签名的期限必须是一个有效的整数!");
			return null;
		}
		
		String signerName = this.keyName.getText();
		if (isNull(signerName)) {
			hint("签名的名称不能为NULL!");
			return null;
		}
		
		String signerPassword = this.keyPassword.getText();
		if (isNull(signerPassword)) {
			hint("签名的密码不能为NULL!");
			return null;
		}
		
		String name = this.nameText.getText();
		if (isNull(name)) {
			hint("签名信息中的名字不能为NULL!");
			return null;
		}
		
		String organization = this.organizationText.getText();
		if (isNull(organization)) {
			hint("签名信息中的单位(组织)不能为NULL!");
			return null;
		}
		
		String city = this.cityText.getText();
		if (isNull(city)) {
			hint("签名信息中的所在城市不能为NULL!");
			return null;
		}
		
		String province = this.provinceText.getText();
		if (isNull(province)) {
			hint("签名信息中的所在省份不能为NULL!");
			return null;
		}
		
		String code = this.codeText.getText();
		if (isNull(code)) {
			hint("签名信息中的所在国家编码不能为NULL!");
			return null;
		}
		
		SignerInfo signerInfo = new SignerInfo();
		signerInfo.setKeystorePassword(keystorePassword);
		signerInfo.setTerm(Integer.parseInt(term));
		signerInfo.setSignerName(signerName);
		signerInfo.setSignerPassword(signerPassword);
		signerInfo.setName(name);
		signerInfo.setOrganization(organization);
		signerInfo.setCity(city);
		signerInfo.setProvince(province);
		signerInfo.setCode(code);
		
		return signerInfo;
	}
	
	private boolean isNull(String strs) {
		
		if (strs == null
				|| strs.trim().length() <= 0) {
			return true;
		}
		return false;
	}
	
	private void hint(String hint) {
		
		if (hint == null) {
			hintLabel.setText("");
			return ;
		}
		hintLabel.setText(hint);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("OK".equals(action)) {
			SignerInfo signerInfo = verifySignerInfo();
			if (signerInfo != null) {
				this.cancel = false;
				this.signerInfo = signerInfo;
				this.dispose();
			}
		} else if ("Cancel".equals(action)) {
			this.dispose();
		} else if ("DemandButton".equals(action)) {
			int value = randomCharacter();
			if (value > 0) {
				showInfo.setText("人品: " + value + "(自动填充!)");
				hint("当前功能暂未实现!");
				return ;
			}
			showInfo.setText("人品: " + value + "(快求人品吧!)");
		}
	}
}
