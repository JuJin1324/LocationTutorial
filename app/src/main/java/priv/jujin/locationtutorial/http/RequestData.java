package priv.jujin.locationtutorial.http;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Volley 네트워크 설정 데이터
 * 빌더를 이용해서 처리
 */
@Builder
@Setter
@Getter
public class RequestData {
    public RequestQueue queue;
    /**
     * Request Method 타입
     * GET, POST, PUT, DELETE ....
     */
    public int requestType;
    /**
     * Request Url
     */
    public String requestUrl;
    /**
     * Request Param
     * 바디 형식으로 넣어줄때 사용
     */
    public JSONObject requestParams;
}