package cm3019.lab14.ex02;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import cm3019.lab14.ex02.rssreader.RssItem;
/**
 * Created by Alasdairp on 20/04/2016.
 */
public class MainArrayAdapter extends ArrayAdapter<RssItem>{

    private final List<RssItem> mListView;
    private final Activity context;

    public MainArrayAdapter(Activity context, List<RssItem> list) {
        super(context, R.layout.main_list_item, list);
        this.context = context;
        this.mListView = list;
    }

    static class ViewHolder {
        protected TextView newsTitle;
        protected TextView desc;
        protected TextView date;
        protected TextView url;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.main_list_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.desc = (TextView) view.findViewById(R.id.description);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.url = (TextView) view.findViewById(R.id.url);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.newsTitle.setText(mListView.get(position).getTitle());
        holder.date.setText(mListView.get(position).getDate());
        holder.desc.setText(mListView.get(position).getDesc());
        holder.url.setText(mListView.get(position).getLink());

        return view;
    }



}

