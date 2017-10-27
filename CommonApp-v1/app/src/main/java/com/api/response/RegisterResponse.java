package com.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 12/5/2016.
 */

public class RegisterResponse extends CommonResponse
{

    @SerializedName("user")
    public Data data;


    public class Data
    {
        @SerializedName("id")
        public String uid;

        @SerializedName("fname")
        public String fname;

        @SerializedName("lname")
        public String lname;

        @SerializedName("email")
        public String email;

        @SerializedName("city")
        public String city;

        @SerializedName("state")
        public String state;

        @SerializedName("country")
        public String country;

        @SerializedName("badge")
        public String badge;

        @SerializedName("uimg")
        public String uimg;

        @SerializedName("accessToken")
        public String accessToken;

    }

}
