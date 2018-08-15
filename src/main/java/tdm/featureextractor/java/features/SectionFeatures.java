package tdm.featureextractor.java.features;

import java.util.Map;

public class SectionFeatures implements BasicFeatures {
    protected final int tokenCount;
    protected final int lineCount;
    protected final int emptyLineCount;
    protected final Integer sentenceCount;
    protected final int capAlphaSeq;
    protected final Map<String, Integer> beginCharCount;
    protected final Map<String, Integer> endCharCount;
    protected final Map<String, Map<String, Integer>> tokenPosCount;

    /**
     * Object representing the set of features recorded for a page section
     *
     * @param tokenCount     The total token count for the section
     * @param lineCount      The total line count for the section
     * @param emptyLineCount The empty line count for the section
     * @param sentenceCount  The sentence count for the section
     * @param capAlphaSeq    The longest sequence of lines that has the first character capitalized in
     *                       alphabetic order
     * @param beginCharCount The counts of characters occurring at the beginning of each line
     * @param endCharCount   The counts of characters occurring at the end of each line
     * @param tokenPosCount  The token counts for each token on the page, for each part-of-speech
     *                       tag it represents
     */
    public SectionFeatures(int tokenCount, int lineCount, int emptyLineCount,
                           Integer sentenceCount, int capAlphaSeq,
                           Map<String, Integer> beginCharCount,
                           Map<String, Integer> endCharCount,
                           Map<String, Map<String, Integer>> tokenPosCount) {
        this.tokenCount = tokenCount;
        this.lineCount = lineCount;
        this.emptyLineCount = emptyLineCount;
        this.sentenceCount = sentenceCount;
        this.capAlphaSeq = capAlphaSeq;
        this.beginCharCount = beginCharCount;
        this.endCharCount = endCharCount;
        this.tokenPosCount = tokenPosCount;
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

    public int getCapAlphaSeq() {
        return capAlphaSeq;
    }

    public Map<String, Integer> getBeginCharCount() {
        return beginCharCount;
    }

    public Map<String, Integer> getEndCharCount() {
        return endCharCount;
    }

    public Map<String, Map<String, Integer>> getTokenPosCount() {
        return tokenPosCount;
    }
}
