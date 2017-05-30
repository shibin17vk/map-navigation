package com.example.shibin.myapplication.network;

import com.example.shibin.myapplication.model.getdirection.GetDirectionResponse;
import com.example.shibin.myapplication.utils.Constants;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public interface ApiService {

    @GET(Constants.API_GOOGLE_GET_DIRECTIONS)
    Observable<GetDirectionResponse> getDirections(
            @Query("origin") String origin,  @Query("destination") String destination,
            @Query("sensor") String ensor,@Query("alternatives") String alternatives);

}