package device.device.aes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import device.device.aes.R;


public class CustomGridAdapter extends BaseAdapter {
	
	private Context context; 
	
    private ControllerApplication aController;
	
	
	public CustomGridAdapter(Context context, ControllerApplication aController) {
		this.context = context;
		this.aController = aController;
	}
	
	@Override
	public int getCount() {
		
	
		return aController.getUserDataSize();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}
	
	
  
	
	public View getView(int position, View convertView, ViewGroup parent) {

	
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			
			gridView = inflater.inflate(R.layout.grid_item, null);

			UserData userdataObj = aController.getUserData(position);
			
		
			
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label);
			textView.setText(userdataObj.getName());

			
		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}
}
