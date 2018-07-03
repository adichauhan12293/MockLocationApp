package adichauhan.com.mocklocationapplication.app.service;

import java.util.List;

import adichauhan.com.entities.DirectionsResponse;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by aditya on 23/03/18.
 */

public interface GoogleService {
    @GET("/maps/api/directions/json")
    Observable<Response<DirectionsResponse>> getDirections(@Query("origin") String originPlaceId,
                                                           @Query("destination") String destinationPlaceId);
}
