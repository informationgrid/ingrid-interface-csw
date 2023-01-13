/*-
 * **************************************************-
 * InGrid Interface CSW
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys.CSWT_USERS;
import static de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys.INGRID_ADMIN_PASSWORD;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password(ApplicationProperties.get(INGRID_ADMIN_PASSWORD))
                .roles("admin")
                .build();

        String[] users = ApplicationProperties.get(CSWT_USERS, ",").split(",");
        List<UserDetails> userDetails = mapCswtUserConfigToUserDetails(users);

        userDetails.add(admin);

        return new InMemoryUserDetailsManager(userDetails);
    }

    private static List<UserDetails> mapCswtUserConfigToUserDetails(String[] users) {
        return Arrays.stream(users).map(user -> {
            String[] userAndPassword = user.split("::");
            return User.withUsername(userAndPassword[0])
                    .password(userAndPassword[1])
                    .roles("user")
                    .build();
        }).collect(Collectors.toList());
    }

    @Bean
    @Order(0)
    SecurityFilterChain resources(HttpSecurity http) throws Exception {
        http
                .requestMatchers((matchers) -> matchers.antMatchers("/login*", "/css/**", "/images/**", "/js/**"))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .requestCache().disable()
                .securityContext().disable()
                .sessionManagement().disable();

        return http.build();
    }

    @Configuration
    @Order(2)
    public static class App1ConfigurationAdapter {

        @Bean
        public SecurityFilterChain generalSecurity(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/csw").permitAll()
                    .antMatchers(HttpMethod.POST, "/csw").permitAll()
                    .and()
                    .requestMatchers()
                    .requestMatchers(new NegatedRequestMatcher(new OrRequestMatcher(
                            new AntPathRequestMatcher("/csw-t"),
                            new AntPathRequestMatcher("/error"))))
                    .and()
                    .authorizeRequests()
                    .anyRequest()
                    .hasRole("admin")
                    .and()
                    .formLogin()
                    .loginPage("/login.html")
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .loginProcessingUrl("/j_spring_security_check")
                    .defaultSuccessUrl("/welcome.html", true)
                    .failureUrl("/loginFailure.html")
                    .and()
                    .logout()
                    .logoutUrl("/perform_logout")
                    .deleteCookies("JSESSIONID");

            return http.build();
        }
    }

    @Configuration
    @Order(1)
    public static class App2ConfigurationAdapter {

        @Bean
        public SecurityFilterChain cswtSecurity(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/csw-t").and()
                    .authorizeRequests()
                    .anyRequest()
                    .hasAnyRole("admin", "user")
                    .and()
                    .httpBasic()
                    .and()
                    .csrf()
                    .disable();
            return http.build();
        }

    }

}
