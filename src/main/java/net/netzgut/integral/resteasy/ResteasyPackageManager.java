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
package net.netzgut.integral.resteasy;

// Original project:    Tapestry-Resteasy https://github.com/tynamo/tapestry-resteasy
// Original module:     tapestry-resteasy
// Original file:       org.tynamo.resteasy.ResteasyPackageManager

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

/**
 * Contains a set of contributed package names from which to load REST resources
 * <p/>
 * The service's configuration is the names of Java packages to search for REST resources.
 */
@UsesConfiguration(String.class)
public interface ResteasyPackageManager {

    /**
     * Returns packages from which read REST resource classes
     */
    Collection<String> getPackageNames();
}
