package vaccaro.andrew.rssreader;

/**
 * Class for holding RSS item's url, photo, and headline.
 */
public class RSSEntry {
    String url;
    String photoURL;
    String headline;

    public RSSEntry() {
    }

    public RSSEntry(String headline, String url, String photoUrl) {
        this.url = url;
        this.photoURL = photoUrl;
        this.headline = headline;
    }

    public String getHeadline(){
        return headline;
    }

    public String getPhotoURL(){
        return photoURL;
    }

    public String getUrl(){
        return url;
    }

}