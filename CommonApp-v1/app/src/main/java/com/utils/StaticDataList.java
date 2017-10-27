package com.utils;

import com.api.model.AddressListModel;

import java.util.ArrayList;

/**
 * Created by prashant.patel on 9/21/2017.
 */

public class StaticDataList {

    // for the addresses
    public static ArrayList<AddressListModel> getStaticData()
    {
        ArrayList<AddressListModel> list = new ArrayList<>();

        list.add(new AddressListModel("Primary","WASHINGTON D.C.. 1607 23rd Street NW,Lilburn, Georgia 30047. CHARLOTTE"));
        list.add(new AddressListModel("Home","Acrewood Drive 45249;  Arborcreek Lane, 45242; Arcturus Drive 45249; Ashley"));
        list.add(new AddressListModel("Primary-2","Arcturus D.C.. 1607 23rd Street NW,Lilburn, Georgia 30047. CHARLOTTE"));
        return list;
    }
}
