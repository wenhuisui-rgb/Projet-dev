package com.example.demo.service;

import com.example.demo.model.Commentaire;
import com.example.demo.model.Activite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.CommentaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentaireServiceTest {

    @Mock
    private CommentaireRepository commentaireRepository;

    @InjectMocks
    private CommentaireService commentaireService;

    private Utilisateur utilisateur;
    private Activite activite;
    private Commentaire commentaire1;
    private Commentaire commentaire2;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPseudo("Dupont");

        activite = new Activite();
        activite.setId(1L);

        commentaire1 = new Commentaire();
        commentaire1.setId(1L);
        commentaire1.setContenu("Super activité !");
        commentaire1.setDateCommentaire(LocalDateTime.now().minusDays(1));
        commentaire1.setActivite(activite);
        commentaire1.setAuteur(utilisateur);

        commentaire2 = new Commentaire();
        commentaire2.setId(2L);
        commentaire2.setContenu("Très bien");
        commentaire2.setDateCommentaire(LocalDateTime.now());
        commentaire2.setActivite(activite);
        commentaire2.setAuteur(utilisateur);
    }

    @Test
    void ajouterCommentaire_ShouldSaveAndAddToActivite() {
        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(commentaire1);
        
        Commentaire result = commentaireService.ajouterCommentaire("Super activité !", activite, utilisateur);
        
        assertNotNull(result);
        assertEquals("Super activité !", result.getContenu());
        verify(commentaireRepository).save(any(Commentaire.class));
    }

    @Test
    void getCommentairesParActivite_ShouldReturnList() {
        List<Commentaire> commentaires = Arrays.asList(commentaire1, commentaire2);
        when(commentaireRepository.findByActiviteOrderByDateCommentaireDesc(activite))
            .thenReturn(commentaires);
        
        List<Commentaire> result = commentaireService.getCommentairesParActivite(activite);
        
        assertEquals(2, result.size());
        verify(commentaireRepository).findByActiviteOrderByDateCommentaireDesc(activite);
    }

    @Test
    void getCommentairesParActiviteId_ShouldReturnList() {
        List<Commentaire> commentaires = Arrays.asList(commentaire1);
        when(commentaireRepository.findByActiviteId(1L)).thenReturn(commentaires);
        
        List<Commentaire> result = commentaireService.getCommentairesParActiviteId(1L);
        
        assertEquals(1, result.size());
        verify(commentaireRepository).findByActiviteId(1L);
    }

    @Test
    void getCommentairesParAuteur_ShouldReturnList() {
        List<Commentaire> commentaires = Arrays.asList(commentaire1, commentaire2);
        when(commentaireRepository.findByAuteurOrderByDateCommentaireDesc(utilisateur))
            .thenReturn(commentaires);
        
        List<Commentaire> result = commentaireService.getCommentairesParAuteur(utilisateur);
        
        assertEquals(2, result.size());
        verify(commentaireRepository).findByAuteurOrderByDateCommentaireDesc(utilisateur);
    }

    @Test
    void getCommentaireParId_WhenExists_ShouldReturnCommentaire() {
        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(commentaire1));
        
        Commentaire result = commentaireService.getCommentaireParId(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Super activité !", result.getContenu());
    }

    @Test
    void getCommentaireParId_WhenNotExists_ShouldReturnNull() {
        when(commentaireRepository.findById(99L)).thenReturn(Optional.empty());
        
        Commentaire result = commentaireService.getCommentaireParId(99L);
        
        assertNull(result);
    }

    @Test
    void supprimerCommentaire_WhenExists_ShouldRemoveFromActiviteAndDelete() {
        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(commentaire1));
        doNothing().when(commentaireRepository).deleteById(1L);
        
        commentaireService.supprimerCommentaire(1L);
        
        verify(commentaireRepository).findById(1L);
        verify(commentaireRepository).deleteById(1L);
    }

    @Test
    void supprimerCommentaire_WhenNotExists_ShouldStillCallDeleteById() {
        when(commentaireRepository.findById(99L)).thenReturn(Optional.empty());
        
        commentaireService.supprimerCommentaire(99L);
        
        verify(commentaireRepository).findById(99L);
        verify(commentaireRepository).deleteById(99L);
    }

    @Test
    void supprimerCommentairesParActivite_ShouldDeleteByActiviteId() {
        commentaireService.supprimerCommentairesParActivite(1L);
        
        verify(commentaireRepository).deleteByActiviteId(1L);
    }

    @Test
    void modifierCommentaire_WhenExists_ShouldUpdateAndReturn() {
        Commentaire updatedCommentaire = new Commentaire();
        updatedCommentaire.setId(1L);
        updatedCommentaire.setContenu("Nouveau contenu");
        updatedCommentaire.setDateCommentaire(LocalDateTime.now());
        
        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(commentaire1));
        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(updatedCommentaire);
        
        Commentaire result = commentaireService.modifierCommentaire(1L, "Nouveau contenu");
        
        assertNotNull(result);
        assertEquals("Nouveau contenu", result.getContenu());
        verify(commentaireRepository).save(commentaire1);
    }

    @Test
    void modifierCommentaire_WhenNotExists_ShouldReturnNull() {
        when(commentaireRepository.findById(99L)).thenReturn(Optional.empty());
        
        Commentaire result = commentaireService.modifierCommentaire(99L, "Nouveau contenu");
        
        assertNull(result);
        verify(commentaireRepository, never()).save(any());
    }

    @Test
    void getNombreCommentairesParActivite_ShouldReturnCount() {
        when(commentaireRepository.countByActiviteId(1L)).thenReturn(5L);
        
        long result = commentaireService.getNombreCommentairesParActivite(1L);
        
        assertEquals(5L, result);
        verify(commentaireRepository).countByActiviteId(1L);
    }
}