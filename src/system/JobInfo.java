package system;

import java.util.ArrayList;
import java.util.HashMap;

public class JobInfo extends ArrayList<HashMap<String, Object>> {
	
	private static final long serialVersionUID = 3611922217025448285L;

	public String toString() {
		String info = "";
		int tasksExecuted = 0;
		long totalExecutionTime = 0;
		
		for (HashMap<String, Object> jobInfoHash : this) {
			tasksExecuted += (Integer)jobInfoHash.get("tasksExecuted");
			totalExecutionTime += (Long)jobInfoHash.get("jobExecutionTime");
		}
		
		info += "Tasks executed: " + tasksExecuted + "\nJob execution time: " + (totalExecutionTime / 10E9) + " seconds";
		return info;
	}
}
