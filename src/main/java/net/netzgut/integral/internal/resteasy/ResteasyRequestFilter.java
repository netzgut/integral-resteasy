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
package net.netzgut.integral.internal.resteasy;

// Original project:    Tapestry-Resteasy https://github.com/tynamo/tapestry-resteasy
// Original module:     tapestry-resteasy
// Original file:       org.tynamo.resteasy.ResteasyRequestFilter

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.services.CheckForUpdatesFilter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.IntermediateType;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.util.TimeInterval;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.UpdateListenerHub;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpRequestFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpResponseFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.netzgut.integral.resteasy.ResteasySymbols;
import net.netzgut.integral.resteasy.header.ResteasyCustomerHeaderService;

public class ResteasyRequestFilter implements HttpServletRequestFilter, HttpRequestFactory, HttpResponseFactory {

    private static final Logger                 log          = LoggerFactory.getLogger(ResteasyRequestFilter.class);

    private final ServletContainerDispatcher    servletContainerDispatcher;
    private final Dispatcher                    dispatcher;
    private final ResteasyProviderFactory       providerFactory;
    private final ApplicationGlobals            globals;

    private final Pattern                       filterPattern;

    private final boolean                       productionMode;

    private final CheckForUpdatesFilter         checkForUpdatesFilter;

    private final RequestHandler                dummyHandler = (request, response) -> false;
    private final ResteasyCustomerHeaderService headerProviderManager;

    public ResteasyRequestFilter(@Inject @Symbol(ResteasySymbols.MAPPING_PREFIX) String filterPath,
                                 ApplicationGlobals globals,
                                 Application application,
                                 SymbolSource source,
                                 @Symbol(SymbolConstants.PRODUCTION_MODE) boolean productionMode,
                                 UpdateListenerHub updateListenerHub,
                                 @Symbol(SymbolConstants.FILE_CHECK_INTERVAL) @IntermediateType(TimeInterval.class) long checkInterval,
                                 @Symbol(SymbolConstants.FILE_CHECK_UPDATE_TIMEOUT) @IntermediateType(TimeInterval.class) long updateTimeout,
                                 ResteasyCustomerHeaderService headerProviderManager) throws ServletException {
        this.headerProviderManager = headerProviderManager;
        this.filterPattern = Pattern.compile(filterPath + ".*", Pattern.CASE_INSENSITIVE);
        this.globals = globals;
        this.productionMode = productionMode;

        ListenerBootstrap bootstrap = new ResteasyBootstrap(globals.getServletContext(), source);

        this.servletContainerDispatcher = new ServletContainerDispatcher();
        this.servletContainerDispatcher.init(globals.getServletContext(), bootstrap, this, this);
        this.dispatcher = this.servletContainerDispatcher.getDispatcher();
        this.providerFactory = this.servletContainerDispatcher.getDispatcher().getProviderFactory();
        processApplication(application);

        this.checkForUpdatesFilter = new CheckForUpdatesFilter(updateListenerHub, checkInterval, updateTimeout);
    }

    @Override
    public boolean service(HttpServletRequest request,
                           HttpServletResponse response,
                           HttpServletRequestHandler handler) throws IOException {

        String path = getPath(request);

        if (this.filterPattern.matcher(path).matches() == false) {
            return handler.service(request, response);
        }

        if (!this.productionMode) {
            this.checkForUpdatesFilter.service(null, null, this.dummyHandler);
        }

        try {
            this.servletContainerDispatcher.service(request.getMethod(), request, response, true);
            this.headerProviderManager.provide(request, response);
        }
        // this exception is thrown when request contains illegal characters
        catch (ReaderException ex) {
            log.warn("Problem occured reading REST request: " + ex.getMessage(), ex);
            return false;
        }

        return true;
    }

    private String getPath(HttpServletRequest request) {
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        if (pathInfo != null) {
            path += pathInfo;
        }

        return path;
    }

    @Override
    public HttpRequest createResteasyHttpRequest(String httpMethod,
                                                 HttpServletRequest request,
                                                 ResteasyHttpHeaders headers,
                                                 ResteasyUriInfo uriInfo,
                                                 HttpResponse theResponse,
                                                 HttpServletResponse response) {
        return new HttpServletInputMessage(request,
                                           response,
                                           this.globals.getServletContext(),
                                           theResponse,
                                           headers,
                                           uriInfo,
                                           httpMethod.toUpperCase(),
                                           (SynchronousDispatcher) this.dispatcher);
    }

    @Override
    public HttpResponse createResteasyHttpResponse(HttpServletResponse response) {
        return createServletResponse(response);
    }

    protected HttpResponse createServletResponse(HttpServletResponse response) {
        return new HttpServletResponseWrapper(response, this.providerFactory);
    }

    private void processApplication(Application config) {
        log.info("Deploying {}: {}", Application.class.getName(), config.getClass());

        List<Class<?>> actualResourceClasses = new ArrayList<>();
        List<Class<?>> actualProviderClasses = new ArrayList<>();

        List<Object> resources = new ArrayList<>();
        List<Object> providers = new ArrayList<>();

        if (config.getClasses() != null) {
            for (Class<?> clazz : config.getClasses()) {
                if (GetRestful.isRootResource(clazz)) {
                    actualResourceClasses.add(clazz);
                }
                else if (clazz.isAnnotationPresent(Provider.class)) {
                    actualProviderClasses.add(clazz);
                }
                else {
                    throw new RuntimeException("Application.getClasses() returned unknown class type: "
                                               + clazz.getName());
                }
            }
        }

        if (config.getSingletons() != null) {
            for (Object obj : config.getSingletons()) {
                if (GetRestful.isRootResource(obj.getClass())) {
                    log.info("Adding singleton resource {} from Appliction {}",
                             obj.getClass().getName(),
                             Application.class.getName());
                    resources.add(obj);
                }
                else if (obj.getClass().isAnnotationPresent(Provider.class)) {
                    providers.add(obj);
                }
                else {
                    throw new RuntimeException("Application.getSingletons() returned unknown class type: "
                                               + obj.getClass().getName());
                }
            }
        }

        actualProviderClasses.forEach(this.providerFactory::registerProvider);
        providers.forEach(this.providerFactory::registerProviderInstance);

        actualResourceClasses.forEach(this.dispatcher.getRegistry()::addPerRequestResource);
        resources.forEach(this.dispatcher.getRegistry()::addSingletonResource);
    }
}
