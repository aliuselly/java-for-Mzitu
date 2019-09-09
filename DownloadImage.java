import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Response;

class DownloadImage implements Runnable
{
	private static int sum = 0;  //记录总页数
	private static int amount = 0;  //记录当前所有所下载到的页数
	
	private int page = 0;  //记录当前线程所下载的页数
	private Document document = null;
	
	DownloadImage(int page, int su)
	{
		this.page = page;
		sum = su;
		
		if(page > amount)
			amount = page;
	}
	
	public void run()
	{
		while(amount <= sum)
		{
			download();
		}
	}
	
	public void download()
	{
		File file = new File("E://op//爬取//" + page + "//address.txt");
		
		String dataUrl = null;
		
		int count = 0;  //记录套图数目
		
		boolean flag = true;
		
		try
		{
			BufferedReader bufr = new BufferedReader(new FileReader(file));
			
			System.out.println("第" + page + "页图集下载开始...");
			
			while((dataUrl = bufr.readLine()) != null)
			{
				getConnect(dataUrl);
				
				File dir = new File("E://op//爬取//" + page + "//" + document.title());
				dir.mkdirs();
				
				int getPage = getDownloadPage();
				
				Thread.sleep(600);
				
				for(int num = 1; num <= getPage; num++)
				{
					File object = new File(dir, num + ".jpg");
						
					try
					{
						//综合而言，获取到图片源连接后睡600毫秒，再下载图片，再睡600毫秒，这才是最稳的，好过之前的1500毫秒，虽然仅节约了300毫秒，不过比原来的单1500毫秒要稳
						if(!object.exists())
						{
							if(num == 1)
							{
								String ss = getImageUrl();
								//Thread.sleep(1500);  //避免过度请求被pass
								Thread.sleep(600);
								startDownload(ss, object, num);
								Thread.sleep(600);
							}
							else
							{
								String ss = getImageUrl(num, dataUrl);
								//Thread.sleep(1500);  //避免过度请求被pass
								Thread.sleep(600);
								startDownload(ss, object, num);
								Thread.sleep(600);
							}
						}
						else
						{
							System.out.println(object + "---!该图片已存在!");
							//Thread.sleep(300);  //原本是下载过程中，不能立刻请求的，但是，如果不睡下的话，立刻请求会导致403
						}
					}
					
					catch(IOException e)
					{
						System.out.println("----------警号----------");
						System.out.println("获取图源连接过程出现了异常!");
						//e.printStackTrace();
						Thread.sleep(600);
						String ss = getImageUrl(num, dataUrl);
						Thread.sleep(600);
						startDownload(ss, object, num);
						System.out.println("+++++重新下载完毕+++++");
					}
				}
				System.out.println(dir + ">>>该套图下载完成~");
				count++;
				//Thread.sleep(1000);  //避免过度请求被pass
			}
			System.out.println("第" + page + "页下载完毕，套图数为：" + count);
			amount++;
			page = amount;
		}
		catch(Exception e)
		{
			System.out.println("控制下载主方法出现了异常!!!");
			e.printStackTrace();
		}
	}
	
	public Document getConnect(String address)
	{
		try
		{
			document = Jsoup.connect(address).get();
			
			return document;
		}
		catch(IOException e)
		{
			System.out.println("与图源地址连接失败!");
			e.printStackTrace();
		}
		return null;
	}
	
	public int getDownloadPage()
	{
		Elements spans = document.select("span");
		
		int imagePage = 0;
		
		for(Element span : spans)
		{
			try
			{
				imagePage = Integer.parseInt(span.text());
				
				if(imagePage > 10)
					return imagePage;
			}
			catch(NumberFormatException e)
			{
				//这里最好还是不要打印了吧
				//System.out.println("抓取到了意外的东西");
			}
		}
		
		return 0;
	}
	
	public String getImageUrl(int num, String url) throws Exception
	{
		url = url + "/" + num + "/";
		
		document = Jsoup.connect(url).get();
		Elements img = document.select("div.main-image").select("img");
		//Thread.sleep(500);
		return img.attr("src");
	}
	
	public String getImageUrl() throws Exception
	{
		Elements img = document.select("div.main-image").select("img");
		return img.attr("src");
	}
	
	public void startDownload(String downloadUrl, File fileName, int num) throws Exception
	{
		Response response = Jsoup.connect(downloadUrl)
			.ignoreContentType(true)
			.header("Referer", "https://i5.meizitu.net")
			.execute();
				
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(response.bodyAsBytes());
		out.close();
	}
}