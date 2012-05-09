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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.crossconnect.bible.actions.R;

public class WelcomeActivity extends ListActivity{

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

        
        // Tell the list view which view to display when the list is empty
        getListView().setEmptyView(findViewById(R.id.empty));
        
//        BibleDataHelper.installJSwordErrorReportListener();
        
        forceBasicFlow = SwordDocumentFacade.getInstance().getBibles().size()==0;
        
        installService = new DownloadManager();

			try {
				Log.e("BookManager", "Getting avialable books");
				List<Book > availableBooks= installService.getDownloadableBooks(SUPPORTED_DOCUMENT_TYPES, CROSSWIRE_REPOSITORY, false);
			List<Book> installedBooks = SwordDocumentFacade.getInstance().getDocuments();
			allBooks = new ArrayList<Book>(installedBooks);
			allBooks.addAll(availableBooks);

			bibles = new ArrayList<Book>();
			
			for (Book book : allBooks) {
				if(BookCategory.BIBLE.equals(book.getBookCategory())){
					bibles.add(book);
				}
	        }
			
			
			
			
			Collections.sort(bibles);
			
        List<String> bibleNames = new ArrayList<String>();
        for (Book book : bibles) {
        	bibleNames.add(book.getInitials());
        }
//        Collections.sort(booksName);
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, bibleNames.toArray(new String[0])));
        getListView().setTextFilterEnabled(true);
        
        final ListView listView = getListView();
        
        listView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
//				Log.e("BookManager", "Click");
//				Intent i = new Intent ();
//				i.putExtra("Translation", ((TextView) view).getText());
//				setResult(RESULT_OK, i);
//				finish();				

				
				Log.d("Installer", "Installing " + bibles.get(position).getInitials());
				doDownload(bibles.get(position));
				
//				try {
//				crossWireInstaller.install(availableBooks.get(position));
//			} catch (InstallException e) {
//				Log.e("BookManager", "Install Book", e);
//			}

			}
        	
        });
        
			} catch (InstallException e) {
				Log.e("BookManager", "Get avialable books", e);
			}



	}
	
	private void doDownload(Book document) {

    	try {
    		Log.e("DoDownload", "Starting Download");
    		// the download happens in another thread
    		SwordDocumentFacade.getInstance().downloadDocument(document);
	    	
	    	Intent intent;
			if (forceBasicFlow) {
	    		intent = new Intent(this, DownloadStatus.class);
				//	    		intent = new Intent(this, EnsureBibleDownloaded.class);
//	    		// finish this so when EnsureDalogDownload finishes we go straight back to StartupActivity which will start MainBibleActivity
//	    		finish();
	    	} else {
	    		intent = new Intent(this, DownloadStatus.class);
	    	}
        	startActivityForResult(intent, 1);

    	} catch (Exception e) {
    		Log.e("BookManagerActivity", "Error on attempt to download", e);
    	}

    }


}
