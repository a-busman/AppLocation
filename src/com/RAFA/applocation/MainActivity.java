package com.RAFA.applocation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.applocation.R;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private TabsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	// Current networks observed
	private static List<Map<String, String>> network_list = new ArrayList<Map<String, String>>();
	
	private WifiManager main_wifi;
	private WifiReceiver receiver_wifi;
	
	private final Handler handler = new Handler();
	// A user defined space/fence/zone
	private static List<Space> spaces = new ArrayList<Space>();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Get previously saved spaces
		 * Please! Do this here!
		 */
		try {
			Log.d(WIFI_SERVICE, getFilesDir()+"/spaces.dat");
			FileInputStream fis = new FileInputStream(getFilesDir()+"/spaces.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object inputObject = ois.readObject();
			if (inputObject instanceof ArrayList<?>) {
				spaces = (ArrayList<Space>) inputObject;
			}
			ois.close();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
        }
		
		// Set up wifi listener
		main_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		receiver_wifi = new WifiReceiver();
		registerReceiver(receiver_wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		if(main_wifi.isWifiEnabled() == false)
		{
			main_wifi.setWifiEnabled(true);
		}
		
		doInback();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.logo);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(0xFF7DC5FF));  // Light blue
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new TabsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause()
	{
		unregisterReceiver(receiver_wifi);
		
		try {
			Log.d(WIFI_SERVICE, getFilesDir()+"/spaces.dat");
			FileOutputStream fos = new FileOutputStream(getFilesDir()+"/spaces.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(spaces);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		registerReceiver(receiver_wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_settings:
            startActivity(new Intent(this, WifiListActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void doInback()
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				main_wifi.startScan();
				doInback();
			}
		}, 1000);
	}
	
	public class TabsPagerAdapter extends FragmentPagerAdapter
	{
		public TabsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		
		@Override
		public Fragment getItem(int index)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Don't pass along any arguments for now
			switch (index)
			{
			case 0:
				return new SpacesFragment();
			case 1:
				return new LocationFragment();
			case 2:
				return new SettingsFragment();
			}
			return null;
		}
		
		@Override
		public int getCount()
		{
			// There are 3 pages so far
			return 3;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	public List<Space> getSpaces()
	{
		return spaces;
	}
	
	public void setSpaces(List<Space> newSpaces)
	{
		spaces = newSpaces;
	}
	
	public List<Map<String, String>> getNetworkList()
	{
		return network_list;
	}
	
	class WifiReceiver extends BroadcastReceiver
	{
		private HashMap<String, String> createNetwork(String BSSID, int level)
		{
			HashMap<String, String> network = new HashMap<String, String>();
			network.put("BSSID", BSSID);
			network.put("level", String.format("%d", level));
			
			return network;
		}
		public void onReceive(Context c, Intent intent)
		{
			String BSSID = new String();
			int level = 0;
			List<ScanResult> wifi_list;
			
			wifi_list = main_wifi.getScanResults();
			String currentBSSID = main_wifi.getConnectionInfo().getBSSID();
			network_list.clear();
			for (int i = 0; i < wifi_list.size(); i++)
			{
				BSSID = wifi_list.get(i).BSSID;
				level = wifi_list.get(i).level;
				
				// Don't include currently connected WiFi, as Android just reports constant strength
				// PLUS: we don't want to connect to our nodes anyway, so just ignore it.
				
				if (!BSSID.equals(currentBSSID)) {
					network_list.add(createNetwork(BSSID, level));
				}

			}
		}
	}
}
