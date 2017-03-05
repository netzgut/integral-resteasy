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

import java.io.IOException;
import java.util.Base64;
import java.util.function.BiConsumer;

import javax.ws.rs.container.ContainerRequestContext;

import org.apache.tapestry5.ioc.internal.util.InternalUtils;

/**
 * Small utility for handling basic HTTP authentication.
 */
public class BasicHttpAuthUtil {

    public static boolean handleBasicHttpAuth(ContainerRequestContext requestContext,
                                              BiConsumer<String, String> handler) {
        String headerValue = requestContext.getHeaderString("Authorization");
        return handleBasicHttpAuth(headerValue, handler);
    }

    public static boolean handleBasicHttpAuth(String authHeader, BiConsumer<String, String> handler) {
        if (InternalUtils.isBlank(authHeader) || authHeader.startsWith("Basic ") == false) {
            return false;
        }

        String encodedUserPassword = authHeader.replaceFirst("Basic ", "");
        String usernameAndPassword = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        String[] split = usernameAndPassword.split(":");
        if (split == null || split.length != 2) {
            return false;
        }

        handler.accept(split[0], split[1]);

        return true;
    }
}
