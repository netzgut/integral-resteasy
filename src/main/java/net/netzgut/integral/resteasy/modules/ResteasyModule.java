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
package net.netzgut.integral.resteasy.modules;

// Original project:    Tapestry-Resteasy https://github.com/tynamo/tapestry-resteasy
// Original module:     tapestry-resteasy
// Original file:       org.tynamo.resteasy.ResteasyModule

import java.util.Collection;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ClassNameLocator;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.HttpServletRequestHandler;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;

import net.netzgut.integral.internal.resteasy.ResteasyApplication;
import net.netzgut.integral.internal.resteasy.ResteasyCustomHeaderServiceImplementation;
import net.netzgut.integral.internal.resteasy.ResteasyRequestFilter;
import net.netzgut.integral.internal.resteasy.header.CorsHeaderProvider;
import net.netzgut.integral.internal.resteasy.provider.VersionProvider;
import net.netzgut.integral.resteasy.ResteasyPackageManager;
import net.netzgut.integral.resteasy.ResteasySymbols;
import net.netzgut.integral.resteasy.header.ResteasyCustomerHeaderService;
import net.netzgut.integral.resteasy.header.ResteasyHeaderProvider;

public class ResteasyModule {

    public static final String REQUEST_FILTER_ID             = "ResteasyRequestFilter";
    public static final String DEFAULT_MAPPING_PREFIX        = "/rest";
    public static final String DEFAULT_AUTOSCAN_PACKAGE_NAME = "rest";

    public static void bind(ServiceBinder binder) {
        binder.bind(Application.class, ResteasyApplication.class);
        binder.bind(HttpServletRequestFilter.class, ResteasyRequestFilter.class)
              .withId(ResteasyModule.REQUEST_FILTER_ID);
        binder.bind(ResteasyCustomerHeaderService.class, ResteasyCustomHeaderServiceImplementation.class);
    }

    @FactoryDefaults
    @Contribute(SymbolProvider.class)
    public static void supplyFactoryDefaults(MappedConfiguration<String, String> conf) {
        conf.add(ResteasySymbols.MAPPING_PREFIX, ResteasyModule.DEFAULT_MAPPING_PREFIX);
        conf.add(ResteasySymbols.AUTOSCAN, Boolean.TRUE.toString());
        conf.add(ResteasySymbols.AUTOSCAN_PACKAGE_NAME, ResteasyModule.DEFAULT_AUTOSCAN_PACKAGE_NAME);
        conf.add(ResteasySymbols.CORS_ENABLED, Boolean.FALSE.toString());
        conf.add(ResteasySymbols.VERSIONING_ENABLED, Boolean.FALSE.toString());
    }

    @Contribute(HttpServletRequestHandler.class)
    public static void supplyHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> configuration,
                                                       @InjectService(ResteasyModule.REQUEST_FILTER_ID) HttpServletRequestFilter resteasyRequestFilter) {
        configuration.add(ResteasyModule.REQUEST_FILTER_ID, resteasyRequestFilter, "after:IgnoredPaths", "before:GZIP");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Contribute(javax.ws.rs.core.Application.class)
    public static void supplyJavaxWsRsCoreApplication(Configuration<Object> singletons,
                                                      ObjectLocator locator,
                                                      ResteasyPackageManager resteasyPackageManager,
                                                      ClassNameLocator classNameLocator,
                                                      Logger logger) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        for (String packageName : resteasyPackageManager.getPackageNames()) {
            for (String className : classNameLocator.locateClassNames(packageName)) {
                try {
                    Class clazz = contextClassLoader.loadClass(className);
                    Class rootResourceClass = GetRestful.getRootResourceClass(clazz);

                    if (rootResourceClass != null) {
                        if (rootResourceClass.equals(clazz)) {
                            if (!clazz.isInterface()) {
                                singletons.add(locator.autobuild(clazz));
                            }
                        }
                        else {
                            try {
                                singletons.add(locator.getService(rootResourceClass));
                            }
                            catch (RuntimeException e) {
                                logger.info(e.getMessage());
                                logger.info("Trying to create a proxy for {}", rootResourceClass.getName());
                                singletons.add(locator.proxy(rootResourceClass, clazz));
                            }
                        }
                    }
                    else if (clazz.isAnnotationPresent(Provider.class)) {
                        singletons.add(locator.autobuild(clazz));
                    }
                }
                catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * Contributes the package "&lt;root&gt;.rest" (InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ResteasyModule.)
     * to the configuration, so that it will be scanned for annotated REST resource classes.
     */
    @Contribute(ResteasyPackageManager.class)
    public static void supplyResteasyPackageManager(Configuration<String> configuration,
                                                    @Symbol(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM) String appPackage,
                                                    @Symbol(ResteasySymbols.AUTOSCAN) Boolean autoScan,
                                                    @Symbol(ResteasySymbols.AUTOSCAN_PACKAGE_NAME) String autoScanPackageName) {
        if (autoScan) {
            configuration.add(String.format("%s.%s", appPackage, autoScanPackageName));
        }
    }

    public static ResteasyPackageManager buildResteasyPackageManager(final Collection<String> packageNames) {
        return () -> packageNames;
    }

    @Contribute(ResteasyCustomerHeaderService.class)
    public static void supplyHeaderProviderManager(Configuration<ResteasyHeaderProvider> configuration,
                                                   @Symbol(ResteasySymbols.CORS_ENABLED) boolean corsEnabled) {
        if (corsEnabled) {
            configuration.addInstance(CorsHeaderProvider.class);
        }
    }

    @Contribute(Application.class)
    public static void configureRestProviders(Configuration<Object> singletons,
                                              @Symbol(ResteasySymbols.VERSIONING_ENABLED) boolean versioningEnabled) {
        if (versioningEnabled) {
            singletons.addInstance(VersionProvider.class);
        }
    }
}
