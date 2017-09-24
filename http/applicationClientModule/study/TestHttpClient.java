package study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;

public class TestHttpClient {
    public static void main(String[] args){
        try {
            JSONObject json = new JSONObject();
            json.put("Cookie", "JSESSIONID=45D7B02F9388C281DD2973F9682D5398; __lnkrntdmcvrd=-1; _ga=GA1.2.1521788423.1505877411; _gid=GA1.2.782346574.1505977729; _gat=1");
            json.put("Content-Type", "application/x-www-form-urlencoded");

            String responseStr = HttpClientUtil.getStringDoPost("https://www.flightontime.cn/deviceAction.do?method=list&togo=1&divDisplay=none", "listType=&flightNo=&DepAP3=&ArrAP3=&currentpage=1&startDate=20170920&endDate=20170920&inputstatus=0&row=&openDiv=2&togo=", HttpClientUtil.JSON_NO, json, "UTF-8");
            
            Document doc = Jsoup.parse(responseStr);
            
            List<Element> list = doc.getElementsByTag("tr");
            
           	List<String> list4 = new ArrayList<String>();
           	for(int i=0; i<list.size(); i++){
           		Element e = list.get(i);
           		
            	Elements eles = e.getElementsByTag("input");
            	
            	for(Element inputElement : eles){
            		String inputStr = inputElement.toString();
            		
            		if(inputStr.contains("readonly")){
            			System.out.println("value: "+inputElement.val());
            			list4.add(inputElement.val());
            		}
            	}
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
