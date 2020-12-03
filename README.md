# LocationTutorial
> 안드로이드에서 위/경도 및 방위각 읽어오기 샘플

## Location(위도/경도)
### AndroidManifest.xml
> 위치 관련 API 사용을 위해서 다음을 추가한다.
> ```xml
> <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
> <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
> ``` 

### 위도/경도 읽기 방법
> GPS 위도/경도를 3가지 방법으로 읽어올 수 있다.  
> 1. GPS Provider: 스마트폰에 내장된 GPS 모듈에서 측정  
> 2. Network Provider: 이동통신 기지국 또는 WiFi access point들을 이용하여 측정  
> 3. Passive Provider: 다른 어플리케이션이나 서비스가 좌표 값을 구하면 단순히 그 값을 받아 오기만 하는 전달자 역할  
> 위의 3개에 대한 위도/경도를 textView 로 각각 표기하였다.  

### layout/activity_main.xml  
> GPS Provider: tvGpsLatitude / tvGpsLongitude  
> Network Provider: tvNetworkLatitude / tvNetworkLongitude  
> Passive Provider: tvPassiveLatitude / tvPassiveLongitude    
> 속도: tvSpeed

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
>     double speed = location.getSpeed();
>     tvSpeed.setText("speed: " + speed);
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

## 리버스 지오코딩
### UI
> 위경도로 부터 변환된 주소를 표시할 TextView 를 하나 추가한다. 이름은 tvAddress

### MainActivity
> 멤버 변수로 Geocoder 를 추가한다.
> ```java
> public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
>     private Geocoder geocoder;
>     ...
> }
> ```
>
> onCreate 에서 변수를 초기화 한다.
> ```java
> @Override
> protected void onCreate(Bundle savedInstanceState) {
>     ...
>     geocoder = new Geocoder(this, Locale.KOREA); 
> ```
> 
> 위경도로 부터 주소를 반환 받을 메서드 getAddress 를 추가한다.
> ```java
> private String getAddress(double lat, double lng) {
>     List<Address> addressList;
>     String address = null;
> 
>     try {
>         if (geocoder != null) {
>             addressList = geocoder.getFromLocation(lat, lng, 1);
> 
>             if (addressList != null && addressList.size() > 0) {
>                 address = addressList.get(0).getAddressLine(0);
>             }
>         }
>     } catch (IOException e) {
>         Log.e(TAG, e.getMessage());
>     }
> 
>     return address;
> }
> ```
> 
> 위치가 변화하면 자동으로 호출되는 onLocationChanged 메서드에 위에서 정의한 메서드를 호출하여 TextView 에 표시하도록 한다.
> ```java
> @Override
> public void onLocationChanged(@NonNull Location location) {
>     ...
>     String address = getAddress(latitude, longitude);
>     Log.d(TAG, "Address: " + address);
>     tvAddress.setText(address);
> }
> ```

## Volley를 이용한 HTTP 비동기 Request 
### AndroidManifest
> AndroidManifest.xml 에서 인터넷 사용을 위해 다음의 권한을 추가한다.
> ```xml
> <uses-permission android:name="android.permission.INTERNET" />
> ```

### 라이브러리 추가
> app/build.gradle 에 dependencies 에 Veolley 라이브러리 추가
> ```
> dependencies {
>     implementation 'com.android.volley:volley:1.1.1'
>     ...
> }
> ```

### Request Body 
> Request Body 가 JSON 형식인 Request 콜을 위해서 Request Body POJO 추가
> 먼저 Lombok 사용을 위해 app/build.gradle 에 라이브러리 추가
> ```
> dependencies {
>     compileOnly 'org.projectlombok:lombok:1.18.16'
>     annotationProcessor 'org.projectlombok:lombok:1.18.16'
>     ...
> }
> ```
>
> ```java
> @Data
> @AllArgsConstructor
> @NoArgsConstructor
> public class HttpReqBody {
>     private final JSONObject jsonObject = new JSONObject();
> 
>     private Double latitude;
>     private Double longitude;
>     private Double temperature1;
>     private Double temperature2;
>     private Double temperature3;
>     private Double azimuth;
>     private Double speed;
> 
>     public JSONObject getJson() {
>         try {
>             jsonObject.put("latitude", this.latitude == null ? JSONObject.NULL : this.latitude);
>             jsonObject.put("longitude", this.longitude == null ? JSONObject.NULL : this.longitude);
>             jsonObject.put("temperature1", this.temperature1 == null ? JSONObject.NULL : this.temperature1);
>             jsonObject.put("temperature2", this.temperature2 == null ? JSONObject.NULL : this.temperature2);
>             jsonObject.put("temperature3", this.temperature3 == null ? JSONObject.NULL : this.temperature3);
>             jsonObject.put("azimuth", this.azimuth == null ? JSONObject.NULL : this.azimuth);
>             jsonObject.put("speed", this.speed == null ? JSONObject.NULL : this.speed);
>         } catch (JSONException e) {
>             e.printStackTrace();
>         }
>         return jsonObject;
>     }
> }
> ```

