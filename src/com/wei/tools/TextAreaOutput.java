package com.wei.tools;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class TextAreaOutput extends OutputAdapt {
	
	public JTextArea textArea;
	
	public TextAreaOutput() {};
	
	public TextAreaOutput(JTextArea textArea) {
		
		this.textArea = textArea;
	}
	
	public void setTextOutput(JTextArea textArea) {
		
		this.textArea = textArea;
	}

	@Override
	public void outputInfoln(String info) {
		
		append(info + "\n");
		Tools.log.debug(info);
	}

	@Override
	public void outputInfoln(String info, Throwable throwable) {
		
		append(info + " - " + throwable.getMessage() + "\n");
		Tools.log.error(info, throwable);
	}
	
	public void append(String info) {
		
		if (textArea == null) {
			System.out.print(info);
			return ;
		}
		
		textArea.append(info);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		
		int lineCount = textArea.getLineCount();
		if (lineCount > 1000) {
			try {
				int startOff = textArea.getLineStartOffset(300);
				int endOff = textArea.getLineEndOffset(lineCount - 1);
				int length = endOff - startOff;
				if (length > 0) {
					String newText = textArea.getText(startOff, length);
					textArea.setText(newText);
					textArea.append("Clear 0-300 Line Data!!!\n");
				}
			} catch (BadLocationException e) {
				textArea.setText("BadLocationException: " + e.getMessage());
				Tools.log.error("BadLocationException!", e);
			}
		}
	}
}
