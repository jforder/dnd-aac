/*
 * Copyright (c) 2011, Lauren Darcey and Shane Conder
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following disclaimer.
 *   
 * * Redistributions in binary form must reproduce the above copyright notice, this list 
 *   of conditions and the following disclaimer in the documentation and/or other 
 *   materials provided with the distribution.
 *   
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific prior 
 *   written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * <ORGANIZATION> = Mamlambo
 */
package com.dnd.privacyapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dnd.privacyapp.data.PrivacyAppDatabase;
import com.dnd.privacyapp.data.PrivacyAppProvider;
import com.dnd.privacyapp.service.PrivacyAppDownloaderService;
import com.dnd.privacyapp.R;

public class ReferenceListFragment extends ListFragment  {
	private OnSectSelectedListener sectSelectedListener;
    private ReferenceAdapter adapter;
    private Cursor sections;
    private int currentPosition;
    private long currentID;
    private ImageView currentImg;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] uiBindFrom = { PrivacyAppDatabase.COL_SEC_NAME}; //Add as many columns using ,
        int[] uiBindTo = { R.id.title}; //And you can bind it to as many variables using ,

        String[] projection = { PrivacyAppDatabase.COL_SEC_ID + " as _id", PrivacyAppDatabase.COL_SEC_NAME, "secQuizComplete" };
        sections = getActivity().getContentResolver().query(PrivacyAppProvider.SECTION_URI, projection, null, null, null);
        adapter = new ReferenceAdapter(
                getActivity().getApplicationContext(), R.layout.list_item,
                sections, uiBindFrom, uiBindTo,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        setListAdapter(adapter);
        setHasOptionsMenu(true);
        
        currentPosition = 0;
        currentID = 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	sectSelectedListener = (OnSectSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTutSelectedListener");
        }
    }
    
    @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		adapter.notifyDataSetChanged();

	}
    
    public interface OnSectSelectedListener {
        public void onSectSelected(long id);
    }
 
    // options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);

        // pref menu item
        Intent prefsIntent = new Intent(getActivity().getApplicationContext(),
                ReferenceListPreferencesActivity.class);

        MenuItem preferences = menu.findItem(R.id.settings_option_item);
        preferences.setIntent(prefsIntent);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {           	
    	currentPosition = position;
        currentID = id;
    
    	
    	if(currentImg == null)Log.d("myapp","ITS NULL");
		else Log.d("myapp","ITS NOT NULL");
        
        l.setItemChecked(position, true);
    	sectSelectedListener.onSectSelected(id);      
    	
    	currentPosition = l.getSelectedItemPosition();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings_option_item:
            getActivity().startActivity(item.getIntent());
            break;
        }
        return true;  
    }
}
