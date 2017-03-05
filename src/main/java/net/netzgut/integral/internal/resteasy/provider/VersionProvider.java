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
package net.netzgut.integral.internal.resteasy.provider;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.netzgut.integral.resteasy.annotations.Version;
import net.netzgut.integral.resteasy.utils.ResteasyUtil;

/**
 * Adds the API version and deprecation if provided.
 */
@Provider
public class VersionProvider implements ContainerResponseFilter {

    private static final Logger log = LoggerFactory.getLogger(VersionProvider.class);

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        ResourceMethodInvoker resourceMethodInvoker = ResteasyUtil.getResourceMethodInvoker(requestContext);

        Version version = ResteasyUtil.getAnnotation(resourceMethodInvoker, Version.class);
        if (version == null) {
            log.warn("Couldn't find @Version {}#{}",
                     resourceMethodInvoker.getResourceClass().getName(),
                     resourceMethodInvoker.getMethod().getName());
            return;
        }

        responseContext.getHeaders().put("Api-Version", Arrays.asList(version.value()));
        if (version.deprecated() != null && version.deprecated().length() > 0) {
            responseContext.getHeaders().put("Api-Deprecated", Arrays.asList(version.deprecated()));
        }
    }
}
