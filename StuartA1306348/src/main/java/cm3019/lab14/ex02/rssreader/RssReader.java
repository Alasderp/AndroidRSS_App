package cm3019.lab14.ex02.rssreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Class reads RSS data.
 */
public class RssReader {
    // Our class has an attribute which represents RSS Feed URL
    private String rssUrl;
    // A string keyword that must be present in RSSItem title
    // not used in present implementation
    private String searchString;
    private static SAXParserFactory factory;
    private static SAXParser saxParser;

    private Date searchDate;

    static {
        try {
            // At first we need to get an SAX Parser Factory object
            factory = SAXParserFactory.newInstance();
            // Using factory we create a new SAX Parser instance
            saxParser = factory.newSAXParser();
        }
        catch(Exception e){
            Log.e("RssReader", "static initialisation:"+e.getMessage());
        }
    }

    /**
     * We set this URL with the constructor
     */
    public RssReader(String rssUrl, String searchString, Date newDate) {
        this.rssUrl = rssUrl;
        this.searchString = searchString;

        this.searchDate = newDate;

    }
    /**
     * Get RSS items. This method will be called to get the parsing process result.
     * @return
     */
    public List<RssItem> getItems() /*throws Exception*/ {
    	try {
	        // We need the SAX parser handler object
	        RssParseHandler handler = new RssParseHandler(searchString, searchDate);
	        // We call the method parsing our RSS Feed
	        saxParser.parse(rssUrl, handler);
	        // The result of the parsing process is being stored in the handler object
	        return handler.getItems();
    	}
    	catch(Exception e){
    		Log.e("RssReader", "in getItems:"+e.getMessage());
    		return new ArrayList<RssItem>();
    	}
    }
}
