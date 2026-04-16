package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Commentaire;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.CommentaireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentaireServiceTest {

    @Mock
    private CommentaireRepository commentaireRepository;

    @InjectMocks
    private CommentaireService commentaireService;

    @Test
    void testAjouterCommentaire() {
        Activite activite = new Activite();
        Utilisateur auteur = new Utilisateur();
        
        Commentaire saved = new Commentaire();
        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(saved);

        Commentaire result = commentaireService.ajouterCommentaire("Super", activite, auteur);
        
        assertNotNull(result);
        assertTrue(activite.getCommentaires().contains(saved));
        verify(commentaireRepository).save(any(Commentaire.class));
    }

    @Test
    void testGettersLists() {
        Activite act = new Activite();
        Utilisateur u = new Utilisateur();
        when(commentaireRepository.findByActiviteOrderByDateCommentaireDesc(act)).thenReturn(Arrays.asList(new Commentaire()));
        when(commentaireRepository.findByActiviteId(1L)).thenReturn(Arrays.asList(new Commentaire()));
        when(commentaireRepository.findByAuteurOrderByDateCommentaireDesc(u)).thenReturn(Arrays.asList(new Commentaire()));
        when(commentaireRepository.countByActiviteId(1L)).thenReturn(5L);

        assertEquals(1, commentaireService.getCommentairesParActivite(act).size());
        assertEquals(1, commentaireService.getCommentairesParActiviteId(1L).size());
        assertEquals(1, commentaireService.getCommentairesParAuteur(u).size());
        assertEquals(5L, commentaireService.getNombreCommentairesParActivite(1L));
    }

    @Test
    void testGetCommentaireParId() {
        Commentaire c = new Commentaire();
        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(c));
        assertEquals(c, commentaireService.getCommentaireParId(1L));
    }

    @Test
    void testSupprimerCommentaire() {
        Commentaire c = new Commentaire();
        Activite act = new Activite();
        c.setActivite(act);
        act.ajouterCommentaire(c);

        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(c));

        commentaireService.supprimerCommentaire(1L);

        // Verify bidirectionnal relationship is cut
        assertFalse(act.getCommentaires().contains(c));
        verify(commentaireRepository).deleteById(1L);
    }

    @Test
    void testSupprimerCommentairesParActivite() {
        commentaireService.supprimerCommentairesParActivite(1L);
        verify(commentaireRepository).deleteByActiviteId(1L);
    }

    @Test
    void testModifierCommentaire() {
        Commentaire c = new Commentaire();
        c.setContenu("Old");
        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(c));
        when(commentaireRepository.save(c)).thenReturn(c);

        Commentaire result = commentaireService.modifierCommentaire(1L, "New");
        assertEquals("New", result.getContenu());

        when(commentaireRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(commentaireService.modifierCommentaire(2L, "New"));
    }

    @Test
    void testSupprimerCommentaire_NullCases() {
        // 分支1: 找不到评论 (commentaire == null)
        when(commentaireRepository.findById(99L)).thenReturn(Optional.empty());
        commentaireService.supprimerCommentaire(99L);
        verify(commentaireRepository).deleteById(99L); // 依然会执行 deleteById

        // 分支2: 找到评论，但 activite 为 null (commentaire.getActivite() == null)
        Commentaire c = new Commentaire();
        c.setActivite(null);
        when(commentaireRepository.findById(100L)).thenReturn(Optional.of(c));
        commentaireService.supprimerCommentaire(100L);
        verify(commentaireRepository).deleteById(100L);
    }
}