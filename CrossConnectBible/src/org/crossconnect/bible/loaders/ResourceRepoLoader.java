package org.crossconnect.bible.loaders;


import java.util.List;

import org.crossconnect.bible.model.ResourceRepository;
import org.crossconnect.bible.service.CrossConnectServerService;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;


    /**
     * A custom Loader that loads all of the installed applications.
     */
    public class ResourceRepoLoader extends AsyncTaskLoader<List<ResourceRepository>> {

        List<ResourceRepository> resourceRepos;
        

        public ResourceRepoLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<ResourceRepository> loadInBackground() {
			Log.d(this.getClass().getName(), "Load in Background");

            //resourceService.doSomething();
            return new CrossConnectServerService().getResourceRepos();
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<ResourceRepository> books) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (books != null) {
                    onReleaseResources(books);
                }
            }
            List<ResourceRepository> oldApps = books;
            resourceRepos = books;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(books);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (resourceRepos != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(resourceRepos);
            } else {
            	forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<ResourceRepository> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (resourceRepos != null) {
                onReleaseResources(resourceRepos);
                resourceRepos = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<ResourceRepository> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }
