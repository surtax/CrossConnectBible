package com.crossconnect.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crossconnect.actions.R;
import com.crossconnect.model.ResourceRepository;

public class ResourceRepoListAdapter extends ArrayAdapter<ResourceRepository> {
    private final LayoutInflater mInflater;
    private Context ctx;
    
    public ResourceRepoListAdapter(Context context) {
        super(context, R.layout.list_item_icon_text_follow);
        ctx = context;
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

        final ResourceRepository item = getItem(position);
//        ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
        ((TextView)view.findViewById(R.id.text)).setText(item.getChurchName());
        final ImageView following = ((ImageView)view.findViewById(R.id.following));
    	SharedPreferences settings = ctx.getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE);
        boolean currentFollow = settings.getBoolean(item.getChurchName(), true);

        if (currentFollow) {
        	following.setImageResource(R.drawable.following_selector);
        } else {
        	following.setImageResource(R.drawable.follow_selector);
        }

        
        following.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				if (!item.getChurchName().equals("Coming Soon...")) {
			    	SharedPreferences settings = ctx.getSharedPreferences("APP SETTINGS", Context.MODE_PRIVATE);
			        boolean currentFollow = settings.getBoolean(item.getChurchName(), true);
			        SharedPreferences.Editor editor = settings.edit();
			        
			        //Swap to other value
			        currentFollow = !currentFollow;
			        editor.putBoolean(item.getChurchName(), currentFollow);
	
			        // Commit the edits!
			        editor.commit();
	
			        if (currentFollow) {
			        	following.setImageResource(R.drawable.following_selector);
			        } else {
			        	following.setImageResource(R.drawable.follow_selector);
			        }
				}
			}
        	
        });
        return view;
    }
}
