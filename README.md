# LocationTutorial
> 안드로이드에서 위/경도 및 방위각 읽어오기 샘플

## Location(위도/경도)
### AndroidManifest.xml
> 위치 관련 API 사용을 위해서 다음을 추가한다.
> ```xml
> <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
> <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
> ``` 

### UI
> GPS 위도/경도를 3가지 방법으로 읽어올 수 있다.  
> 1. GPS Provider: 스마트폰에 내장된 GPS 모듈에서 측정  
> 2. Network Provider: 이동통신 기지국 또는 WiFi access point들을 이용하여 측정  
> 3. Passive Provider: 다른 어플리케이션이나 서비스가 좌표 값을 구하면 단순히 그 값을 받아 오기만 하는 전달자 역할  
> 위의 3개에 대한 위도/경도를 textView 로 각각 표기하였다.  
>  
> layout/activity_main.xml  
> GPS Provider: tvGpsLatitude / tvGpsLongitude  
> Network Provider: tvNetworkLatitude / tvNetworkLongitude  
> Passive Provider: tvPassiveLatitude / tvPassiveLongitude  

### MainActivity
> 인터페이스 LocationListener 를 구현 및 다음 변수들을 선언한다.
> ```java
> public class MainActivity extends AppCompatActivity implements LocationListener {
>     private LocationManager locationManager;
>     ...
> }
> ```
> 
> onCreate 에서 변수를 초기화 한다.
> ```java
> @Override
> protected void onCreate(Bundle savedInstanceState) {
>     ...
>     locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
> }
> 
> ```
> 
> onStart 에서 위치 관련 권한 요청에 대한 문장을 추가한다.
> ```java
> @Override
> protected void onStart() {
>     super.onStart();
>     if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
>             && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
>         //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
>         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
>     }
> }
> ```
>  
> onPause 및 onDestory 에서 위치 관련 업데이트를 하지 않겠다는 문장을 추가한다.
> ```java
> @Override
> protected void onPause() {
>     super.onPause();
>     locationManager.removeUpdates(this);
> }
> 
> @Override
> protected void onDestroy() {
>     super.onDestroy();
>     locationManager.removeUpdates(this);
> }
> ```
> 
> onResume 에서 위치 관련 다시 업데이트를 하겠다는 문장을 추가한다.
> ```java
> @Override
> protected void onResume() {
>     super.onResume();
>     if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
>         return;
>     }
>     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
>     locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
>     locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
> }
> ```
> 
> onProviderEnabled 에서 GPS / Network / Passive Provider 에서 업데이트 요청을 보낸다.
> ```java
> @Override
> public void onProviderEnabled(@NonNull String provider) {
>     if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
>         return;
>     }
>     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
>     locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
>     locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
> }
> ```
> 
> onLocationChanged 에서 위치 값이 변경되면 textView 를 업데이트 하도록 한다.
> ```java
> @Override
> public void onLocationChanged(@NonNull Location location) {
>     double latitude;
>     double longitude;
> 
>     if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
>         latitude = location.getLatitude();
>         longitude = location.getLongitude();
>         tvGpsLatitude.setText(String.valueOf(latitude));
>         tvGpsLongitude.setText(String.valueOf(longitude));
>         Log.d(TAG, " GPS : " + latitude + '/' + longitude);
>     }
> 
>     if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
>         latitude = location.getLatitude();
>         longitude = location.getLongitude();
>         tvNetworkLatitude.setText(String.valueOf(latitude));
>         tvNetworkLongitude.setText(String.valueOf(longitude));
>         Log.d(TAG, " Network : " + latitude + '/' + longitude);
>     }
> 
>     if (location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
>         latitude = location.getLatitude();
>         longitude = location.getLongitude();
>         tvPassiveLatitude.setText(String.valueOf(latitude));
>         tvPassiveLongitude.setText(String.valueOf(longitude));
>         Log.d(TAG, " Passive : " + latitude + '/' + longitude);
>     }
> }
> ```

## 방위각(Azimuth)
### UI
> 방위각을 표시할 TextView 를 하나 추가한다. 이름은 tvAzimuth

### MainActivity
> 인터페이스 SensorEventListener 를 구현 및 다음 변수들을 선언한다.
> ```java
> public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
>     private SensorManager sensorManager;
>     private Sensor sensorAccel, sensorMag;
>     ...
> }
> ```
> 
> onCreate 에서 변수를 초기화 한다.
> ```java
> @Override
> protected void onCreate(Bundle savedInstanceState) {
>     ... 
>     sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
>     sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
>     sensorMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
> ```
> 
> onPause 및 onDestory 에서 Listener 등록을 해제한다.
> ```java
> @Override
> protected void onPause() {
>     super.onPause();
>     sensorManager.unregisterListener(this);
> }
> 
> @Override
> protected void onDestroy() {
>     super.onDestroy();
>     sensorManager.unregisterListener(this);
> }
> ```
> onResume 에 Listener 를 다시 등록한다.
> ```java
> @Override
> protected void onResume() {
>     ...
>     sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
>     sensorManager.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
> }
> ```
>
> onSensorChanged 에 센서 값(Azimuth 값)이 변하면 tvAzimuth 를 업데이트 하도록 한다.
> ```java
> @Override
> public void onSensorChanged(SensorEvent event) {
>     float azimuth = getAzimuthFromSensor(event);
>     tvAzimuth.setText(String.valueOf(azimuth));
> }
> 
> private float getAzimuthFromSensor(SensorEvent event) {
>     switch (event.sensor.getType()) {
>         case Sensor.TYPE_ACCELEROMETER:
>             System.arraycopy(event.values, 0, accelerometerReading,
>                     0, accelerometerReading.length);
>             break;
>         case Sensor.TYPE_MAGNETIC_FIELD:
>             System.arraycopy(event.values, 0, magnetometerReading,
>                     0, magnetometerReading.length);
>             break;
>     }
>     SensorManager.getRotationMatrix(rotationMatrix, null,
>             accelerometerReading, magnetometerReading);
>     SensorManager.getOrientation(rotationMatrix, orientationAngles);
> 
>     return orientationAngles[0] * 57.2957795f + 180.0f; /* to make azimuth range 0 ~ 360 */
> }
> ``` 

