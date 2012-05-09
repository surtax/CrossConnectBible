package org.crossconnect.bible.activity.main;

/*
 * Copyright 2011 Gary Lo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.crossconnect.bible.adapter.ResourceRepoListAdapter;
import org.crossconnect.bible.loaders.ResourceRepoLoader;
import org.crossconnect.bible.model.ResourceRepository;

import net.londatiga.qahorizontal.ActionItem;
import net.londatiga.qahorizontal.QuickActionHorizontal;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.crossconnect.bible.actions.R;

public class ResourceRepoFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<ResourceRepository>> {

	private static final String TAG = "ResourceFragment";

	ResourceRepoLoader resourceListLoader;

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {

		final int position = pos;

		// Get the corresponding resource
		final ResourceRepository resource = mAdapter.getItem(position);

		final ActionItem playAction = new ActionItem();

		playAction.setTitle("Play");
		playAction.setIcon(getResources().getDrawable(R.drawable.kontak));

		final ActionItem downloadAction = new ActionItem();

		downloadAction.setTitle("Download");
		downloadAction.setIcon(getResources().getDrawable(R.drawable.kontak));

		final ActionItem readAction = new ActionItem();

		readAction.setTitle("Read");
		readAction.setIcon(getResources().getDrawable(R.drawable.kontak));

		final QuickActionHorizontal mQuickAction = new QuickActionHorizontal(v);

		final String text = "blah";

//		// Check the links are actually there
//		if (resource.getAudioURL() != null) {
//			downloadAction.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					final String RESOURCE_FOLDER = "/CrossConnectAudio/Resources/";
//					Toast.makeText(getActivity(),
//							"Downloading Audio File " + text,
//							Toast.LENGTH_SHORT).show();
//					Log.d(TAG, "Downloading Audio "
//							+ mAdapter.getItem(position).getResourceName()
//							+ "from "
//							+ mAdapter.getItem(position).getAudioURL());
//
//					Request request = new Request(Uri.parse(resource
//							.getAudioURL()));
//					File path = new File(
//							Environment
//									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
//									+ RESOURCE_FOLDER);
//					path.mkdir();
//
//					String filePath = Environment
//							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
//							+ RESOURCE_FOLDER
//							+ FileUtil.getFileName(mAdapter.getItem(position),
//									bibleText);
//
//					resourceService.insertUpdate(mAdapter.getItem(position),
//							filePath, bibleText);
//
//					request.setDestinationInExternalPublicDir(
//							Environment.DIRECTORY_PODCASTS,
//							RESOURCE_FOLDER
//									+ FileUtil.getFileName(
//											mAdapter.getItem(position),
//											bibleText));
//
//					((DownloadManager) getActivity().getSystemService(
//							"download")).enqueue(request);
//
//					mQuickAction.dismiss();
//				}
//			});
//			mQuickAction.addActionItem(downloadAction);
//
//			playAction.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(getActivity(),
//							"Streaming Audio File " + text, Toast.LENGTH_SHORT)
//							.show();
//					Log.d(TAG, "Streaming Audio"
//							+ mAdapter.getItem(position).getResourceName()
//							+ "from "
//							+ mAdapter.getItem(position).getAudioURL());
//					try {
//						// the download happens in another thread
//						Intent i = new Intent(MusicService.ACTION_URL);
//						Uri uri = Uri.parse(mAdapter.getItem(position)
//								.getAudioURL());
//						i.setData(uri);
//						getActivity().startService(i);
//
//					} catch (Exception e) {
//						Log.e("BookManagerActivity",
//								"Error on attempt to download", e);
//					}
//
//					mQuickAction.dismiss();
//				}
//			});
//
//			mQuickAction.addActionItem(playAction);
//		}
//
//		if (resource.getReadURL() != null) {
//			readAction.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(getActivity(), "Read " + text,
//							Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(getActivity(),
//							ArticleActivity.class);
//					if (resource.getReadURL() != null) {
//						intent.putExtra("url", resource.getReadURL());
//						startActivity(intent);
//					}
//
//					mQuickAction.dismiss();
//				}
//			});
//			mQuickAction.addActionItem(readAction);
//		}
//
//		mQuickAction.setAnimStyle(QuickActionVertical.ANIM_AUTO);
//
//		mQuickAction.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//			}
//		});
//
//		mQuickAction.show();
	}

	// This is the Adapter being used to display the list's data.
	ResourceRepoListAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListShown(false);

		// // Give some text to display if there is no data. In a real
		// // application this would come from a resource.
		setEmptyText("Sorry No Online Resources Were Found");

		getLoaderManager().initLoader(0, null, this);
		
		// Create an empty adapter we will use to display the loaded data.
		mAdapter = new ResourceRepoListAdapter(getActivity());
		setListAdapter(mAdapter);

	}

	@Override
	public Loader<List<ResourceRepository>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.
		Log.i(TAG, "onCreateLoader()");
		// Start with progress indicator

		resourceListLoader = new ResourceRepoLoader(getActivity());
		return resourceListLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<ResourceRepository>> loader,
			List<ResourceRepository> data) {
		Log.i(TAG, "onLoadFinished");
		// Set the new data in the adapter.
		mAdapter.setData(data);

		setListShown(true);

	}

	@Override
	public void onLoaderReset(Loader<List<ResourceRepository>> arg0) {
		// Clear the data in the adapter.
		mAdapter.setData(null);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}