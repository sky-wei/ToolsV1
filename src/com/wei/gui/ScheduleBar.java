package com.wei.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.wei.tools.Tools;

public class ScheduleBar extends JDialog {

	private static final long serialVersionUID = -4399952239477755761L;
	
	public ScheduleBar(JFrame jFrame, String title, Runnable runnable) {
		
		super(jFrame, true);
		
		setTitle(title);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, 300, 50, dimension.width, dimension.height, false);
		
		this.pack();
		
		runTask(runnable);
	}
	
	private void initPanel() {
		
		setLayout(new BorderLayout());
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		add(progressBar, BorderLayout.CENTER);
	}
	
	private void runTask(Runnable runnable) {
		
		new MonitorTask(runnable).start();
	}
	
	class MonitorTask extends Thread {
		
		private Runnable runnable;
		
		public MonitorTask(Runnable runnable) {
			
			this.runnable = runnable;
		}

		@Override
		public void run() {
			super.run();
			
			try {
				Thread.sleep(1000);
				
				Thread task = new Thread(runnable);
				task.start();
				
				while (task.isAlive()) {
					
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				Tools.log.error("InterruptedException!", e);
			}
			
			ScheduleBar.this.dispose();
		}
	}
}
