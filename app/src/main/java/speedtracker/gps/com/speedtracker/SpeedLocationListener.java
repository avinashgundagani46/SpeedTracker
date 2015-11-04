package speedtracker.gps.com.speedtracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SpeedLocationListener implements LocationListener{

	private final ISpeedListener iSpeedListener;

	public SpeedLocationListener(Context context){
		iSpeedListener = (ISpeedListener) context;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			iSpeedListener.setCurrentSpeed((int)(location.getSpeed()*3.6));
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
}
