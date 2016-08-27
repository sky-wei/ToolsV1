package com.wei.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.wei.bean.ConfigInfo;
import com.wei.bean.SignerInfo;
import com.wei.bean.SimpleApkInfo;
import com.wei.bean.UiProperty;
import com.wei.db.SignerDB;
import com.wei.gui.AppMenu;
import com.wei.gui.ContentPanel;
import com.wei.gui.GuiTools;
import com.wei.gui.SettingsPanel;
import com.wei.imp.AppImp;
import com.wei.tools.AlterPackageName;
import com.wei.tools.BuildSigner;
import com.wei.tools.CompileFile;
import com.wei.tools.DecompileFile;
import com.wei.tools.ExecRunnable;
import com.wei.tools.ExportSigner;
import com.wei.tools.ImportSigner;
import com.wei.tools.OutputAdapt;
import com.wei.tools.SignerFile;
import com.wei.tools.TextAreaOutput;
import com.wei.tools.Tools;
import com.wei.tools.VerifySigner;

public class AppMain extends JFrame implements AppImp {

	private static final long serialVersionUID = 1L;
	
	public static final String THEME = "./res/Tools.theme";
	public static final String VERSION = "1.2.2(Beta)";
	
	public static final int WIDTH = 650;
	public static final int HEIGHT = 600;
	
	public static final int SIMPLE_MODE = 0x0001;
	public static final int COMPLEX_MODE = 0x0002;
	
	private TextAreaOutput textAreaOutput;
	private int alterMode = COMPLEX_MODE;
	
	private AlterPackageName alterPackageName;
	private SignerDB signerDB;
	
