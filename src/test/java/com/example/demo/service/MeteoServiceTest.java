package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeteoServiceTest {

    private MeteoService meteoService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        meteoService = new MeteoService();
        // 因为你的 RestTemplate 是在构造器里 new 出来的，这里用反射替换为 Mock 对象
        ReflectionTestUtils.setField(meteoService, "restTemplate", restTemplate);
    }

    @Test
    void testGetMeteoParLocalisation_NullOrEmpty() {
        assertEquals("Non disponible", meteoService.getMeteoParLocalisation(null));
        assertEquals("Non disponible", meteoService.getMeteoParLocalisation(""));
    }

    @Test
    void testGetMeteoParLocalisation_NotFound() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);
        assertEquals("Localisation non trouvée", meteoService.getMeteoParLocalisation("UnknownCity"));
    }

    @Test
    void testGetMeteoParLocalisation_Success() throws Exception {
        // Sonar Fix: 删除了未使用的局部变量 geoJson
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new RuntimeException("API Error"));
        assertEquals("Erreur météo", meteoService.getMeteoParLocalisation("Paris"));
    }
}