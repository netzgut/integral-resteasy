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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.internal.util.InternalUtils;

import net.netzgut.integral.resteasy.header.ResteasyCustomerHeaderService;
import net.netzgut.integral.resteasy.header.ResteasyHeaderProvider;

public class ResteasyCustomHeaderServiceImplementation implements ResteasyCustomerHeaderService {

    private final Collection<ResteasyHeaderProvider> providers;

    public ResteasyCustomHeaderServiceImplementation(Collection<ResteasyHeaderProvider> providers) {
        this.providers = providers;
    }

    @Override
    public void provide(HttpServletRequest request, HttpServletResponse response) {
        for (ResteasyHeaderProvider provider : this.providers) {
            String name = provider.getName(request, response);
            if (InternalUtils.isBlank(name)) {
                continue;
            }
            String value = provider.getValue(request, response);
            if (InternalUtils.isBlank(value)) {
                continue;
            }

            response.setHeader(name, value);
        }
    }
}