	public AppMain() {
		
		setTitle("Tools" + VERSION + "  -  jingcai.wei");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		GuiTools.setLocationEx(this, WIDTH, HEIGHT);
		
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		setToolsGuiTheme(false);
		
		setJMenuBar(new AppMenu(this));
		
		textAreaOutput = new TextAreaOutput();
		
		ContentPanel contentPanel = new ContentPanel(this);
		contentPanel.setTextOutput(textAreaOutput);
		
		add(contentPanel, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
		
		init();
		localCheck();
	}
	
	private void init() {
		
		signerDB = new SignerDB();
	}
	
	private void localCheck() {
		
		String javaHome = System.getenv("JAVA_HOME");
		if (javaHome == null || javaHome.trim().length() <= 0) {
//			exitConfirmDialog(this, "当前没有配置JAVA_HOME环境变量!", "错误!", JOptionPane.CLOSED_OPTION);
//			return ;
			System.out.println(javaHome);
			exitConfirmDialog(this, "当前没有配置JAVA_HOME环境变量!", "错误!", JOptionPane.CANCEL_OPTION);
		}
		
		File javaBin = new File(javaHome + "/bin");
		
		File singerFile = new File(javaBin, "jarsigner.exe");
		if (!singerFile.exists()) {
//			exitConfirmDialog(this, "JAVA_HOME环境变量配置异常!", "错误!", JOptionPane.CLOSED_OPTION);
//			return ;
			System.out.println(singerFile);
			exitConfirmDialog(this, "JAVA_HOME环境变量配置异常!", "错误!", JOptionPane.CANCEL_OPTION);
		}
		
		File java = new File(javaBin, "java.exe");
		
		String[] cmd = {java.getPath(), "-version"};
		ExecRunnable execRunnable = new ExecRunnable(cmd, new File("C:/"), new OutputAdapt() {
			@Override
			public void outputInfoln(String info) {
				Tools.log.debug(info);
			}
		});
		new Thread(execRunnable).start();
		
		Tools.log.debug("ConfigInfo: \n" + Tools.getConfigInfo());
	}
	
	private void exitConfirmDialog(Component component, String message, String title, int optionType) {
		
		int reuslt = JOptionPane.showConfirmDialog(component, message, title, optionType);
		
		if (reuslt == 0) {
			System.exit(0);
		}
	}
	
	private void setToolsGuiTheme(boolean updateUI) {
		
		File themeFile = new File(THEME);
		
		if (!themeFile.exists()) {
			saveUiProperty(new UiProperty());
		}
		
		GuiTools.setGuiTheme(THEME);
		
		if (updateUI) {
			SwingUtilities.updateComponentTreeUI(this);
		}
	}
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				new AppMain();
			}
		});
	}
	
	@Override
	public JFrame getCurFrame() {
		return this;
	}

	@Override
	public SimpleApkInfo open(File file) {
		
		alterPackageName = new AlterPackageName(file);
		alterPackageName.setOutputImp(textAreaOutput);
		
		if (alterPackageName.decompile(this)) {
			
			return alterPackageName.getSimpleApkInfo();
		}
		
		return null;
	}

	@Override
	public void alterPackageName(SimpleApkInfo pastInfo, SimpleApkInfo alterInfo) {
		
		if (alterPackageName != null) {
			switch (alterMode) {
			case SIMPLE_MODE:
				alterPackageName.alterSimplePackageName(this, pastInfo, alterInfo);
				break;
			case COMPLEX_MODE:
				alterPackageName.alterPackageName(this, pastInfo, alterInfo);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void compileApk(SimpleApkInfo alterApkInfo) {
		
		if (alterPackageName != null) {
			alterPackageName.compileApk(this, alterApkInfo);
		}
	}

	@Override
	public void keystoreSettings() {
		ConfigInfo configInfo = Tools.getConfigInfo();
		SettingsPanel settingsPanel = new SettingsPanel(this, configInfo);
		settingsPanel.setVisible(true);
		Tools.saveConfigInfo(settingsPanel.getResultConfigInfo());		
	}

	@Override
	public void setAlterMode(int mode) {
		this.alterMode = mode;
	}

	@Override
	public void quit() {
		this.dispose();
	}
	
	@Override
	public UiProperty loadUiProperty() {
		
		InputStream is = null;
		
		try {
			Properties properties = new Properties();
			
			is = new FileInputStream(new File(THEME));
			properties.load(is);
			
			UiProperty uiProperty = new UiProperty();
			uiProperty.setProperties(properties);
			
			return uiProperty;
		} catch (FileNotFoundException e) {
			Tools.log.error(THEME + " 文件不存在了...", e);
		} catch (IOException e) {
			Tools.log.error("读取 " + THEME + " 出错了..." , e);
		} finally {
			try {
				if (is != null) is.close();
			} catch (Exception e) {
			}
		}
		
		return null;
	}

	@Override
	public boolean saveUiProperty(UiProperty uiProperty) {
		
		if (uiProperty == null) {
			return false;
		}
		
		OutputStream out = null;
		
		try {
			out = new FileOutputStream(THEME);
			uiProperty.getProperties().store(out, "Tools Theme - jingcai.wei");
			
			setToolsGuiTheme(true);
			
			return true;
		} catch (FileNotFoundException e) {
			Tools.log.error(THEME + " 文件不存在了...", e);
		} catch (IOException e) {
			Tools.log.error("保存 " + THEME + " 出错了..." , e);
		} finally {
			try {
				if (out != null) out.close();
			} catch (Exception e) {
			}
		}
		
		return false;
	}

	@Override
	public void signerFile(File file) {
		
		if (file == null) return ;
		
		SignerFile signerFile = new SignerFile(file, file.getParentFile());
		signerFile.setOutputImp(textAreaOutput);
		
		if (signerFile.signerFile(getCurFrame())) {
			Tools.outputInfoln(textAreaOutput, "Signer File Success...");
			return ;
		}
		
		Tools.outputInfoln(textAreaOutput, "Signer File Fail...");
	}

	@Override
	public void batchSignerFile(File fileDir) {
		
		if (fileDir == null) return ;
		
		File[] files = fileDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
		        if (f != null) {
		            if (f.isDirectory()) {
		                return false;
		            }
		            String fileName = f.getName();
		            int i = fileName.lastIndexOf('.');
		            if (i > 0 && i < fileName.length() - 1) {
		                String ex = fileName.substring(i+1).toLowerCase(Locale.ENGLISH);
		                if ("apk".equals(ex)) {
		                	return true;
		                }
		            }
		        }
		        return false;
			}
		});
		
		for (int i = 0; i < files.length; i++) {
			signerFile(files[i]);
		}
	}

	@Override
	public void verifySigner(File file) {
		
		if (file == null) return ;
		
		VerifySigner verifySigner = new VerifySigner(file);
		verifySigner.setOutputImp(textAreaOutput);
		verifySigner.verifySigner(getCurFrame());
	}

	@Override
	public File buildSignerFile(final SignerInfo signerInfo) {
		
		if (signerInfo == null)	return null;
		
		BuildSigner buildSigner = new BuildSigner(signerInfo, new File("res/keystore/"));
		buildSigner.setOutputImp(textAreaOutput);
		
		return buildSigner.buildSigner(getCurFrame());
	}
	
	@Override
	public boolean saveSignerInfo(SignerInfo signerInfo) {
		
		if (signerInfo == null)	return false;
		
		return signerDB.insertSignerInfo(signerInfo);
	}

	@Override
	public List<SignerInfo> getAllSignerInfo() {
		
		return signerDB.queryAllSignerInfo();
	}

	@Override
	public boolean deleteSignerInfo(SignerInfo signerInfo) {
		
		if (signerInfo == null)	return false;
		
		File signerFile = signerInfo.getFilePath();
		if (signerFile != null 
				&& signerFile.isFile()) {
			signerFile.delete();
		}
		
		return signerDB.deleteSignerInfo(signerInfo.getId());
	}

	@Override
	public void setDefaultSigner(SignerInfo signerInfo) {
		
		if (signerInfo == null) return ;
		
		ConfigInfo configInfo = new ConfigInfo();
		configInfo.setKeystorePassword(signerInfo.getKeystorePassword());
		configInfo.setName(signerInfo.getSignerName());
		configInfo.setPassword(signerInfo.getSignerPassword());
		configInfo.setKeystoreFile(signerInfo.getFilePath().getAbsoluteFile());
		
		Tools.saveConfigInfo(configInfo);
	}

	@Override
	public boolean exportSigner(File exportDir, SignerInfo signerInfo) {
		
		if (signerInfo == null)	return false;
		
		ExportSigner exportSigner = new ExportSigner(exportDir, signerInfo);
		exportSigner.setOutputImp(textAreaOutput);
		
		return exportSigner.exportSigner();
	}

	@Override
	public boolean importSigner(File importFile) {
		
		if (importFile == null)	return false;
		
		ImportSigner importSigner = new ImportSigner(signerDB, importFile, new File("res/keystore/"));
		importSigner.setOutputImp(textAreaOutput);
		
		return importSigner.importSigner();
	}

	@Override
	public boolean decompileFile(File decompileFile) {
		
		if (decompileFile == null)	return false;
		
		DecompileFile decompileFile2 = new DecompileFile(decompileFile);
		decompileFile2.setOutputImp(textAreaOutput);
		
		return decompileFile2.decompile(getCurFrame());
	}

	@Override
	public void compileFile(File compileFile) {
		
		if (compileFile == null)	return ;
		
		CompileFile compileFile2 = new CompileFile(compileFile);
		compileFile2.setOutputImp(textAreaOutput);
		compileFile2.compileApk(getCurFrame());
	}
}
