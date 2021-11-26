package com.brick.test.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Scraper {
  private final String TITLE_CLASS = "css-1bjwylw";
  private final String PRODUCT_CLASS = "css-bk6tzz";
  private final String PRICE_CLASS = "css-4u82jy";
  private final String DESCRIPTION_CLASS = "css-17zm3l";
  private final String VENDOR_CLASS = "css-1n8curp";
  private final String RATING_CLASS = "css-7fidm1";

  public void exec() {
    try {
      ArrayList<String[]> data = new ArrayList<String[]>();

      System.setProperty("webdriver.chrome.driver", "/home/sydneyaldo/Downloads/chromedriver"); // modify this to your chromedriver's path

      ChromeOptions options = new ChromeOptions();
      options.addArguments("--headless", "--ignore-certificate-errors","--no-sandbox","--disable-dev-shm-usage");
      String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
      options.addArguments(String.format("user-agent=%s", userAgent));

      WebDriver driver = new ChromeDriver(options);

      for(int i = 1; i <= 10; i++) {
        driver.get("https://www.tokopedia.com/p/handphone-tablet/handphone?page=" + i);
        Thread.sleep(2000);
        String page = driver.getPageSource();

        Document doc = Jsoup.parse(page);
        
        Elements products = doc.getElementsByClass(PRODUCT_CLASS);

        for(Element product : products) {
          String link = getItemLink(product);
          driver.get(link);
          Thread.sleep(1000);

          // WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofMinutes(1));
          String name;
          String price;
          String source = driver.getPageSource();
          Document itemDetails = Jsoup.parse(source);
          String imageLink;
          String description;
          String vendor;
          String rating;
          
          try {
            name = product.getElementsByClass(TITLE_CLASS).text();
          } catch (Exception e) {
            name = "-";
          }
          try {
            price = product.getElementsByClass(PRICE_CLASS).text();
          } catch (Exception e) {
            price = "-";
          }
          try {
            imageLink = itemDetails.select("img[crossorigin]").first().attr("src");
          } catch (Exception e) {
            imageLink = "-";
          }
          try {
            description = itemDetails.select("span." + DESCRIPTION_CLASS).text();
          } catch (Exception e) {
            description = "-";
          }
          try {
             vendor = itemDetails.getElementsByClass(VENDOR_CLASS).select("h2").text();
          } catch (Exception e) {
            vendor = "-";
          }
          try {
            rating = itemDetails.getElementsByClass(RATING_CLASS).select("span.main[data-testid]").first().text();
          } catch (Exception e) {
            rating = "-";
          }

          data.add(new String[] {
            name,
            description,
            imageLink,
            price,
            rating,
            vendor
          });
        }
        System.out.println("Parsing ... " + (i * 10) + "%");
      }

      driver.close();
      createCSVFile(data);
    } catch(Exception e) {
      e.printStackTrace();
    }
    
  }

  private String getItemLink(Element product) {
    String urlData = product.selectFirst("a").attr("href");

    int startIndex = urlData.indexOf("r=", 0);
    if(startIndex == -1) startIndex = 0;
    else startIndex += 2;

    int endIndex = urlData.indexOf('&', startIndex);
    if(endIndex == -1) endIndex = urlData.length();

    String encodedLink = urlData.substring(startIndex, endIndex);

    return java.net.URLDecoder.decode(encodedLink, StandardCharsets.UTF_8);
  }

  private void createCSVFile(ArrayList<String[]> data) throws Exception {
    String[] headers = {
      "Product Name",
      "Description",
      "Image Link",
      "Price",
      "Rating (out of 5)",
      "Vendor",
    };

    // System.out.print(data);

    File file = new File("phone-scrape-results.csv");
    if(file.exists()) file.delete();
    file.createNewFile();
    FileWriter output = new FileWriter(file, true);

    CSVPrinter printer = new CSVPrinter(output, CSVFormat.DEFAULT.withHeader(headers));

    for(String[] row : data) {
      printer.printRecord(row[0], row[1], row[2], row[3], row[4], row[5]); 
    }

    output.close();
  }
}
