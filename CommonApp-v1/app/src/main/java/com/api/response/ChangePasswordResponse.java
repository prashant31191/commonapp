package com.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 12/5/2016.
 */

public class ChangePasswordResponse extends CommonResponse
{

    @SerializedName("data")
    public Data data;


    public class Data
    {


        @SerializedName("accessToken")
        public String accessToken;
    }

}
