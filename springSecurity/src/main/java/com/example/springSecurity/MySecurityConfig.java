package com.example.springSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class MySecurityConfig {

        @Bean
        SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
            //this will disable cookie and so on session
            http.sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//            http.formLogin(withDefaults());
            http.httpBasic(withDefaults());
            return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService(){
            UserDetails user1=  User.withUsername("user1")
                    .password("{noop}password1") //noop tells password should be saved as plain text to the spring boot
                    .roles("USER")
                    .build();

            UserDetails admin=  User.withUsername("admin")
                    .password("{noop}adminPassword1") //noop tells password should be saved as plain text to the spring boot
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(user1,admin);
        }
}
