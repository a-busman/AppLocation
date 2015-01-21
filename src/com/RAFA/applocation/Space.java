package com.RAFA.applocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Space implements Serializable{
	/**
	 * Serializable to allow for saving to output stream in a file
	 */
	private static final long serialVersionUID = 1L;
	String spaceName = new String();
	List<List<Map<String, String>>> networkLists = new ArrayList<List<Map<String, String>>>();
	List<Beacon> beacons = new ArrayList<Beacon>();
	List<Map<String, String>> points= new ArrayList<Map<String, String>>();
	public Space()
	{
		
	}
	public Space(String newName, List<List<Map<String, String>>> newNetworkLists){
		spaceName = newName;
		networkLists = newNetworkLists;
		for (int i = 0; i < networkLists.size(); i++) {
			List<Map<String, String>> currentList = networkLists.get(i);
			Map<String, String> strongestNetwork = new HashMap<String, String>();
			for (int j = 0; j < currentList.size(); j++) {
				Map<String, String> currentNetwork = currentList.get(j);
				if (j == 0) {
					strongestNetwork = currentNetwork;
				} else {
					if (Integer.parseInt(currentNetwork.get("level")) > Integer.parseInt(strongestNetwork.get("level"))) {
						strongestNetwork = currentNetwork;
					}
				}
			}
			points.add(strongestNetwork);
		}
		return;
	}
	public String getName()
	{
		return spaceName;
	}
	
	public void setName(String newName)
	{
		spaceName = newName;
	}
	
	public List<List<Map<String, String>>> getNetworkLists()
	{
		return networkLists;
	}
}
