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
package net.netzgut.integral.internal.resteasy.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.internal.util.InternalUtils;

import net.netzgut.integral.resteasy.header.ResteasyHeaderProvider;

/**
 * Provives the "Access-Control-Allow-Origin" header if request has Origin header.
 */
public class CorsHeaderProvider implements ResteasyHeaderProvider {

    private static final String HEADER_ORIGIN = "Origin";

    @Override
    public String getName(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader(HEADER_ORIGIN);
        if (InternalUtils.isBlank(origin)) {
            return null;
        }
        return "Access-Control-Allow-Origin";
    }

    @Override
    public String getValue(HttpServletRequest request, HttpServletResponse response) {
        return request.getHeader(HEADER_ORIGIN);
    }

}
