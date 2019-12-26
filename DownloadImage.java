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
	private static int sum = 0;  //��¼��ҳ��
	private static int amount = 0;  //��¼��ǰ���������ص���ҳ��
	private int dirPage = 0;
	
	private int page = 0;  //��¼��ǰ�߳������ص�ҳ��
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
		File file = new File("E://op//��ȡ//" + page + "//address.txt");
		
		String dataUrl = null;
		
		int count = 0;  //��¼��ͼ��Ŀ
		
		boolean flag = true;
		
		try
		{
			BufferedReader bufr = new BufferedReader(new FileReader(file));
			
			System.out.println("��" + page + "ҳͼ�����ؿ�ʼ...");
			
			while((dataUrl = bufr.readLine()) != null)
			{
				getConnect(dataUrl);
				
				String dirName = document.title();
				
				if(dirName.contains("/") || dirName.contains("\\") || dirName.contains(":") || dirName.contains("*") || dirName.contains("?") || dirName.contains("\"") || dirName.contains("<") || dirName.contains(">") || dirName.contains("|") || dirName.contains("��"))
				{
					dirName = "���ļ��в��ܰ����ַ�����˻���(" + dirPage++ + ")";
				}
				
				File dir = new File("E://op//��ȡ//" + page + "//" + dirName);
				
				dir.mkdirs();
				
				int getPage = getDownloadPage();
				
				Thread.sleep(350);
				
				for(int num = 1; num <= getPage; num++)
				{
					File object = new File(dir, num + ".jpg");
						
					try
					{
						//�ۺ϶��ԣ���ȡ��ͼƬԴ���Ӻ�˯600���룬������ͼƬ����˯600���룬��������ȵģ��ù�֮ǰ��1500���룬��Ȼ����Լ��300���룬������ԭ���ĵ�1500����Ҫ��
						if(!object.exists())
						{
							if(num == 1)
							{
								String ss = getImageUrl();
								//Thread.sleep(1500);  //�����������pass
								Thread.sleep(350);
								startDownload(ss, object, num, dataUrl);
								//Thread.sleep(300);
							}
							else
							{
								String ss = getImageUrl(num, dataUrl);
								//Thread.sleep(1500);  //�����������pass
								Thread.sleep(350);
								startDownload(ss, object, num, dataUrl);
								//Thread.sleep(300);
							}
						}
						else
						{
							System.out.println(object + "---!��ͼƬ�Ѵ���!");
							//Thread.sleep(300);  //ԭ�������ع����У�������������ģ����ǣ������˯�µĻ�����������ᵼ��403
						}
					}
					
					catch(IOException e)
					{
						System.out.println("----------����----------");
						System.out.println("��ȡͼԴ���ӹ��̳������쳣!");
						//e.printStackTrace();
						System.out.println(Thread.currentThread().getName() + "����429��ԭ������������죬��ȴ�60s...");
						Thread.sleep(60000);
						String ss = getImageUrl(num, dataUrl);
						Thread.sleep(350);
						startDownload(ss, object, num, dataUrl);
						System.out.println("+++++�����������+++++");
					}
				}
				System.out.println(dir + ">>>����ͼ�������~");
				count++;
				Thread.sleep(350);  //�����������pass����getConnect�Ļ�ȡDocument��������ʱ���쳣
			}
			System.out.println("��" + page + "ҳ������ϣ���ͼ��Ϊ��" + count);
			amount++;
			page = amount;
			Mz.recordDownloadPage(1, page);
		}
		catch(Exception e)
		{
			System.out.println("���������������������쳣!!!");
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
			System.out.println("��ͼԴ��ַ����ʧ��!");
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
				//������û��ǲ�Ҫ��ӡ�˰�
				//System.out.println("ץȡ��������Ķ���");
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
			;  //ʲô������ɣ��Ҳ�����
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
			.header("Referer", uri + "/" + num)  //����Ǳ��������������
			.execute();
				
		FileOutputStream out = new FileOutputStream(fileName);
		out.write(response.bodyAsBytes());
		out.close();
	}
}