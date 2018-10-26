package Java.web;

import Java.Strings;
import Java.models.AiringShowList;
import Java.models.ShowInfo;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class WebSearch {

    private static String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";
    private int timeout = 20 * 1000;

    public WebSearch() {
    }

    void createHTMLFilesFromLink(ShowInfo showInfo, boolean updateExisting) {

        String showTitle = showInfo.getTitle();

        try {

            if (!new File(ProjectPaths.getInstance().getHtmlAnidb() + showTitle).exists() || updateExisting) {
                String link = showInfo.getAniDbLink();
                if (!link.equals(Strings.NULL))
                    Utils.createFile(ProjectPaths.getInstance().getHtmlAnidb(), showTitle, Jsoup.connect(link).userAgent(userAgent).timeout(timeout).get().toString());
                else
                    Utils.createFile(ProjectPaths.getInstance().getHtmlAnidb(), showTitle, Jsoup.connect(getAniDbLinkFromGoogle(showTitle)).userAgent(userAgent).timeout(timeout).get().toString());
            }

            if (!new File(ProjectPaths.getInstance().getHtmlMal() + showTitle).exists() || updateExisting) {
                String link = showInfo.getMalLink();
                if (!link.equals(Strings.NULL))
                    Utils.createFile(ProjectPaths.getInstance().getHtmlMal(), showTitle, Jsoup.connect(link).userAgent(userAgent).timeout(timeout).get().toString());
                else
                    Utils.createFile(ProjectPaths.getInstance().getHtmlMal(), showTitle, Jsoup.connect(getMalLinkFromGoogle(showTitle)).userAgent(userAgent).timeout(timeout).get().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    void createHTMLFilesFromGoogleForAniDb(String showTitle) {

        try {
            System.out.println("searching for: " + showTitle + " AniDb Link");
            String link = getAniDbLinkFromGoogle(showTitle);
            if (!link.equals(Strings.NULL))
                Utils.createFile(ProjectPaths.getInstance().getHtmlAnidb(), showTitle, Jsoup.connect(link).userAgent(userAgent).timeout(timeout).get().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void createHTMLFilesFromGoogleForMAL(String showTitle) {

        try {
            System.out.println("searching for: " + showTitle + " MAL Link");
            String link = getMalLinkFromGoogle(showTitle);
            if (!link.equals(Strings.NULL))
                Utils.createFile(ProjectPaths.getInstance().getHtmlMal(), showTitle, Jsoup.connect(link).userAgent(userAgent).timeout(timeout).get().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getAnimePlanetLinkFromGoogle(String show) {

        try {

            Elements gLinks = Jsoup.connect("http://www.google.com/search?q=" + "anime-planet" + URLEncoder.encode(show, "UTF-8")).userAgent(userAgent).timeout(timeout).get().select(".g>.r>a");

            String url = "";

            for (Element googleLink : gLinks) {

                url = googleLink.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (url.contains("anime-planet.com")) {
                    break;
                }
            }

            return url;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Strings.NULL;
    }


    private String getAniDbLinkFromGoogle(String show) {

        try {

            Elements gLinks = Jsoup.connect("http://www.google.com/search?q=" + "anidb " + URLEncoder.encode(show, "UTF-8")).userAgent(userAgent).timeout(timeout).get().select(".g>.r>a");

            String url = "";

            for (Element googleLink : gLinks) {

                url = googleLink.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (url.contains("anidb.net")) {
                    break;
                }
            }

            return url;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Strings.NULL;
    }


    private String getMalLinkFromGoogle(String show) {

        try {
            Elements gLinks = Jsoup.connect("http://www.google.com/search?q=" + "myanimelist " + URLEncoder.encode(show, "UTF-8")).userAgent(userAgent).timeout(timeout).get().select(".g>.r>a");

            String url = "";

            for (Element googleLink : gLinks) {

                url = googleLink.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (url.contains("myanimelist.net/anime/")) {
                    break;
                }
            }

            StringBuilder tmpUrl = new StringBuilder();

            for (int i = 0; i < url.split("/").length; i++) {
                tmpUrl.append(url.split("/")[i] += "/");

                if (i > 3)
                    break;
            }

            return tmpUrl.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Strings.NULL;
    }


    public void createJsonForAiringShowsMal() {
        try {

            Document document = Jsoup.connect("https://myanimelist.net/anime/season").userAgent(userAgent).timeout(timeout).get();
            Element newShows = document.getElementsByClass("js-seasonal-anime-list-key-1").get(0);
            Element continuingShows = document.getElementsByClass("js-seasonal-anime-list-key-1").get(1);
            Elements shows = newShows.getElementsByClass("link-title");

            String date = "";

            for (Element element : document.getElementsByTag("meta")) {

                if (element.attr("property").equals("og:title")) {
                    date = element.attr("content").replace(" - Anime - MyAnimeList.net ", "");
                    date = date.substring(1);
                    break;
                }
            }

            AiringShowList airingShowList = new AiringShowList(date);


            for (Element show : shows) {
                airingShowList.getShowList().add(show.getElementsByClass("link-title").text().toLowerCase().replace("/", " "));
            }

            shows = continuingShows.getElementsByClass("link-title");

            for (Element show : shows) {
                airingShowList.getShowList().add(show.getElementsByClass("link-title").text().toLowerCase().replace("/", " "));
            }

            Utils.saveJsonToFile(airingShowList, ProjectPaths.getInstance().getJsonOtherFolder(), Strings.JSON_AIRING_SHOWS);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



