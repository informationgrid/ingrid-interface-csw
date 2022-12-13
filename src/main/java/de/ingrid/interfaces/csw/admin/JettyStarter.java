/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.server.cswt.CSWTServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.scan.Constants;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys.INGRID_ADMIN_PASSWORD;

/**
 * This class starts a Jetty server where the webapp will be executed.
 *
 * @author André Wallat
 */
@ImportResource("/springapp-servlet.xml")
@SpringBootApplication(scanBasePackages = "de.ingrid")
@ComponentScan(
        basePackages = "de.ingrid",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "de.ingrid.ibus.IBusApplication"),
        })
public class JettyStarter {
    private static final Log log = LogFactory.getLog(JettyStarter.class);

    private static int DEFAULT_JETTY_PORT = 8082;

    public static void main(String[] args) throws Exception {
        // avoid FileNotFound exceptions by TomCat's JarScanner
        System.setProperty(Constants.SKIP_JARS_PROPERTY, "derby*.jar,unit-api*.jar,geo*.jar,sis*.jar");
        SpringApplication.run(JettyStarter.class, args);
    }

    @Bean
    public ConfigurableServletWebServerFactory servletContainerFactory() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(ApplicationProperties.getInteger(ConfigurationKeys.SERVER_PORT, DEFAULT_JETTY_PORT));
        factory.addServerCustomizers(server ->
                ((WebAppContext) server.getHandler()).setWelcomeFiles(new String[]{"index.jsp"})
        );
        return factory;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login*", "/css/**", "/images/**", "/js/**")
                .permitAll()
                .anyRequest()
                .authenticated()
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
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public ServletRegistrationBean<CSWServlet> cswServlet(CSWServlet cswServlet) {
        ServletRegistrationBean<CSWServlet> bean = new ServletRegistrationBean<>(cswServlet, "/csw/*");
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<CSWTServlet> cswtServlet(CSWTServlet cswtServlet) {
        ServletRegistrationBean<CSWTServlet> bean = new ServletRegistrationBean<>(cswtServlet, "/csw-t/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
    
}
