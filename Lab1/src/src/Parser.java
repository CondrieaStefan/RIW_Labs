package src;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

 
public class Parser{
	public static void main(String[] args) throws IOException {
 
		Document doc = Jsoup.connect("https://ac.tuiasi.ro").get();
		//titlul paginii
		String title = doc.title();
		System.out.println("TITLE : " + title + "\n");
		Elements meta=doc.getElementsByTag("meta");
		

		
 
		//printeaza toate linkurile de pe pagina
		Elements links = doc.select("a[href]");
		for(Element l: links){
			
			System.out.println("link: " +l.attr("abs:href"));
			System.out.println("text : " + l.text());
		}
		//printeaza toate imaginile de pe pagina
		Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
 
			System.out.println("\nsrc : " + image.attr("src"));
			System.out.println("height : " + image.attr("height"));
			System.out.println("width : " + image.attr("width"));
			System.out.println("alt : " + image.attr("alt"));
 
		}
		//printeaza meta
		
		for (Element met: meta)
		{
			
			met.getElementsByAttribute("DESCRIPTION");
			System.out.println("meta desc: " + met);
		}
	

 
	}
}
