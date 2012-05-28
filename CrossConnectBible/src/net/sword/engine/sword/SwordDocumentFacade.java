package net.sword.engine.sword;

import java.io.File;
import java.util.List;

import org.crossconnect.bible.service.DownloadManager;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.WebResource;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.sword.SwordBookPath;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.crosswire.jsword.index.IndexStatus;

import android.util.Log;


/** JSword facade
 * 
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's author.
 */
public class SwordDocumentFacade {
	private static SwordDocumentFacade singleton;

	private static final String LUCENE_DIR = "lucene";
	
	private static final String CROSSWIRE_REPOSITORY = "CrossWire";
			
	private static final String DATA_DIRECTORY =  "/mnt/sdcard/Android/data/org.crossconnect/files";
	
//	private static BookFilter SUPPORTED_DOCUMENT_TYPES = new AcceptableBookTypeFilter();

	private static boolean isSwordLoaded;
		
	private static final String TAG = "SwordDocumentFacade";
//    private static final Logger log = new Logger(SwordDocumentFacade.class.getName()); 

	public static SwordDocumentFacade getInstance() {
		if (singleton==null) {
			synchronized(SwordDocumentFacade.class)  {
				if (singleton==null) {
					SwordDocumentFacade instance = new SwordDocumentFacade();
					instance.initialise();
					singleton = instance;
				}
			}
		}
		return singleton;
	}

	private SwordDocumentFacade() {
	}
	
