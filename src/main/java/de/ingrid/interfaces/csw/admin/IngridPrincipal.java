/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.interfaces.csw.admin;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginContext;

public abstract class IngridPrincipal implements Principal {

    private final Set<String> _roles;
    private final String _name;
    private final String _password;

    public IngridPrincipal(String name, String password, Set<String> roles) {
        _name = name;
        _password = password;
        _roles = roles;
    }

    public Set<String> getRoles() {
        return _roles;
    }

    public String getPassword() {
        return _password;
    }

    @Override
    public String getName() {
        return _name;
    }

    public boolean isInRole(String role) {
        return _roles.contains(role);
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        IngridPrincipal other = (IngridPrincipal) obj;
        return other._name.equals(_name);
    }

    @Override
    public String toString() {
        return _name;
    }

    public abstract boolean isAuthenticated();

    public static class KnownPrincipal extends IngridPrincipal {

        private LoginContext _loginContext;

        public KnownPrincipal(String name, String password, Set<String> roles) {
            super(name, password, roles);
        }

        public LoginContext getLoginContext() {
            return _loginContext;
        }

        public void setLoginContext(LoginContext loginContext) {
            _loginContext = loginContext;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }
    }

    public static class SuperAdmin extends KnownPrincipal {

        public SuperAdmin(String name) {
            super(name, null, new HashSet<String>());
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public boolean isInRole(String role) {
            return true;
        }
    }

    public static class UnknownPrincipal extends IngridPrincipal {

        public UnknownPrincipal() {
            super("Anonymous", null, new HashSet<String>());
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

    }
}
