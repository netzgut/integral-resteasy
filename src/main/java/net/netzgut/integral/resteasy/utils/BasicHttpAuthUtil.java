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
