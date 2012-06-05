package test;

import system.ResultImpl;

public class TestResult extends ResultImpl<String>{
	
	private static final long serialVersionUID = -311508782930161479L;

	@Override
	public String getTaskReturnValue() {
		return "Result";
	}

}

