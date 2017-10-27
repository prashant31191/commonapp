package com.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dhaval.mehta on 03-Mar-2017.
 */

public class PolylineEncodedJSONParser {

    public String abc(JSONObject jObject) {

        String overview_polyline = "";
        JSONArray jRoutes = null;
        JSONObject jOverview_polyline = null;

        try {

            jRoutes = jObject.getJSONArray("routes");
            jOverview_polyline = jRoutes.getJSONObject(0).getJSONObject("overview_polyline");
            overview_polyline = jOverview_polyline.getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return overview_polyline;
    }
}
