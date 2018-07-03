package adichauhan.com.mocklocationapplication.app.di.module;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import adichauhan.com.mocklocationapplication.app.di.annotations.scope.ApplicationScope;
import adichauhan.com.mocklocationapplication.app.service.GoogleService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by aditya on 23/03/18.
 *
 */

@Module(includes = NetworkModule.class)
public class GoogleServiceModule {

    @Provides
    @ApplicationScope
    GoogleService apcoaService(Retrofit retrofit) {
        return retrofit.create(GoogleService.class);
    }

    @Provides
    @ApplicationScope
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    @Provides
    @ApplicationScope
    Retrofit retrofit(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
