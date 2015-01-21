package com.RAFA.applocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.applocation.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class NetworkDetail extends Activity{
	List<Map<String, String>> BSSID_list = new ArrayList<Map<String, String>>();
	SimpleAdapter simpleAdpt;
	
	WifiManager main_wifi;
	DetailedWifiReceiver receiver_wifi;
	
	private final Handler handler = new Handler();
	
	Intent intent;
	String SSID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		intent = getIntent();
		SSID = intent.getExtras().getString("SSID");
		
		getActionBar().setTitle(SSID);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailed_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
main_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		receiver_wifi = new DetailedWifiReceiver();
		registerReceiver(receiver_wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		if(main_wifi.isWifiEnabled() == false)
		{
			main_wifi.setWifiEnabled(true);
		}
		
		doInback();
		
		ListView lv = (ListView) findViewById(R.id.detailedList);
		
		registerForContextMenu(lv);
		simpleAdpt = new SimpleAdapter(this, BSSID_list,
				 android.R.layout.simple_list_item_1, 
				 new String[]{"network"}, 
				 new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
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
	
	private HashMap<String, String> createNetwork(String key, String name)
	{
		HashMap<String, String> network = new HashMap<String, String>();
		network.put(key, name);
		
		return network;
	}
	
	@Override
	protected void onPause()
	{
		unregisterReceiver(receiver_wifi);
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		registerReceiver(receiver_wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case android.R.id.home: 
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
	class DetailedWifiReceiver extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{
			
			String BSSID = new String();
			int strength;
			String capabilities = new String();
			String scannedSSID = new String();
			int frequency;
			
			List<ScanResult> wifi_list;
			wifi_list = main_wifi.getScanResults();

			BSSID_list.clear();
			for (int i = 0; i < wifi_list.size(); i++)
			{
				scannedSSID = wifi_list.get(i).SSID;
				BSSID = wifi_list.get(i).BSSID;
				strength = wifi_list.get(i).level;
				capabilities = wifi_list.get(i).capabilities;
				frequency = wifi_list.get(i).frequency;

				if (scannedSSID.matches(SSID) == true)
				{
					BSSID_list.add(createNetwork("network", String.format("%17s %35ddB\n%s\n%dMHz\nChannel: %d",
							                                              BSSID, strength, 
							                                              capabilities, frequency,
							                                              ((frequency % 2412) / 5) + 1)));
				}
			}
			simpleAdpt.notifyDataSetChanged();
		}
	}
	
	
}
