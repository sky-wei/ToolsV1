package com.wei.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.wei.bean.SignerInfo;
import com.wei.imp.AppImp;
import com.wei.tools.Tools;

public class SignerManage extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6118432284942632713L;
	
	private static final int WIDTH = 650;
	private static final int HEIGHT = 500;
	
	private SignerTableModel signerTableModel;
	private JTable signerTable;
	
	private JButton createSigner;
	private JButton reviseSigner;
	private JButton setCurSigner;
	private JButton deleteSigner;
	private JButton importSigner;
	private JButton exportSigner;
	private JTextArea expandSignerInfo;
	
	private AppImp appImp;
	
	private List<SignerInfo> signerInfos;
	
	public SignerManage(AppImp appImp) {
		
		super(appImp.getCurFrame(), true);
		
		this.appImp = appImp;
		
		setTitle("签名证书管理");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Tools.getImage(new File("./res/icon.png")));
		
		initPanel();
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		GuiTools.revisionSize(this, WIDTH, HEIGHT, dimension.width, dimension.height, true);
		
		pack();
		
		setContent(appImp.getAllSignerInfo());
	}
	
	private void initPanel() {
		
		JPanel contentPanel = new JPanel(new BorderLayout(5, 0));
		contentPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "签名证书管理"));
		
		contentPanel.add(newShowInfoPanel(), BorderLayout.CENTER);
		contentPanel.add(newFunPanel(), BorderLayout.EAST);
		
		this.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		this.add(contentPanel, BorderLayout.CENTER);
	}
	
	private JPanel newShowInfoPanel() {
		
		JPanel infoPanel = new JPanel(new BorderLayout(0, 5));
		
		signerTableModel = new SignerTableModel();
		signerTableModel.addColumn("ID");
		signerTableModel.addColumn("证书密码");
		signerTableModel.addColumn("名称");
		signerTableModel.addColumn("密码");
		signerTableModel.addColumn("路径");
		signerTableModel.addColumn("创建时间");
		signerTableModel.addColumn("其他信息");
		
		signerTable = new JTable(signerTableModel);
		signerTable.addMouseListener(new SignerMouseListener());
		signerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		signerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane jScrollPane = new JScrollPane(signerTable);
		
		infoPanel.add(jScrollPane, BorderLayout.CENTER);
		
		expandSignerInfo = new JTextArea("\n\n\n\n\n\n");
		expandSignerInfo.setEditable(false);
		expandSignerInfo.setBackground(infoPanel.getBackground());
		JScrollPane expandScrollPane = new JScrollPane(expandSignerInfo);
		
		infoPanel.add(expandScrollPane, BorderLayout.SOUTH);
		
		return infoPanel;
	}
	
	private JPanel newFunPanel() {
		
		JPanel funPanel = new JPanel();
		funPanel.setLayout(new BoxLayout(funPanel, BoxLayout.Y_AXIS));
		
		funPanel.add(Box.createVerticalStrut(25));
		
		createSigner = new JButton("创建签名");
		createSigner.setPreferredSize(new Dimension(80, 25));
		createSigner.addActionListener(this);
		createSigner.setActionCommand("CreateSigner");
		createSigner.setToolTipText("创建新的签名文件");
		funPanel.add(createSigner);
		funPanel.add(Box.createVerticalStrut(20));
		
		reviseSigner = new JButton("修改签名");
		reviseSigner.setPreferredSize(new Dimension(80, 25));
		reviseSigner.addActionListener(this);
		reviseSigner.setActionCommand("ReviseSigner");
		reviseSigner.setToolTipText("修改选中的签名文件");
		reviseSigner.setEnabled(false);
		funPanel.add(reviseSigner);
		funPanel.add(Box.createVerticalStrut(5));
		
		deleteSigner = new JButton("删除签名");
		deleteSigner.setPreferredSize(new Dimension(80, 25));
		deleteSigner.addActionListener(this);
		deleteSigner.setActionCommand("DeleteSigner");
		deleteSigner.setToolTipText("删除选中的签名文件");
		deleteSigner.setEnabled(false);
		funPanel.add(deleteSigner);
		funPanel.add(Box.createVerticalStrut(20));
		
		setCurSigner = new JButton("设置默认");
		setCurSigner.setPreferredSize(new Dimension(80, 25));
		setCurSigner.addActionListener(this);
		setCurSigner.setActionCommand("SetCurSigner");
		setCurSigner.setToolTipText("设置选中的签名文件为当前默认的签名文件");
		setCurSigner.setEnabled(false);
		funPanel.add(setCurSigner);
		funPanel.add(Box.createVerticalStrut(5));
		
		importSigner = new JButton("导入签名");
		importSigner.setPreferredSize(new Dimension(80, 25));
		importSigner.addActionListener(this);
		importSigner.setActionCommand("ImportSigner");
		importSigner.setToolTipText("导入相应的签名文件到程序中");
		funPanel.add(importSigner);
		funPanel.add(Box.createVerticalStrut(5));
				
		
		exportSigner = new JButton("导出签名");
		exportSigner.setPreferredSize(new Dimension(80, 25));
		exportSigner.addActionListener(this);
		exportSigner.setActionCommand("ExportSigner");
		exportSigner.setToolTipText("导出选中的签名文件到相应目录下");
		exportSigner.setEnabled(false);
		funPanel.add(exportSigner);
		funPanel.add(Box.createVerticalStrut(30));
		
		JButton exitSigner = new JButton("嗖的退出");
		exitSigner.setPreferredSize(new Dimension(80, 25));
		exitSigner.addActionListener(this);
		exitSigner.setActionCommand("ExitSigner");
		exitSigner.setToolTipText("退出当前签名管理");
		funPanel.add(exitSigner);
		
		return funPanel;
	}
	
	class SignerTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			
			return false;
		}
	}
	
	private void setContent(List<SignerInfo> signerInfos) {
		
		if (signerInfos == null || signerInfos.isEmpty()) {
			this.signerInfos = null;
			return ;
		}
		
		cleanAllContent();
		
		this.signerInfos = signerInfos;
		
		for (int i = 0; i < signerInfos.size(); i++) {
			addRowContent(signerInfos.get(i));
		}
	}
	
	private void addRowContent(SignerInfo signerInfo) {
		
		if (signerInfo == null)	return ;
		
		String[] values = {Integer.toString(signerInfo.getId()), 
				signerInfo.getKeystorePassword(), signerInfo.getSignerName(),
				signerInfo.getSignerPassword(), signerInfo.getFilePath().getPath(),
				signerInfo.getCreateTime(), signerInfo.getOtherInfo()};
		
		signerTableModel.addRow(values);
	}
	
	private void cleanAllContent() {
		
		int rowCount = signerTableModel.getRowCount();
		
		for (int i = 0; i < rowCount; i++) {
			signerTableModel.removeRow(0);
		}
	}
	
	private void setButtonEnabled(boolean enabled) {
		
		// reviseSigner.setEnabled(enabled);
		deleteSigner.setEnabled(enabled);
		
		setCurSigner.setEnabled(enabled);
		exportSigner.setEnabled(enabled);
	}
	
	private void checkSignerTable(int nextSelect) {
		
		int selectRow = signerTable.getSelectedRow();
		
		if (selectRow != -1
				&& signerTable.isRowSelected(selectRow)) {
			setExpandSignerInfo(selectRow);
			setButtonEnabled(true);
			return ;
		}
		
		if (nextSelect >= 0 
				&& nextSelect < signerTable.getRowCount()) {
			if (!signerTable.isRowSelected(nextSelect)) {
				signerTable.setRowSelectionInterval(nextSelect, nextSelect);
			}
			setExpandSignerInfo(nextSelect);
			setButtonEnabled(true);
			return ;
		}
		
		setExpandSignerInfo(-1);
		setButtonEnabled(false);
	}
	
	private void setExpandSignerInfo(int index) {
		
		if (index < 0 || signerInfos == null 
				|| signerInfos.isEmpty()
				|| index >= signerInfos.size()) {
			expandSignerInfo.setText("");
			return ;
		}
		
		SignerInfo signerInfo = signerInfos.get(index);
		
		StringBuilder info = new StringBuilder();
		info.append("Storepass : " + signerInfo.getKeystorePassword() + "\n");
		info.append("Validity : " + signerInfo.getTerm() + "\n");
		info.append("Alias : " + signerInfo.getSignerName() + "\n");
		info.append("Keypass : " + signerInfo.getSignerPassword() + "\n");
		info.append("CN : " + signerInfo.getName());
		info.append(", OU : " + signerInfo.getOrganization());
		info.append(", O : " + signerInfo.getOrganization());
		info.append(", L : " + signerInfo.getCity());
		info.append(", SS : " + signerInfo.getProvince());
		info.append(", C : " + signerInfo.getCode() + "\n");
		info.append("CreateTime : " + signerInfo.getCreateTime() + "\n");
		info.append("FilePath : " + signerInfo.getFilePath().getPath());
		
		expandSignerInfo.setText(info.toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String action = e.getActionCommand();
		
		if ("ExitSigner".equals(action)) {
			this.dispose();
		} else if ("CreateSigner".equals(action)) {
			CreateSignerPanel createSignerPanel = new CreateSignerPanel(appImp);
			createSignerPanel.setVisible(true);
			
			if (!createSignerPanel.isCancel()) {
				SignerInfo signerInfo = createSignerPanel.getSignerInfo();
				File savePath = appImp.buildSignerFile(signerInfo);
				
				if (savePath == null || !savePath.isFile()) {
					JOptionPane.showConfirmDialog(this, "生成签名文件失败!详细可以查看程序的日志信息.", "错误!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				signerInfo.setFilePath(savePath);
				signerInfo.setCreateTime(Tools.DATA_FORMAT.format(new Date()));
				
				if (!appImp.saveSignerInfo(signerInfo)) {
					JOptionPane.showConfirmDialog(this, "保存签名信息失败!详细可以查看程序的日志信息.", "错误!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				setContent(appImp.getAllSignerInfo());
			}
		} else if ("ReviseSigner".equals(action)) {
			
		} else if ("DeleteSigner".equals(action)) {
			int select = signerTable.getSelectedRow();
			if (select != -1) {
				SignerInfo signerInfo = signerInfos.get(select);
				if (!appImp.deleteSignerInfo(signerInfo)) {
					JOptionPane.showConfirmDialog(this, "删除签名信息失败!详细可以查看程序的日志信息.", "错误!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return ;
				}
				signerInfos.remove(select);
				signerTableModel.removeRow(select);
				checkSignerTable(select);
			}
		} else if ("SetCurSigner".equals(action)) {
			int select = signerTable.getSelectedRow();
			if (select != -1) {
				SignerInfo signerInfo = signerInfos.get(select);
				appImp.setDefaultSigner(signerInfo);
				JOptionPane.showConfirmDialog(this, "以成功设置为程序默认签名文件!", "提示!", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
			
		} else if ("ImportSigner".equals(action)) {
			File importFile = selectImportFile(null);
			if (importFile != null) {
				if (!appImp.importSigner(importFile)) {
					JOptionPane.showConfirmDialog(this, "导入签名信息失败!详细可以查看程序的日志信息.", "错误!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				setContent(appImp.getAllSignerInfo());
				JOptionPane.showConfirmDialog(this, "以成功导入签名文件!", "提示!", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
			}
		} else if ("ExportSigner".equals(action)) {
			File exportDir = selectExportDir(null);
			if (exportDir != null) {
				int select = signerTable.getSelectedRow();
				if (select != -1) {
					SignerInfo signerInfo = signerInfos.get(select);
					if (!appImp.exportSigner(exportDir, signerInfo)) {
						JOptionPane.showConfirmDialog(this, "导出签名信息失败!详细可以查看程序的日志信息.", "错误!", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
						return ;
					}
					JOptionPane.showConfirmDialog(this, "以成功导出签名文件!", "提示!", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		
		// 检测下...
		checkSignerTable(-1);
	}
	
	private File selectExportDir(String defaultDir) {
		
		JFileChooser jFileChooser = new JFileChooser(defaultDir);
		jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		
		int returnVal = jFileChooser.showOpenDialog(appImp.getCurFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			File exportDir = jFileChooser.getSelectedFile();
			if (exportDir != null && exportDir.isDirectory()) {
				return exportDir;
			}
		}
		
		return null;
	}
	
	private File selectImportFile(String defaultDir) {
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("选择签名文件(*.keystore)", "keystore");
		
		JFileChooser jFileChooser = new JFileChooser(defaultDir);
		jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jFileChooser.setMultiSelectionEnabled(false);
		jFileChooser.setFileFilter(filter);
		
		int returnVal = jFileChooser.showOpenDialog(appImp.getCurFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			File importFile = jFileChooser.getSelectedFile();
			if (importFile != null && importFile.isFile()) {
				return importFile;
			}
		}
		
		return null;
	}
	
	class SignerMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			
			if (e.getSource() == signerTable) {
				checkSignerTable(-1);
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			
			if (e.getSource() == signerTable) {
				checkSignerTable(-1);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			
			if (e.getSource() == signerTable) {
				checkSignerTable(-1);
			}
		}
	}
}
