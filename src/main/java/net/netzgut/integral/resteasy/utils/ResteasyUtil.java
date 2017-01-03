package net.netzgut.integral.resteasy.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.container.ContainerRequestContext;

import org.jboss.resteasy.core.ResourceMethodInvoker;

public class ResteasyUtil {

    public static <A extends Annotation> A getAnnotation(ContainerRequestContext requestContext,
                                                         Class<A> annotationClass) {

        ResourceMethodInvoker methodInvoker = getResourceMethodInvoker(requestContext);
        return getAnnotation(methodInvoker, annotationClass);
    }

    public static <A extends Annotation> A getAnnotation(ResourceMethodInvoker invoker, Class<A> annotationClass) {
        Method method = invoker.getMethod();

        A annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            Class<?> clazz = invoker.getResourceClass();
            annotation = clazz.getAnnotation(annotationClass);
        }

        return annotation;
    }

    public static ResourceMethodInvoker getResourceMethodInvoker(ContainerRequestContext requestContext) {
        return (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
    }
}
