package com.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressListModel  implements Serializable {

    @SerializedName("addr_id")
    public String addr_id="";

    @SerializedName("addr_title")
    public String addr_title="";

    @SerializedName("addr_detail")
    public String addr_detail="";

    @SerializedName("addr_lat")
    public String addr_lat="";

    @SerializedName("addr_lng")
    public String addr_lng="";

    public AddressListModel()
    {}
    public AddressListModel(String addr_title,String addr_detail)
    {
        this.addr_title = addr_title;
        this.addr_detail = addr_detail;

    }


}
