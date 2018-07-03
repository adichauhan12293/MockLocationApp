package adichauhan.com.mocklocationapplication.app.di.module;

import android.content.Context;

import adichauhan.com.mocklocationapplication.app.di.annotations.scope.ApplicationScope;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    @ApplicationScope
    public Context context() {
        return context;
    }
}
