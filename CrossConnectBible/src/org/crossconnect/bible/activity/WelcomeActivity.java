package org.crossconnect.bible.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sword.engine.sword.AcceptableBookTypeFilter;
import net.sword.engine.sword.SwordDocumentFacade;

import org.crossconnect.bible.service.DownloadManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.install.InstallException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.crossconnect.bible.R;

public class WelcomeActivity extends FragmentActivity{

	DownloadManager installService;
	
	private static final String CROSSWIRE_REPOSITORY = "CrossWire";
	
	private static BookFilter SUPPORTED_DOCUMENT_TYPES = new AcceptableBookTypeFilter();
	
	boolean forceBasicFlow;
	

    List<Book> allBooks;
    
    List<Book> bibles;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);
        
        if (Build.VERSION.SDK_INT >= 11) {//HOneycomb
    		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
    		StrictMode.setThreadPolicy(policy);
        }

        forceBasicFlow = SwordDocumentFacade.getInstance().getBibles().size()==0;

	}

}
