package cm3019.lab14.ex02;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import cm3019.lab14.ex02.rssreader.RssItem;

/**
 * Class implements a list listener.
 */
public class ListListener implements OnItemClickListener {
    // And a reference to a calling activity
    // Calling activity reference
    Activity mParent;
    /** We will set those references in our constructor.*/
    public ListListener(Activity parent) {
        mParent  = parent;
    }
 
    /** Start a browser with url from the rss item.*/
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // We create an Intent which is going to display data
        Intent i = new Intent(Intent.ACTION_VIEW);
        // We have to set data for our new Intent;
        i.setData(Uri.parse(((RssItem)(parent.getItemAtPosition(pos))).getLink()));
        // And start activity with our Intent
        mParent.startActivity(i);
    }
}
