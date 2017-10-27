package com.api.response;

import com.api.model.NotificationListModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by prashant.patel on 12/14/2016.
 */

public class NotificationListResponse extends CommonResponse {

    @SerializedName("data")
    public ArrayList<NotificationListModel> arrayListNotificationListModel;

    @SerializedName("t")
    public String t="0";

}
