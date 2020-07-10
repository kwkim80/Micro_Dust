package ca.algonquin.kw2446.microdust.util;

import ca.algonquin.kw2446.microdust.model.FineDust;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FineDustApi {
    String BASE_URL = "http://api.weatherplanet.co.kr/";

    @Headers("appKey: 6b200e091d1a4d7e83fb9b4732809b33")
    // 쿼리
    @GET("weather/dust?version=1")
    Call<FineDust> getFineDust(@Query("lat") double latitude,
                               @Query("lon") double longitude);
}
