package com.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 12/5/2016.
 */

public class ViewProfileResponse extends CommonResponse
{

    @SerializedName("user")
    public Data data;

    public class Data
    {
        @SerializedName("id")
        public String uid;

        @SerializedName("name")
        public String name;

        @SerializedName("fname")
        public String fname;

        @SerializedName("lname")
        public String lname;

        @SerializedName("email")
        public String email;

        @SerializedName("url")
        public String url;

        @SerializedName("about")
        public String about;

        @SerializedName("age")
        public String age;

        @SerializedName("weight")
        public String weight;

        @SerializedName("height")
        public String height;

        @SerializedName("city")
        public String city;

        @SerializedName("ctid")
        public String ctid;

        @SerializedName("sid")
        public String sid;

        @SerializedName("ctrid")
        public String ctrid;

        @SerializedName("state")
        public String state;

        @SerializedName("cntry")
        public String cntry;

        @SerializedName("r_cre")
        public String r_cre;

        @SerializedName("r_own")
        public String r_own;

        @SerializedName("r_lost")
        public String r_lost;

        @SerializedName("r_con")
        public String r_con;

        @SerializedName("uimg")
        public String uimg;

        @SerializedName("college")
        public String college;

        @SerializedName("company")
        public String company;

        @SerializedName("accessToken")
        public String accessToken;
    }

}
