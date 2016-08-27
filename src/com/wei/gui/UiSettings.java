package com.wei.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.ColorUIResource;

import com.wei.bean.UiProperty;
import com.wei.imp.AppImp;
import com.wei.tools.Tools;

public class UiSettings extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6955393994400276381L;
	
	private static final int WIDTH = 320;
	private static final int HEIGHT = 230;
	
	private AppImp appImp;
	private boolean cancel;
	
	private JPanel selection;
	private JPanel background;
	private JPanel fontColor;
	private JLabel fontLabel;
	
	private UiProperty modifyUiProperty;
	
	public UiSettings(AppImp appImp, UiProperty uiProperty) {
		
		super(appImp.getCurFrame(), true);
		
		this.appImp = appImp;
		this.modifyUiProperty = new UiProperty(uiProperty);
		this.cancel = true;
		
		setTitle("界面设置");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, false);
		
		setContentPanel(modifyUiProperty);
		
		pack();
	}
	
	private void initPanel() {
		
		setLayout(null);
		
		add(newContentPanel());
		
		JButton okButton = new JButton("确定");
		okButton.setActionCommand("OK");
		okButton.setToolTipText("保存当前的设置");
		okButton.setBounds(WIDTH - 160, HEIGHT - 59, 65, 25);
		okButton.addActionListener(this);
		add(okButton);
		
		JButton cancelButton = new JButton("取消");
		cancelButton.setActionCommand("Cancel");
		cancelButton.setToolTipText("取消当前的设置");
		cancelButton.setBounds(WIDTH - 83, HEIGHT - 59, 65, 25);
		cancelButton.addActionListener(this);
		add(cancelButton);
	}
	
	private JPanel newContentPanel() {
		
		JPanel contentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		contentPanel.setBounds(10, 10, WIDTH - 26, HEIGHT - 75);
		contentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "界面设置"));
		
		UiMouseListener uiMouseListener = new UiMouseListener();
		
		selection = newPanel(uiMouseListener);
		selection.setToolTipText("设置程序的焦点颜色");
		background = newPanel(uiMouseListener);
		background.setToolTipText("设置程序的背景颜色");
		fontColor = newPanel(uiMouseListener);
		fontColor.setToolTipText("设置程序的字体颜色");

		
		JPanel colorPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		colorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "颜色设置"));
		colorPanel.add(selection);
		colorPanel.add(background);
		colorPanel.add(fontColor);
		
	    JPanel fontPanel = new JPanel( new BorderLayout());
	    fontPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
	    		BorderFactory.createEtchedBorder(), "字体设置"),BorderFactory.createEmptyBorder( 5, 5, 5, 5)));
	    
	    fontLabel = new JLabel( "我只是打酱油的...");
	    
	    JButton fontButton = new JButton("字体");
	    fontButton.setToolTipText("设置程序字体");
	    fontButton.setActionCommand("Fonts");
	    fontButton.addActionListener(this);
	    
	    fontPanel.add( fontLabel, BorderLayout.CENTER);
	    fontPanel.add( fontButton, BorderLayout.EAST);
		
		contentPanel.add(colorPanel);
		contentPanel.add(fontPanel);
		
		return contentPanel;
	}
	
	private JPanel newPanel(MouseAdapter mouseAdapter) {
		
		JPanel panel = new JPanel();
		
		panel.setPreferredSize(new Dimension(40, 40));
		panel.addMouseListener(mouseAdapter);
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		return panel;
	}
	
	private Color getColor(String title, Color initialColor) {
		
		Color result = JColorChooser.showDialog(this, title, initialColor);
		
		if (result != null) {
			return result;
		}
		
		return initialColor;
	}
	
	private void setContentPanel(UiProperty uiProperty) {
		
		selection.setBackground(uiProperty.getPrimary3());
		background.setBackground(uiProperty.getSecondary3());
		fontColor.setBackground(uiProperty.getBlack());
		
		fontLabel.setFont(uiProperty.getFont());
	}

	public boolean isCancel() {
		return cancel;
	}
	
	public UiProperty getUiProperty() {
		return modifyUiProperty;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("OK".equals(action)) {
			cancel = false;
			this.dispose();
		} else if ("Cancel".equals(action)) {
			cancel = true;
			this.dispose();
		} else if ("Fonts".equals(action)) {
			FontDialog fontDialog = new FontDialog(appImp, modifyUiProperty.getFont());
			fontDialog.setVisible(true);
			
			if (!fontDialog.isCancel()) {
				modifyUiProperty.setFont(fontDialog.getSelectFont());
				setContentPanel(modifyUiProperty);
			}
		}
	}
	
	class UiMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			super.mouseClicked(mouseEvent);
			
			if (selection == mouseEvent.getSource()) {
				selection.setBackground(getColor("设置焦点颜色", selection.getBackground()));
				modifyUiProperty.setPrimary(selection.getBackground());
				setContentPanel(modifyUiProperty);
			} else if (background == mouseEvent.getSource()) {
				background.setBackground(getColor("设置背景颜色", background.getBackground()));
				modifyUiProperty.setSecondary(background.getBackground());
				setContentPanel(modifyUiProperty);
			} else if (fontColor == mouseEvent.getSource()) {
				fontColor.setBackground(getColor("设置字体颜色", fontColor.getBackground()));
				modifyUiProperty.setBlack(new ColorUIResource(fontColor.getBackground()));
				setContentPanel(modifyUiProperty);
			}
		}
	}
}
