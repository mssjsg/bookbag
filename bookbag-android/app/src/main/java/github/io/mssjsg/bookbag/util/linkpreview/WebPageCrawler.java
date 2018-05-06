package github.io.mssjsg.bookbag.util.linkpreview;

/**
 * Created by sing on 2/26/17.
 */

public interface WebPageCrawler {
    SourceContent makePreview(String url) throws LinkPreviewException;
}
