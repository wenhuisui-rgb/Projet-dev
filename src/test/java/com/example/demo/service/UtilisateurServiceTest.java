package com.example.demo.service;

import com.example.demo.model.Utilisateur;
import com.example.demo.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @Test
    void testInscrireUtilisateur() {
        Utilisateur user = new Utilisateur();
        user.setMotDePasse("plainText");

        when(passwordEncoder.encode("plainText")).thenReturn("hashedPassword");
        when(utilisateurRepository.save(user)).thenReturn(user);

        Utilisateur saved = utilisateurService.inscrireUtilisateur(user);

        assertEquals("hashedPassword", saved.getMotDePasse());
        verify(passwordEncoder).encode("plainText");
        verify(utilisateurRepository).save(user);
    }

    @Test
    void testAuthentifier_Success() {
        Utilisateur user = new Utilisateur();
        user.setEmail("test@test.com");
        user.setMotDePasse("hashedPassword");

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainText", "hashedPassword")).thenReturn(true);

        Utilisateur result = utilisateurService.authentifier("test@test.com", "plainText");
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testAuthentifier_WrongPassword() {
        Utilisateur user = new Utilisateur();
        user.setEmail("test@test.com");
        user.setMotDePasse("hashedPassword");

        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        Utilisateur result = utilisateurService.authentifier("test@test.com", "wrongPass");
        assertNull(result);
    }

    @Test
    void testAuthentifier_UserNotFound() {
        when(utilisateurRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        assertNull(utilisateurService.authentifier("unknown@test.com", "anyPass"));
    }

    @Test
    void testFindAndExistsMethods() {
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(u));
        when(utilisateurRepository.findByPseudo("john")).thenReturn(Optional.of(u));
        
        when(utilisateurRepository.existsByEmail("a@b.com")).thenReturn(true);
        when(utilisateurRepository.existsByPseudo("john")).thenReturn(true);

        assertEquals(u, utilisateurService.findByEmail("a@b.com"));
        assertEquals(u, utilisateurService.findById(1L));
        assertEquals(u, utilisateurService.findByPseudo("john"));
        assertTrue(utilisateurService.emailExiste("a@b.com"));
        assertTrue(utilisateurService.pseudoExiste("john"));
    }

    @Test
    void testUpdateAndSupprimer() {
        Utilisateur u = new Utilisateur();
        when(utilisateurRepository.save(u)).thenReturn(u);

        assertEquals(u, utilisateurService.updateUtilisateur(u));
        
        utilisateurService.supprimerUtilisateur(1L);
        verify(utilisateurRepository).deleteById(1L);
    }

    @Test
    void testRechercherParPseudo() {
        when(utilisateurRepository.findByPseudoContainingIgnoreCaseAndIdNot("jo", 1L))
                .thenReturn(Arrays.asList(new Utilisateur()));
        assertEquals(1, utilisateurService.rechercherParPseudo("jo", 1L).size());
    }

    @Test
    void testObtenirListeAmis() {
        assertNull(utilisateurService.obtenirListeAmis(1L));
    }

}