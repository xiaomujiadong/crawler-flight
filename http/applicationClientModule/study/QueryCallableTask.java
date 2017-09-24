package study;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class QueryCallableTask implements Callable<Integer>{
	
	private String cookie;
	
	private String startDate;
	
	private String endDate;
	
	private String path;
	
	private LinkedHashMap<String, String> tableHeader;
	
	public QueryCallableTask(String cookie, String date,
			String path, LinkedHashMap<String, String> tableHeader){
		this.cookie = cookie;
		this.startDate = date;
		this.endDate = date;
		this.path = path;
		this.tableHeader = tableHeader;
	}
	
	@Override
	public Integer call() {
		List<String> list = new ArrayList<String>();
		
		try{
			HttpClientUtil.http(startDate, endDate, list, cookie);	
		}catch(Exception ex){
			String errorContent = "查询出错了，开始日期："+startDate+", 结束日期："+endDate;
			CSVUtils.writeErrorLog(errorContent);
			CSVUtils.writeErrorLog(CSVUtils.stackTraceToString(ex));
			try{
				HttpClientUtil.http(startDate, endDate, list, cookie);
			}catch (Exception e) {
				String errorContentAgain = "查询又出错了，开始日期："+startDate+", 结束日期："+endDate;
				CSVUtils.writeErrorLog(errorContentAgain);
				CSVUtils.writeErrorLog(CSVUtils.stackTraceToString(e));
			}
		}
        String fileName = startDate+"-"+endDate;
		
		CSVUtils.createCSVFile(list, tableHeader, path, fileName);
		
		return 1;
	}
}
