package com.big_hackathon.backend_v2.service;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class JavaWebScraper {

    private final Logger logger = LoggerFactory.getLogger(JavaWebScraper.class);

    public Map<String, String> scrapeKijiji(String path){
        //TODO look into proxy rotation
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
        Response response = null;
        Document doc = null;
        try {
            response = Jsoup.connect(path)
                    .userAgent(userAgent)
                    .referrer("http://www.google.com")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en")
                    .timeout(8000) //if it takes more than 8 seconds to scrape, throw an IOException
                    .maxBodySize(0)
                    .followRedirects(true)
                    .execute();

            doc = response.parse();
        } catch (IOException e){
            logger.info("Ran out of time: " + e);
        }

        Map<String, String> result = new HashMap<>();

        if(doc != null) {
            //Title
            String title = doc.getElementsByClass("sc-9d9a3b6-0 cwhKRe").text();
            result.put("title", title);

            // Price
            String price = doc.getElementsByClass("sc-9d9a3b6-0 bhudfV").text();
            result.put("price", price);

            // Location
            Element location = doc.getElementsByClass("sc-c8742e84-0 fukShK").first();
            if (location != null) {
                result.put("location", location.text());
            }

            //TODO how to scrape image
        }

        // Original URL
        result.put("url", path);

        return result;
    }
}
