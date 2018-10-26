package Java.web;

import Java.Strings;
import Java.comparators.NaturalOrderComparator;
import Java.models.ShowInfo;
import Java.models.ShowRecommendation;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


class HtmlParser {

    HtmlParser() {}


    void populateShowInfoFromHTML(String showTitle, ShowInfo showInfo) {

        showInfo.setTitle(showTitle);
        ArrayList<String> tags = new ArrayList<>();

        try {
            Document document = Jsoup.parse(new File(ProjectPaths.getInstance().getHtmlMal() + showTitle), "UTF-8");
            showInfo.setMalTitle(getTitleFromMal(document));
            showInfo.setDefaultTitle(Utils.getDefaultShowTitle(showInfo.getMalTitle()));
            showInfo.setJapaneseTitle(getJapaneseTitleFromMal(document));
            showInfo.setPlot(getPlotFromMAL(document));
            showInfo.setRating(getRatingFromMAL(document));
            showInfo.setAirDate(getPremieredDateFromMAL(document));
            showInfo.setMalLink(getLinkMAL(document));
            showInfo.setAgeRating(getAgeRatingFromMAL(document));
            showInfo.setStudio(getStudioFromMal(document));
            showInfo.setRecommendations(getRecommendationsFromMAL(document, showInfo));

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Document document = Jsoup.parse(new File(ProjectPaths.getInstance().getHtmlAnidb() + showTitle), "UTF-8");
            showInfo.setWikiLink(getWikipediaFromAniDb(document));
            showInfo.setAniDbLink(getLinkAniDb(document));
            getImageFromAniDb(document, showInfo);
            tags = getTagsFromAniDb(document);

        } catch (IOException e) {
            e.printStackTrace();
        }

        tags.sort(new NaturalOrderComparator<>(false));
        showInfo.setTags(tags);

    }

    private String getWikipediaFromAniDb(Document document) {

        for (Element link :  document.select("a[href]")) {
            if (link.toString().contains("en.wikipedia.org")) {
                return link.attr("abs:href");
            }
        }

        return Strings.NULL;
    }

    private void getImageFromAniDb(Document document, ShowInfo showInfo) {

        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(document.select("meta[property=og:image]").attr("content")));

            String backgroundImagePath = ProjectPaths.getInstance().getBackgroundImagesFolder().replace("file:", "") + showInfo.getDefaultTitle() + ".jpg";
            String coverImagePath = ProjectPaths.getInstance().getCoverImagesFolder().replace("file:", "") + showInfo.getDefaultTitle() + ".jpg";

            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setStroke(new BasicStroke(7));
            graphics.setColor(Color.BLACK);
            graphics.drawRect(2,3, bufferedImage.getWidth() - 5, bufferedImage.getHeight() - 7);

            if (!new File(backgroundImagePath).exists())
                ImageIO.write(bufferedImage, "JPG", new File(backgroundImagePath));
            if (!new File(coverImagePath).exists())
                ImageIO.write(bufferedImage, "JPG", new File(coverImagePath));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private ArrayList<String> getTagsFromAniDb(Document document) {

        ArrayList<String> tagsList = new ArrayList<>();
        Elements tagElements = document.getElementsByClass("tag");

        for (Element element : tagElements) {

            String weight = element.attr("data-anidb-weight");

            if (weight != null && !weight.equals("") && Integer.valueOf(weight) >= 300) {
                String tag = element.getElementsByClass("tagname").text();
                addTag(tagsList, tag);
            }
        }

        return tagsList;
    }


