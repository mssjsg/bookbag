package github.io.mssjsg.bookbag.util.linkpreview;

/**
 * Callback that is invoked with before and after the loading of a link preview
 * 
 */
public interface LinkPreviewCallback {
	/**
	 * 
	 * @param sourceContent
	 *            Class with all contents from preview.
	 * @param isNull
	 *            Indicates if the content is null.
	 */
	void onPreviewLoaded(SourceContent sourceContent, boolean isNull);
}
