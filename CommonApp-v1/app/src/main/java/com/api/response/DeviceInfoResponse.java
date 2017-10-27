package com.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 12/5/2016.
 */

public class DeviceInfoResponse extends CommonResponse
{

    @SerializedName("data")
    public Data data;


    public class Data
    {
        @SerializedName("id")
        public String id;

        @SerializedName("custEmail")
        public String custEmail;

        @SerializedName("currency")
        public String currency;

        @SerializedName("accessToken")
        public String accessToken;

        @SerializedName("minStripAmount")
        public String minStripAmount;
    }

}
