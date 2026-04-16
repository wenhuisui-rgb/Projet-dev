package com.example.demo.service;

import com.example.demo.model.Activite;
import com.example.demo.model.Reaction;
import com.example.demo.model.TypeReaction;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void testAjouterReaction() {
        Activite activite = new Activite();
        Utilisateur auteur = new Utilisateur();

        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.empty());
        
        Reaction mockSaved = new Reaction();
        when(reactionRepository.save(any(Reaction.class))).thenReturn(mockSaved);

        Reaction result = reactionService.ajouterReaction(TypeReaction.KUDOS, activite, auteur);
        assertNotNull(result);
        assertTrue(activite.getReactions().contains(mockSaved));

        // Test already exists
        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.of(mockSaved));
        assertNull(reactionService.ajouterReaction(TypeReaction.KUDOS, activite, auteur));
    }

    @Test
    void testModifierReaction() {
        Activite activite = new Activite();
        Utilisateur auteur = new Utilisateur();
        Reaction existing = new Reaction();
        existing.setType(TypeReaction.KUDOS);

        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.of(existing));
        when(reactionRepository.save(existing)).thenReturn(existing);

        Reaction result = reactionService.modifierReaction(auteur, activite, TypeReaction.BRAVO);
        assertEquals(TypeReaction.BRAVO, result.getType());

        // Test not found
        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.empty());
        assertNull(reactionService.modifierReaction(auteur, activite, TypeReaction.BRAVO));
    }

    @Test
    void testSupprimerReaction() {
        Activite activite = new Activite();
        Utilisateur auteur = new Utilisateur();
        Reaction existing = new Reaction();
        activite.ajouterReaction(existing);

        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.of(existing));

        reactionService.supprimerReaction(auteur, activite);
        assertFalse(activite.getReactions().contains(existing));
        verify(reactionRepository).delete(existing);
    }
}