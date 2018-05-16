package com.markm.simplenotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.markm.simplenotes.R;

public class EditPage extends Activity{
	
	private DBHandler dataBase;
	private Intent intent;
	private EditText subj;
	private EditText ed;
	private String itemId;
	
	private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_page);
		
		try {
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId("ca-app-pub-7088636357536669/9549757830");
			AdRequest adRequest = new AdRequest.Builder().build();
			interstitial.loadAd(adRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		dataBase = new DBHandler(EditPage.this);
		
		intent = getIntent();
		subj = (EditText)findViewById(R.id.editTextSubject);
		ed = (EditText)findViewById(R.id.editTextContents);
		
		String subj1 = intent.getStringExtra("subject");
		String ed1 = intent.getStringExtra("contents");
		itemId = String.valueOf(intent.getIntExtra("itemId", -1));
		
		subj.setText(subj1);
		ed.setText(ed1);
		ed.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 6 || s.length() == 100) {
					try {
						displayInterstitial();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		ImageButton cancelbtn = (ImageButton)findViewById(R.id.cancelbtn);
		cancelbtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		
		ImageButton savebtn = (ImageButton)findViewById(R.id.savebtn);
		savebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		
		ImageButton deletebtn = (ImageButton)findViewById(R.id.deletebtn);
			if (intent.getBooleanExtra("add", false)) {
				deletebtn.setVisibility(View.GONE);
		} else {
			deletebtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContextThemeWrapper ctx = new ContextThemeWrapper(EditPage.this, R.style.Theme_Base);
					AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
					.setTitle(R.string.dialog_delete_question)
					.setMessage(subj.getText().toString())
					.setIcon(android.R.drawable.ic_menu_delete)
					.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dataBase.deleteNote(itemId);
							Toast.makeText(EditPage.this, R.string.deleteItemToast, Toast.LENGTH_SHORT).show();
							finish();
						}
					})
					.setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			});
		}	
			
			ImageButton sharebtn = (ImageButton)findViewById(R.id.sharebtn);
			sharebtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent share_intent = new Intent(Intent.ACTION_SEND);
					share_intent.setType("text/plain");
					share_intent.putExtra(Intent.EXTRA_SUBJECT, subj.getText().toString() + "\n");
					share_intent.putExtra(Intent.EXTRA_TEXT, ed.getText().toString());
					startActivity(share_intent);
				}
			});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editoptionsmenu, menu);
		setTheme(R.style.Theme_Base);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.saveMenuItem:
			save();
			break;
		case R.id.cancelMenuItem:
			cancel();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void save () {
		String subj1 = subj.getText().toString();
		String cont1 = ed.getText().toString();
		if (intent.getBooleanExtra("add", false)) {
			if (subj1.isEmpty()) {
				Toast.makeText(EditPage.this, R.string.subjectEmptyWarning, Toast.LENGTH_LONG).show();
				subj.requestFocus();
			} else {
				dataBase.addNote(subj1, cont1);
				Toast.makeText(EditPage.this, R.string.noteSavedToast, Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			if (subj1.isEmpty()) {
				Toast.makeText(EditPage.this, R.string.subjectEmptyWarning, Toast.LENGTH_LONG).show();
				subj.requestFocus();
			} else {
				dataBase.updateNote(itemId, subj1, cont1);
				Toast.makeText(EditPage.this, R.string.noteSavedToast, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		Toast.makeText(EditPage.this, R.string.noteSavedToast, Toast.LENGTH_SHORT).show();
	}
	
	private void cancel () {
		if (!subj.getText().toString().equals(intent.getStringExtra("subject")) || !ed.getText().toString().equals(intent.getStringExtra("contents"))) {
			
			ContextThemeWrapper ctx = new ContextThemeWrapper(EditPage.this, R.style.Theme_Base);
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
			.setMessage(R.string.dialog_note_not_saved)
			.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					save();
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setNeutralButton(R.string.dialog_dont_save, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.create().show();
		} else {
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		cancel();
	}
	
	public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }
}
