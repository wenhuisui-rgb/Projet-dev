package com.example.demo.model;


import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.service.ObtentionBadgeService;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ObtentionBadgeServiceTest {

    @Autowired
    private ObtentionBadgeService obtentionBadgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void testAttribuerBadge() {

        Utilisateur user = new Utilisateur();
        user.setPseudo("testUser");
        user = utilisateurRepository.save(user);

        Badge badge = new Badge("5K", "Courir 5km");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);
        badge = badgeRepository.save(badge);

        ObtentionBadge obtention = obtentionBadgeService.attribuerBadge(user, badge);

        assertNotNull(obtention);
        assertNotNull(obtention.getId());
        assertEquals(user.getId(), obtention.getUtilisateur().getId());
        assertEquals(badge.getId(), obtention.getBadge().getId());
    }

    @Test
    void testUtilisateurPossedeBadge() {

        Utilisateur user = new Utilisateur();
        user.setPseudo("user2");
        user = utilisateurRepository.save(user);

        Badge badge = new Badge("10K", "Courir 10km");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(10f);
        badge = badgeRepository.save(badge);

        obtentionBadgeService.attribuerBadge(user, badge);

        boolean existe = obtentionBadgeService.utilisateurPossedeBadge(user, badge);

        assertTrue(existe);
    }

    @Test
    void testVerifierCondition() {

        Utilisateur user = new Utilisateur();
        user.setPseudo("user3");
        user = utilisateurRepository.save(user);

        Badge badge = new Badge("5K", "Courir 5km");
        badge.setTypeSport(TypeSport.COURSE);
        badge.setSeuil(5f);
        badge = badgeRepository.save(badge);

        boolean ok = obtentionBadgeService.verifierCondition(
                badge,
                user,
                TypeSport.COURSE,
                6f
        );

        assertTrue(ok);
    }
}