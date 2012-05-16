package com.dnd.aac;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MotionEvent;

public class MainActivity extends FragmentActivity implements OnItemClickListener, OnInitListener {
	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech myTTS;
	
	private GestureDetector mGestureDetector;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        
        initEditText(); //attach listeners etc     
    }   

    
    public void saySomething(View view)  
    {  
    	 EditText enteredText = (EditText)findViewById(R.id.enter);
         String words = enteredText.getText().toString();
         myTTS.speak(words, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
            	myTTS = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }    
    }

	@Override
	public void onInit(int initStatus) {
	    if (initStatus == TextToSpeech.SUCCESS) {
	        myTTS.setLanguage(Locale.US);
	    }
	    else if (initStatus == TextToSpeech.ERROR) {
	        Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
	    }

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//
	}
	
	private void initEditText()
	{
		final EditText enteredText = (EditText)findViewById(R.id.enter);
		enteredText.setFocusable(false); //Cannot click in textbox

		final Drawable x = getResources().getDrawable(android.R.drawable.presence_offline);//your x image, this one from standard android images looks pretty good actually
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		enteredText.setCompoundDrawables(null, null, null, null);

		mGestureDetector = new GestureDetector(new SimpleOnGestureListener()
		{
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				enteredText.setText("");
				enteredText.setCompoundDrawables(null, null, null, null);
				return false;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {         	
				String txt = enteredText.getText().toString();   
				//Replace last alphanumeric word regardless of leading or trailing white space
				txt = txt.replaceFirst("\\s*\\w+\\s*$", "");
				if(TextUtils.isEmpty(txt)) enteredText.setCompoundDrawables(null, null, null, null);;
				enteredText.setText(txt);
				return false;
			}
		});

		enteredText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (enteredText.getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_DOWN 
						&& event.getAction() !=  MotionEvent.ACTION_UP) {
					return false;
				}

				if (event.getX() > enteredText.getWidth() - enteredText.getPaddingRight() - x.getIntrinsicWidth()) { 
					mGestureDetector.onTouchEvent(event);     
				}
				return false;
			}
		});
		enteredText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				enteredText.setCompoundDrawables(null, null, enteredText.getText().toString().equals("") ? null : x, null);
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}
	/*public void onDestroy(){
		myTTS.shutdown();		
	}*/
	
}