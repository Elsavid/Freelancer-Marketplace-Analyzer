package modules;

import com.google.inject.AbstractModule;

import services.ApiService;
import services.ApiServiceInterface;

public class ApiServiceModule extends AbstractModule {
    
    @Override
    protected final void configure() {
        bind(ApiServiceInterface.class).to(ApiService.class);
    }
}
