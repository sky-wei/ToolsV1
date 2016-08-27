package com.wei.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wei.imp.AppImp;
import com.wei.tools.Tools;

public class FontDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 377;
	private static final int HEIGHT = 280;
	
	private Font curFont;
	private JList<String> fontNames;
	private JList<String> fontStyles;
	private JList<String> fontSizes;
	private JLabel showLabel;
	
	private boolean cancel;
	
	public FontDialog(AppImp appImp, Font initialFont) {
		
		super(appImp.getCurFrame(), true);
		
		cancel = true;
		
		setTitle("字体设置");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, false);
		
		setDefaultContent(initialFont);
		
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
		
		String[] styles = { "正常", "粗体", "斜体", "粗体+斜体"};
		String[] sizes = new String[100];
		for (int i = 0; i < 100; i++) {
			sizes[i] = "" + (i + 1);
		}
		
		JPanel contentPanel = new JPanel(null);
		contentPanel.setBounds(10, 10, WIDTH - 26, HEIGHT - 75);
		contentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "字体设置"));
		
		FontListListener fontListListener = new FontListListener();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		fontNames = new JList<String>(ge.getAvailableFontFamilyNames());
		fontNames.setVisibleRowCount(7);
		fontNames.addListSelectionListener(fontListListener);
		
		fontStyles = new JList<String>(styles);
		fontStyles.setVisibleRowCount(7);
		fontStyles.addListSelectionListener(fontListListener);
		
		fontSizes = new JList<String>(sizes);
		fontSizes.setVisibleRowCount(7);
		fontSizes.addListSelectionListener(fontListListener);
		
	    JLabel name = new JLabel( "名称");
	    name.setBounds(10, 25, 41, 16);

	    JLabel style = new JLabel( "样式");
	    style.setBounds(170, 25, 41, 16);

	    JLabel size = new JLabel( "大小");
	    size.setBounds(270, 25, 41, 16);
	    
	    showLabel = new JLabel( "AaBbCcDdEeFfGgHhIiJjKkLlMm...XxYyZz", SwingConstants.CENTER);
	    showLabel.setBounds(10, 160, 340, 49);
		
		JScrollPane fontNamesp = new JScrollPane(fontNames);
		fontNamesp.setBounds(10, 45, 150, 110);
		
		JScrollPane fontStylesp = new JScrollPane(fontStyles);
		fontStylesp.setBounds( 170, 45, 90, 110);
		
		JScrollPane fontSizesp = new JScrollPane(fontSizes);
		fontSizesp.setBounds( 270, 45, 70, 110);
		
		contentPanel.add(name);
		contentPanel.add(style);
		contentPanel.add(size);
		contentPanel.add(showLabel);
		contentPanel.add(fontNamesp);
		contentPanel.add(fontStylesp);
		contentPanel.add(fontSizesp);
		
		return contentPanel;
	}
	
	private void setDefaultContent(Font initialFont) {
		
		curFont = initialFont == null ? showLabel.getFont() : initialFont;
		
		String fontName = curFont.getName();
		int fontStyle = curFont.getStyle();
		int fontSize = curFont.getSize();
		
		fontNames.setSelectedValue(fontName, true);
		fontSizes.setSelectedValue("" + fontSize, true);
		
	    if (((fontStyle & Font.BOLD) != 0) && ((fontStyle & Font.ITALIC) != 0) ) {
	        fontStyles.setSelectedValue( "粗体+斜体", true);
	     } else if ( (fontStyle & Font.BOLD) != 0  ) {
	    	fontStyles.setSelectedValue( "粗体", true);
	     } else if ( (fontStyle & Font.ITALIC) != 0 ) {
	    	fontStyles.setSelectedValue( "斜体", true);
	     } else {
	    	fontStyles.setSelectedValue( "正常", true);
	     }
	}
	
	private void refresh() {
		showLabel.setFont(curFont);
	}
	
	public Font getSelectFont() {
		return curFont;
	}
	
	public boolean isCancel() {
		return cancel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("OK".equals(action)) {
			cancel = false;
		} else if ("Cancel".equals(action)) {
			cancel = true;
		}
		
		this.dispose();
	}
	
	class FontListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			String fontName = curFont.getName();
			int fontStyle = curFont.getStyle();
			int fontSize = curFont.getSize();
			
			String selectName = fontNames.getSelectedValue();
			if (selectName != null) {
				fontName = selectName;
			}
			
			String selectSize = fontSizes.getSelectedValue();
			if (selectSize != null) {
				fontSize = Integer.parseInt(selectSize);
			}
			
			String selectStyle = fontStyles.getSelectedValue();
			if (selectStyle != null) {
				fontStyle = 0;
				
				if (selectStyle.indexOf("正常") != -1) {
					fontStyle = Font.PLAIN;
				} else {
					if (selectStyle.indexOf("斜体") != -1) {
						fontStyle = Font.ITALIC;
					}
					if (selectStyle.indexOf("粗体") != -1) {
						fontStyle |= Font.BOLD;
					}
				}
			}
			
			curFont = new Font(fontName, fontStyle, fontSize);
			
			refresh();
		}
	}
}
