package adichauhan.com.mocklocationapplication.app;

import android.support.multidex.MultiDexApplication;

import adichauhan.com.mocklocationapplication.app.di.component.ApplicationComponent;
import adichauhan.com.mocklocationapplication.app.di.component.DaggerApplicationComponent;
import adichauhan.com.mocklocationapplication.app.di.module.ApplicationLocationModule;
import adichauhan.com.mocklocationapplication.app.di.module.ContextModule;

/**
 * Created by adityachauhan on 10/10/17.
 *
 */

public class MockLocationApplication extends MultiDexApplication {

    private ApplicationComponent component;
    private static MockLocationApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = DaggerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationLocationModule(new ApplicationLocationModule(this))
                .build();
    }

    public static MockLocationApplication getInstance() {
        return instance;
    }

    public ApplicationComponent component() {
        return component;
    }
}