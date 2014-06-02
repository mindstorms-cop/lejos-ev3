package org.lejos.android.sample.control;

import java.io.IOException;

import lejos.hardware.Audio;
import lejos.hardware.lcd.TextLCD;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private RemoteRequestEV3 ev3;
	private RegulatedMotor left, right;
	private Button connect;
	private Audio audio;
	private TextLCD lcd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button left = (Button) findViewById(R.id.left);
		Button right = (Button) findViewById(R.id.right);
		Button stop = (Button) findViewById(R.id.stop);
		Button forward = (Button) findViewById(R.id.forward);
		Button backward = (Button) findViewById(R.id.backward);
		connect = (Button) findViewById(R.id.connect);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		stop.setOnClickListener(this);
		forward.setOnClickListener(this);
		backward.setOnClickListener(this);
		connect.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.left) new Control().execute("rotate left");
		else if (v.getId() == R.id.right) new Control().execute("rotate right");
		else if (v.getId() == R.id.stop) new Control().execute("stop");
		else if (v.getId() == R.id.forward) new Control().execute("forward");
		else if (v.getId() == R.id.backward) new Control().execute("backward");
		else if (v.getId() == R.id.connect) {
			if (ev3 == null) {
				new Control().execute("connect","192.168.0.9");
				connect.setText("Disconnect");
			}
			else {
				new Control().execute("disconnect");
				connect.setText("Connect");
			}
		}
	}
	
	private class Control extends AsyncTask<String, Integer, Long> {
		 
		 protected Long doInBackground(String... cmd) {
			 
			 if (cmd[0].equals("connect")) {
				try {
					ev3 = new RemoteRequestEV3(cmd[1]);
					left = ev3.createRegulatedMotor("A", 'L');
					right = ev3.createRegulatedMotor("B", 'L');
					audio = ev3.getAudio();
					lcd = ev3.getTextLCD();
					audio.systemSound(3);
					return 0l;
				} catch (IOException e) {
					return 1l;
				}
			 } else if (cmd[0].equals("disconnect") && ev3 != null) {
				 audio.systemSound(2);
				 left.close();
				 right.close();
				 ev3.disConnect();
				 ev3 = null;
				 return 0l;
			 } 
			 
			 if (ev3 == null) return 2l;
			 
			 ev3.getAudio().systemSound(1);
			 
			 if (cmd[0].equals("stop")) {
				 lcd.clear();
				 left.stop(true);
				 right.stop(true);
			 } else if (cmd[0].equals("forward")) {
				 lcd.clear();
				 lcd.drawString("Forwards", 4, 3);
				 left.forward();
				 right.forward();
			 } else if (cmd[0].equals("backward")) {
				 lcd.clear();
				 lcd.drawString("Backwards", 4, 3);
				 left.backward();
				 right.backward();
			 } else if (cmd[0].equals("rotate left")) {
				 lcd.clear();
				 lcd.drawString("Left", 4, 3);
				 left.backward();
				 right.forward();
			 } else if (cmd[0].equals("rotate right")) {
				 lcd.clear();
				 lcd.drawString("Right", 4, 3);
				 left.forward();
				 right.backward();
			 }
			 
			 return 0l;
		 }
		 
	     protected void onPostExecute(Long result) {
	    	 if (result == 1l) Toast.makeText(MainActivity.this, "Could not connect to EV3", Toast.LENGTH_LONG).show();
	    	 else if (result == 2l) Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
	     }
	}

}
