package com.crossconnect.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.crossconnect.model.ResourceRepository;
import com.crossconnect.actions.R;

public class ResourceRepoListAdapter extends ArrayAdapter<ResourceRepository> {
    private final LayoutInflater mInflater;

    public ResourceRepoListAdapter(Context context) {
        super(context, R.layout.list_item_icon_text_follow);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<ResourceRepository> data) {
        clear();
        //AddAll is not supported till 3.x
        if(data != null) {
            for (ResourceRepository book : data) {
                if (book != null) {
                    add(book);
                }
            }
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        //Convertview will not be null if already drawn so no need to redraw
        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item_icon_text_follow, parent, false);
        } else {
            view = convertView;
        }

        ResourceRepository item = getItem(position);
//        ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
        ((TextView)view.findViewById(R.id.text)).setText(item.getChurchName());

        return view;
    }
}
