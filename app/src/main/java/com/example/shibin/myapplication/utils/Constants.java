package com.example.shibin.myapplication.utils;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 *
 * Class to define all the constants
 *
 */

public class Constants {

    public static final String APP_TAG                    =        "Assignment APP - Rapid Bikes";

    // Activity request codes
    public static final int ACT_REQ_GET_START_PLACE       =       1;
    public static final int ACT_REQ_GET_DEST_PLACE        =       2;

    // API staus code
    public static final int API_ON_REQ_INITIATED          =        1;
    public static final int API_ON_REQ_COMPLETED          =        2;
    public static final int API_ON_REQ_SUCCESS            =        3;
    public static final int API_ON_REQ_FAILURE            =        4;

    // API Endpoints
    public static final String API_END_POINT_GOOGLE_SERVICE     =   "https://maps.googleapis.com";

    // API services
    public static final String API_GOOGLE_GET_DIRECTIONS        = "/maps/api/directions/json";

    // Permision Req
    public static final int REQ_PERMISSION_ACCESS_LOCATION      =   1000;

}
