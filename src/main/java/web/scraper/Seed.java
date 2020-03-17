package web.scraper;

public class Seed {
    private String sourceUrl;
    private String newUrl;

    public Seed (String s, String n) {
        this.sourceUrl = s;
        this.newUrl = n;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public String getNewUrl() {
        return newUrl;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", sourceUrl, newUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Seed)) {
            return false;
        }

        Seed s = (Seed) o;

        // Only need to compare the new url as this is to check whether should the new url be
        // added to the crawler's queue
        return newUrl.equals(s.newUrl);
    }
}