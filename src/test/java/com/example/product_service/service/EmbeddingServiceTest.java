package com.example.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmbeddingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmbeddingService embeddingService;

    private final String aiServiceUrl = "http://localhost:8085";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(embeddingService, "aiServiceUrl", aiServiceUrl);
        ReflectionTestUtils.setField(embeddingService, "restTemplate", restTemplate);
    }

    @Test
    void testGetEmbedding_Success() {
        String text = "NexShop laptop computer";
        List<Double> mockResponse = Arrays.asList(0.1, 0.2, 0.3);

        when(restTemplate.postForObject(
                eq(aiServiceUrl + "/api/ai/embeddings"),
                eq(text),
                eq(List.class)
        )).thenReturn(mockResponse);

        List<Double> result = embeddingService.getEmbedding(text);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(0.1, result.get(0));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(List.class));
    }

    @Test
    void testGetEmbedding_Failure() {
        String text = "test";
        when(restTemplate.postForObject(anyString(), any(), eq(List.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> embeddingService.getEmbedding(text));
        assertTrue(exception.getMessage().contains("AI embedding service unavailable"));
    }
}
