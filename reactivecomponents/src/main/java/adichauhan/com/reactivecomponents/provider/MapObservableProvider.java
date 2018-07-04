package adichauhan.com.reactivecomponents.provider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class MapObservableProvider {
    
    public static Observable<GoogleMap> getMapReadyObservable(final SupportMapFragment supportMapFragment) {
        return Observable.create(new ObservableOnSubscribe<GoogleMap>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<GoogleMap> e) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        e.onNext(googleMap);
                        e.onComplete();
                    }
                });
            }
        });
    }
}