package com.zerostudios.besideme;

import com.zerostudios.besideme.Model.MyPlaces;
import com.zerostudios.besideme.Model.Results;
import com.zerostudios.besideme.Remote.IGoogleAPIService;
import com.zerostudios.besideme.Remote.RetrofitClient;
import com.zerostudios.besideme.Remote.RetrofitScalarsClient;

public class Common
{

    public static Results currentResult;


    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

    public static IGoogleAPIService getGoogleAPIServiceScalars()
    {
        return RetrofitScalarsClient.getScalarClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }

}
