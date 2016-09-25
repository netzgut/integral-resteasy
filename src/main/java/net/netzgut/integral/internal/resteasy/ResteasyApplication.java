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
// Original file:       org.tynamo.resteasy.Application

package net.netzgut.integral.internal.resteasy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ResteasyApplication extends javax.ws.rs.core.Application {

    private final Set<Object>   singletons;
    private final Set<Class<?>> classes = new HashSet<>();

    public ResteasyApplication(Collection<Object> singletons) {
        this.singletons = new HashSet<>(singletons);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
}
