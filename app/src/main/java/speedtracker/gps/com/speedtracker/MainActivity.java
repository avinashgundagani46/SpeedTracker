package speedtracker.gps.com.speedtracker;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayDeque;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ISpeedListener {

	public static ArrayDeque speed;
	public static int averageSpeed = 0;
	private CustomSpeedGraph speedGraph;
	private TextView txt_view_currentSpeed;
	private TextView txt_view_averageSpeed;
	private TextView txt_view_overallTime;
	private Button btn_track;
	private LocationManager locationManager;
	private SpeedLocationListener speedListener;
	int minTimeToListenSpeed = 1000;
	float minDistanceToTravel = 5;
	private Criteria criteria;
	private int recordedCount;
	private int totalSpeedRecorded;
	Handler handler = new Handler();
	private int seconds = 0;
	private boolean isTracking;
	private int min;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	/**
	 * Sets all required views
	 */
	private void init() {
		speed = new ArrayDeque(100);
		speedGraph = (CustomSpeedGraph)findViewById(R.id.speed_graph);
		txt_view_currentSpeed = (TextView) findViewById(R.id.txt_current_speed);
		txt_view_averageSpeed = (TextView) findViewById(R.id.txt_avg_speed);
		txt_view_overallTime = (TextView) findViewById(R.id.txt_overall_time);
		btn_track = (Button) findViewById(R.id.btn_track);
		btn_track.setOnClickListener(this);
		setCustomGraphHeight();
		speedListener = new SpeedLocationListener(MainActivity.this);
		criteria = new Criteria();
	}

	/**
	 * Sets the height of custom graph
	 */
	private void setCustomGraphHeight() {
		WindowManager wm = (WindowManager) this
				.getSystemService(this.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
				speedGraph .getLayoutParams();
		params.height = display.getWidth();
		speedGraph .setLayoutParams(params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if(btn_track.getText().toString().contains(getString(R.string.text_start_tracking))){
			handleStartTracking();
		} else {
			handleStopTracking();
		}
	}

	/**
	 * Handles the functionality of stop tracking
	 * @throws SecurityException
	 */
	private void handleStopTracking() throws SecurityException{
		isTracking = false;
		btn_track.setText(getString(R.string.text_start_tracking));
		locationManager.removeUpdates(speedListener);
		locationManager = null;
		speedGraph.invalidate();
	}

	/**
	 * Handles the functionality of starting tracking
	 * @throws SecurityException
	 */
	private void handleStartTracking() throws SecurityException{
		btn_track.setText(getString(R.string.text_stop_tracking));
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(bestProvider, minTimeToListenSpeed, minDistanceToTravel, speedListener);
		isTracking = true;
		startTimer();
	}

	@Override
	public void setCurrentSpeed(int currentSpeed) {
		recordedCount++;
		totalSpeedRecorded = totalSpeedRecorded + currentSpeed;
		averageSpeed = totalSpeedRecorded / recordedCount;
		speed.add(currentSpeed);
		txt_view_averageSpeed.setText(getString(R.string.text_average_speed) + " " + averageSpeed
				+ " " + getString(R.string.kmperhr));
		txt_view_currentSpeed.setText(getString(R.string.text_current_speed) + " " + currentSpeed
				+ " " + getString(R.string.kmperhr));
		if (speed.size() > 100) {
			speed.poll();
		}
		speedGraph.invalidate();
	}

	/**
	 * Sets the time, from when the user is tracking the speed
	 */
	void startTimer(){
		seconds++;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isTracking) {
					min = min + seconds / 60;
					if (seconds >= 60) {
						seconds = seconds - 60;
					}
					if (min > 0) {
						txt_view_overallTime.setText(getString(R.string.text_overall_time) + " "
								+ min + " min " + seconds + " s");
					} else {
						txt_view_overallTime.setText(getString(R.string.text_overall_time) + " "
								+ seconds + " s");
					}
					startTimer();
				}
			}
		}, 1000);
	}
}
