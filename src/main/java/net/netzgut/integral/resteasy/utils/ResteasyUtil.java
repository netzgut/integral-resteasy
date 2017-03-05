/**
 * Copyright 2017 Netzgut GmbH <info@netzgut.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
