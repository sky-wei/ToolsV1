package com.wei.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import com.wei.tools.Tools;

public class GuiTools {

	public static void revisionSize(Dialog dialog,int width,int height,int screenWidth,int screenHeight,boolean resizable) {
		
		if (dialog == null) {
			return ;
		}
		
		/* 设置提示框的大小,和设置显示位置 */
		dialog.setLocation((screenWidth - width) >> 1, (screenHeight - height) >> 1);
		dialog.setPreferredSize(new Dimension(width, height));
		dialog.setResizable(resizable);	
	}
	
	public static void revisionSize(JFrame jFrame,int width,int height,int screenWidth,int screenHeight,boolean resizable) {
		
		if (jFrame == null) {
			return ;
		}
		
		if (width > screenWidth) width = screenWidth;
		if (height > screenHeight) height = screenHeight;
		
		jFrame.setLocation((screenWidth - width) >> 1, (screenHeight - height) >> 1);
		jFrame.setPreferredSize(new Dimension(width, height));
		jFrame.setResizable(resizable);
	}
	
	/**
	 * 设置控件在桌面显示的位置
	 * 该方法默认全用桌面居中显示
	 * @param window 控件显示窗体
	 * @param width 窗体的宽度
	 * @param height 窗体的高度
	 */
	public static void setLocationEx(Window window, int width, int height) {
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((dimension.width - width) >> 1, (dimension.height - height) >> 1);
	}
	
	/**
	 * 设置当前程序的界面主题
	 * @param theme 主题文件
	 */
	public static void setGuiTheme(String theme) {
		
		try {
			NimRODLookAndFeel nimbusLookAndFeel = new NimRODLookAndFeel();
			NimRODTheme nimRODTheme = new NimRODTheme(theme);
			
			NimRODLookAndFeel.setCurrentTheme(nimRODTheme);
			UIManager.setLookAndFeel(nimbusLookAndFeel);
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Tools.log.error("Set Theme Exception!", e);
		} 
	}
}
