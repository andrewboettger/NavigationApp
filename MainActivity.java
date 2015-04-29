package com.example.lab4_v3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.lab4_v3.LineGraphView;

import mapper.InterceptPoint;
import mapper.LabeledPoint;
import mapper.LineSegment;
import mapper.MapLoader;
import mapper.MapView;
import mapper.NavigationalMap;
import mapper.PositionListener;
import mapper.VectorUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	public static MapView mv;
	public static NavigationalMap map;
	
	List<PointF> list = new ArrayList<PointF>(2);
	@SuppressLint("WrongCall")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			mv = new MapView(getApplicationContext(), 800, 800, 30, 30);
	
			registerForContextMenu(mv);
	
			map = MapLoader.loadMap(getExternalFilesDir(null),
					"E2-3344.svg");
			mv.setMap(map);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mv.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item)
				|| mv.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
		public LineGraphView graph;
		public float[] degrees= new float[3];
		public int steps = 0;
		public int steps_distance = 0;
		public int esteps = 0;
		public int nsteps = 0;
		public boolean stepsTaken = false;
		public float[] G = new float[3];
		public float[] M = new float[3];
		float[] r = new float[16];
		float[] I = new float[16];
		float[] O = new float[3];

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			Button resetbutton;
			resetbutton = (Button) rootView.findViewById(R.id.button);
			resetbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					steps = 0;
					steps_distance = 0;
					esteps = 0;
					nsteps = 0;
				}
			});
			LinearLayout lmain = (LinearLayout) rootView
					.findViewById(R.id.Lab4_v3);

			lmain.addView(mv);
			mv.setVisibility(View.VISIBLE);
			mv.addListener(templistener);
			lmain.setOrientation(LinearLayout.VERTICAL);

			SensorManager sensorManager = (SensorManager) rootView.getContext()
					.getSystemService(SENSOR_SERVICE);
			TextView tv1 = new TextView(rootView.getContext());
			lmain.addView(tv1);
			TextView tv = new TextView(rootView.getContext());
			lmain.addView(tv);
			tv.setText("\n\n");
			TextView stepss = new TextView(rootView.getContext());
			lmain.addView(stepss);
			TextView tv3 = new TextView(rootView.getContext());
			lmain.addView(tv3);
			
			Sensor LinaccelerationSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			SensorEventListener Linaccel = new LinearAccelerationEventListener(stepss);
			sensorManager.registerListener(Linaccel, LinaccelerationSensor,
					SensorManager.SENSOR_DELAY_FASTEST);
			Sensor magneticSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			SensorEventListener magnetic = new MagneticEventListener();
			sensorManager.registerListener(magnetic, magneticSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			Sensor accelerationSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			SensorEventListener accel = new AccelerationEventListener();
			sensorManager.registerListener(accel, accelerationSensor,
					SensorManager.SENSOR_DELAY_FASTEST);	
			Sensor rotationSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			SensorEventListener rotation = new RotationEventListener(tv3,tv1);
			sensorManager.registerListener(rotation, rotationSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			return rootView;
		}

		public class RotationEventListener implements SensorEventListener {
			TextView tv3;
			TextView tv1;
			String direction;
			public RotationEventListener(TextView tv3, TextView tv1) {
				this.tv3 = tv3;
				this.tv1 = tv1;
			}
			@Override
			public void onSensorChanged(SensorEvent event) {

				// TODO Auto-generated method stub
				if (G != null && M != null) {
					boolean success = SensorManager.getRotationMatrix(r, I, G,M);
					SensorManager.getOrientation(r, O);
					degrees[0] = (float) (O[0] * (180/Math.PI));
					if(degrees[0] < 0){
						degrees [0] += 360.0;
					}
					degrees[1] = (float) (O[1] * (180/Math.PI));
					degrees[2] = (float) (O[2] * (180/Math.PI));
					
					tv3.setText(String.format("\n\norientation X: %.3f"
							+ " \norientation Y: %.3f" + " \norientation Z: %.3f",
							degrees[0], degrees[1], degrees[2]));
					
					if (degrees[0] >= 337 && degrees[0] < 360 || degrees[0]>= 0 && degrees[0] < 22){
						direction = "NORTH";
					}
					else if ( degrees[0] >= 22 && degrees[0] < 67){
						direction = "NORTH EAST";
					}
					else if (degrees[0] >= 67 && degrees[0] < 112){
						direction = "EAST";
					}
					else if (degrees[0] >= 112 && degrees[0] < 157){
						direction = "SOUTH EAST";
					}
					else if( degrees[0] >= 157 && degrees[0] < 202){
						direction = "SOUTH";
					}
					else if (degrees[0]>= 202 && degrees[0] < 247){
						direction = "SOUTHWEST";
					}
					else if(degrees[0] >= 247 && degrees[0] < 292){
						direction = "WEST";
					}
					else if(degrees[0] >= 292 && degrees[0] < 337){
						direction = "NORTHWEST";
					}
					
					
					tv1.setText("Current direction:" + direction);
				}
			}
			public void onOrientationChanged (int orientation){}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
				// TODO Auto-generated method stub}
		}

		class MagneticEventListener implements SensorEventListener {
			public MagneticEventListener() {}
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
					M = event.values;
			}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub}
			}
		}
	class AccelerationEventListener implements SensorEventListener {
			public AccelerationEventListener() {
			}
			@Override
			public void onAccuracyChanged(Sensor s, int i) {
			}
			@Override
			public void onSensorChanged(SensorEvent se) {
				if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
					G = se.values;
			}
	}
			
			public class LinearAccelerationEventListener implements SensorEventListener {
		
				TextView stepcount;
		    	int state1 = 0;
		    	int state2 = 0;
		    	int state3 = 0;
		    	int state4 = 0;
		    	
				public LinearAccelerationEventListener(TextView steps) {
					stepcount = steps;
				}
				public void onAccuracyChanged(Sensor s, int i) {
				}
				public void onSensorChanged(SensorEvent se) {
					if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {	
						float [] smoothedAccel = new float[3];
						smoothedAccel[0] += (se.values[0] - smoothedAccel[0]) / 800f;
						smoothedAccel[1] += (se.values[1] - smoothedAccel[1]) / 800f;
						smoothedAccel[2] += (se.values[2] - smoothedAccel[2]) / 290f;
						statemachine(smoothedAccel[2], smoothedAccel[1], degrees[0],getActivity());
						
						stepcount.setText("Steps (net displacement):"
								+  Integer.toString(steps)
								+ "\nSteps (distance)"
								+ Integer.toString(steps_distance)
								+ "\nSteps (north)"
								+ Integer.toString(nsteps)
								+ "\nSteps (east)"
								+ Integer.toString(esteps));
					}
				}
				public void statemachine (float zvalue, float yvalue, float orientation, Activity hi){
					
					if(Math.abs(zvalue) > 0 && Math.abs(zvalue)< 0.0015 && Math.abs(yvalue) > 0.001 ){
						state1 = 1;
						return;}
					if(state1 == 1 && Math.abs(zvalue) > 0.0006 && Math.abs(zvalue) < 0.005 && Math.abs(yvalue) > 0.0008){
						state2 = 2;
						return;}
					if(Math.abs(zvalue) > 0.002 && Math.abs(zvalue) < 0.01 & state2==2 && state1 == 1 && Math.abs(yvalue) > 0.001  ){
						state3 = 3;
						return;}
					if(Math.abs(zvalue) > 0.0015  &&  state1 == 1 && state2 == 2 && state3 ==3 && Math.abs(yvalue) > 0.001){
						state4 = 4;
						steps_distance++;
						if(state1==1 && state2==2 && state3==3 && state4==4){stepsTaken = true;}
						state1 = 0;
						state2 = 0;
						state3 = 0;
						state4 = 0;
						return;}
					PointF current = mv.getUserPoint();
					PointF next = new PointF(current.x, current.y);
					if(stepsTaken == true){
						if(orientation >= 315 || orientation < 45){
							nsteps++;
						
							next.y += 0.7;
							System.out.println("HIIII: " + current + next);
							List<InterceptPoint>  temp = map.calculateIntersections(current, next);
							if(temp.isEmpty())
							{
								mv.setUserPoint(next);
							}
							
						
						
						}
						else if (orientation >= 45 && orientation < 135){
							esteps++;
							System.out.println("HIIII: " + current + next);
							next.x += 0.7;
							List<InterceptPoint>  temp = map.calculateIntersections(current, next);
							if(temp.isEmpty())
							{
								mv.setUserPoint(next);
							}
							
							
						}
						else if(orientation >= 135 && orientation < 225){
							nsteps--;
							System.out.println("HIIII: " + current + next);
							next.y -= 0.7;
							List<InterceptPoint>  temp = map.calculateIntersections(current, next);
							if(temp.isEmpty())
							{
								mv.setUserPoint(next);
							}
						
						}
						else{
							esteps--;
							System.out.println("HIIII: " + current + next);
							next.x -= 0.7;
							List<InterceptPoint>  temp = map.calculateIntersections(current, next);
							if(temp.isEmpty())
							{
								mv.setUserPoint(next);
							}
						}
						steps = (int) Math.round(Math.sqrt(Math.pow(esteps, 2) + Math.pow(nsteps,2))); 
						current=mv.getUserPoint();
						if(Math.abs(current.x - mv.getDestinationPoint().x) <0.7&& Math.abs(current.y - mv.getDestinationPoint().y) <0.7){
							Toast.makeText(hi, "Destination Reached", Toast.LENGTH_LONG).show();
							
						}
						pathcalc.path();
						stepsTaken = false;
						return;
					}
				}
			}
		}

		 private final static PositionListener templistener = new PositionListener(){
		 			@Override
			public void originChanged(MapView source, PointF loc) {
				// TODO Auto-generated method stub
				source.setUserPoint(loc);
				source.setOriginPoint(loc);
				pathcalc.path();
			}
			@Override
			public void destinationChanged(MapView source, PointF dest) {
				// TODO Auto-generated method stub
				source.setDestinationPoint(dest);
				pathcalc.path();
			}	
		 };
}
		 
