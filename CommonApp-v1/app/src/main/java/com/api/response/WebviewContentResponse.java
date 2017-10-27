package com.api.response;


import com.api.model.WebviewContentListModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11/15/2016.
 */

public class WebviewContentResponse extends CommonResponse
{
    @SerializedName("data")
    public ArrayList<WebviewContentListModel> arrayListWebviewContentListModel;

}
