package com.crossconnect.views;

import android.app.ListActivity;
import android.os.Bundle;

public class ResourceRepositoryActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resource_respositary_view);
        // Tell the list view which view to display when the list is empty
        getListView().setEmptyView(findViewById(R.id.empty));
    }

}
