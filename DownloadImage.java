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
	private int dirPage = 0;
	
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
				
				String dirName = document.title();
				
				if(dirName.contains("/") || dirName.contains("\\") || dirName.contains(":") || dirName.contains("*") || dirName.contains("?") || dirName.contains("\"") || dirName.contains("<") || dirName.contains(">") || dirName.contains("|") || dirName.contains("？"))
				{
					dirName = "有文件夹不能包含字符，因此换名(" + dirPage++ + ")";
				}
				
				File dir = new File("E://op//爬取//" + page + "//" + dirName);
				
				dir.mkdirs();
				
				int getPage = getDownloadPage();
				
				Thread.sleep(350);
				
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
								Thread.sleep(350);
								startDownload(ss, object, num, dataUrl);
								//Thread.sleep(300);
							}
							else
							{
								String ss = getImageUrl(num, dataUrl);
								//Thread.sleep(1500);  //避免过度请求被pass
								Thread.sleep(350);
								startDownload(ss, object, num, dataUrl);
								//Thread.sleep(300);
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
						System.out.println(Thread.currentThread().getName() + "遭遇429，原因：爬虫请求过快，请等待60s...");
						Thread.sleep(60000);
						String ss = getImageUrl(num, dataUrl);
						Thread.sleep(350);
						startDownload(ss, object, num, dataUrl);
						System.out.println("+++++重新下载完毕+++++");
					}
				}
				System.out.println(dir + ">>>该套图下载完成~");
				count++;
				Thread.sleep(350);  //避免过度请求被pass，是getConnect的获取Document对象连接时的异常
			}
			System.out.println("第" + page + "页下载完毕，套图数为：" + count);
			amount++;
			page = amount;
			Mz.recordDownloadPage(1, page);
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
			document = Jsoup.connect(address)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("Accept-Encoding", "gzip, deflate")
			.header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
			.header("Connection", "keep-alive")
			.header("Host", address)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
			.get();
			
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
		
		document = Jsoup.connect(url)
		.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.header("Accept-Encoding", "gzip, deflate")
		.header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
		.header("Connection", "keep-alive")
		.header("Host", url)
		.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
		.get();
		Elements img = document.select("div.main-image").select("img");
		//Thread.sleep(500);
		return img.attr("src");
	}
	
	public String getImageUrl() throws Exception
	{
		Elements img = document.select("div.main-image").select("img");
		return img.attr("src");
	}
	
	public void startDownload(String downloadUrl, File fileName, int num, String uri) throws Exception
	{
		if(num == 1)
			;  //什么都不想干，我不干了
		else
			num--;
		Response response = Jsoup.connect(downloadUrl)
			.ignoreContentType(true)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.header("Accept-Encoding", "gzip, deflate")
			.header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3")
			.header("Connection", "keep-alive")
			.header("Host", uri + "/" + num)
			.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0")
			.header("Referer", uri + "/" + num)  //这个是表明你从那里来的
			.execute();
				
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(response.bodyAsBytes());
		out.close();
	}
}