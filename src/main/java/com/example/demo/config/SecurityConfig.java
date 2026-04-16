package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration globale de la sécurité de l'application (Spring Security).
 */
@Configuration
public class SecurityConfig {

    /**
     * Déclare le Bean responsable du hachage des mots de passe.
     * Utilise l'algorithme BCrypt, standard de l'industrie.
     *
     * @return L'instance de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure la chaîne de filtres de sécurité HTTP (SecurityFilterChain).
     * <p>
     * Attention : Dans l'état actuel de ce projet, Spring Security est principalement utilisé
     * pour fournir le {@code PasswordEncoder}. La gestion des sessions et des permissions
     * est gérée manuellement via {@code HttpSession} dans les contrôleurs.
     *
     * @param http L'objet HttpSecurity à configurer
     * @return La chaîne de filtres construite
     * @throws Exception Si une erreur de configuration survient
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactivation de la protection CSRF (pour faciliter le développement/tests)
            .csrf(csrf -> csrf.disable()) 
            // Autorise l'accès libre à toutes les routes (la sécurité est gérée par les contrôleurs)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );
        
        return http.build();
    }
}