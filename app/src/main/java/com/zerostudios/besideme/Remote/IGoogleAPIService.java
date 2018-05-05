package com.zerostudios.besideme.Remote;

import com.zerostudios.besideme.Model.MyPlaces;
import com.zerostudios.besideme.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);

    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination")String destination);

}
