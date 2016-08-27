package com.wei.tools;

import com.wei.imp.OutputImp;

public class OutputAdapt implements OutputImp {

	@Override
	public void outputInfoln(String info) {
		System.out.println(info);
	}

	@Override
	public void outputInfoln(String info, Throwable throwable) {
		System.out.println(info);
		throwable.printStackTrace();
	}
}
