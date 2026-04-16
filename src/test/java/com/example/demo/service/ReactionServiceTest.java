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

    @Test
    void testSupprimerReaction_NotFound() {
        Utilisateur auteur = new Utilisateur();
        Activite activite = new Activite();
        // 模拟没找到记录
        when(reactionRepository.findByAuteurAndActivite(auteur, activite)).thenReturn(Optional.empty());
        
        reactionService.supprimerReaction(auteur, activite);
        
        // 验证绝对没有调用 delete 方法
        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void testDelegationMethods() {
        Activite activite = new Activite();
        Utilisateur auteur = new Utilisateur();

        // 挨个调用那些被遗忘的单行查询方法
        reactionService.getReactionsParActivite(activite);
        verify(reactionRepository).findByActiviteOrderByDateReactionDesc(activite);

        reactionService.getReactionsParActiviteId(1L);
        verify(reactionRepository).findByActiviteId(1L);

        reactionService.getReactionByAuteurEtActivite(auteur, activite);
        verify(reactionRepository).findByAuteurAndActivite(auteur, activite);

        reactionService.aDejaReagi(auteur, activite);
        verify(reactionRepository).existsByAuteurAndActivite(auteur, activite);

        reactionService.getNombreReactionsParActivite(1L);
        verify(reactionRepository).countByActiviteId(1L);

        reactionService.getNombreReactionsParType(1L, TypeReaction.BRAVO);
        verify(reactionRepository).countByActiviteIdAndType(1L, TypeReaction.BRAVO);
    }
}