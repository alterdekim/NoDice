package com.alterdekim.game.security;

import com.alterdekim.game.handler.CustomAccessDeniedHandler;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/async/**").permitAll()
                                .requestMatchers("/image/**").hasAnyAuthority("ROLE_ADMIN")
                                .requestMatchers("/game").hasAnyAuthority("ROLE_ADMIN")
                                .requestMatchers("/games").hasAnyAuthority("ROLE_ADMIN")
                                .requestMatchers("/profile/**").hasAnyAuthority("ROLE_ADMIN")
                                .requestMatchers("/api/**").hasAnyAuthority("ROLE_ADMIN")
                                .requestMatchers("/static/**").permitAll()
                                .requestMatchers("/access-denied").permitAll()
                                .requestMatchers("/signup").permitAll()
                                .requestMatchers("/rules").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/signup/**").permitAll()
                                .requestMatchers("/").permitAll()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .failureForwardUrl("/")
                                .defaultSuccessUrl("/games")
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                )
                .requestCache((cache) -> cache
                        .requestCache(requestCache)
                )
                .exceptionHandling((exc) -> exc
                        .accessDeniedHandler(accessDeniedHandler())
                        .accessDeniedPage("/access-denied"));
        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return (web) -> web.httpFirewall(firewall);
    }
}

