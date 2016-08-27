package com.wei.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.wei.imp.OutputImp;

public class ExecRunnable implements Runnable {

	private String[] cmdarray;
	private String[] envp;
	private File workDir;
	private OutputImp output;
	
	public ExecRunnable(String[] cmdarray, File workDir, OutputImp output) {
		
		this.cmdarray = cmdarray;
		this.workDir = workDir;
		this.output = output;
	}
	
	public ExecRunnable(String[] cmdarray,String[] envp, File workDir, OutputImp output) {
		
		this(cmdarray, workDir, output);
		this.envp = envp;
	}
	
	public void run() {
		
		try {
			exec();
		} catch (BrutException e) {
			output.outputInfoln("BrutException!", e);
		}
	}
	
	private void exec() throws BrutException {
		
        try {
        	Process ps = Runtime.getRuntime().exec(cmdarray, envp, workDir);

            new StreamForwarder(ps.getInputStream(), output).start();
            new StreamForwarder(ps.getErrorStream(), output).start();
            if (ps.waitFor() != 0) {
                throw new BrutException(
                    "could not exec command: " + Arrays.toString(cmdarray));
            }
        } catch (IOException ex) {
            throw new BrutException(
                "could not exec command: " + Arrays.toString(cmdarray), ex);
        } catch (InterruptedException ex) {
            throw new BrutException(
                "could not exec command: " + Arrays.toString(cmdarray), ex);
        }
    }
	
	private class StreamForwarder extends Thread {

        private final InputStream mIn;
        private final OutputImp mOut;
		
        public StreamForwarder(InputStream in, OutputImp out) {
            mIn = in;
            mOut = out;
        }
        
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(mIn));
                String line;
                while ((line = in.readLine()) != null) {
                	mOut.outputInfoln(line);
                }
            } catch (IOException ex) {
                Tools.log.error("StreamForwarder Exception!", ex);
            }
        }
    }
	
	private class BrutException extends Exception {

		private static final long serialVersionUID = 4256669169296619920L;

		public BrutException(String msg) {
			
			super(msg);
		}
		
		public BrutException(String msg, Throwable t) {
			
			super(msg, t);
		}
	}
}
