package github.io.mssjsg.bookbag.util.linkpreview;

import android.support.annotation.WorkerThread;
import android.support.v4.util.LruCache;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by sing on 3/13/17.
 */
@Singleton
public class UrlPreviewManager {

    private LruCache<String, Item> mCache = new LruCache<>(100);
    private WebPageCrawler mWebPageCrawler;

    @Inject
    public UrlPreviewManager(WebPageCrawler webPageCrawler) {
        mWebPageCrawler = webPageCrawler;
    }

    public void put(String link, UrlPreviewManager.Item item) {
        mCache.put(link, item);
    }

    @WorkerThread
    public Item get(String url) throws LinkPreviewException {

        if (mCache.get(url) == null) {
            SourceContent sourceContent = mWebPageCrawler.makePreview(url);

            String previewUrl = "";
            if (sourceContent.getImages().size() > 0) {
                previewUrl = sourceContent.getImages().get(0);
            }
            String title = sourceContent.getTitle();

            UrlPreviewManager.Item item = new UrlPreviewManager.Item(title, previewUrl);
            mCache.put(url, item);
        }

        return mCache.get(url);
    }

    public static class Item {
        public final String title;
        public final String previewUrl;

        public Item(String title, String previewUrl) {
            this.title = title;
            this.previewUrl = previewUrl;
        }
    }
}
