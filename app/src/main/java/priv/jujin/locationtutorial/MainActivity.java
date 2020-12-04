package priv.jujin.locationtutorial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import priv.jujin.locationtutorial.domain.HttpReqBody;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private final String TAG = "LocationProvider";

    private TextView tvGpsLatitude, tvGpsLongitude;
    private TextView tvPassiveLatitude, tvPassiveLongitude;
    private TextView tvNetworkLatitude, tvNetworkLongitude;
    private TextView tvAzimuth, tvAddress, tvSpeed;
    private Button btnStartSend, btnStopSend;
    private EditText etSendInterval, etSendAddress;

    private LocationManager locationManager;
    private SensorManager sensorManager;
    private Sensor sensorAccel, sensorMag;

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    private Geocoder geocoder;

    private HttpReqBody reqBody;
    RequestData requestData;

    private final int DEFAULT_SEND_INTERVAL_SEC = 60;
    private int sendIntervalSec = DEFAULT_SEND_INTERVAL_SEC;
    Timer timer;
    TimerTask timerTask;

    @SuppressLint({"CheckResult", "SetTextI18n"})
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
        tvAddress = findViewById(R.id.tvAddress);
        tvSpeed = findViewById(R.id.tvSpeed);
        btnStartSend = findViewById(R.id.btnStartSend);
        btnStopSend = findViewById(R.id.btnStopSend);
        btnStopSend.setEnabled(false);
        etSendInterval = findViewById(R.id.etSendInterval);
        etSendAddress = findViewById(R.id.etSendAddress);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        geocoder = new Geocoder(this, Locale.KOREA);

        /* For Http Request */
        String url = "http://192.168.0.98:3000/trip/send";
        requestData = RequestData.builder()
                .queue(Volley.newRequestQueue(getApplicationContext()))
                .requestType(Request.Method.POST)
                .requestUrl(url)
                .build();

        sendIntervalSec = Integer.parseInt(etSendInterval.getText().toString());
        etSendInterval.addTextChangedListener(new AfterTextChangedWatcher(s -> {
            try {
                sendIntervalSec = Integer.parseInt(s.toString());
            } catch (NumberFormatException e) {
                sendIntervalSec = DEFAULT_SEND_INTERVAL_SEC;
            }
        }));
        etSendAddress.addTextChangedListener(new AfterTextChangedWatcher(
                s -> requestData.setRequestUrl("http://" + s.toString() + ":3000/trip/send")
        ));
    }

    public void onBtnStartSendClick(View view) {
        btnStartSend.setEnabled(false);
        btnStopSend.setEnabled(true);
        etSendInterval.setEnabled(false);
        etSendAddress.setEnabled(false);

        registerSensorListeners();
        requestLocationUpdates(100, 0);

        /* 참조사이트:
         * https://arabiannight.tistory.com/67
         * https://m.blog.naver.com/PostView.nhn?blogId=ssarang8649&logNo=220948756167&proxyReferer=https:%2F%2Fwww.google.com%2F */
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (reqBody != null) {
                    requestData.setRequestParams(reqBody.getJson());
                    NetworkHelper.apiCall(requestData,
                            response -> {
                                Date dt = new Date();
                                SimpleDateFormat full_sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
                                Log.d(TAG, "[" + full_sdf.format(dt) + "]" + "response: " + response);
                            },
                            error -> Log.e(TAG, "error: " + error)
                    );

                    reqBody = null;
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, sendIntervalSec * 1000);
    }

    public void onBtnStopSendClick(View view) {
        btnStartSend.setEnabled(true);
        btnStopSend.setEnabled(false);
        etSendInterval.setEnabled(true);
        etSendAddress.setEnabled(true);

        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        timer.cancel();
        timer.purge();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth = getAzimuthFromSensor(event);
        tvAzimuth.setText("azimuth: " + azimuth);
        if (reqBody != null)
            reqBody.setAzimuth((double) azimuth);
    }

    private void requestLocationUpdates(int updateIntervalMillisec, int updateMinDistanceMeter) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateIntervalMillisec, updateMinDistanceMeter, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateIntervalMillisec, updateMinDistanceMeter, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, updateIntervalMillisec, updateMinDistanceMeter, this);
    }

    private void registerSensorListeners() {
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
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

    private String getAddress(double lat, double lng) {
        List<Address> addressList;
        String address = null;

        try {
            if (geocoder != null) {
                addressList = geocoder.getFromLocation(lat, lng, 1);

                if (addressList != null && addressList.size() > 0) {
                    address = addressList.get(0).getAddressLine(0);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return address;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = 0.0;
        double longitude = 0.0;

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvGpsLatitude.setText(String.valueOf(latitude));
            tvGpsLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, "GPS : " + latitude + '/' + longitude);
        }

        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvNetworkLatitude.setText(String.valueOf(latitude));
            tvNetworkLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, "Network : " + latitude + '/' + longitude);
        }

        if (location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            tvPassiveLatitude.setText(String.valueOf(latitude));
            tvPassiveLongitude.setText(String.valueOf(longitude));
            Log.d(TAG, "Passive : " + latitude + '/' + longitude);
        }
        /* 참조사이트: https://copycoding.tistory.com/38 */
        double speed = location.getSpeed();
        tvSpeed.setText(String.valueOf(speed));

        String address = getAddress(latitude, longitude);
        if (address != null)
            tvAddress.setText(address);

        reqBody = new HttpReqBody(latitude, longitude,
                null, null, null, null, speed);
    }

}