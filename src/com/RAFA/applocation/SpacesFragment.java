package com.RAFA.applocation;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SpacesFragment extends Fragment
{
	// Local spaces storage
	private static List<Space> spaces = new ArrayList<Space>();
	private static List<Map<String, String>> spaceNames = new ArrayList<Map<String, String>>();
	private static ListView lv;
	private static SimpleAdapter simpleAdpt;
	
	private static boolean spacesDefined = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		View rootView = inflater.inflate(R.layout.fragment_spaces, container, false);
		lv = (ListView) rootView.findViewById(R.id.settingsList);
		spaces = ((MainActivity)getActivity()).getSpaces();
		spaceNames.clear();
		for (int i = 0; i < spaces.size(); i++) {
			spaceNames.add(createSpaceName(spaces.get(i).getName()));
			spacesDefined = true;
		}
		if (!spacesDefined) {
			spaceNames.add(createSpaceName("No Spaces Defined"));
		}
		registerForContextMenu(lv);
		simpleAdpt = new SimpleAdapter(getActivity(), spaceNames,
					android.R.layout.simple_list_item_1,
					new String[]{"name"},
					new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parentAdapter, View view,
									int position, long id)
			{
				if (spacesDefined) {
					Space thisSpace = spaces.get(position);
					Toast.makeText(getActivity(), thisSpace.getName()+": "+thisSpace.getNetworkLists().size(), Toast.LENGTH_SHORT).show();
				}
				return;
			}
		});
		
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
										int position, long id)
			{
				if (spacesDefined) {
					editSpace(position);
				}
				return true;
			}
		});
		
		simpleAdpt.notifyDataSetChanged();
		setHasOptionsMenu(true);
		return rootView;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.space_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // handle item selection
       switch (item.getItemId()) {
          case R.id.action_settings:
             return true;
          case R.id.newSpace:
        	  newSpace();
        	  break;
          default:
             return super.onOptionsItemSelected(item);
       }
       return true;
    }  
    
    private void newSpace() {
    	NewSpaceDialogFragment mDialog = new NewSpaceDialogFragment();
    	mDialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }
    
    private void editSpace(int position) {
    	EditSpaceDialogFragment mDialog = new EditSpaceDialogFragment();
    	
    	Bundle args = new Bundle();
    	args.putString("name", spaces.get(position).getName());
    	args.putInt("position", position);
    	mDialog.setArguments(args);
    	mDialog.show(getActivity().getSupportFragmentManager(), "EditDialogFragment");
    }
    
    private static HashMap<String, String> createSpaceName(String name) {
    	HashMap<String, String> nameHash = new HashMap<String, String>();
    	nameHash.put("name", name);
    	return nameHash;
    }
    
    public static class EditSpaceDialogFragment extends DialogFragment {
    	private String spaceName;
    	private int position;
    	
    	private View dialogView;
    	private EditText newSpaceName;
    
    	@Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            dialogView = inflater.inflate(R.layout.edit_space_dialog, null);
            
            spaceName = getArguments().getString("name");
            position = getArguments().getInt("position");
            newSpaceName = (EditText) dialogView.findViewById(R.id.editSpaceName);
            
            newSpaceName.setText(spaceName);
            TextWatcher textWatcher = new TextWatcher() {
            	@Override
            	public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            	{
            		if (charSequence.length() > 0) {
            			AlertDialog d = (AlertDialog) getDialog();
            			if (d != null) {
            				d.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
            			}
            		} else {
            			AlertDialog d = (AlertDialog) getDialog();
            			if (d != null) {
            				d.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            			}
            		}
            	}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}
            };

            newSpaceName.addTextChangedListener(textWatcher);

            builder.setView(dialogView);
            builder.setPositiveButton(R.string.save_space, new DialogInterface.OnClickListener() {
                	   public void onClick(DialogInterface dialog, int id)
                	   {
                		// User pressed Save
                		   spaces.get(position).setName(newSpaceName.getText().toString());
                		   ((MainActivity)getActivity()).setSpaces(spaces);
                		   if (!spacesDefined)
                		   {
                			   spaceNames.clear();
                			   spacesDefined = true;
                		   }
                		   spaceNames.get(position).clear();
                		   spaceNames.get(position).put("name", newSpaceName.getText().toString());
                		   simpleAdpt.notifyDataSetChanged();
                		   
                	   }
                   })
                   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   })
                   .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						spaces.remove(position);
						((MainActivity)getActivity()).setSpaces(spaces);
						spaceNames.remove(position);
						if (spaceNames.size() == 0)
						{
							spacesDefined = false;
							spaceNames.add(createSpaceName("No Spaces Defined"));
						}
						simpleAdpt.notifyDataSetChanged();
					}
				});
            // Create the AlertDialog object and return it
            return builder.create();
        }
        @Override
        public void onStart()
        {
        	super.onStart();
        	AlertDialog d = (AlertDialog) getDialog();
        	if (d != null)
        	{
        		Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
        		positiveButton.setEnabled(false);
        	}
        }
    }
	
    public static class NewSpaceDialogFragment extends DialogFragment {
    	private int pointCount = 0;
    	private List<List<Map<String, String>>> newNetworkList = new ArrayList<List<Map<String, String>>>();
    	private boolean textNotEmpty = false;
    	
    	private View dialogView;
    	private EditText newSpaceName;
    	
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            dialogView = inflater.inflate(R.layout.new_space_dialog, null);
            
            Button newPointButton = (Button) dialogView.findViewById(R.id.addPoint);
            
            newSpaceName = (EditText) dialogView.findViewById(R.id.newSpaceName);
            TextWatcher textWatcher = new TextWatcher() {
            	@Override
            	public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            	{
            		if (charSequence.length() > 0)
            		{
            			textNotEmpty = true;
            			if (pointCount > 2){
                			AlertDialog d = (AlertDialog) getDialog();
                			if (d != null)
                			{
                				d.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                			}
                		}
            		}
            		else
            		{
            			textNotEmpty = false;
            			AlertDialog d = (AlertDialog) getDialog();
            			if (d != null)
            			{
            				d.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
            			}
            		}
            	}

				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}
            };

            newSpaceName.addTextChangedListener(textWatcher);
            newPointButton.setOnClickListener(new View.OnClickListener() {
            	@Override
            	public void onClick(View view)
            	{
            		AlertDialog d = (AlertDialog) getDialog();

            		pointCount++;
            		newNetworkList.add(((MainActivity)getActivity()).getNetworkList());
            		if (pointCount > 2 && textNotEmpty)
            		{
            			if (d != null)
            			{
            				d.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
            			}
            		}
            	}
            });
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.save_space, new DialogInterface.OnClickListener() {
                	   public void onClick(DialogInterface dialog, int id)
                	   {
                		// User pressed Save
                		   Space addSpace = new Space(newSpaceName.getText().toString(), newNetworkList);
                		   spaces.add(addSpace);
                		   ((MainActivity)getActivity()).setSpaces(spaces);
                		   if (!spacesDefined)
                		   {
                			   spaceNames.clear();
                			   spacesDefined = true;
                		   }
                		   spaceNames.add(createSpaceName(newSpaceName.getText().toString()));
                		   simpleAdpt.notifyDataSetChanged();
                		   
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
        @Override
        public void onStart()
        {
        	super.onStart();
        	AlertDialog d = (AlertDialog) getDialog();
        	if (d != null)
        	{
        		Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
        		positiveButton.setEnabled(false);
        	}
        }
    }
}
