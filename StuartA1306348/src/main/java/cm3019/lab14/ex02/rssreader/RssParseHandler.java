package cm3019.lab14.ex02.rssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cm3019.lab14.ex02.MainActivity;

/**
 * SAX tag handler. The Class contains a list of RssItems which is being filled while the parser is working
 */
public class RssParseHandler extends DefaultHandler {
 
    // List of items parsed
    private List<RssItem> rssItems;
    // We have a local reference to an object which is constructed while parser is working on an item tag
    // Used to reference item while parsing
    private RssItem currentItem;
    // parsing item indicator
    private boolean parsingItem;
    // We have two indicators which are used to differentiate whether a tag title or link is being processed by the parser
    // Parsing title indicator
    private boolean parsingTitle;
    // Parsing link indicator
    private boolean parsingLink;
    //Parsing date indicator
    private boolean parsingDate;
    //Parsing description indicator
    private boolean parsingDesc;
    // Add characters to title while parsing title
    StringBuilder title;
    // Add characters to link while parsing link
    StringBuilder link;
    // Add characters to date while parsing link
    StringBuilder date;
    // Add characters to desc while parsing link
    StringBuilder desc;
    // Search string used to match as substring in RssItem.title
    private String searchString;

    //Date to filter articles by date
    private Date searchDate;
    private Date itemDate;

    private DateFormat inputFormat;
    private DateFormat outputFormat;
 
    public RssParseHandler(String searchString, Date newDate) {
        rssItems = new ArrayList<RssItem>();
        parsingItem = false;
        parsingTitle = false;
        parsingLink = false;
        parsingDate = false;
        parsingDesc = false;
        this.searchString = searchString.toLowerCase();
        this.searchDate = newDate;
        itemDate = new Date();
        inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        outputFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

    }

    // We have an access method which returns a list of items that are read from the RSS feed. This method will be called when parsing is done.
    public List<RssItem> getItems() {
        return rssItems;
    }

    // The StartElement method creates an empty RssItem object when an item start tag is being processed. When a title or link tag are being processed appropriate indicators are set to true.
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) /*throws SAXException*/ {
    	try {
	        if ("item".equals(qName)) {
	        	parsingItem = true;
	            currentItem = new RssItem();
	        } else if ("title".equals(qName) && parsingItem) {
	            parsingTitle = true;
	            title = new StringBuilder();
	        } else if ("link".equals(qName) && parsingItem) {
	            parsingLink = true;
	            link = new StringBuilder();
	        } else if ("pubDate".equals(qName) && parsingItem) {
                parsingDate = true;
                date = new StringBuilder();
            } else if ("description".equals(qName) && parsingItem) {
                parsingDesc = true;
                desc = new StringBuilder();
            }
    	}
    	catch(Exception e){
    		Log.e("RssParseHandler", "in startElement: "+e.getMessage());
    	}
    }
    // The EndElement method adds the  current RssItem to the list when a closing item tag is processed. It sets appropriate indicators to false -  when title and link closing tags are processed
    @Override
    public void endElement(String uri, String localName, String qName) /*throws SAXException*/ {
    	try {
            if ("item".equals(qName)) {

                String tempDate = currentItem.getDate();
                itemDate = outputFormat.parse(tempDate);

                // apply searchString
                if (searchString.isEmpty() && (itemDate.after(searchDate)  || itemDate.equals(searchDate) )){
                    rssItems.add(currentItem);
                    //Add the RssItem to the database
                    MainActivity.mTaskSource.createEntry(currentItem);
                }
                else {
                    String allLowerCaseTitle = currentItem.getTitle().toLowerCase();
                    if (allLowerCaseTitle.contains(searchString) && (itemDate.after(searchDate) || itemDate.equals(searchDate))) {
                        rssItems.add(currentItem);
                        //Add the RssItem to the database
                        MainActivity.mTaskSource.createEntry(currentItem);
                    }
                }
                currentItem = null;
                parsingItem = false;
	        } else if ("title".equals(qName) && parsingItem) {
	        	// now save the title in current item
	            currentItem.setTitle(title.toString());
	            parsingTitle = false;
	        } else if ("link".equals(qName) && parsingItem) {
	        	// now save link in current item
	        	currentItem.setLink(link.toString());
	            parsingLink = false;
	        } else if ("pubDate".equals(qName) && parsingItem) {

                String tempDate = "";
                itemDate = inputFormat.parse(date.toString());
                tempDate = outputFormat.format(itemDate);

                currentItem.setDate(tempDate);
                parsingDate = false;
            } else if ("description".equals(qName) && parsingItem) {
                currentItem.setDesc(desc.toString());
            }
    	}
    	catch(Exception e){
    		Log.e("RssParseHandler", "in endElement: "+e.getMessage());  	
    	}
    }
    // Characters method adds to title or link
    @Override
    public void characters(char[] ch, int start, int length) /*throws SAXException*/ {
    	try {
	        if (parsingTitle && parsingItem) {
	            title.append(ch, start, length);
	        } else if (parsingLink && parsingItem) {
	            link.append(ch, start, length);
	        }
            else if (parsingDate && parsingItem) {
                date.append(ch, start, length);
            }
            else if (parsingDesc && parsingItem) {
                desc.append(ch, start, length);
            }
    	}
    	catch (Exception e) {
    		Log.e("RssParseHandler", "in characters: "+e.getMessage());
    	}
    }
}