package web.scraper;

class Data {
    private String sourceUrl;
    private String newUrl;
    private String document;

    public Data(String s, String n, String d) {
        this.sourceUrl = s;
        this.newUrl = n;
        this.document = d;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public String getNewUrl() {
        return this.newUrl;
    }

    public String getDocument() {
        return this.document;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", sourceUrl, newUrl, document);
    }
}