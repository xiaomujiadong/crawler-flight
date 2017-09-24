package study;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by wdcao on 2017/8/16.
 */
public class HttpClientUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final boolean JSON_YES = true;
    public static final boolean JSON_NO = false;

    public static final int DEFAULT_TIME_OUT = 60;

    public static String getStringDoPost(String url, String param, boolean isJson, JSONObject headerJSON, String charset) throws IOException {
        return processResponse(doPost(url, param, isJson, headerJSON, charset), charset);
    }

    public static HttpResponse doPost(String url, String param, boolean isJson) throws IOException {
        StringEntity stringEntity = new StringEntity(param);
        if(isJson){
            stringEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        }
        return httpPost(url, stringEntity, null, "UTF-8");
    }

    public static HttpResponse doPost(String url, String param, boolean isJson, JSONObject headerJSON, String charset) throws IOException {
        StringEntity stringEntity = new StringEntity(param);
        if(isJson){
            stringEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON));
        }
        return httpPost(url, stringEntity, headerJSON, charset);
    }

    private static HttpResponse httpPost(String url, StringEntity param, JSONObject headerJSON, String charset) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        
        RequestConfig requestConfig = RequestConfig.custom()    
                .setConnectTimeout(60 * 1000).setConnectionRequestTimeout(60 * 1000)    
                .setSocketTimeout(60 * 1000).build();    
        httpPost.setConfig(requestConfig);
        
        setHeader(httpPost, headerJSON);
        httpPost.setEntity(param);

        return httpclient.execute(httpPost);
    }

    private static String processResponse(HttpResponse response, String charset) throws IOException {
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                return EntityUtils.toString(resEntity,charset);
            }
        }

        return null;
    }

    public static void setHeader(HttpRequestBase httpRequestBase, JSONObject headerJSON){
        if(headerJSON != null && !headerJSON.isEmpty()){
            for(String key : headerJSON.keySet()){
                String value = headerJSON.getString(key);

                if(StringUtils.isBlank(value))
                    continue;
                httpRequestBase.setHeader(key, value);
            }
        }else{
            httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.107 Safari/537.36");
            httpRequestBase.setHeader("Accept-Encoding", "gzip, deflate");
            httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
            httpRequestBase.setHeader("Cache-Control", "max-age=0");
        }
    }
    
    public static ParseHtml hangbanruru(String url, String params, JSONObject header) throws Exception{
    	ParseHtml parseHtml = new ParseHtml();
    	
        String responseStr = "";
		try {
			responseStr = HttpClientUtil.getStringDoPost(url, params, HttpClientUtil.JSON_NO, header, "UTF-8");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return parseHtml;
		}
    	
    	Document doc = Jsoup.parse(responseStr);
    	
    	parseHtml.setPage(getPage(doc));
    	
    	List<String> list = new ArrayList<String>();
    	parseHtml.setData(list);
    	
    	parseOtherTd(doc, list);

   		List<String> spanList = getHangBanHao(doc);
    	int index = 0;
    	
    	for(int j=0;j<spanList.size(); j++){
    		list.add(index, spanList.get(j));
    		index +=18; 
    	}
       	return parseHtml;
    }
    
    public static Page getPage(Document doc){
    	Page page = new Page();
    	Elements pageEles = doc.getElementsByTag("li");
    	
    	String pageStr = pageEles.get(0).html();
    	
    	String[] pageArr = pageStr.split("/"); 
        
    	pageArr[0] = pageArr[0].replace("第", "").replace("页", "");
    	pageArr[1] = pageArr[1].replace("共", "").replace("页", "");
   		
    	String totalRecordsStr = pageEles.get(1).html().replace("｜共", "").replace("条记录", "");
    	page.currentPage = Integer.valueOf(pageArr[0]);
		page.totalPage = Integer.valueOf(pageArr[1]);
		page.totalRecords = Integer.valueOf(totalRecordsStr);
   		return page;
    }
    
    //获取航班号信息
    public static List<String> getHangBanHao(Document doc){
    	List<String> list = new ArrayList<String>();
    	Elements eleSpan = doc.getElementsByTag("span");
    	
   		for(Element spanElement : eleSpan){
       		String spanStr = spanElement.toString();
       		if(spanStr.contains("宋体")){
       			list.add(spanElement.html());
       		}
   		}
   		
   		return list;
    }
    
    //获取出去航班号的其他列信息
    public static void parseOtherTd(Document doc, List<String> list){
    	Elements trEles = doc.getElementsByTag("tr");
   		for(Element e : trEles){
   			Elements tdEles = e.children();
   			for(Element te : tdEles){
   				String tdEleStr = te.toString();
   				
   				if(!tdEleStr.contains("td"))
   					continue;
   				
   				//td中存在nobr子节点，input在nobr中
   				if(tdEleStr.contains("nobr")){
   					te = te.child(0);
   				}
   				
   				Elements tdChildrenEles = te.children();
   				
   				//有的列中信息为，td中没有其他元素，需要填充值
   				if(tdChildrenEles == null || tdChildrenEles.size() == 0){
   					String tdHtml = te.html();
   					if(tdHtml == null || tdHtml.trim().equals("")){
   						list.add("");
   					}
   				}else{
   					for(Element ele : tdChildrenEles){
   						String tdChildrenStr = ele.toString();
   						//表中的值都是放在input中，且input有readonly属性
   						if(tdChildrenStr.contains("input") && tdChildrenStr.contains("readonly")){
   			    			list.add(ele.val());
   		   			    }	
   					}
   				}
   			}
   		}
    }
    
    public static void http(String startDate, String endDate, List<String> list, String cookie) throws Exception{
		int currentPage = 1;
		
		ParseHtml parseHtml = http(startDate, endDate, currentPage, cookie);
		
		list.addAll(parseHtml.getData());
		int totalPage = parseHtml.getPage().getTotalPage();
		int totalReocrds = parseHtml.getPage().getTotalRecords();
		System.out.println("------------------------总共有  "+totalPage+" 页, 共有 "+totalReocrds+" 条数据------------------------");
		System.out.println("------------开始日期："+startDate+", 结束日期："+endDate+", 正在查询第 1 页------------");
		for(int i=currentPage+1; i<totalPage+1; i++){
			
			ParseHtml parseHtmlTemp = new ParseHtml();
			try{
				parseHtmlTemp = http(startDate, endDate, i, cookie);	
			}catch(Exception ex){
				String exInfo = "-----------开始日期："+startDate+", 结束日期："+endDate+", 出错页码："+i+"查询出现错误----------";
				CSVUtils.writeErrorLog(exInfo);
				CSVUtils.writeErrorLog(CSVUtils.stackTraceToString(ex));
				--i;
				continue;
			}
			if(parseHtmlTemp.getData() == null || parseHtmlTemp.getData().size() == 0){
				--i;
				continue;
			}
			list.addAll(parseHtmlTemp.getData());
		}
	}
	
	public static ParseHtml http(String startDate, String endDate,int currentPage, String cookie) throws Exception{
		if(currentPage != 1)
			System.out.println("------------开始日期："+startDate+", 结束日期："+endDate+", 正在查询第 "+currentPage+" 页------------");
		
		String url = "https://www.flightontime.cn/deviceAction.do?method=list&togo="+currentPage+"&divDisplay=none";
		String params = "listType=&flightNo=&DepAP3=&ArrAP3=&currentpage="+currentPage+"&startDate="+startDate+"&endDate="+endDate+"&inputstatus=0&row=&openDiv=2&togo=";
		JSONObject header = new JSONObject();
        header.put("Cookie", cookie);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        
        Thread.sleep(100);
        
        return hangbanruru(url, params, header);
	}
}
