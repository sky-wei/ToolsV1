package com.wei.tools;

import java.io.File;
import java.util.Locale;

public class FileNameExtensionFilter {
	
	private final String[] extensions;
	private final String[] lowerCaseExtensions;
	
	public FileNameExtensionFilter(String... extensions) {
		
		this.extensions = new String[extensions.length];
		this.lowerCaseExtensions = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i] == null || extensions[i].length() == 0) {
                throw new IllegalArgumentException(
                    "Each extension must be non-null and not empty");
            }
            this.extensions[i] = extensions[i];
            lowerCaseExtensions[i] = extensions[i].toLowerCase(Locale.ENGLISH);
        }
	}
	
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return false;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if (i > 0 && i < fileName.length() - 1) {
                String desiredExtension = fileName.substring(i+1).toLowerCase(Locale.ENGLISH);
                for (String extension : lowerCaseExtensions) {
                    if (desiredExtension.equals(extension)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
