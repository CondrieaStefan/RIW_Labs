package main_package;

import dns.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sun.rmi.runtime.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;


public class TestMain {

    private static final Logger log = Logger.getLogger(TestMain.class.getName());

    private static HashMap<String, String> _domanIp = new HashMap<>();

    public static void main(String[] args) {

        Queue<URLFormater> urlQueue = new LinkedList<>();
        try {
            int i = 0;

            urlQueue.add(new URLFormater("http://riweb.tibeica.com/crawl"));

            while (!urlQueue.isEmpty()) {
                try {
                    // get head of the queue to process
                    URLFormater processedURL = urlQueue.peek();
                    urlQueue.remove();

                    String domain = processedURL.get_domain();
                    String localPath = processedURL.get_localPath();
                    String page = processedURL.get_page();

                    // check if domain was processed before
                    if (processedURL.wasProcessed())
                        continue;
                    String ipAddress;

                    if (!_domanIp.containsKey(domain)) {
                        DnsClient dnsClient = new DnsClient(domain, "81.180.223.1", 53);
                        ipAddress = dnsClient.getIpAddres();
                        _domanIp.put(domain, ipAddress);
                    } else {
                        ipAddress = _domanIp.get(domain);
                    }
                    // check for /robots.txt resource


                    HTTPClient httpClient = new HTTPClient(processedURL, ipAddress);
                    if (!_domanIp.containsKey(domain)) {
                        if (!httpClient.checkForRobosts()) {
                            continue;
                        }
                    }
                    if (!httpClient.sendRequest())
                        continue;

                    // initialize jsoup
                    File htmlFile = new File(domain + localPath + "/" + page);
                    Document document = Jsoup.parse(htmlFile, null, "http://" + domain + localPath + "/");

                    // extract text to certain file
                    String text = document.body().text();

                    BufferedWriter writer = new BufferedWriter(new FileWriter(domain + localPath + "/"
                            + page.replaceAll(".html", ".txt")));
                    writer.write(text);

                    writer.close();

                    // check for robots meta
                    Element robotsMeta = document.selectFirst("meta[name=robots]");
                    if (robotsMeta != null) {
                        String content = robotsMeta.attr("content");
                        if (content == null || content.contains("nofollow"))
                            continue;
                    }

                    // Extract link from currentPage
                    List<Element> links = document.select("a");
                    List<String> linksHref = new LinkedList<>();
                    for (Element e : links) {
                        String link = e.attr("abs:href");
                        if (!link.isEmpty()) {
                            if (!link.contains("https://")) {
                                URLFormater urlToAdd = new URLFormater(link);
                                if (!urlToAdd.wasProcessed())
                                    urlQueue.add(urlToAdd);
                            }
                        }

                    }
                    htmlFile.delete();
                    log.info("Files processed: " + (++i));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
