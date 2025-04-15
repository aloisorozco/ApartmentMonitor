package com.big_hackathon.backend_v2.service;


import com.big_hackathon.backend_v2.model.Apartment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection.Response;

import java.io.IOException;
import java.util.UUID;

public class JavaWebScraper {

    public static Apartment scrapeKijiji(String path){
        //TODO look into proxy rotation
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
        Response response = null;
        Document doc = null;
        try {
            //Connect to scraping service
            response = Jsoup.connect(path)
                    .userAgent(userAgent)
                    .referrer("http://www.google.com")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("Accept-Language", "en")
                    .timeout(8000) //if it takes more than 8 seconds to scrape, throw an IOException
                    .maxBodySize(0)
                    .followRedirects(true)
                    .execute();

            //Scrape
            doc = response.parse();
        } catch (IOException e){
            System.out.println(e);
        }

        Apartment apartment = null;

        //If scraping works
        if(doc != null) {
            //Title
            String description = doc.getElementsByClass("sc-9d9a3b6-0 cwhKRe").text();

            // Price
            //TODO is this the right approach to convert later on into double?
            double price = Double.parseDouble(doc.getElementsByClass("sc-9d9a3b6-0 bhudfV").text().replaceAll("[^\\d.]", ""));

            // Location
            Element locationElement = doc.getElementsByClass("sc-c8742e84-0 fukShK").first();
            String location = null;
            if(locationElement != null){
                location = locationElement.text();
            }

            //Image
            Element image = doc.getElementsByClass("sc-3930aaf4-2 bAqjYF").first();
            String imageLink = null;
            if(image != null){
                imageLink = image.attr("src");
            }

            //Build apartment
            apartment = Apartment.builder()
                    .listingID(UUID.randomUUID().toString())
                    .description(description)
                    .price(price)
                    .location(location)
                    .imageLink(imageLink)
                    .url(path)
                    .build();
        }

        //Return scraped apartment
        return apartment;
    }
}
