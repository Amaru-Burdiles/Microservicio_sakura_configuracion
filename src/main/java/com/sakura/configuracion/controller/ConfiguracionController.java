package com.sakura.configuracion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakura.configuracion.client.SesionGuard;
import com.sakura.configuracion.model.Configuracion;
import com.sakura.configuracion.service.ConfiguracionService;

@RestController
@RequestMapping("/api/v1/configuracion")
public class ConfiguracionController {

	@Autowired
	private ConfiguracionService configuracionService;

	@Autowired
	private SesionGuard sesionGuard;

	@GetMapping
	public Configuracion obtener(@RequestHeader(value = "Authorization", required = false) String authorization) {
		sesionGuard.exigirAdmin(authorization);
		return configuracionService.obtener();
	}

	@GetMapping("/vigente")
	public Configuracion vigente() {
		return configuracionService.obtener();
	}

	@PutMapping
	public Configuracion actualizar(@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestBody Configuracion datos) {
		sesionGuard.exigirAdmin(authorization);
		return configuracionService.actualizar(datos);
	}

}
