import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class Mz
{
	
	public static int cachePage = 0;
	public static boolean flag = true;
	public static int beforeDownloadPage = 0;
	
	public static void main(String[] args)
	{
		try
		{	
			String url = "https://www.mzitu.com";
			Document document = Jsoup.connect(url).get();
			
			cachePage = getPageNumber(document);
			
			//想让更新图源地址的时候呢，删除掉每个目录下面的address.txt或者将WriteURL中的exists()方法给去掉，不想写扩展了，我好懒
			getURL(cachePage);
			
			while(beforeDownloadPage == 0)
			{
				//该方法功能为创建一配置文件，避免每次从头开始
				recordDownloadPage(0, 0);
			}
			
			while(flag)
			{
				Thread.sleep(5000);  //先让URL缓存多点，同时避免下载与缓存URL同时访问，造成过度访问而403
			}
			
			downloadURL();
		}
		catch(Exception e)
		{
			System.out.println("主程序中出现了IO异常");
			e.printStackTrace();
		}
	}
	
	public static int getPageNumber(Document document)
	{
		//抓取网站总页数
		Elements as = document.select("a.page-numbers");
		
		try
		{
			for(Element a : as)
			{
				int number = Integer.parseInt(a.text());
				
				if(number > 18)
					return number;
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("出现了以外的错误，比如有字符串了");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void getURL(int number)
	{
		//抓取每个图库的开始地址，并记录
		new Thread(new WriteURL(number)).start();
	}
	
	public static void downloadURL()
	{
		//开4个线程会导致很多图片重新下载，开5个会导致访问太快而403，所以3个才是比较好点的选择
		//new Thread(new DownloadImage(73,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage,cachePage)).start();
	}
	
	public static void recordDownloadPage(int select, int page) throws IOException
	{
		File file = new File("E://op//爬取//配置文件.ini");
		
		switch(select)
		{
			case 0:
				if(file.exists())
				{
					FileReader in = new FileReader(file);
					beforeDownloadPage = in.read();
					in.close();
				}
				else
				{
					FileWriter fw = new FileWriter(file);
					fw.write(1);
					fw.flush();
					fw.close();
				}
				break;
			
			case 1:
				FileWriter fw = new FileWriter(file);
				fw.write(page);
				fw.flush();
				fw.close();
				break;
		}
	}
}