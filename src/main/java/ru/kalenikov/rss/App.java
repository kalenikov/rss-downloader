package ru.kalenikov.rss;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class App {
    public static final String URL = "https://podster.fm/rss.xml?pid=44844";
    private static final String FOLDER = "c:\\temp\\java\\rss\\src\\main\\resources\\";

    public static void main(String[] args) throws IOException, FeedException, InterruptedException {
        getFeeds(URL).forEach(App::getFile);
    }

    @SneakyThrows
    public static void getFile(String url){
        Connection.Response res = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(600000)
                .followRedirects(true)
                .ignoreContentType(true)
                .maxBodySize(0)
                .execute();

        String remoteFilename = Objects.requireNonNull(res.header("Content-Disposition"))
                .replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
        System.out.println("start download" + remoteFilename);
        try (FileOutputStream out = new FileOutputStream(FOLDER + remoteFilename)) {
            out.write(res.bodyAsBytes());
        }
        System.out.println("end download" + remoteFilename);
    }

    private static List<String> getFeeds(String URL) throws FeedException, IOException {
        List<String> rsl = new ArrayList<>();
        URL feedUrl = new URL(URL);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        for (Object item : feed.getEntries()) {
            for (Object e : ((SyndEntry) item).getEnclosures()) {
                SyndEnclosure se = (SyndEnclosure) e;
                rsl.add(se.getUrl());
            }
        }
        return rsl;
    }
}


//    public static void apache() throws IOException {
//        String location = "https://gran-collection.podster.fm/3/download/audio.mp3?media=rss";
//        HttpURLConnection connection = null;
//        for (; ; ) {
//            URL url = new URL(location);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setInstanceFollowRedirects(false);
//            String redirectLocation = connection.getHeaderField("Location");
//            if (redirectLocation == null) break;
//            location = redirectLocation;
//        }
//        String fileName = location.substring(location.lastIndexOf('/') + 1, location.length());
//    }
//        List<String> links = List.of(
//                "https://gran-collection.podster.fm/3/download/audio.mp3?media=rss",
//                "https://gran-collection.podster.fm/2/download/audio.mp3?media=rss",
//                "https://gran-collection.podster.fm/1/download/audio.mp3?media=rss"
//
//        );
//        for (String url : links) {
//            getFile(url);
//        }
//    static String TEST_URL = "https://gran-collection.podster.fm/3/download/audio.mp3?media=rss";
//    static String target = "c:\\temp\\java\\rss\\src\\main\\resources\\1.mp3";