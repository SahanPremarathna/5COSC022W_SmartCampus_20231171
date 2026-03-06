package com.mycompany.cosc022w_smartcampus_20231171.config;

import com.mycompany.cosc022w_smartcampus_20231171.filter.LoggingFilter;
import com.mycompany.cosc022w_smartcampus_20231171.mapper.GlobalExceptionMapper;
import com.mycompany.cosc022w_smartcampus_20231171.mapper.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.cosc022w_smartcampus_20231171.mapper.RoomNotEmptyExceptionMapper;
import com.mycompany.cosc022w_smartcampus_20231171.mapper.SensorUnavailableExceptionMapper;
import com.mycompany.cosc022w_smartcampus_20231171.resource.DiscoveryResource;
import com.mycompany.cosc022w_smartcampus_20231171.resource.SensorResource;
import com.mycompany.cosc022w_smartcampus_20231171.resource.SensorRoomResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS bootstrap class that defines API base path and registers components.
 */
@ApplicationPath("/api/v1")
public class RestApplication extends Application {

    /**
     * Registers all resources, exception mappers, and filters explicitly.
     *
     * @return registered JAX-RS classes
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // Core resources.
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);
        // Exception mapping strategy.
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);
        // Cross-cutting request/response logging.
        classes.add(LoggingFilter.class);
        return classes;
    }
}
