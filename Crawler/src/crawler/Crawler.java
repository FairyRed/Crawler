package crawler;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;  
import java.net.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class Crawler 
{
	
	public static void main(String[] args) throws IOException 
	{
		LinkedList<String> HTML = new LinkedList<String>(); //存HTML
		LinkedList<String> tempNonsense = new LinkedList<String>(); //存沒意義的東西
		String url = "https://www.ptt.cc/bbs/Beauty/index.html";
		String result = sendGet(url);
		
		String LastPage1 = RegexString( result , "href=\"(.+?)\"" , tempNonsense , tempNonsense , "" ); //找上頁
		    String url3 = LastPage1;
		    String result3 = sendGet(url3);
    String LastPage2 = RegexString( result3 , "href=\"(.+?)\"" , tempNonsense , tempNonsense , "" ); //找上上頁
        String url4 = LastPage2;
        String result4 = sendGet(url4);
		String LastPage3 = RegexString( result4 , "href=\"(.+?)\"" , tempNonsense , tempNonsense , "" ); //找上上上頁
        String url5 = LastPage3;
        String result5 = sendGet(url5);
		String LastPage4 = RegexString( result4 , "href=\"(.+?)\"" , tempNonsense , tempNonsense , "" ); //找上上上上頁
        String url6 = LastPage4;
        String result6 = sendGet(url6);

		
		RegexString( result, "a href=\"(.+?)\"" , HTML , tempNonsense , "" );
		for(int i=0;i<4;i++)
			HTML.removeLast();
		RegexString( result3, "a href=\"(.+?)\"" , HTML , tempNonsense , "" ); 
		RegexString( result4, "a href=\"(.+?)\"" , HTML , tempNonsense , "" );
		RegexString( result5, "a href=\"(.+?)\"" , HTML , tempNonsense , "" );
		RegexString( result6, "a href=\"(.+?)\"" , HTML , tempNonsense , "" );

		File files=new File("C:\\Users\\user\\Desktop\\"+"Crawler3");
		if(files.mkdir()) 
			System.out.println("建立總資料夾成功"); 
		else 
			System.out.println("建立總資料夾失敗"); 
		
		Document doc1;
		for(int i=0;i<50;i++)	//設定下載幾個網頁
		{
			String url2 = HTML.get(i); //下載指定的HTML
			String result2 = sendGet(url2);
			System.out.println("第"+(i+1)+"個HTML");
			System.out.println(HTML.get(i));
			
			doc1 = Jsoup.connect(url2).get(); //建立子資料夾
			String title=doc1.title();
			String special="#,%,&,*,|,:,\\,<,>,?,/";
			String[] star1=special.split(",");
			for(int j=0;j<star1.length;j++)
			{	
			if(title.contains(star1[j]))
				title=title.replace(star1[j]," ");
			}
			File files1=new File("C:\\Users\\user\\Desktop\\Crawler3\\"+title);
			if(files1.mkdir()) 
				System.out.println("建立子資料夾成功"); 
			else 
				System.out.println("建立子資料夾失敗");//
			
			RegexString( result2 , "a href=\"(.+?)\"" , tempNonsense , tempNonsense , title );//進入下載
		}
		
		System.out.println("Finish Downloading!!");
	
	}
	
	static String sendGet( String url ) //獲取網頁原始碼
	{
		
		String result = "";
		BufferedReader in = null;
		try
		{
			URL realURL = new URL(url);
			URLConnection connection = (HttpURLConnection)realURL.openConnection();
			connection.setRequestProperty("User-agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null ) //這邊有問題*****已解決
			{
				result += line + "\n";
			}
		}
		catch(Exception e)
		{
			System.out.println("發送GET請求出現異常!"+e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(in != null)
				{
					in.close();
				}
			}
			catch(Exception e2)
			{
				e2.printStackTrace();
			}
		}
		
		return result;
	}

	static String RegexString(String targetString,String patternString , LinkedList<String> listHTML , LinkedList<String> listb , String tit)
	{
		LinkedList<String> list1 = new LinkedList<String>(); //存所有搜尋的HTML
		LinkedList<String> list2 = new LinkedList<String>();
		LinkedList<String> list3 = new LinkedList<String>(); //存需要的HTML
		String str = "";
		Pattern pattern = Pattern.compile(patternString);//建立Pattern物件通過compile method搜尋所需要的資料
		Matcher matcher = pattern.matcher(targetString); //Matcher為設定搜索的範圍

		while(matcher.find())
		{
			//System.out.println(matcher.group(1));
			list1.add(matcher.group(1));
		}
		
		for(int i=0;i<list1.size();i++) //篩選圖片的條件
		{
			if(list1.get(i).contains("index2")) //找上i頁HTML
			{
				str="https://www.ptt.cc"+list1.get(i);
				break;
			}
			if(list1.get(i).contains(".html"))
				list3.add("https://www.ptt.cc"+list1.get(i));
			if(list1.get(i).contains(".jpg") && !list1.get(i).contains("/.jpg"))
				list2.add(list1.get(i));
		    if(list1.get(i).contains("http://imgur.com"))
		    {
		    	if(list1.get(i).contains("http://imgur.com/a/"))
		    		list2.add(list1.get(i));
		    	else if(list1.get(i).contains("http://imgur.com") && !list1.get(i).contains(".jpg"))
		    		list2.add(list1.get(i)+".jpg");
		    }
		    	
//		    if(list1.get(i).contains("goo.gl"))
//		    {
//		    	if( list1.get(i).contains("goo.gl/eSjZsr"));
//		    	else
//		    		list2.add(list1.get(i));
//		    }
		}

		
		for(int i=0;i<list2.size();i++) //排除重複的網址
		{
			for(int j=0;j<list2.size();j++)
			{
				if(list2.get(i).contentEquals(list2.get(j)) && i!=j )
					list2.remove(j);
			}
			System.out.println(list2.get(i));
		}
		
		if( !(tit.isEmpty()) )
		for(int i=0;i<list2.size();i++)
		{
			DownloadPic( list2.get(i) , tit , (i+1) );	//正式進入下載
		}

//		if( !(tit.isEmpty()) )  //下載指定的jpg
//		for(int i=0;i<1;i++)
//		{
//			DownloadPic( "http://imgur.com/a/zCRbU" , tit , (i+1) );	//正式進入下載
//		}
		
		listHTML.addAll(list3);
		
		return str;
	}
	
	static void DownloadPic( String url , String title , int i )
	{
		try
		{
			URL realURL = new URL(url);
			//HttpURLConnection connection = (HttpURLConnection)realURL.openConnection();
			//connection.setRequestProperty("User-agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			//connection.connect();
			/*int status = connection.getResponseCode();
            System.out.println(status);
            switch (status) {
            case java.net.HttpURLConnection.HTTP_GATEWAY_TIMEOUT://504
                System.out.println("連線網址逾時!");
                break;
            case java.net.HttpURLConnection.HTTP_FORBIDDEN://403
                System.out.println("連線網址禁止!");
                break;
            case java.net.HttpURLConnection.HTTP_INTERNAL_ERROR://500
                System.out.println("連線網址錯誤或不存在!");
                break;
            case java.net.HttpURLConnection.HTTP_NOT_FOUND://404
                System.out.println("連線網址不存在!");
                break;
            case java.net.HttpURLConnection.HTTP_OK:
                System.out.println("OK!");
                break;
            }*/
			InputStream fStream = realURL.openConnection().getInputStream(); 
			System.out.println("Downloading..."+(i));
		    FileOutputStream fos = new FileOutputStream("C:\\Users\\user\\Desktop\\Crawler3\\"+title+"\\"+i+".jpg");
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fStream); //提高複製速度 緩衝
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);  //提高複製速度 緩衝
			int b = 0;  
			byte[] buffer = new byte[1024]; //緩衝區大小
			while(true)
	        {
				if(fStream.available() < 1024 )
				{	
					while ((b/*=fStream.read())*/!=-1))
					{  
						//fos.write(b);
						b = bufferedInputStream.read();
						bufferedOutputStream.write(b);
					}
					break;
				}
				else
				{
					bufferedInputStream.read(buffer);
					bufferedOutputStream.write(buffer);
				}
			}  
//	            fStream.close();  
//	            fos.close();
	        //流的關閉
			bufferedOutputStream.flush();//強制清除緩衝區內容
			bufferedInputStream.close();
			bufferedOutputStream.close();
	            System.out.println("testing..."+(i));
		}
		catch(Exception e)
		{
			System.out.println("發送GET請求出現異常!"+e);
			e.printStackTrace();
		}
	}
	
}
