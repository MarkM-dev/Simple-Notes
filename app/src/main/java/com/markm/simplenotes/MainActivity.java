package com.markm.simplenotes;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

@SuppressLint("NewApi")
public class MainActivity extends ListActivity {
	
	private InterstitialAd interstitial;

	private DBHandler dataBase;
	private ArrayList<Note> list;
	private ListView lv;
	private ArrayAdapter<Note> adapter;
	private static final int confirmation = 20;
	private int position1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);	
		
		try {
			// Look up the AdView as a resource and load a request.
			final AdView adView = (AdView)this.findViewById(R.id.adView);
			AdRequest adRequestBanner = new AdRequest.Builder().build();
			adView.loadAd(adRequestBanner);

			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId("ca-app-pub-7088636357536669/3636798635");
			AdRequest adRequest = new AdRequest.Builder().build();
			interstitial.loadAd(adRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dataBase = new DBHandler(MainActivity.this);
		list = dataBase.getAllNotes();
		lv = getListView();
		adapter = new ArrayAdapter<Note>(MainActivity.this, R.layout.list_row, list);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				Intent intent = new Intent(MainActivity.this, EditPage.class);
				intent.putExtra("itemId", list.get(position).getId());
				intent.putExtra("position", position);
				intent.putExtra("subject", list.get(position).getSubject().toString());
				intent.putExtra("contents", list.get(position).getContents().toString());
				position1 = position;
				startActivityForResult(intent, 11);			
			}
		});
		
		ImageButton addbtn = (ImageButton)findViewById(R.id.addbtn);
		addbtn.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				Intent intent = new Intent(MainActivity.this, EditPage.class);
				intent.putExtra("add", true);
				startActivityForResult(intent, confirmation);	
			}
		});	
		
		ImageButton settingsbtn = (ImageButton)findViewById(R.id.settingsbtn);
		registerForContextMenu(settingsbtn);
		settingsbtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				openOptionsMenu();
			}
		});
		
		ImageButton adCloseBtn = (ImageButton)findViewById(R.id.Main_adCloseBtn);
		adCloseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout adLayout = (LinearLayout)findViewById(R.id.adlayout);
				adLayout.setVisibility(View.GONE);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		list = dataBase.getAllNotes();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setListPos(i+1);
		}
		lv = getListView();
		adapter = new ArrayAdapter<Note>(MainActivity.this, R.layout.list_row, list);
		lv.setAdapter(adapter);
		
		TextView noitems = (TextView)findViewById(R.id.noitems);
		int siz = list.size();
		if (siz < 1) {
			noitems.setVisibility(1);
		} else {
			noitems.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		setTheme(R.style.Theme_Base);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ContextThemeWrapper ctx = new ContextThemeWrapper(MainActivity.this, R.style.Theme_Base);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		
		switch (item.getItemId()) {
		case R.id.deleteAllItems:
			builder.setTitle(R.string.dialog_delete_all_question)
			.setIcon(android.R.drawable.ic_menu_delete)
			.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dataBase.deleteAll();
					Toast.makeText(MainActivity.this, R.string.deleteAllToast, Toast.LENGTH_SHORT).show();
					onResume();
				}
			})
			.setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		case R.id.more_apps:
			final String developerId = "MarkM";
			   try {
			       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=" + developerId)));
			   } catch (android.content.ActivityNotFoundException anfe) {
			       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=" + developerId)));
			   }
			break;
		case R.id.exit:
			try {
				displayInterstitial();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		setTheme(R.style.Theme_Base);
		
		if (v.getId() == R.id.settingsbtn) {
			getMenuInflater().inflate(R.menu.main, menu);
		} else {
			getMenuInflater().inflate(R.menu.itemsmenu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo(); 
		ContextThemeWrapper ctx = new ContextThemeWrapper(MainActivity.this, R.style.Theme_Base);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		
		switch (item.getItemId()) {
		case R.id.editMenuItem:   
			position1 = (int)info.id;
			Intent intentEdit = new Intent(MainActivity.this, EditPage.class);
			intentEdit.putExtra("position", position1);
			intentEdit.putExtra("itemId", list.get(position1).getId());
			intentEdit.putExtra("subject", list.get(position1).getSubject().toString());
			intentEdit.putExtra("contents", list.get(position1).getContents().toString());
			startActivityForResult(intentEdit, 11);	
			break;
		case R.id.shareMenuItem:
			position1 = (int)info.id;
			Intent share_intent = new Intent(Intent.ACTION_SEND);
			share_intent.setType("text/plain");
			share_intent.putExtra(Intent.EXTRA_SUBJECT, list.get(position1).getSubject().toString() + "\n");
			share_intent.putExtra(Intent.EXTRA_TEXT, list.get(position1).getContents().toString());
			startActivity(share_intent);
			break;
		case R.id.deleteMenuItem:   
			position1 = (int)info.id;
			builder.setTitle(R.string.dialog_delete_question)
			.setMessage(list.get(position1).getSubject().toString())
			.setIcon(android.R.drawable.ic_menu_delete)
			.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dataBase.deleteNote(String.valueOf(list.get(position1).getId()));
		            Toast.makeText(MainActivity.this, R.string.deleteItemToast, Toast.LENGTH_SHORT).show();
		            onResume();
				}
			})
			.setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}
	
	 public void displayInterstitial() {
		    if (interstitial.isLoaded()) {
		      interstitial.show();
		    }
		  }
}
