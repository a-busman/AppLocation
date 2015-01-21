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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WifiListActivity extends Activity {
	List<Map<String, String>> network_list = new ArrayList<Map<String, String>>();
	SimpleAdapter simpleAdpt;
	
	WifiManager main_wifi;
	WifiReceiver receiver_wifi;
	
	private final Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_list);
		getActionBar().setTitle(R.string.wifi_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		main_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		receiver_wifi = new WifiReceiver();
		registerReceiver(receiver_wifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		if(main_wifi.isWifiEnabled() == false)
		{
			main_wifi.setWifiEnabled(true);
		}
		
		doInback();
		
		ListView lv = (ListView) findViewById(R.id.listView);
		/*final Button rescan = (Button) findViewById(R.id.rescan_button);
		
		rescan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(WifiListActivity.this, "Refreshing List", Toast.LENGTH_SHORT).show();
				main_wifi.startScan();
				
				return;
			}
		});*/
		
		registerForContextMenu(lv);
		simpleAdpt = new SimpleAdapter(this, network_list,
				 android.R.layout.simple_list_item_1, 
				 new String[]{"network"}, 
				 new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parentAdapter, View view,
									int position, long id)
			{
				TextView clickedView = (TextView) view;
				Intent i = new Intent(WifiListActivity.this, NetworkDetail.class);
				i.putExtra("SSID", clickedView.getText());
				WifiListActivity.this.startActivity(i);
				Toast.makeText(WifiListActivity.this, "Item with id ["+id+"] - Position ["+position+"] - Network ["+clickedView.getText()+"]", Toast.LENGTH_SHORT).show();
				return;
			}
		});
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
	
	class WifiReceiver extends BroadcastReceiver
	{
		public void onReceive(Context c, Intent intent)
		{

			String SSID = new String();
			
			List<ScanResult> wifi_list;
			wifi_list = main_wifi.getScanResults();
			
			boolean in_list = false;

			network_list.clear();
			for (int i = 0; i < wifi_list.size(); i++)
			{
				SSID = wifi_list.get(i).SSID;
				for (int j = 0; j < network_list.size(); j++)
				{
					if (network_list.get(j).containsValue(SSID) == true)
						in_list = true;
				}
				if (in_list == false){
					network_list.add(createNetwork("network", SSID));
				}
				in_list = false;
			}
			simpleAdpt.notifyDataSetChanged();
		}
	}
}