    private void addTag(ArrayList<String> tagsList, String tag) {

        tag = tag.toLowerCase();

        if (!tag.equals("earth") && !tag.equals("asia") && !tag.equals("japan") && !tag.equals("tokyo")
                && !tag.equals("europe") && !tag.equals("animeism") && !tag.equals("noitamina") && !tag.equals("episodic")
                && !tag.equals("parental abandonment") && !tag.equals("plot continuity") && !tag.equals("stereotypes")) {

            //in case you use both aniDB and animePlant tags
//            switch (tag) {
//                case "post-apocalyptic":
//                    tag = "Post-apocalypse";
//                    break;
//                case "political":
//                    tag = "Politics";
//                    break;
//                case "panty shots":
//                    tag = "Pantsu";
//                    break;
//                case "aliens":
//                    tag = "Alien";
//                    break;
//                case "sci fi":
//                    tag = "Science Fiction";
//                    break;
//                case "demons":
//                    tag = "Demon";
//                    break;
//                case "androids":
//                    tag = "Android";
//                    break;
//                case "detectives":
//                    tag = "Detective";
//                    break;
//                case "cyborgs":
//                    tag = "Cyborg";
//                    break;
//                case "vampires":
//                    tag = "Vampire";
//                    break;
//                case "cross-dressing":
//                    tag = "crossdressing";
//                    break;
//                case "shounen-ai":
//                    tag = "Shounen ai";
//                    break;
//                case "assassin":
//                    tag = "Assassins";
//                    break;
//                case "thievery":
//                    tag = "Thieves";
//                    break;
//                case "world war II":
//                    tag = "World War 2";
//                    break;
//                case "super deformed":
//                    tag = "Chibi";
//                    break;
//                case "super power":
//                    tag = "Superpowers";
//                    break;
//                case "school clubs":
//                    tag = "School Club";
//                    break;
//                case "daily life":
//                    tag = "Slice of Life";
//                    break;
//                case "violent retribution for accidental infringement":
//                    tag = "Violent retribution for accident";
//                    break;
//                case "robots":
//                    tag = "Robot";
//                    break;
//            }

            tag = tag.substring(0, 1).toUpperCase() + tag.substring(1).toLowerCase();

            if (!tagsList.contains(tag))
                tagsList.add(tag);

        }
    }


    private String getJapaneseTitleFromAniDb(Document document) {

        for (Element element : document.getElementsByTag("label")) {

            if (element.parent().children().size() > 0 && element.parent().children().get(0).children().size() > 0) {
                if (element.parent().children().get(0).child(0).attr("title").equals("language: japanese")
                        && element.attr("itemprop").contains("alternateName")) {
                    return element.text();
                }
            }

        }

        return Strings.NULL;
    }


    private String getLinkMAL(Document document) {

        for (Element element : document.getElementsByTag("meta")) {

            if (element.attr("property").equals("og:url")) {
                StringBuilder tmpUrl = new StringBuilder();
                String url = element.attr("content");

                for (int i = 0; i < url.split("/").length; i++) {
                    tmpUrl.append(url.split("/")[i] += "/");

                    if (i > 3)
                        break;
                }

                return tmpUrl.toString();
            }
        }

        return Strings.NULL;
    }


    private String getLinkAniDb(Document document) {

        for (Element element : document.getElementsByTag("meta")) {
            if (element.attr("id").equals("anidb-url"))
                try {
                    return "https://anidb.net/perl-bin/animedb.pl?show=anime&aid=" + element.attr("data-anidb-url").split("aid=")[1];
                } catch (Exception e){
                    return Strings.NULL;
                }
        }

        return Strings.NULL;
    }

    private String getPremieredDateFromMAL(Document document) {

        if (!document.getElementsByClass("season").text().equals(""))
            return document.getElementsByClass("season").text();

        for (Element element : document.getElementsByClass("js-scrollfix-bottom")) {
            for (Element sidebarElement : element.select("div.spaceit")) {
                if (sidebarElement.text().contains("Aired:"))
                    return fixDateMAL(sidebarElement.text());

            }
        }
        return Strings.NULL;
    }

    private String fixDateMAL(String date) {

        date = date.replace("Aired: ", "");
        //removing end date
        date = date.split(" to ")[0];
        //getting the year
        String year = date.split(", ")[1];
        //getting the month
        String month = date.split(" ")[0];
        //converting month to season
        if (month.equals("Mar") || month.equals("Apr") || month.equals("May"))
            month = "Spring";
        else if (month.equals("Jun") || month.equals("Jul") || month.equals("Aug"))
            month = "Summer";
        else if (month.equals("Sep") || month.equals("Oct") || month.equals("Nov"))
            month = "Fall";
        else
            month = "Winter";

        return month + " " + year;
    }

    private String getPlotFromMAL(Document document) {

        for (Element element : document.getElementsByTag("meta")) {
            if (element.attr("property").equals("og:description")) {
                return element.attr("content").replaceAll(" \\[Written.*\\]", "").replaceAll(" \\(Source.*\\)", "");
            }
        }
        return Strings.NULL;
    }


