package adichauhan.com.reactivecomponents.provider;

import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by adityachauhan on 10/18/17.
 */

public class LocationSearchObservableProvider {

    public static Observable<AutocompletePredictionBufferResponse> getLocationSearchObservable(final String searchText,
                                                                                               final GeoDataClient geoDataClient) {
        return Observable.create(new ObservableOnSubscribe<AutocompletePredictionBufferResponse>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull
                                  final ObservableEmitter<AutocompletePredictionBufferResponse> e) throws Exception {
                Task<AutocompletePredictionBufferResponse> searchResult = geoDataClient.getAutocompletePredictions(searchText,
                        null,null);
                searchResult.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@android.support.annotation.NonNull Exception ex) {
                        e.onError(ex);
                    }
                });
                searchResult.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AutocompletePredictionBufferResponse> task) {
                        if (task.isSuccessful()) {
                            AutocompletePredictionBufferResponse response = task.getResult();
                            if (response != null) {
                                e.onNext(response);
                            } else {
                                e.onComplete();
                            }
                        } else {
                            e.onComplete();
                        }
                    }
                });
            }
        });
    }
}