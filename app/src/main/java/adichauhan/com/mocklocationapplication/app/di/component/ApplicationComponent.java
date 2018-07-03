package adichauhan.com.mocklocationapplication.app.di.component;

import com.google.android.gms.location.FusedLocationProviderClient;

import adichauhan.com.mocklocationapplication.app.di.annotations.scope.ApplicationScope;
import adichauhan.com.mocklocationapplication.app.di.module.ApplicationLocationModule;
import adichauhan.com.mocklocationapplication.app.di.module.ContextModule;
import adichauhan.com.mocklocationapplication.app.di.module.GoogleServiceModule;
import adichauhan.com.mocklocationapplication.app.service.GoogleService;
import dagger.Component;

/**
 * Created by adityachauhan on 10/4/17.
 *
 */

@ApplicationScope
@Component(modules = {GoogleServiceModule.class,ContextModule.class, ApplicationLocationModule.class})
public interface ApplicationComponent {

    GoogleService getGoogleService();

    FusedLocationProviderClient getFusedLocationProviderClient();
}
