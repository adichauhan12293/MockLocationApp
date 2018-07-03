package adichauhan.com.reactivecomponents.provider;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import adichauhan.com.entities.Optional;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 * Created by adityachauhan on 10/8/17.
 *
 */

public class FusedLocationObservableProvider {

    public static Observable<Optional<Location>> getUserLocationUpdatesObservable(final Context context,
                                                                                  final FusedLocationProviderClient fusedLocationProviderClient) {
        return Observable.create(new ObservableOnSubscribe<Optional<Location>>() {
            @SuppressWarnings("MissingPermission")
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Optional<Location>> e) throws Exception {
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(2000);
                locationRequest.setFastestInterval(0);
                locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (e.isDisposed()) {
                            fusedLocationProviderClient.removeLocationUpdates(this);
                            return;
                        }
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            e.onNext(Optional.of(locationResult.getLastLocation()));
                        }
                    }
                }, context.getMainLooper());
            }
        });
    }
}