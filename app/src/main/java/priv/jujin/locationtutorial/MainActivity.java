package priv.jujin.locationtutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private final String TAG = "LocationProvider";
    private TextView tvGpsLatitude, tvGpsLongitude;
    private TextView tvPassiveLatitude, tvPassiveLongitude;
    private TextView tvNetworkLatitude, tvNetworkLongitude;
    private TextView tvAzimuth;

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor sensorAccel, sensorMag;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGpsLatitude = findViewById(R.id.tvGpsLatitude);
        tvGpsLongitude = findViewById(R.id.tvGpsLongitude);
        tvNetworkLatitude = findViewById(R.id.tvNetworkLatitude);
        tvNetworkLongitude = findViewById(R.id.tvNetworkLongitude);
        tvPassiveLatitude = findViewById(R.id.tvPassiveLatitude);
        tvPassiveLongitude = findViewById(R.id.tvPassiveLongitude);
        tvAzimuth = findViewById(R.id.tvAzimuth);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth = getAzimuthFromSensor(event);
        tvAzimuth.setText(String.valueOf(azimuth));
    }

    /* It's referenced from
     * https://www.ssaurel.com/blog/get-android-device-rotation-angles-with-accelerometer-and-geomagnetic-sensors/ */
    private float getAzimuthFromSensor(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, accelerometerReading,
                        0, accelerometerReading.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, magnetometerReading,
                        0, magnetometerReading.length);
                break;
        }
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

//        return orientationAngles[0] * 57.2957795f;      /* default azimuth range is -180 to 180 */
        return orientationAngles[0] * 57.2957795f + 180.0f; /* to make azimuth range 0 ~ 360 */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude;
        double longitude;

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvGpsLatitude.setText(String.valueOf(latitude));
            tvGpsLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, " GPS : " + latitude + '/' + longitude);
        }

        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvNetworkLatitude.setText(String.valueOf(latitude));
            tvNetworkLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, " Network : " + latitude + '/' + longitude);
        }

        if (location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvPassiveLatitude.setText(String.valueOf(latitude));
            tvPassiveLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, " Passive : " + latitude + '/' + longitude);
        }
    }
}