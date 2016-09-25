// Copyright 2016 Netzgut GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Original project:    Tapestry-Resteasy https://github.com/tynamo/tapestry-resteasy
// Original module:     tapestry-resteasy
// Original file:       org.tynamo.resteasy.ResteasySymbols

package net.netzgut.integral.resteasy;

public class ResteasySymbols {

    /**
     * Set this if you map the Resteasy servlet to something other than /*
     */
    public static final String MAPPING_PREFIX        = "resteasy.servlet.mapping.prefix";

    /**
     * If "true", then the InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ResteasySymbols.AUTOSCAN_PACKAGE_NAME
     * package will be added to the ResteasyPackageManager so that it will be scanned for annotated REST resource classes.
     */
    public static final String AUTOSCAN              = "integral.resteasy.autoscan";

    /**
     * Name of the package to include in ResteasyPackageManager for AUTOSCAN. Do not include the dot,
     * e.g. "rest" instea of ".rest".
     */
    public static final String AUTOSCAN_PACKAGE_NAME = "integral.resteasy.autoscan-package-name";

    /**
     * add the CORS authorization to the header
     */
    public static final String CORS_ENABLED          = "integral.resteasy.cors-enabled";
}
