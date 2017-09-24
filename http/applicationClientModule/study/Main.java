package study;

import java.util.LinkedHashMap;

public class Main {
	
	public static void main(String[] args) throws Exception {
		String startDate = "20170801";
		String endDate = "20170804";
		
		String cookie = "JSESSIONID=EB844A8E9FF79DD19FCB1707C35CFBAB; _ga=GA1.2.1738086714.1506097668; _gid=GA1.2.1838967527.1506097668";

        String path = "c:/export/";
		
        System.out.println("开始执行");
        
		QueryTaskCallablePool threadPool = new QueryTaskCallablePool(30, startDate, endDate, cookie, 
				path, getTableHeader());
		
		threadPool.process();
	}
	
	public static LinkedHashMap<String, String> getTableHeader(){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for(int i=0; i<18; i++){
        	map.put(i+"", "第 "+(i+1)+"列");
        }
		return map;
	}
}
