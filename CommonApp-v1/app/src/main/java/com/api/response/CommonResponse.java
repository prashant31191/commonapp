package com.api.response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 11/15/2016.
 */

public class CommonResponse
{

    @SerializedName("m")
    public String status;

    @SerializedName("msg")
    public String msg;

}
