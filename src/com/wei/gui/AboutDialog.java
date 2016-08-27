package com.wei.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import com.wei.main.AppMain;
import com.wei.tools.Tools;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = -527176379813615392L;
	
	private static final int WIDTH = 400;
	private static final int HEIGHT = 300;
	
	public AboutDialog(Frame frame) {
		super(frame, true);
		
		setTitle("关于本程序");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, false);
		
		pack();
	}
	
	private void initPanel() {
		
		setLayout(null);
		
		Icon icon = new ImageIcon("./res/icon.png");
		JLabel lable = new JLabel(icon);
		lable.setBounds(30, 30, 128, 128);
		add(lable);
		
		lable = new JLabel("没有最好,只有更好...");
		lable.setBounds(200, 40, 150, 30);
		add(lable);
		
		lable = new JLabel("特别感谢贺华同学加强版apktool");
		lable.setBounds(200, 60, 200, 30);
		add(lable);
		
		lable = new JLabel("的大力支持... ");
		lable.setBounds(200, 80, 200, 30);
		add(lable);
		
		lable = new JLabel("'威'笑的人生更精彩...");
		lable.setBounds(200, 100, 100, 30);
		add(lable);
		
		lable = new JLabel("-- jingcai.wei");
		lable.setBounds(280, 120, 100, 30);
		add(lable);
		
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setBounds(30, 170, 340, 2);
		add(separator);
		
		lable = new JLabel("版本: " + AppMain.VERSION);
		lable.setBounds(30, 180, 200, 30);
		add(lable);
		
		lable = new JLabel("作者: jingcai.wei");
		lable.setBounds(30, 205, 200, 30);
		add(lable);
		
		lable = new JLabel("邮箱: jingcai.wei@163.com");
		lable.setBounds(30, 230, 200, 30);
		add(lable);
		
		JButton ok = new JButton("确定");
		ok.setBounds(305, 230, 65, 25);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AboutDialog.this.dispose();
			}
		});
		add(ok);
	}
}
