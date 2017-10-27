package com.api.model;

import com.google.gson.annotations.SerializedName;

public class NotificationListModel {


    @SerializedName("type")
    public String type;

    @SerializedName("noti_id")
    public String noti_id;

    @SerializedName("isRead")
    public String isRead="0";

    @SerializedName("rid")
    public String rid;


    @SerializedName("createdDate")
    public String createddate;  //createdDate: "2017-05-20 19:12:15",

    @SerializedName("message")
    public String message="";

    @SerializedName("total_time")
    public String total_time;

    @SerializedName("profilepic")
    public String profilepic;

}
