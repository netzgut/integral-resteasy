package net.netzgut.integral.resteasy.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext;

import org.jboss.resteasy.core.ResourceMethodInvoker;

public class ResteasyUtil {

    /**
     * Extracts an {@link Annotation} from a {@link ContainerRequestContext}. Method annotations
     * have precedence over class annotations.
     */
    public static <A extends Annotation> A getAnnotation(ContainerRequestContext requestContext,
                                                         Class<A> annotationClass) {
        ResourceMethodInvoker methodInvoker = getResourceMethodInvoker(requestContext);
        return getAnnotation(methodInvoker, annotationClass);
    }

    /**
     * Extracts an {@link Annotation} from a {@link ResourceMethodInvoker}. Method annotations
     * have precedence over class annotations.
     */
    public static <A extends Annotation> A getAnnotation(ResourceMethodInvoker invoker, Class<A> annotationClass) {
        Method method = invoker.getMethod();
        Class<?> clazz = invoker.getResourceClass();

        A annotation = clazz.getAnnotation(annotationClass);
        if (method.isAnnotationPresent(annotationClass)) {
            annotation = method.getAnnotation(annotationClass);
        }

        return annotation;
    }

    /**
     * Gets the {@link ResourceMethodInvoker} from the {@link ContainerRequestContext}.
     */
    public static ResourceMethodInvoker getResourceMethodInvoker(ContainerRequestContext requestContext) {
        return (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
    }

}
