package web.scraper;

class Data {
    private String sourceUrl;
    private String newUrl;
    private String document;
    private boolean isNewUrlDead;

    public Data(String sourceUrl, String newUrl, String document) {
        this(sourceUrl, newUrl, document, false);
    }

    // Used when the newUrl is dead
    public Data(String sourceUrl, String newUrl) {
        this(sourceUrl, newUrl, "", true);
    }

    private Data(String sourceUrl, String newUrl, String document, boolean isNewUrlDead) {
        this.sourceUrl = sourceUrl.replace("\n", "\\n").replace("\r", "\\r");
        this.newUrl = newUrl.replace("\n", "\\n").replace("\r", "\\r");
        this.document = document;
        this.isNewUrlDead = isNewUrlDead;
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

    /**
     * @return true if the new url is dead (e.g. 404 not found)
     */
    public boolean isNewUrlDead() {
        return isNewUrlDead;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", sourceUrl, newUrl, document);
    }
}