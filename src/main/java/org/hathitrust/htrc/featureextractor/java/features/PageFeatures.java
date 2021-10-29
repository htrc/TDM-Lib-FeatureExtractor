package org.hathitrust.htrc.featureextractor.java.features;

public class PageFeatures implements BasicFeatures {
    protected final String version;
    protected final String language;
    protected final int tokenCount;
    protected final int lineCount;
    protected final int emptyLineCount;
    protected final Integer sentenceCount;
    protected final SectionFeatures header;
    protected final SectionFeatures body;
    protected final SectionFeatures footer;

    /**
     * Object recording aggregate features at the page level
     *
     * @param version        The MD5 hash of the page content
     * @param language       The identified page language (if any)
     * @param tokenCount     The total token count for the page
     * @param lineCount      The total line count for the page
     * @param emptyLineCount The empty line count for the page
     * @param sentenceCount  The sentence count for the page
     * @param header         The page header features
     * @param body           The page body features
     * @param footer         The page footer features
     */
    public PageFeatures(String version, String language, int tokenCount, int lineCount,
                        int emptyLineCount, Integer sentenceCount,
                        SectionFeatures header,
                        SectionFeatures body,
                        SectionFeatures footer) {
        this.version = version;
        this.language = language;
        this.tokenCount = tokenCount;
        this.lineCount = lineCount;
        this.emptyLineCount = emptyLineCount;
        this.sentenceCount = sentenceCount;
        this.header = header;
        this.body = body;
        this.footer = footer;
    }

    public String getVersion() {
        return version;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int getTokenCount() {
        return tokenCount;
    }

    @Override
    public int getLineCount() {
        return lineCount;
    }

    @Override
    public int getEmptyLineCount() {
        return emptyLineCount;
    }

    @Override
    public Integer getSentenceCount() {
        return sentenceCount;
    }

    public SectionFeatures getHeader() {
        return header;
    }

    public SectionFeatures getBody() {
        return body;
    }

    public SectionFeatures getFooter() {
        return footer;
    }
}
