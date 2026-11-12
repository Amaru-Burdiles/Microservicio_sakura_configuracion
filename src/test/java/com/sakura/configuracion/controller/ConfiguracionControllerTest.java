package com.sakura.configuracion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sakura.configuracion.client.SesionGuard;
import com.sakura.configuracion.exception.AccesoDenegadoException;
import com.sakura.configuracion.exception.ReglaNegocioException;
import com.sakura.configuracion.model.Configuracion;
import com.sakura.configuracion.service.ConfiguracionService;

@WebMvcTest(ConfiguracionController.class)
@ActiveProfiles("test")
class ConfiguracionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ConfiguracionService configuracionService;

	@MockitoBean
	private SesionGuard sesionGuard;

	private static final String AUTH = "Bearer token-admin";

	@Test
	void obtener_Exito() throws Exception {
		when(configuracionService.obtener()).thenReturn(configuracion());

		mockMvc.perform(get("/api/v1/configuracion").header("Authorization", AUTH))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.porcentajeAbono").value(50));
	}

	@Test
	void obtener_SinPermiso_Responde403() throws Exception {
		doThrow(new AccesoDenegadoException("No tiene permiso")).when(sesionGuard).exigirAdmin(any());

		mockMvc.perform(get("/api/v1/configuracion").header("Authorization", AUTH))
				.andExpect(status().isForbidden());
	}

	@Test
	void vigente_Exito() throws Exception {
		when(configuracionService.obtener()).thenReturn(configuracion());

		mockMvc.perform(get("/api/v1/configuracion/vigente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.diasAnticipacionMaxima").value(30));
	}

	@Test
	void actualizar_Exito() throws Exception {
		when(configuracionService.actualizar(any(Configuracion.class))).thenReturn(configuracion());

		mockMvc.perform(put("/api/v1/configuracion")
				.header("Authorization", AUTH)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"porcentajeAbono\":50,\"diasAnticipacionMaxima\":30,\"horasAnticipacionMinima\":2}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.horasAnticipacionMinima").value(2));
	}

	@Test
	void actualizar_PorcentajeInvalido_Responde409() throws Exception {
		when(configuracionService.actualizar(any(Configuracion.class)))
				.thenThrow(new ReglaNegocioException("El porcentaje de abono debe estar entre 0 y 100"));

		mockMvc.perform(put("/api/v1/configuracion")
				.header("Authorization", AUTH)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"porcentajeAbono\":150,\"diasAnticipacionMaxima\":30,\"horasAnticipacionMinima\":2}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value("El porcentaje de abono debe estar entre 0 y 100"));
	}

	private Configuracion configuracion() {
		Configuracion configuracion = new Configuracion();
		configuracion.setId(1L);
		configuracion.setPorcentajeAbono(50);
		configuracion.setDiasAnticipacionMaxima(30);
		configuracion.setHorasAnticipacionMinima(2);
		return configuracion;
	}
}
