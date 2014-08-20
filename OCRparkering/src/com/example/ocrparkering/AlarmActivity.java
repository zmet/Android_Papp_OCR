package com.example.ocrparkering;

import java.util.Currency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmActivity extends Activity implements OnClickListener {
	
		TimePicker timepick1;
		Integer hour, min;
		
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);
	
		 
		 Intent message = getIntent();
		 String alarm = message.getStringExtra(MainActivity.ALARM_MESSAGE);
		 
		
	
		 
		 String[] separated = alarm.split(":");
		 
		 separated[0] = separated[0].trim();
		 separated[1] = separated[1].trim();
		 
		 hour = Integer.parseInt(separated[0].toString());
		 min = Integer.parseInt(separated[1].toString());
		 
		 Button btn = (Button)findViewById(R.id.submit);
		 timepick1 = (TimePicker)findViewById(R.id.timePicker1);
		 timepick1.setIs24HourView(true);
		 
		 timepick1.setCurrentHour(hour);
		 timepick1.setCurrentMinute(min);
		 
		 btn.setOnClickListener(this);	
		 
		}
public void onClick(View v) {
		
		Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
		i.putExtra(AlarmClock.EXTRA_HOUR, timepick1.getCurrentHour());
		i.putExtra(AlarmClock.EXTRA_MINUTES, timepick1.getCurrentMinute());
		i.putExtra(AlarmClock.EXTRA_MESSAGE, "Din tid går ut!");
		startActivity(i);
	
	}
}
