package mtn.momo.contract.repayment.configs;

import lombok.RequiredArgsConstructor;
import mtn.momo.contract.repayment.filter.JwtRequestFilter;
import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * Configures the HTTP security chain for the application.
     *
     * @param http HttpSecurity object to configure web based security for specific http requests.
     * @return The configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/login/v1", "/api/user/register/v1")
                        .permitAll()
                        .requestMatchers("index.html", "static/index.html",
                                "/webjars/**", "static/**",
                                "/js/**", "/css/**", "/images/**",
                                "/css/**", "/", "/forward:",
                                "static/", "index.html", "/index.html",
                                "/styles.*.css", "/runtime.*.js",
                                "/polyfills.*.js", "/main.*",
                                "/META-INF/resources/**",
                                "/resources/**",
                                "/static/**",
                                "/public/**")
                        .permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().authenticated());
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates an AuthenticationManager instance used to authenticate a request in the application.
     *
     * @param http HttpSecurity object for configuring temporary usage within method.
     * @param bCryptPasswordEncoder Encoder for hashing passwords for storage and comparison.
     * @return A fully configured AuthenticationManager.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }

    /**
     * Defines a BCryptPasswordEncoder bean to be used throughout the application.
     *
     * @return A new instance of BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS settings for the application.
     *
     * @return A WebMvcConfigurer with CORS mappings configured.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // For testing only, restrict as necessary
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
