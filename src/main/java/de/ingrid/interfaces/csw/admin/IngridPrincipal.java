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
