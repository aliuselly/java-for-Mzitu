import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class WriteURL implements Runnable
{
	private static int num = 0;
	
	WriteURL(int num)
	{
		this.num = num;
	}
	
	public void run()
	{
		File parent = null;
		File file = null;
		BufferedWriter bufw = null;
		String url = null;
		String writeUrl = null;
		Document document = null;
		
		for(int x = 1; x <= num; x++)
		{
			try
			{
				url = "https://www.mzitu.com/page/" + x + "/";
				
				parent = new File("E://op//爬取//" + x);
				file = new File(parent, "address.txt");
				
				if(!file.exists())
				{
					parent.mkdirs();
					
					bufw = new BufferedWriter(new FileWriter(file));
					
					parent.mkdir();
					
					System.out.println("正在抓取第" + x + "页的图源地址...");
					
					document = Jsoup.connect(url).get();
					Elements lis = document.select("#pins").select("li");
					
					for(Element li : lis)
					{
						writeUrl = li.select("a").attr("href");
						bufw.write(writeUrl);
						bufw.newLine();
						bufw.flush();
					}
					
					bufw.flush();
					System.out.println("第" + x + "页图源地址抓取完毕!");
					
					if(x % 2 == 0)
						Thread.sleep(1000);  //暂停一下，服务器有反爬虫
				}
				else
					System.out.println("第" + x + "页的图源地址已存在!");
			}
			catch(Exception e)
			{
				System.out.println("图源地址抓取过程异常!!!");
				e.printStackTrace();
			}
			finally
			{
				if(bufw != null)
					try
					{
						bufw.close();
					}
					catch(IOException e)
					{
						System.out.println("在抓取图源地址进本地时，出现异常!");
						e.printStackTrace();
					}
					
				Mz.flag = false;
			}
		}
	}
}