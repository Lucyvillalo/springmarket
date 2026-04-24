package sv.edu.udb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/frontend/**")
                .addResourceLocations("file:frontend/");
        
        registry.addResourceHandler("/css/**")
                .addResourceLocations("file:frontend/css/");
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("file:frontend/js/");
        
        registry.addResourceHandler("/pages/**")
                .addResourceLocations("file:frontend/pages/");
    }
}
