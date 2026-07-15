package com.alnoor.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

// Serves the React SPA's static bundle and falls back to index.html for any
// path that isn't a real static asset (client-side routes like /bookings,
// /resources/123), so React Router owns navigation. Registered as a
// resource handler (lower priority than @RestController mappings, so
// /api/** is untouched) rather than a @GetMapping catch-all pattern.
//
// A prior version of this file used
// `@GetMapping(value = {"/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})`, which
// Spring 6's PathPatternParser rejects outright with "No more pattern data
// allowed after ** " — it fails at startup on any current Spring Boot 3.x
// version. This resource-handler approach has no such pattern restriction
// and is the standard idiom for SPA fallback in Spring MVC.
@Configuration
public class SpaController implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        return requested.exists() && requested.isReadable()
                                ? requested
                                : new ClassPathResource("/static/index.html");
                    }
                });
    }
}
