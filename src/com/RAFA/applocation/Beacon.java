package com.RAFA.applocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Beacon implements Serializable {
	
	/**
	 * Serializable to allow for saving to output stream in a file
	 */
	private static final long serialVersionUID = 1L;
	String BSSID = "";
	List<Map<String, String>> neighbours = new ArrayList<Map<String, String>>();
	Integer currentStrength = 0;
	
	public Beacon() {
		
	}
	
	public Beacon(String newBSSID) {
		BSSID = newBSSID;
	}
	
	public void addNeighbour(Map<String, String> neighbour) {
		neighbours.add(neighbour);
	}
	
	public void updateStrength(Integer newStrength) {
		currentStrength = newStrength;
	}
}