    private String getStudioFromMal(Document document) {
        return document.getElementsByClass("author").text();
    }


    private String getRatingFromMAL(Document document) {

        if (!document.select("div.fl-l.score").isEmpty() && document.select("div.fl-l.score").get(0).text() != null)
            return document.select("div.fl-l.score").get(0).text();
        else if (document.select("span[itemprop = ratingValue]") != null && document.select("span[itemprop = ratingValue]").text() != null)
            return document.select("span[itemprop = ratingValue]").text();

        return Strings.NULL;
    }


    private String getAgeRatingFromMAL(Document document) {

        for (Element element : document.getElementsByClass("dark_text")) {
            if (element.text().contains("Rating:"))
                return element.parent().text().replace("Rating: ", "");
        }

        return Strings.NULL;
    }


    private ArrayList<ShowRecommendation> getRecommendationsFromMAL(Document document, ShowInfo show) {

        ArrayList<ShowRecommendation> recommendations = new ArrayList<>();

        for (Element element : document.getElementsByClass("btn-anime")) {

            if (recommendations.size() < 3) {

                String title = element.attr("title");

                if (title.equals(""))
                    continue;

                String[] showIdUrl = show.getMalLink().split("/");
                String showId = showIdUrl[showIdUrl.length - 2];

                String[] recommendedShowIdUrl = element.getElementsByTag("a").attr("href").split("/");
                String[] recommendedShowId = recommendedShowIdUrl[recommendedShowIdUrl.length - 1].split("-");

                if (recommendedShowId.length < 2)
                    continue;

                String link = Strings.WEBSITE_MAL_ANIME_URL;

                if (recommendedShowId[0].equals(showId))
                    link += recommendedShowId[1];
                else
                    link += recommendedShowId[0];

                recommendations.add(new ShowRecommendation(title, Strings.NULL, link, false));
            } else {
                break;
            }
        }

        return recommendations;
    }


    private String getTitleFromMal(Document document) {

        for (Element element : document.getElementsByTag("meta")) {
            if (element.attr("property").equals("og:title"))
                return element.attr("content");
        }

        return Strings.NULL;
    }


    private String getJapaneseTitleFromMal(Document document) {

        for (Element element : document.getElementsByClass("dark_text")) {
            if (element.text().contains("Japanese:"))
                return element.parent().text().replace("Japanese: ", "");
        }

        return Strings.NULL;
    }



    private static boolean containsCaseInsensitive(String strToCompare, ArrayList<String> list) {
        for (String str : list) {
            if (str.equalsIgnoreCase(strToCompare)) {
                return (true);
            }
        }
        return (false);
    }


    private String getLinkAnimePlanet(Document document) {

        for (Element element : document.getElementsByTag("meta")) {
            if (element.attr("property").equals("og:url"))
                return element.attr("content");
        }
        return Strings.NULL;
    }

    private String getStudioFromAnimePlanet(Document document) {

        for (Element link : document.select("a[href]")) {
            if (link.toString().contains("/anime/studios/")) {
                return link.text();
            }
        }

        return Strings.NULL;
    }

    private String getPlotFromAnimePlanet(Document document) {
        return document.select("meta[property=og:description]").attr("content");
    }

    private ArrayList<String> getTagsFromAnimePlanet(Document document) {

        ArrayList<String> tagsList = new ArrayList<>();
        Elements tagElements = document.getElementsByClass("categories");

        if (tagElements != null)
            tagElements = document.getElementsByClass("tags");

        Elements tags = tagElements.select("ul li");

        for (Element tagElement : tags) {
            String tag = tagElement.text();
            addTag(tagsList, tag);
        }

        return tagsList;
    }

    private String getRatingFromAnimePlanet(Document document) {

        if (document.getElementsByClass("avgRating") != null)
            return document.getElementsByClass("avgRating").select("[itemprop = ratingValue]").get(0).attr("content");

        return Strings.NULL;

    }

    private String getAirDateFromAnimePlanet(Document document) {

        Elements avgRatingElement = document.getElementsByClass("iconYear");

        if (avgRatingElement != null)
            return avgRatingElement.get(0).select("a[href]").text();

        return Strings.NULL;

    }


}
