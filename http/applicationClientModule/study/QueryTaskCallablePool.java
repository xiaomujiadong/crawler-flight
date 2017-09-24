package study;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class QueryTaskCallablePool {
	private ExecutorService fixedThreadPool;

	private String startDate;
	
	private String endDate;
	
	private String cookie;
	
	private String path;
	
	private LinkedHashMap<String, String> tableHeader;
	
	public QueryTaskCallablePool(int processThreadCount, String startDate, String endDate, 
			String cookie, String path, LinkedHashMap<String, String> tableHeader){
		 fixedThreadPool = Executors.newFixedThreadPool(processThreadCount);
		 this.startDate = startDate;
		 this.endDate = endDate;
		 this.cookie = cookie;;
		 this.path = path;
		 this.tableHeader = tableHeader;
	}
	
	public void process(){
		long startTime = System.currentTimeMillis();
		int startDateInt = Integer.valueOf(startDate);
		int endDateInt = Integer.valueOf(endDate);
		System.out.println("线程池开始执行");
		Map<String,FutureTask<Integer>> taskMap = new HashMap<String, FutureTask<Integer>>();
		if(endDateInt < startDateInt){
			System.out.println("查询的开始时间不能小宇结束时间");
		}else{
			for(int i = startDateInt; i <= endDateInt; i++){
	           FutureTask<Integer> ft = new FutureTask<Integer>(new QueryCallableTask(cookie, i+"", path, tableHeader));  
	            taskMap.put(i+"", ft);
	            // 提交给线程池执行任务，也可以通过exec.invokeAll(taskList)一次性提交所有任务;  
	            fixedThreadPool.submit(ft); 
			}
		}
		
		Integer totalResult = 0;  
        for (String key : taskMap.keySet()) {  
        	FutureTask<Integer> ft = taskMap.get(key);
            try {  
                //FutureTask的get方法会自动阻塞,直到获取计算结果为止  
                totalResult = totalResult + ft.get();  
            } catch (Exception e) {  
            	String errorContent = "查询  "+key+" 获取线程返回值出现异常";
            	CSVUtils.writeErrorLog(errorContent);
            	CSVUtils.writeErrorLog(CSVUtils.stackTraceToString(e));
            }
        }
        fixedThreadPool.shutdown();
        
        mergeFile(startDateInt, endDateInt, path);
        
        System.out.println("查询时间： "+(System.currentTimeMillis()-startTime)+" ms");
	}
	
	
	public static void mergeFile(int startDateInt, int endDateInt, String path){
		System.out.println("开始合并各个文件， 总共需要合并： "+(endDateInt-startDateInt)+" 文件");
		
		List<String> list = new ArrayList<String>();
        
        for(int i = startDateInt; i <= endDateInt; i++){
        	list.addAll(CSVUtils.getListWithFile(path+"/"+i+"-"+i+".csv"));
		}
        String fileName = startDateInt+"-"+endDateInt;
        
        CSVUtils.createCSVFile(list, getTableHeaderMerge(), path, fileName);
        
        for(int i = startDateInt; i <= endDateInt; i++){
        	String fileNameTemp = path+"/"+i+"-"+i+".csv";
        	CSVUtils.deleteFile(fileNameTemp);
		}
        
		System.out.println("合并完成");
	}
	
	public static void main(String[] args){
		
		System.out.println("testtt");
		List<String> list = new ArrayList<String>();
        String path = "c:/export/";
        for(int i = 20170805; i <= 20170807; i++){
        	list.addAll(CSVUtils.getListWithFile(path+"/"+i+"-"+i+".csv"));
        	
		}
        String fileName = "20170805"+"-"+"20170807";
        
        System.out.println("list size is "+list.size());
        
        CSVUtils.createCSVFile(list, getTableHeaderMerge(), path, fileName);
        
        
        for(int i = 20170805; i <= 20170807; i++){
        	String fileNameTemp = path+"/"+i+"-"+i+".csv";
        	CSVUtils.deleteFile(fileNameTemp);
        	
		}
	}
	
	public static LinkedHashMap<String, String> getTableHeaderMerge(){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<18; i++){
        	builder.append("第 "+(i+1)+"列").append(", ");
        }
		map.put("1", builder.toString());
		return map;
	}
}
