package adichauhan.com.mocklocationapplication.modules.mocklocation.di.component;

import adichauhan.com.mocklocationapplication.app.di.component.ApplicationComponent;
import adichauhan.com.mocklocationapplication.modules.mocklocation.MockLocationActivity;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.annotations.scope.MockLocationActivityScope;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.module.MockLocationActivityModule;
import adichauhan.com.mocklocationapplication.modules.mocklocation.di.module.PlacesModule;
import dagger.Component;

/**
 * Created by adityachauhan on 10/4/17.
 *
 */

@MockLocationActivityScope
@Component(modules = {MockLocationActivityModule.class, PlacesModule.class},
        dependencies = {ApplicationComponent.class})
public interface MockLocationActivityComponent {
    void injectMockLocationActivity(MockLocationActivity mockLocationActivity);
}