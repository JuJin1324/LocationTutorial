package priv.jujin.locationtutorial.domain;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpReqBody {
    private final JSONObject jsonObject = new JSONObject();

    private Double latitude;
    private Double longitude;
    private Double temperature1;
    private Double temperature2;
    private Double temperature3;
    private Double azimuth;
    private Double speed;

    public JSONObject getJson() {
        try {
            jsonObject.put("latitude", this.latitude == null ? JSONObject.NULL : this.latitude);
            jsonObject.put("longitude", this.longitude == null ? JSONObject.NULL : this.longitude);
            jsonObject.put("temperature1", this.temperature1 == null ? JSONObject.NULL : this.temperature1);
            jsonObject.put("temperature2", this.temperature2 == null ? JSONObject.NULL : this.temperature2);
            jsonObject.put("temperature3", this.temperature3 == null ? JSONObject.NULL : this.temperature3);
            jsonObject.put("azimuth", this.azimuth == null ? JSONObject.NULL : this.azimuth);
            jsonObject.put("speed", this.speed == null ? JSONObject.NULL : this.speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
