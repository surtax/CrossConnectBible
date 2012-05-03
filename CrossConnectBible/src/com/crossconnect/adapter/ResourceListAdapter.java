package com.crossconnect.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crossconnect.actions.R;
import com.crossconnect.model.OnlineAudioResource;

    public class ResourceListAdapter extends ArrayAdapter<OnlineAudioResource> {
        private final LayoutInflater mInflater;

        public ResourceListAdapter(Context context) {
            super(context, R.layout.list_item_icon_text);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<OnlineAudioResource> data) {
            clear();
            //AddAll is not supported till 3.x
            if(data != null) {
                for (OnlineAudioResource book : data) {
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
                view = mInflater.inflate(R.layout.list_item_icon_text_two_line, parent, false);
            } else {
                view = convertView;
            }

            OnlineAudioResource item = getItem(position);
            if (item.getAudioURL() != null) {
            	((ImageView)view.findViewById(R.id.icon)).setImageResource(R.drawable.icon_orb_rss);
            } else {
            	((ImageView)view.findViewById(R.id.icon)).setImageResource(R.drawable.icon_file_document);
            }
            ((TextView)view.findViewById(R.id.text_line_1)).setText(item.getResourceName());
            ((TextView)view.findViewById(R.id.text_line_2)).setText(item.getResourceVerse());


            return view;
        }
    }