### NetworkHelper
> Volley 를 이용한 비동기 HTTP 통신을 위한 클래스 추가
> ```java
> /**
>  * 네트워크 헬퍼
>  * 사용자를 대신하여 Volley 네트워크 처리를 해주며, 사용자는 RequestData와 리스너만 연동하면 된다.
>  */
> public class NetworkHelper {
>     public static void apiCall(RequestData requestData, OnSuccessListener onSuccessListener, OnFailListener onFailListener) {
>         RequestQueue queue = requestData.queue;
>         JsonObjectRequest request = new JsonObjectRequest(requestData.requestType, requestData.requestUrl,
>                 requestData.requestParams, getNetworkSuccessListener(onSuccessListener),
>                 getNetworkErrorListener(onFailListener));
>         queue.add(request);
>     }
> 
>     interface OnSuccessListener {
>         void onSuccess(JSONObject response);
>     }
> 
>     interface OnFailListener {
>         void onFail(Throwable error);
>     }
>     /**
>      * Network 성공 리스너.
>      * @param onSuccessListener  네트워크 리스너
>      */
>     private static Response.Listener<JSONObject> getNetworkSuccessListener(final OnSuccessListener onSuccessListener) {
>         return onSuccessListener::onSuccess;
>     }
> 
>     /**
>      * Network 실패 리스너.
>      * 네트워크 과정에서 에러 또는 데이터가 내려오지 못했을 때 처리한다.
>      * @param onFailListener  네트워크 리스너
>      */
>     private static Response.ErrorListener getNetworkErrorListener(final OnFailListener onFailListener) {
>         return onFailListener::onFail;
>     }
> }
> ```

### RequestData
> Volley 를 보다 편하게 사용하기 위한 Request 관련 클래스 추가
> ```java
>/**
> * Volley 네트워크 설정 데이터
> * 빌더를 이용해서 처리
> */
> @Builder
> @Setter
> @Getter
> public class RequestData {
>     public RequestQueue queue;
>     /**
>      * Request Method 타입
>      * GET, POST, PUT, DELETE ....
>      */
>     public int requestType;
>     /**
>      * Request Url
>      */
>     public String requestUrl;
>     /**
>      * Request Param
>      * 바디 형식으로 넣어줄때 사용
>      */
>     public JSONObject requestParams;
> }
> ```

### MainActivity
> 위에서 정의한 클래스 선언: HttpReqBody 는 RequestBody에 관한 클래스 / RequestData 는 Http Request 에 사용될 Volley Queue 및 
> Request Header 정보를 담은 클래스 
> ```java
> public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
>     private HttpReqBody reqBody;
>     RequestData requestData;
>     ...
> }
> ```
>
> onCreate 에 RequestData 초기화
> ```java
> String url = "http://192.168.0.98:3000/trip/send";  /* 본인이 준비한 서버 요청 URL 로 교체 */
> requestData = RequestData.builder()
>        .queue(Volley.newRequestQueue(getApplicationContext()))
>        .requestType(Request.Method.POST)
>        .requestUrl(url)
>        .build();
> ```
>
> onLocationChanged 에 HttpReqBody 초기화
> ```java
> public void onLocationChanged(@NonNull Location location) {
>     ...
>     reqBody = new HttpReqBody(latitude, longitude, null, null, null, null, speed);
> }
> ```
> 
> 방위각(azimuth) 또한 함께 넘겨야 함으로 onLocationChanged 가 아닌 <b>onSensorChanged</b> 에서 실제 HTTP Request 콜을 한다.
> ```java
> public void onSensorChanged(SensorEvent event) {
>    ...
>    if (reqBody != null) {
>        reqBody.setAzimuth((double) azimuth);
>        Log.d(TAG, "reqBody: " + reqBody.getJson().toString());
>
>        requestData.setRequestParams(reqBody.getJson());
>        NetworkHelper.apiCall(requestData,
>                response -> Log.d(TAG, "response: " + response),
>                error -> Log.e(TAG, "error: " + error)
>        );
>
>        reqBody = null;
>    }
>}
> ```
> 출처: [[Android] Volley를 써보자!!](https://layers7.tistory.com/25)