package com.example.demo.service;

import com.example.demo.model.Objectif;
import com.example.demo.model.TypeSport;
import com.example.demo.model.Unite;
import com.example.demo.model.Utilisateur;
import com.example.demo.repository.ActiviteRepository;
import com.example.demo.repository.ObjectifRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectifServiceTest {

    @Mock
    private ObjectifRepository objectifRepository;

    @Mock
    private ActiviteRepository activiteRepository;

    @InjectMocks
    private ObjectifService objectifService;

    @Test
    void testCreerObjectif() {
        Objectif obj = new Objectif();
        Utilisateur user = new Utilisateur();
        when(objectifRepository.save(obj)).thenReturn(obj);

        Objectif saved = objectifService.creerObjectif(obj, user);
        assertEquals(user, saved.getUtilisateur());
        verify(objectifRepository).save(obj);
    }

    @Test
    void testUpdateObjectif() {
        Objectif existant = new Objectif();
        existant.setId(1L);
        existant.setDescription("Old");

        Objectif modifie = new Objectif();
        modifie.setDescription("New");
        modifie.setUnite(Unite.KM);

        when(objectifRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(objectifRepository.save(existant)).thenReturn(existant);

        Objectif result = objectifService.updateObjectif(1L, modifie);
        assertEquals("New", result.getDescription());
        assertEquals(Unite.KM, result.getUnite());

        assertNull(objectifService.updateObjectif(2L, modifie));
    }

    @Test
    void testGetProgressionObjectif_KM() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KM);
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now().plusDays(5));
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(15.5f);

        Float progression = objectifService.getProgressionObjectif(obj, user);
        assertEquals(15.5f, progression);
    }

    @Test
    void testGetProgressionObjectif_MinutesWithSport() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.MINUTES);
        obj.setTypeSport(TypeSport.COURSE);
        // Null dateDebut -> should test the minusMonths(1) branch
        obj.setDateDebut(null); 
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getDureeBySportAndPeriod(eq(user), eq(TypeSport.COURSE), any(), any())).thenReturn(120);

        Float progression = objectifService.getProgressionObjectif(obj, user);
        assertEquals(120f, progression);
    }

    @Test
    void testGetPourcentageObjectifEtIsAtteint() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KCAL);
        obj.setCible(1000f);
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getCaloriesByPeriod(eq(user), any(), any())).thenReturn(500f);

        assertEquals(50f, objectifService.getPourcentageObjectif(obj, user));
        assertFalse(objectifService.isObjectifAtteint(obj, user));

        // Test objective zero or null
        obj.setCible(0f);
        assertEquals(0f, objectifService.getPourcentageObjectif(obj, user));
    }

    @Test
    void testGetObjectifsParUtilisateurEtSupprimer() {
        Utilisateur user = new Utilisateur();
        objectifService.getObjectifsParUtilisateur(user);
        verify(objectifRepository).findByUtilisateur(user);

        objectifService.supprimerObjectif(1L);
        verify(objectifRepository).deleteById(1L);
    }

    @Test
    void testGetProgressionObjectif_KM_NoSport() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KM);
        obj.setTypeSport(null); // 覆盖 typeSport == null 的分支
        obj.setDateDebut(LocalDate.now()); // 同时覆盖 getDateDebut() != null 的分支
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(20.0f);
        assertEquals(20.0f, objectifService.getProgressionObjectif(obj, user));
    }

    @Test
    void testGetProgressionObjectif_Minutes_NoSport_NullDuree() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.MINUTES);
        obj.setTypeSport(null); // 覆盖 typeSport == null 的分支
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();

        // 模拟查出来的时长是 null，触发 (duree != null) ? duree.floatValue() : 0f; 的备用分支
        when(activiteRepository.getDureeByPeriod(eq(user), any(), any())).thenReturn(null);
        assertEquals(0f, objectifService.getProgressionObjectif(obj, user));
    }

    @Test
    void testGetProgressionObjectif_Kcal_BothBranches() {
        Utilisateur user = new Utilisateur();
        
        // 分支 1: KCAL 且有运动类型
        Objectif objSport = new Objectif();
        objSport.setUnite(Unite.KCAL);
        objSport.setTypeSport(TypeSport.VELO);
        objSport.setDateDebut(LocalDate.now());
        objSport.setDateFin(LocalDate.now());
        when(activiteRepository.getCaloriesBySportAndPeriod(eq(user), eq(TypeSport.VELO), any(), any())).thenReturn(300f);
        assertEquals(300f, objectifService.getProgressionObjectif(objSport, user));

        // 分支 2: KCAL 且没有运动类型
        Objectif objNoSport = new Objectif();
        objNoSport.setUnite(Unite.KCAL);
        objNoSport.setTypeSport(null);
        objNoSport.setDateDebut(LocalDate.now());
        objNoSport.setDateFin(LocalDate.now());
        when(activiteRepository.getCaloriesByPeriod(eq(user), any(), any())).thenReturn(500f);
        assertEquals(500f, objectifService.getProgressionObjectif(objNoSport, user));
    }

    @Test
    void testGetProgressionObjectif_UniteNull() {
        Objectif obj = new Objectif();
        obj.setUnite(null);
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();
        assertEquals(0f, objectifService.getProgressionObjectif(obj, user));
    }

    @Test
    void testGetPourcentageObjectif_NullCible_And_IsAtteintTrue() {
        Objectif obj = new Objectif();
        obj.setUnite(Unite.KM);
        obj.setCible(null); // 覆盖 getCible() == null 的黄色分支
        obj.setDateDebut(LocalDate.now());
        obj.setDateFin(LocalDate.now());
        Utilisateur user = new Utilisateur();
        
        assertEquals(0f, objectifService.getPourcentageObjectif(obj, user));

        // 测试 isObjectifAtteint 成功的分支 (之前只测了失败的)
        obj.setCible(10f);
        when(activiteRepository.getDistanceByPeriod(eq(user), any(), any())).thenReturn(15f);
        assertTrue(objectifService.isObjectifAtteint(obj, user));
    }

    @Test
    void testGetProgressionObjectif_FullBranches() {
        Utilisateur user = new Utilisateur();

        // 分支 1: KM 且指定了运动类型
        Objectif objKm = new Objectif();
        objKm.setUnite(Unite.KM);
        objKm.setTypeSport(TypeSport.COURSE);
        objKm.setDateDebut(LocalDate.now());
        objKm.setDateFin(LocalDate.now());
        when(activiteRepository.getDistanceBySportAndPeriod(eq(user), eq(TypeSport.COURSE), any(), any())).thenReturn(10f);
        assertEquals(10f, objectifService.getProgressionObjectif(objKm, user));

        // 分支 2: MINUTES 且指定了运动类型
        Objectif objMin = new Objectif();
        objMin.setUnite(Unite.MINUTES);
        objMin.setTypeSport(TypeSport.COURSE);
        objMin.setDateDebut(LocalDate.now());
        objMin.setDateFin(LocalDate.now());
        when(activiteRepository.getDureeBySportAndPeriod(eq(user), eq(TypeSport.COURSE), any(), any())).thenReturn(60);
        assertEquals(60f, objectifService.getProgressionObjectif(objMin, user));

        // 分支 3: KCAL 且指定了运动类型
        Objectif objKcal = new Objectif();
        objKcal.setUnite(Unite.KCAL);
        objKcal.setTypeSport(TypeSport.COURSE);
        objKcal.setDateDebut(LocalDate.now());
        objKcal.setDateFin(LocalDate.now());
        when(activiteRepository.getCaloriesBySportAndPeriod(eq(user), eq(TypeSport.COURSE), any(), any())).thenReturn(500f);
        assertEquals(500f, objectifService.getProgressionObjectif(objKcal, user));

        // 分支 4: 强制让数据库返回 null，触发最后的 resultat != null ? resultat : 0f 的兜底 (false) 分支
        Objectif objKcalNoSport = new Objectif();
        objKcalNoSport.setUnite(Unite.KCAL);
        objKcalNoSport.setDateDebut(LocalDate.now());
        objKcalNoSport.setDateFin(LocalDate.now());
        when(activiteRepository.getCaloriesByPeriod(eq(user), any(), any())).thenReturn(null);
        assertEquals(0f, objectifService.getProgressionObjectif(objKcalNoSport, user));
    }
}