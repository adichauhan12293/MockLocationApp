package adichauhan.com.mocklocationapplication.modules.mocklocation.di.module;

import android.content.Context;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import dagger.Module;
import dagger.Provides;

/**
 * Created by adityachauhan on 10/4/17.
 */

@Module
public class PlacesModule {

    private final Context context;

    public PlacesModule(Context context) {
        this.context = context;
    }

    @Provides
    GeoDataClient geoDataClient() {
        return Places.getGeoDataClient(context);
    }
}
