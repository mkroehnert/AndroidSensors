package com.github.mkroehnert.Sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mkroehnert.Sensor.R;


public class SensorActivity extends Activity {
	private SensorManager mngr;
	private SensorEventListener listener;
	
	private TextView xView;
	private TextView yView;
	private TextView zView;
	private View colorView;
	
	private float[] valuesMin = {0, 0, 0};
	private float[] valuesMax = {0, 0, 0};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        xView = (TextView) findViewById(R.id.x);
        yView = (TextView) findViewById(R.id.y);
        zView = (TextView) findViewById(R.id.z);
        
        colorView = (View) findViewById(R.id.colorView);
                
        mngr = (SensorManager) this.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        changeSensor(Sensor.TYPE_GYROSCOPE);

        setupSpinner();
    }
    
    private void setupSpinner() {
        Spinner sensorSelection = (Spinner) findViewById(R.id.sensorType);
        ArrayAdapter<CharSequence> sensorAdapter = ArrayAdapter.createFromResource(this, R.array.SensorTypes, android.R.layout.simple_spinner_item);
        sensorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSelection.setAdapter(sensorAdapter);
        
        OnItemSelectedListener sensorTypeChanged = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            	int sensor = 0;
            	switch (pos) {
				case 0:
					sensor = Sensor.TYPE_GYROSCOPE;
					break;

				case 1:
					sensor = Sensor.TYPE_ACCELEROMETER;
					break;

				case 2:
					sensor = Sensor.TYPE_LINEAR_ACCELERATION;
					break;

				default:
					break;
				}
            	Toast.makeText(parent.getContext(), "Sensor: " +
            		  parent.getItemAtPosition(pos).toString() + " " + pos, Toast.LENGTH_LONG).show();
            	changeSensor(sensor);
            }

            public void onNothingSelected(AdapterView parent) {
              // Do nothing.
            }
        };
        sensorSelection.setOnItemSelectedListener(sensorTypeChanged);
	}

	@Override
    public void onPause()
    {
    	super.onPause();
    	mngr.unregisterListener(listener);
    }
    
	private float normalize(float value)
	{
		// normalerweise +/- 10
		float range = 10;
		float maxColorValue = 255;
		float color = value;
    	color = (float) Math.max(-range, color);
    	color = (float) Math.min(range, color);
    	color += range;
    	
    	color = color / (2 * range) * maxColorValue;
    	
    	return color;
	}
	
    private void changeColor(float x, float y, float z)
    {
    	//storeMinMax(x, 0);
    	//storeMinMax(y, 1);
    	//storeMinMax(z, 2);

    	int[] colors = {(int)normalize(x), (int)normalize(y), (int)normalize(z)};    	

    	// benutze Lichtsensor für Alpha Wert
    	xView.setText("x = " + colors[0]);
    	yView.setText("y = " + colors[1]);
    	zView.setText("z = " + colors[2]);
    	//xView.setText("x = " + valuesMin[0] + " " + valuesMax[0] + " " + colors[0]);
    	//yView.setText("y = " + valuesMin[1] + " " + valuesMax[1] + " " + colors[1]);
    	//zView.setText("z = " + valuesMin[2] + " " + valuesMax[2] + " " + colors[2]);

    	
    	colorView.setBackgroundColor(Color.rgb(colors[0], colors[1], colors[2]));
    }
    
    private void storeMinMax(float value, int index) {
    	valuesMin[index] = Math.min(value, valuesMin[index]);
    	valuesMax[index] = Math.max(value, valuesMax[index]);
	}

	private void changeSensor(int sensorType)
    {
    	if (null != listener)
    	{
    		mngr.unregisterListener(listener);
    	}
        Sensor newSensor = mngr.getDefaultSensor(sensorType);  
        
        listener = new SensorEventListener() { 
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
            	
            }

            public void onSensorChanged(SensorEvent event)
            {
            	float x = event.values[0];
            	float y = event.values[1];
            	float z = event.values[2];
            	
            	changeColor(x, y, z);
            }
        };

        // registering the new listener
        mngr.registerListener(listener, newSensor, SensorManager.SENSOR_DELAY_UI);
    }
}