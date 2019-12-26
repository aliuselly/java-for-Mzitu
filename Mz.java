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
			//���ø���ͼԴ��ַ��ʱ���أ�ɾ����ÿ��Ŀ¼�����address.txt���߽�WriteURL�е�exists()������ȥ��������д��չ�ˣ��Һ���
			getURL(cachePage);
			
			while(beforeDownloadPage == 0)
			{
				//�÷�������Ϊ����һ�����ļ�������ÿ�δ�ͷ��ʼ
				recordDownloadPage(0, 0);
			}
			
			while(flag)
			{
				Thread.sleep(5000);  //����URL�����㣬ͬʱ���������뻺��URLͬʱ���ʣ���ɹ��ȷ��ʶ�403
			}
			
			downloadURL();
		}
		catch(Exception e)
		{
			System.out.println("�������г�����IO�쳣");
			e.printStackTrace();
		}
	}
	
	public static int getPageNumber(Document document)
	{
		//ץȡ��վ��ҳ��
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
			System.out.println("����������Ĵ��󣬱������ַ�����");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void getURL(int number)
	{
		//ץȡÿ��ͼ��Ŀ�ʼ��ַ������¼
		new Thread(new WriteURL(number)).start();
	}
	
	public static void downloadURL()
	{
		//��4���̻߳ᵼ�ºܶ�ͼƬ�������أ���5���ᵼ�·���̫���403������3�����ǱȽϺõ��ѡ��
		//new Thread(new DownloadImage(73,cachePage)).start();
		
		//�ŷ����ң�����5������������λ����������python�����̳̣�������429�ķ���Ҳ�Ǵ�python����ѧϰ�ģ���Ȼ�Ҳ���python...
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage++,cachePage)).start();
		new Thread(new DownloadImage(beforeDownloadPage,cachePage)).start();
	}
	
	public static void recordDownloadPage(int select, int page) throws IOException
	{
		File dir = new File("E://op//��ȡ");
		File file = new File("E://op//��ȡ//�����ļ�.ini");
		
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
					dir.mkdirs();
					file.createNewFile();
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