	private void initialise() {
		try {
			// ensure required module directories exist and register them with jsword
			File moduleDir = new File(DATA_DIRECTORY);

			// main module dir
			ensureDirExists(moduleDir);
			// mods.d
			ensureDirExists(new File(moduleDir, SwordConstants.DIR_CONF));
			// modules
			ensureDirExists(new File(moduleDir, SwordConstants.DIR_DATA));
			// indexes
			ensureDirExists(new File(moduleDir, LUCENE_DIR));
			//fonts
			ensureDirExists(new File(DATA_DIRECTORY + "/fonts"));

			// the second value below is the one which is used in effectively all cirumstances
	        CWProject.setHome("jsword.home", moduleDir.getAbsolutePath(), new File("/mnt/sdcard/jsword").getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	        // the following causes Sword to initialise itself and can take quite a few seconds
			SwordBookPath.setAugmentPath(new File[] {new File("/mnt/sdcard/jsword")});  // add manual install dir to this list
			
			// 10 sec is too low, 15 may do but put it at 20 secs
			WebResource.setTimeout(20000);
			
			// because the above line causes initialisation set the is initialised flag here
			isSwordLoaded = true;
			
//				log.debug(("Sword paths:"+getPaths()));
		} catch (Exception e) {
			Log.e(TAG, "Initialisation failed", e);
		}
	}

	public void reset() {
		singleton = null;
		isSwordLoaded = false;
	}
	
	public List<Book> getBibles() {
//		log.debug("Getting bibles");
		List<Book> documents = Books.installed().getBooks(BookFilters.getBibles());
//		log.debug("Got bibles, Num="+documents.size());
		isSwordLoaded = true;
		return documents;
	}

	public List<Book> getBooks(final BookCategory bookCategory) {
//		log.debug("Getting commentaries");
		List<Book> documents = Books.installed().getBooks(new BookFilter() {

			public boolean test(Book book) {
		            return book.getBookCategory().equals(bookCategory) && !book.isLocked();
			}
		});
//		log.debug("Got books, Num="+documents.size());
		isSwordLoaded = true;
		return documents;
	}

	public List<Book> getDictionaries() {
//		log.debug("Getting dictionaries");
		List<Book> documents = Books.installed().getBooks(BookFilters.getDictionaries());
//		log.debug("Got dictionaries, Num="+documents.size());
		isSwordLoaded = true;
		return documents;
	}

	/** return all supported documents - bibles and commentaries for now
	 * 
	 * @return
	 */
	public List<Book> getDocuments() {
//		log.debug("Getting books");
		// currently only bibles and commentaries are supported
		List<Book> allDocuments = Books.installed().getBooks(new AcceptableBookTypeFilter());
		
//		log.debug("Got books, Num="+allDocuments.size());
		isSwordLoaded = true;
		return allDocuments;
	}

	public Book getDefaultBibleWithStrongs() {
		List<Book> bibles = getBibles();
		for (Book book : bibles) {
			if (book.hasFeature(FeatureType.STRONGS_NUMBERS)) {
				if (book.getIndexStatus().equals(IndexStatus.DONE)) {
					return book;
				}
			}
		}
		return null;
	}
	
	public Book getDocumentByInitials(String initials) {

		return Books.installed().getBook(initials);
	}
	
//	public List<Book> getDownloadableDocuments(boolean refresh) throws InstallException {
//		log.debug("Getting downloadable documents");
//
//		//CrossWire
//		DownloadManager crossWireDownloadManager = new DownloadManager();
//        List<Book> crosswireBookList = crossWireDownloadManager.getDownloadableBooks(SUPPORTED_DOCUMENT_TYPES, CROSSWIRE_REPOSITORY, refresh);
//
//        List<Book> allBooks = new ArrayList<Book>(crosswireBookList);
//        
//		XiphosRepo xiphosRepo = new XiphosRepo();
//        allBooks.addAll(xiphosRepo.getXiphosRepoBooks());
//
//		BetaRepo betaRepo = new BetaRepo();
//        allBooks.addAll(betaRepo.getRepoBooks(refresh));
//
//        // get them in the correct order
//        Collections.sort(allBooks);
//
//		return allBooks;	
//	}

//	public void downloadDocument(Book document) throws InstallException, BookException {
//		DownloadManager downloadManager = new DownloadManager();
//		String repo = (String)document.getProperty(DownloadManager.REPOSITORY_KEY);
//		if (repo==null) {
//			repo = CROSSWIRE_REPOSITORY;
//		}
//		downloadManager.installBook(repo, document);
//	}
//
//	public void downloadFont(String font) throws InstallException {
//		log.debug("Download font "+font);
//		URI source = null;
//		try {
//			source = new URI("http://www.crosswire.org/and-bible/fonts/v1/"+font);
//		} catch (URISyntaxException use) {
//    		Log.e(TAG, "Invalid URI", use);
//    		throw new InstallException("Error downloading font");
//		}
//		File target = new File(SharedConstants.FONT_DIR, font);
//		
//		GenericFileDownloader downloader = new GenericFileDownloader();
//		downloader.downloadFileInBackground(source, target);
//	}
//
//	public boolean isIndexDownloadAvailable(Book document) throws InstallException, BookException {
//		// not sure how to integrate reuse this in JSword index download
//		Version versionObj = (Version)document.getBookMetaData().getProperty("Version");
//        String version = versionObj==null ? null : versionObj.toString();
//        String versionSuffix = version!=null ? "-"+version : "";
//
//		String url = "http://www.crosswire.org/and-bible/indices/v1/"+document.getInitials()+versionSuffix+".zip";
//		return CommonUtils.isHttpUrlAvailable(url);
//	}
//	public void downloadIndex(Book document) throws InstallException, BookException {
//		DownloadManager downloadManager = new DownloadManager();
//		downloadManager.installIndex(CROSSWIRE_REPOSITORY, document);
//	}
//	
//	public void deleteDocument(Book document) throws BookException {
//		// delete index first if it exists but wrap in try to ensure an attempt is made to delete the document
//		try {
//	        IndexManager imanager = IndexManagerFactory.getIndexManager();
//	        if (imanager.isIndexed(document)) {
//	            imanager.deleteIndex(document);
//	        }
//		} catch (Exception e) {
//			// just log index delete error, deleting doc is the important thing
//			log.error("Error deleting document index", e);
//		}
//
//        document.getDriver().delete(document);
//	}
//	
//	public void deleteDocumentIndex(Book document) throws BookException {
//        IndexManager imanager = IndexManagerFactory.getIndexManager();
//        if (imanager.isIndexed(document)) {
//            imanager.deleteIndex(document);
//        }
//	}
//	
//
//	/** this custom index creation has been optimised for slow, low memory devices
//	 * If an index is in progress then nothing will happen
//	 * 
//	 * @param book
//	 * @throws BookException
//	 */
//	public void ensureIndexCreation(Book book) throws BookException {
//    	log.debug("ensureIndexCreation");
//
//    	// ensure this isn't just the user re-clicking the Index button
//		if (!book.getIndexStatus().equals(IndexStatus.CREATING) && !book.getIndexStatus().equals(IndexStatus.SCHEDULED)) {
//
//			PdaLuceneIndexManager lim = new PdaLuceneIndexManager();
//	        lim.scheduleIndexCreation(book);
//		}
//	}
	
	private String getPaths() {
		String text = "Paths:";
		try {
			// SwordBookPath.setAugmentPath(new File[] {new
			// File("/data/bible")});
			File[] swordBookPaths = SwordBookPath.getSwordPath();
			for (File file : swordBookPaths) {
				text += file.getAbsolutePath();
			}
			text += "Augmented paths:";
			File[] augBookPaths = SwordBookPath.getAugmentPath();
			for (File file : augBookPaths) {
				text += file.getAbsolutePath();
			}
		} catch (Exception e) {
			text += e.getMessage();
		}
		return text;
	}

	private void ensureDirExists(File dir) {
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
	}

	/** needs to be static because otherwise the constructor triggers initialisation
	 * 
	 * @return
	 */
	static public boolean isSwordLoaded() {
		return isSwordLoaded;
	}

	public void downloadDocument(Book document) {
			DownloadManager downloadManager = new DownloadManager();
			String repo = (String)document.getProperty(DownloadManager.REPOSITORY_KEY);
			if (repo==null) {
				repo = CROSSWIRE_REPOSITORY;
			}
			try {
				downloadManager.installBook(repo, document);
			} catch (BookException e) {
				Log.e(TAG, "downloadDOcumentException", e);
			} catch (InstallException e) {
				Log.e(TAG, "downloadDOcumentException", e);
			}
		}

}
