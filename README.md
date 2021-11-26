***How to use***

* Check your chrome version and download a driver with the same build version from [here](https://chromedriver.chromium.org/downloads)

* Exract the driver and modify the second parameter in line `32` in `src/main/java/com/brick/test/scraper/Scraper.java` to your the driver's path

* Install Maven (if you haven't). Documentation is [here](https://maven.apache.org/install.html)

* Run these commands, a new file (`phone-scrape-results.csv`) will be generated every time in the root folder.

```
  $ mvn install
  $ mvn exec:java -Dexec.mainClass="com.brick.test.scraper.App" -Dexec.cleanupDaemonThreads=false
```