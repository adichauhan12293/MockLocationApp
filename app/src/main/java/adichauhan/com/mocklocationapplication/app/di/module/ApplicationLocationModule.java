package adichauhan.com.mocklocationapplication.app.di.module;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import adichauhan.com.mocklocationapplication.app.di.annotations.scope.ApplicationScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by adityachauhan on 10/4/17.
 *
 */

@Module
public class ApplicationLocationModule {

    private final Context context;

    public ApplicationLocationModule(Context context) {
        this.context = context;
    }

    @Provides
    @ApplicationScope
    FusedLocationProviderClient fusedLocationProviderClient() {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
