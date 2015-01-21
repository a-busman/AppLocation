package com.RAFA.applocation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.applocation.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SettingsFragment extends Fragment{
	private static SimpleAdapter simpleAdpt;
	private static ListView lv;
	private static List<Map<String, String>> settingsList = new ArrayList<Map<String, String>>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		lv = (ListView) rootView.findViewById(R.id.settingsList);
		
		simpleAdpt = new SimpleAdapter(getActivity(), settingsList,
				android.R.layout.simple_list_item_1,
				new String[]{"name"},
				new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parentAdapter, View view,
									int position, long id)
			{
				deleteConfirm();
				return;
			}
		});
		if (settingsList.size() == 0) {
			settingsList.add(createNewListElement("Delete spaces"));
		}
		simpleAdpt.notifyDataSetChanged();
		return rootView;
	}
	
	private HashMap<String, String> createNewListElement(String title)
	{
		HashMap<String, String> nameHash = new HashMap<String, String>();
    	nameHash.put("name", title);
    	return nameHash;
	}
	
	private void deleteConfirm()
	{
		ConfirmDeleteDialogFragment d = new ConfirmDeleteDialogFragment();
		d.show(getActivity().getSupportFragmentManager(), "ConfirmDeleteDialogFragment");
	}
	
	public static class ConfirmDeleteDialogFragment extends DialogFragment {
  
    	@Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.delete)
            .setMessage("Are you sure you want to delete all spaces?")
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                	   public void onClick(DialogInterface dialog, int id)
                	   {
                		   File settingsFile = new File(getActivity().getFilesDir()+"spaces.dat");
                		   settingsFile.delete();
                		   ((MainActivity)getActivity()).setSpaces(new ArrayList<Space>());
                		// User pressed Save

                		   
                	   }
                   })
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
