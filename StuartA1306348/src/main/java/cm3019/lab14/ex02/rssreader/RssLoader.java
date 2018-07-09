package cm3019.lab14.ex02.rssreader;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by David on 12/03/2016.
 */
public class RssLoader extends AbstractLoader<List<RssItem>> {
    private String mRssSource;
    private String mSelection;

    private Date mDateSelection;

    public RssLoader(Context context, String rssSource, String selection, Date dateSelection) {
        super(context);
        mRssSource = rssSource;
        mSelection = selection;

        mDateSelection = dateSelection;
    }

    @Override
    protected List<RssItem> readDataSource() {
        // I won't implement any searching through the retrieved Rss items just yet
        RssReader rssReader = new RssReader(mRssSource, mSelection,mDateSelection);
        return rssReader.getItems();
    }
}
