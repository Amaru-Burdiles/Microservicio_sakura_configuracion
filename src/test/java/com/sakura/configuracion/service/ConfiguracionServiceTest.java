package com.sakura.configuracion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sakura.configuracion.exception.ReglaNegocioException;
import com.sakura.configuracion.model.Configuracion;
import com.sakura.configuracion.repository.ConfiguracionRepository;

@ExtendWith(MockitoExtension.class)
class ConfiguracionServiceTest {

    @Mock
    private ConfiguracionRepository configuracionRepository;

    @InjectMocks
    private ConfiguracionService configuracionService;

    private static final int PORCENTAJE_INICIAL_PROP = 50;

    @BeforeEach
    void setUp() {
        // Simula la inyección de la propiedad @Value("${sakura.abono.porcentaje}")
        ReflectionTestUtils.setField(configuracionService, "porcentajeInicial", PORCENTAJE_INICIAL_PROP);
    }

    // ==========================================
    // PRUEBAS DE obtener()
    // ==========================================

    @Test
    @DisplayName("obtener() debe retornar la configuración existente si la BD no está vacía")
    void obtener_Existente_RetornaConfiguracion() {
        Configuracion configExistente = new Configuracion();
        configExistente.setId(1L);
        configExistente.setPorcentajeAbono(20);

        when(configuracionRepository.findAll()).thenReturn(List.of(configExistente));

        Configuracion resultado = configuracionService.obtener();

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(20, resultado.getPorcentajeAbono());
        verify(configuracionRepository, times(1)).findAll();
        verify(configuracionRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtener() debe crear y guardar la configuración inicial con valores por defecto si la BD está vacía")
    void obtener_Vacia_CreaYRetornaConfiguracionInicial() {
        when(configuracionRepository.findAll()).thenReturn(Collections.emptyList());
        when(configuracionRepository.save(any(Configuracion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Configuracion resultado = configuracionService.obtener();

        assertNotNull(resultado);
        assertEquals(PORCENTAJE_INICIAL_PROP, resultado.getPorcentajeAbono());
        assertEquals(30, resultado.getDiasAnticipacionMaxima());
        assertEquals(2, resultado.getHorasAnticipacionMinima());

        verify(configuracionRepository, times(1)).findAll();
        verify(configuracionRepository, times(1)).save(any(Configuracion.class));
    }

    // ==========================================
    // PRUEBAS DE actualizar() - CASOS EXITOSOS
    // ==========================================

    @Test
    @DisplayName("actualizar() debe actualizar y guardar los datos correctamente si son válidos")
    void actualizar_DatosValidos_ActualizaCorrectamente() {
        Configuracion actual = new Configuracion();
        actual.setId(1L);
        actual.setPorcentajeAbono(10);
        actual.setDiasAnticipacionMaxima(15);
        actual.setHorasAnticipacionMinima(1);

        Configuracion nuevosDatos = new Configuracion();
        nuevosDatos.setPorcentajeAbono(40);
        nuevosDatos.setDiasAnticipacionMaxima(45);
        nuevosDatos.setHorasAnticipacionMinima(5);

        when(configuracionRepository.findAll()).thenReturn(List.of(actual));
        when(configuracionRepository.save(any(Configuracion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Configuracion resultado = configuracionService.actualizar(nuevosDatos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(40, resultado.getPorcentajeAbono());
        assertEquals(45, resultado.getDiasAnticipacionMaxima());
        assertEquals(5, resultado.getHorasAnticipacionMinima());

        verify(configuracionRepository, times(1)).save(actual);
    }

    // ==========================================
    // PRUEBAS DE VALIDACIONES (EXCEPCIONES)
    // ==========================================

    @Test
    @DisplayName("actualizar() debe lanzar excepción si el porcentaje de abono es menor a 0")
    void actualizar_PorcentajeNegativo_LanzaExcepcion() {
        Configuracion datosInvalidos = new Configuracion();
        datosInvalidos.setPorcentajeAbono(-1);

        ReglaNegocioException exception = assertThrows(
            ReglaNegocioException.class,
            () -> configuracionService.actualizar(datosInvalidos)
        );

        assertEquals("El porcentaje de abono debe estar entre 0 y 100", exception.getMessage());
        verify(configuracionRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizar() debe lanzar excepción si el porcentaje de abono es mayor a 100")
    void actualizar_PorcentajeMayorA100_LanzaExcepcion() {
        Configuracion datosInvalidos = new Configuracion();
        datosInvalidos.setPorcentajeAbono(101);

        ReglaNegocioException exception = assertThrows(
            ReglaNegocioException.class,
            () -> configuracionService.actualizar(datosInvalidos)
        );

        assertEquals("El porcentaje de abono debe estar entre 0 y 100", exception.getMessage());
        verify(configuracionRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizar() debe lanzar excepción si los días de anticipación son menores a 1")
    void actualizar_DiasAnticipacionMenorA1_LanzaExcepcion() {
        Configuracion datosInvalidos = new Configuracion();
        datosInvalidos.setPorcentajeAbono(50);
        datosInvalidos.setDiasAnticipacionMaxima(0);

        ReglaNegocioException exception = assertThrows(
            ReglaNegocioException.class,
            () -> configuracionService.actualizar(datosInvalidos)
        );

        assertEquals("Los días de anticipación deben ser al menos 1", exception.getMessage());
        verify(configuracionRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizar() debe lanzar excepción si las horas de anticipación son negativas")
    void actualizar_HorasAnticipacionNegativas_LanzaExcepcion() {
        Configuracion datosInvalidos = new Configuracion();
        datosInvalidos.setPorcentajeAbono(50);
        datosInvalidos.setDiasAnticipacionMaxima(10);
        datosInvalidos.setHorasAnticipacionMinima(-1);

        ReglaNegocioException exception = assertThrows(
            ReglaNegocioException.class,
            () -> configuracionService.actualizar(datosInvalidos)
        );

        assertEquals("Las horas mínimas no pueden ser negativas", exception.getMessage());
        verify(configuracionRepository, never()).save(any());
    }
}