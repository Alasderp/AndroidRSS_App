package cm3019.lab14.ex02;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alasdairp on 19/04/2016.
 */
public class MyArrayAdapter extends ArrayAdapter<RSSFeed> {

    private final List<RSSFeed> list;
    private final Activity context;

    public MyArrayAdapter(Activity context, List<RSSFeed> list) {
        super(context, R.layout.list_item, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView mcode;
        protected TextView mdesc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.mcode = (TextView) view.findViewById(R.id.code);
            viewHolder.mdesc = (TextView) view.findViewById(R.id.desc);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mcode.setText(list.get(position).getCode());
        holder.mdesc.setText(list.get(position).getDescription());
        return view;
    }

}
