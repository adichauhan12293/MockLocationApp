package adichauhan.com.reactivecomponents.provider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import adichauhan.com.entities.Optional;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class MapObservableProvider {
    
    public static Observable<GoogleMap> getMapReadyObservable(final SupportMapFragment supportMapFragment) {
        return Observable.create(new ObservableOnSubscribe<GoogleMap>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<GoogleMap> e) throws Exception {
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

    public static Observable<Optional<Boolean>> getMapCameraIdleObservable(final GoogleMap googleMap) {
        return Observable.create(new ObservableOnSubscribe<Optional<Boolean>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Optional<Boolean>> e) throws Exception {
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (e != null)
                            e.onNext(Optional.of(true));
                    }
                });
            }
        });
    }

}