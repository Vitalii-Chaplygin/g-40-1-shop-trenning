package de.ait_tr.g_40_1_shop.security.sec_config;


import de.ait_tr.g_40_1_shop.security.sec_filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {
    private TokenFilter filter;

    public SecurityConfig(TokenFilter filter) {
        this.filter = filter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder (){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(x->x
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)   // .httpBasic(Customizer.withDefaults()) это для базовой настройки
                .authorizeHttpRequests(x->x
                        .requestMatchers(HttpMethod.GET,"/products/all").permitAll()
                        .requestMatchers(HttpMethod.GET,"/products/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST,"/products").hasRole("ADMIN")
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/hello").permitAll().anyRequest().authenticated()
                )
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
