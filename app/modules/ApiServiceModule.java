package modules;

import com.google.inject.AbstractModule;

import services.ApiService;
import services.ApiServiceInterface;

/**
 * Module to bind the ApiServiceInterface to its implementation (ApiService class)
 *
 * @author Yan Ren
 */
public class ApiServiceModule extends AbstractModule {
    
    @Override
    protected final void configure() {
        bind(ApiServiceInterface.class).to(ApiService.class);
    }
}
