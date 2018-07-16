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

import java.util.Optional;

// Original project:    Tapestry-Resteasy https://github.com/tynamo/tapestry-resteasy
// Original module:     tapestry-resteasy
// Original file:       org.tynamo.resteasy.TapestryResteasyBootstrap

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.services.SymbolSource;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;

public class ResteasyBootstrap extends ListenerBootstrap {

    private final SymbolSource source;

    public ResteasyBootstrap(ServletContext servletContext, SymbolSource source) {
        super(servletContext);
        this.source = source;
    }

    @Override
    public String getParameter(String name) {
        String val = null;

        try {
            val = this.source.valueForSymbol(name);
        }
        catch (RuntimeException e) {
            // Ignore symbol not found
        }

        return Optional.ofNullable(val).orElse(super.getParameter(name));
    }

}
