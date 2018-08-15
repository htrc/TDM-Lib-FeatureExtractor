package tdm.featureextractor.java.features;

import java.io.Serializable;

/**
 * Interface representing the basic features that will be recorded for a page
 */
public interface BasicFeatures extends Serializable {
    int getTokenCount();
    int getLineCount();
    int getEmptyLineCount();
    Integer getSentenceCount();
}
