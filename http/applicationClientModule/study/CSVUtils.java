package study;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

//生成csv文件
public class CSVUtils {
	public static File createCSVFile(List<String> exportData, LinkedHashMap<String, String> map,
			String outPutPath, String fileName) {
		File csvFile = null;
		BufferedWriter csvFileOutputStream = null;
		try {
			File file = new File(outPutPath);
			if (!file.exists()) {
				file.mkdir();
			}
			
			csvFile = new File(outPutPath+fileName+".csv");
//			csvFile = File.createTempFile(fileName, ".csv",
//					new File(outPutPath));
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-8"), 1024);
			// 列的名称
			for (Iterator<java.util.Map.Entry<String, String>> propertyIterator = map.entrySet().iterator(); propertyIterator
					.hasNext();) {
				java.util.Map.Entry<String, String> propertyEntry = (java.util.Map.Entry<String, String>) propertyIterator
						.next();
				csvFileOutputStream.write((String) propertyEntry.getValue() != null ? new String(
								((String) propertyEntry.getValue())
										.getBytes("UTF-8"), "UTF-8") : "");
				if (propertyIterator.hasNext()) {
					csvFileOutputStream.write(",");
				}
			}
			csvFileOutputStream.write("\r\n");
			int i = 1;
			for(String str : exportData){
					
				if(str == null){
					str = " ";
				}
				
				csvFileOutputStream.write(str);
				if(i%map.keySet().size()  == 0){
					i = 1;
					csvFileOutputStream.write("\r\n");
				}else{
					i++;
					csvFileOutputStream.write(",");
				}
			}
			csvFileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			String errorContent = "获取 "+fileName+" 异常";
			writeErrorLog(errorContent);
			writeErrorLog(stackTraceToString(e));
			
			System.out.println("------------------------出错了！！！！！！！！------------------------");
		} finally {
			try {
				if(csvFileOutputStream != null)
					csvFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("------------------------出错了！！！！！！！！------------------------");
			}
		}
		System.out.println("------------------------完成------------------------");
		return csvFile;
	}
	
	public static void main(String[] args){
		System.out.println("test");
		List<String> list = getListWithFile("C:\\Users\\Administrator\\Desktop\\test.csv");
		
		for(String str : list){
			System.out.println(str);
		}
		
	}
	
	public static List<String> getListWithFile(String fileName){
		FileInputStream fis = null;
		List<String> list = new ArrayList<String>();
		try{
			File file = new File(fileName);
			fis = new FileInputStream(file);
			
			byte b[]=new byte[(int)file.length()];     //创建合适文件大小的数组   
	        fis.read(b);    //读取文件中的内容到b[]数组   
	        fis.close();   
			
	        String tempStr = new String(b, "UTF-8");
	        
	        String[] tempStrArr = tempStr.split("\r\n");
	        
	        for(int i=0; i<tempStrArr.length; i++){
	        	if(i != 0)
	        		tempStrArr[i] = tempStrArr[i].substring(0, tempStrArr[i].length()-1);
	        	list.add(tempStrArr[i]);
	        }
	        list.remove(0);
		}catch(Exception ex){
			ex.printStackTrace();
			String errorContent = "获取 "+fileName+" 异常";
			writeErrorLog(errorContent);
			writeErrorLog(stackTraceToString(ex));
		}
		
		return list;
	}
	

	public static void writeErrorLog(String errorContent){
		writeLog(errorContent, "error");
	}
	
	public static void writeInfoLog(String errorContent){
		writeLog(errorContent, "info");
	}
	
	public static void writeLog(String errorContent, String logInfo){
		File file = new File("C:\\Users\\Administrator\\Desktop\\机场爬出数据\\"+logInfo+".log");
		FileOutputStream fos = null;
		try {
			
			if(logInfo.equals("info")){
				fos = new FileOutputStream(file);	
			}else if(logInfo.equals("error"))
				fos = new FileOutputStream(file,true);
			
			
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			
			String errorContentTemp = currentTime.toString()+" "+logInfo+" "+errorContent+"\r\n";
			
			fos.write(errorContentTemp.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos != null){
				try{
					fos.close();	
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
	}
	
	public static String stackTraceToString(Exception e){
		StringWriter sw = new StringWriter();    
		PrintWriter pw = new PrintWriter(sw);    
		e.printStackTrace(pw);    
		return sw.toString();
	}
	
	 /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
