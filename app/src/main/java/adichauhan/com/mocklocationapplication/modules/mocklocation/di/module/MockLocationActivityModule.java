package adichauhan.com.mocklocationapplication.modules.mocklocation.di.module;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.GeoDataClient;

import adichauhan.com.mocklocationapplication.app.service.GoogleService;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.annotations.scope.MockLocationActivityScope;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.model.MockLocationActivityModel;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.presenter.MockLocationActivityPresenter;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityView;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityViewState;
import dagger.Module;
import dagger.Provides;

/**
 * Created by adityachauhan on 10/9/17.
 *
 */

@Module
public class MockLocationActivityModule {

    private final AppCompatActivity activity;

    public MockLocationActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @MockLocationActivityScope
    MockLocationActivityView mockLocationActivityView(MockLocationActivityViewState initState) {
        return new MockLocationActivityView(activity, initState);
    }

    @Provides
    @MockLocationActivityScope
    MockLocationActivityPresenter mockLocationActivityPresenter(MockLocationActivityView view,
                                                                MockLocationActivityModel model) {
        return new MockLocationActivityPresenter(activity, view, model);
    }

    @Provides
    @MockLocationActivityScope
    MockLocationActivityModel mockLocationActivityModel(GoogleService googleService,
                                          FusedLocationProviderClient fusedLocationProviderClient,
                                          GeoDataClient geoDataClient) {
        return new MockLocationActivityModel(activity,googleService, fusedLocationProviderClient,geoDataClient);
    }
}
