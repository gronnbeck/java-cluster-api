package test;

import system.ResultImpl;

public class TestResult extends ResultImpl{
	
	@Override
	public Object getTaskReturnValue() {
		return "Result";
	}

}

