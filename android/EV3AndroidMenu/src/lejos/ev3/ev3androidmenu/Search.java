package lejos.ev3.ev3androidmenu;

import lejos.ev3.ev3androidmenu.R;
import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;
import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity {
	
    private int selectedRow = -1;
    private BrickInfo[] bricks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Allow network access in main thread, until I decide on a better solution
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		bricks = BrickFinder.discover();
		
		if (bricks.length == 0)
			Toast.makeText(getApplicationContext(), "No bricks found", Toast.LENGTH_LONG).show();

        try {
        	TableLayout tl = (TableLayout) getLayoutInflater().inflate(R.layout.search_layout, null, false);
	        
	        refreshTable(tl, getApplicationContext());
			
			setContentView(tl);
        } catch (Exception e) {
        	Toast.makeText(getApplicationContext(), "Failed to refresh table: " + e, Toast.LENGTH_LONG).show();
        }
	}
	
	private void refreshTable(TableLayout ll, final Context context) {
		final TableRow[] rows = new TableRow[bricks.length];
		
	    for (int i = 0; i <bricks.length; i++) {
	        final TableRow row= new TableRow(context);
	        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
	        rows[i] = row;
	        row.setBackgroundResource(drawable.screen_background_light_transparent);
	        row.setLayoutParams(lp);
	        row.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	for(int i=0;i<rows.length;i++) {
	            		rows[i].setBackgroundResource(drawable.screen_background_light_transparent);
	            		if (row == rows[i]) selectedRow = i;
	            	}
	            	row.setBackgroundResource(drawable.screen_background_dark_transparent);
	            	try {
	            		Intent intent = new Intent(Search.this.getApplicationContext(), MainActivity.class);
	            		Bundle b = new Bundle();
	            		b.putString("address", bricks[selectedRow].getIPAddress());
	            		intent.putExtras(b);
	            		startActivity(intent);
	            		finish();
	            	} catch (Exception e) {
	            		Toast.makeText(getApplicationContext(), "Failed to start main activity: " + e, Toast.LENGTH_LONG).show();
	            	}
	             }   
	        });
	        TextView tv1 = new TextView(context);
	        tv1.setText(bricks[i].getName());
	        row.addView(tv1);
	        TextView tv2 = new TextView(context);
	        tv2.setText(bricks[i].getIPAddress());
	        row.addView(tv2);
	        ll.addView(row);
	    }			
	}
	
}
