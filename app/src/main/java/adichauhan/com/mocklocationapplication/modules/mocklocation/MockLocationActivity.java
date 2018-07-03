package adichauhan.com.mocklocationapplication.modules.mocklocation;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import adichauhan.com.mocklocationapplication.R;
import adichauhan.com.mocklocationapplication.app.MockLocationApplication;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.component.DaggerMockLocationActivityComponent;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.component.MockLocationActivityComponent;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.module.MockLocationActivityModule;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.module.PlacesModule;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.presenter.MockLocationActivityPresenter;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityView;

public class MockLocationActivity extends AppCompatActivity {

    @Inject
    MockLocationActivityPresenter presenter;

    @Inject
    MockLocationActivityView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MockLocationActivityComponent component = DaggerMockLocationActivityComponent.builder()
                .mockLocationActivityModule(new MockLocationActivityModule(this))
                .placesModule(new PlacesModule(this))
                .applicationComponent(MockLocationApplication.getInstance().component())
                .build();
        component.injectMockLocationActivity(this);
        setContentView(view);
        presenter.onCreate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MockLocationActivityPresenter.LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.locationPermissionGranted();
                } else {
                    presenter.showLocationSnackbar();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
