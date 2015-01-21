package com.RAFA.applocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.applocation.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationFragment extends Fragment{
	private static final int MAX_DISTANCE_RSSI = 55;
	
	
	private static String currentLocation = new String();
	private static List<Space> spaces;
	private static TextView currentLocationView;
	private static TextView beacon1;
	private static TextView beacon2;
	private static TextView beacon3;
	private static TextView beacon4;
	private static Spinner spinner;
	private static MainActivity mActivity;
	private static View rootView;
	private static View actionBarView;
	private static Beacon B1;
	private static Beacon B2;
	private static Beacon B3;
	private static Beacon B4;
	private static int currentAlgorithm;
	private static ArrayAdapter<String> adapter;
	private static List<String> algorithmList = new ArrayList<String>();
	// To keep trying to determine location
	private final Handler handler = new Handler();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_location, container, false);
		
		((MainActivity)getActivity()).getActionBar().setDisplayShowCustomEnabled(true);
		
		LayoutInflater actionInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionBarView = actionInflater.inflate(R.layout.location_action_bar, null);
		currentLocationView = (TextView) rootView.findViewById(R.id.currentLocation);
		beacon1 = (TextView) rootView.findViewById(R.id.beacon1);
		beacon2 = (TextView) rootView.findViewById(R.id.beacon2);
		beacon3 = (TextView) rootView.findViewById(R.id.beacon3);
		beacon4 = (TextView) rootView.findViewById(R.id.beacon4);
		spinner = (Spinner)  actionBarView.findViewById(R.id.algorithm_spinner);
		
		B1 = new Beacon("30:85:a9:d6:14:d6", 100); //Asus
		B2 = new Beacon("00:18:4d:8f:94:d2", 100); //By door
		B3 = new Beacon("00:0e:c6:00:31:c5", 100); //Devkit
		B4 = new Beacon("00:90:4c:7e:00:6e", 100); //Under Desk
		mActivity = (MainActivity) getActivity();
		
		algorithmList.add("Max Distance");
		algorithmList.add("Beacon Neighbours");
		adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item, algorithmList);
		spinner.setAdapter(adapter);
		mActivity.getActionBar().setCustomView(actionBarView);
		adapter.notifyDataSetChanged();
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> av, View v, int index, long arg3)
			{
				currentAlgorithm = index;
				Toast.makeText(mActivity, "Current Algorithm: "+spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> av)
			{
				// Do Nothing
			}
			
		});
		
		spaces = mActivity.getSpaces();
		if (spaces.size() == 0) {
			currentLocation = "No Spaces Defined";
			currentLocationView.setText(currentLocation);
		}
		currentAlgorithm = 0;
		determineLocation();
		return rootView;
	}
	
	private void determineLocation()
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				spaces = mActivity.getSpaces();
				
				if (spaces.size() != 0) {
					boolean inZone = false;
					switch (currentAlgorithm)
					{
					case 0:
						inZone = maxDistance();
						break;
					case 1:
						inZone = beaconNeighbours();
						break;
					default:
						inZone = false;
					}
					if (inZone) {
						switch (currentAlgorithm)
						{
						case 0:
							currentLocation = "In Space";
							break;
						case 1:
							List<Space> mainSpaces = ((MainActivity)getActivity()).getSpaces();
							currentLocation = "In "+mainSpaces.get(mainSpaces.size()-1).getName();
							break;
						default:
							currentLocation = "Error";
						}
						rootView.setBackgroundColor(0xFF00FF00); //Green
					} else {
						currentLocation = "Not in space";
						rootView.setBackgroundColor(0xFFFF0000); //Red
					}
				} else {
					currentLocation = "No Spaces Defined";
				}
				currentLocationView.setText(currentLocation);
				determineLocation();
			}
		}, 3000);
	}
	
	private boolean maxDistance()
	{
		List<Map<String, String>> netList = mActivity.getNetworkList();
		List<Integer> levelList = new ArrayList<Integer>();
		// For demo and debug purposes only!
		
		Map<String, String> currentNetwork;

		for (int i = 0; i < netList.size(); i++) {
			currentNetwork = netList.get(i);
			if (currentNetwork.containsValue(B1.getBSSID())) {
				B1.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B2.getBSSID())) {
				B2.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B3.getBSSID())) {
				B3.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B4.getBSSID())) {
				B4.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
		}
		
		// Check if device is too far from any of the beacons
		// TODO: Add filter!
		boolean inZone = false;
		
		if (B1.validChange()) {
			levelList.add(B1.getLocation());
		}
		if (B2.validChange()) {
			levelList.add(B2.getLocation());
		}
		if (B3.validChange()) {
			levelList.add(B3.getLocation());
		}
		if (B4.validChange()) {
			levelList.add(B4.getLocation());
		}
		
		for (int i = 0; i < levelList.size(); i++) {
			inZone = levelList.get(i) <= MAX_DISTANCE_RSSI;
			if (!inZone) {
				break;
			}
		}
		
		beacon1.setText("B1: "+B1.getLocation()+" p:"+B1.getPreviousLocation()+" v?:"+B1.validChange());
		beacon2.setText("B2: "+B2.getLocation()+" p:"+B2.getPreviousLocation()+" v?:"+B2.validChange());
		beacon3.setText("B3: "+B3.getLocation()+" p:"+B3.getPreviousLocation()+" v?:"+B3.validChange());
		beacon4.setText("B4: "+B4.getLocation()+" p:"+B4.getPreviousLocation()+" v?:"+B4.validChange());
		
		return inZone;
	}
	
	private boolean beaconNeighbours()
	{
		List<Map<String, String>> netList = mActivity.getNetworkList();
		List<Integer> levelList = new ArrayList<Integer>();
		// For demo and debug purposes only!
		
		Map<String, String> currentNetwork;

		for (int i = 0; i < netList.size(); i++) {
			currentNetwork = netList.get(i);
			if (currentNetwork.containsValue(B1.getBSSID())) {
				B1.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B2.getBSSID())) {
				B2.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B3.getBSSID())) {
				B3.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
			else if (currentNetwork.containsValue(B4.getBSSID())) {
				B4.setLocation(Integer.parseInt(currentNetwork.get("level")) * -1);
			}
		}
		
		// Check if device is too far from any of the beacons
		// TODO: Add filter!
		boolean inZone = false;
		
		if (B1.validChange()) {
			levelList.add(B1.getLocation());
		}
		if (B2.validChange()) {
			levelList.add(B2.getLocation());
		}
		if (B3.validChange()) {
			levelList.add(B3.getLocation());
		}
		if (B4.validChange()) {
			levelList.add(B4.getLocation());
		}
		
		for (int i = 0; i < levelList.size(); i++) {
			inZone = levelList.get(i) <= MAX_DISTANCE_RSSI;
			if (!inZone) {
				break;
			}
		}
		
		beacon1.setText("B1: "+B1.getLocation()+" p:"+B1.getPreviousLocation()+" v?:"+B1.validChange());
		beacon2.setText("B2: "+B2.getLocation()+" p:"+B2.getPreviousLocation()+" v?:"+B2.validChange());
		beacon3.setText("B3: "+B3.getLocation()+" p:"+B3.getPreviousLocation()+" v?:"+B3.validChange());
		beacon4.setText("B4: "+B4.getLocation()+" p:"+B4.getPreviousLocation()+" v?:"+B4.validChange());
		
		return inZone;
	}
	private class Beacon
	{
		private static final int MAX_DEVIATION = 6;
		
		private String BSSID;
		private int RSSILocation;
		private int previousRSSI;
		private List<Beacon> neighbours = new ArrayList<Beacon>();

		public Beacon(String newName, int newLocation)
		{
			BSSID = newName;
			RSSILocation = newLocation;
			previousRSSI = newLocation;
		}
		public void setLocation(int newLocation)
		{
			if (RSSILocation != newLocation) {
				previousRSSI = RSSILocation;
				RSSILocation = newLocation;
			}
		}
		/*public void setBSSID(String newName)
		{
			BSSID = newName;
		}*/
		public String getBSSID()
		{
			return BSSID;
		}
		public int getLocation()
		{
			return RSSILocation;
		}
		public int getPreviousLocation()
		{
			return previousRSSI;
		}
		public boolean validChange()
		{
			return Math.abs(RSSILocation - previousRSSI) <= MAX_DEVIATION;
		}
	}
}
