/*-
 * **************************************************-
 * InGrid Interface CSW
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
import static org.springframework.security.config.Customizer.withDefaults;

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

    /**
     * Security Filter für /csw-t mit Basic Authentication
     */
    @Bean
    @Order(1)
    public SecurityFilterChain basicAuthFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/csw-t"),
                        new AntPathRequestMatcher("/error")))  // Gilt nur für /csw-t
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/csw-t").hasAnyRole("user", "admin")
                )
                .httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable);  // Falls CSRF-Schutz nicht nötig ist

        return http.build();
    }

    /**
     * Standard Security Filter für alle anderen Endpunkte
     */
    @Bean
    @Order(2)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(new NegatedRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/csw-t"),
                        new AntPathRequestMatcher("/error"))))
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/csw", "/login*", "/WEB-INF/jsp/login**", "/css/**", "/images/**", "/js/**").permitAll()
                    .anyRequest().hasRole("admin")
            )
            .formLogin((form) -> form
                    .loginPage("/login.html")                  // Benutzerdefinierte Login-Seite
                    .usernameParameter("j_username")          // Benutzername-Feld
                    .passwordParameter("j_password")          // Passwort-Feld
                    .loginProcessingUrl("/j_spring_security_check") // URL, die das Login-Formular verarbeitet
                    .defaultSuccessUrl("/welcome.html", true) // Bei erfolgreichem Login
                    .failureUrl("/loginFailure.html")          // Bei fehlerhaftem Login
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /*@Bean
    @Order(1) // Diese FilterChain hat höchste Priorität
    public SecurityFilterChain basicAuthSecurity(HttpSecurity http) throws Exception {
        http
//                .securityMatcher("/csw-t") // Nur für den Pfad "/csw-t"
                .csrf(AbstractHttpConfigurer::disable) // Optional: CSRF deaktivieren bei Basic Auth
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/csw-t").hasAnyRole("user", "admin")
                )
                .httpBasic(withDefaults()); // Basic Authentication aktivieren

        return http.build();
    }

    // FilterChain 2: Für alle anderen Pfade (inkl. "/csw")
    @Bean
    public SecurityFilterChain formLoginSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Optional
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login*", "/WEB-INF/jsp/login**","/css/**", "/images/**", "/js/**").permitAll()
                        .requestMatchers("/csw").permitAll() // Pfad "/csw" ist öffentlich zugänglich
//                        .anyRequest().authenticated() // Alle anderen Pfade erfordern Authentifizierung
                        .requestMatchers(new NegatedRequestMatcher(new OrRequestMatcher(
                                new AntPathRequestMatcher("/csw-t"),
                                new AntPathRequestMatcher("/error")))).hasRole("admin")
                )
                .formLogin((form) -> form
                        .loginPage("/login.html")                  // Benutzerdefinierte Login-Seite
                        .usernameParameter("j_username")          // Benutzername-Feld
                        .passwordParameter("j_password")          // Passwort-Feld
                        .loginProcessingUrl("/j_spring_security_check") // URL, die das Login-Formular verarbeitet
                        .defaultSuccessUrl("/welcome.html", true) // Bei erfolgreichem Login
                        .failureUrl("/loginFailure.html")          // Bei fehlerhaftem Login
                )
                .logout((logout) -> logout
                        .logoutUrl("/perform_logout")
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }*/
/*


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .securityMatcher("/login*", "/css/**", "/images/**", "/js/**").
//                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
//                .requestCache(RequestCacheConfigurer::disable)
//                .securityContext(AbstractHttpConfigurer::disable)
//                .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login*", "/WEB-INF/jsp/login**","/css/**", "/images/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/csw").permitAll()
                        .requestMatchers(HttpMethod.POST, "/csw").permitAll()
                        .requestMatchers("/csw-t").hasAnyRole("user", "admin")
                        .anyRequest().authenticated()
//                        .requestMatchers(new NegatedRequestMatcher(new OrRequestMatcher(
//                                new AntPathRequestMatcher("/csw-t"),
//                                new AntPathRequestMatcher("/error")))).hasRole("admin")
                )
                .httpBasic(basicAuth -> basicAuth.realmName("InGrid CSW"))
                .formLogin((form) -> form
                        .loginPage("/login.html")
                        .usernameParameter("j_username")
                        .passwordParameter("j_password")
                        .loginProcessingUrl("/j_spring_security_check")
                        .defaultSuccessUrl("/welcome.html", true)
                        .failureUrl("/loginFailure.html")
                )
                .logout((logout) -> logout
                        .logoutUrl("/perform_logout")
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }
*/

/*
    @Configuration
    public static class App1ConfigurationAdapter {

        @Bean
        public SecurityFilterChain generalSecurity(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers(HttpMethod.GET, "/csw").permitAll()
                            .requestMatchers(HttpMethod.POST, "/csw").permitAll()
                            .requestMatchers(new NegatedRequestMatcher(new OrRequestMatcher(
                                    new AntPathRequestMatcher("/csw-t"),
                                    new AntPathRequestMatcher("/error")))).hasRole("admin")
                            .requestMatchers("/csw-t").hasAnyRole("user", "admin")

                    )
                    .httpBasic(Customizer.withDefaults())
                    .formLogin((form) -> form
                            .loginPage("/login.html")
                            .usernameParameter("j_username")
                            .passwordParameter("j_password")
                            .loginProcessingUrl("/j_spring_security_check")
                            .defaultSuccessUrl("/welcome.html", true)
                            .failureUrl("/loginFailure.html")
                    )
                    .logout((logout) -> logout
                            .logoutUrl("/perform_logout")
                            .deleteCookies("JSESSIONID")
                    );

            return http.build();
        }
    }
*/

/*    @Configuration
    @Order(1)
    public static class App2ConfigurationAdapter {

        @Bean
        public SecurityFilterChain cswtSecurity(HttpSecurity http) throws Exception {
            http.securityMatcher("/csw-t")
                    .authorizeHttpRequests((authz) -> authz
                            .anyRequest()
                            .hasAnyRole("admin", "user")
                    )
                    .httpBasic(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

    }*/

}
