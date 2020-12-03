package priv.jujin.locationtutorial;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * 네트워크 헬퍼
 * 사용자를 대신하여 Volley 네트워크 처리를 해주며, 사용자는 RequestData와 리스너만 연동하면 된다.
 */
public class NetworkHelper {
    public static void apiCall(RequestData requestData, OnSuccessListener onSuccessListener, OnFailListener onFailListener) {
        RequestQueue queue = requestData.queue;
        JsonObjectRequest request = new JsonObjectRequest(requestData.requestType, requestData.requestUrl,
                requestData.requestParams, getNetworkSuccessListener(onSuccessListener),
                getNetworkErrorListener(onFailListener));
        queue.add(request);
    }

    interface OnSuccessListener {
        void onSuccess(JSONObject response);
    }

    interface OnFailListener {
        void onFail(Throwable error);
    }
    /**
     * Network 성공 리스너.
     * @param onSuccessListener  네트워크 리스너
     */
    private static Response.Listener<JSONObject> getNetworkSuccessListener(final OnSuccessListener onSuccessListener) {
        return onSuccessListener::onSuccess;
    }

    /**
     * Network 실패 리스너.
     * 네트워크 과정에서 에러 또는 데이터가 내려오지 못했을 때 처리한다.
     * @param onFailListener  네트워크 리스너
     */
    private static Response.ErrorListener getNetworkErrorListener(final OnFailListener onFailListener) {
        return onFailListener::onFail;
    }
}