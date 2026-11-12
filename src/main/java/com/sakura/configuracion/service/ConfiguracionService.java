package com.sakura.configuracion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sakura.configuracion.exception.ReglaNegocioException;
import com.sakura.configuracion.model.Configuracion;
import com.sakura.configuracion.repository.ConfiguracionRepository;

@Service
public class ConfiguracionService {

	@Autowired
	private ConfiguracionRepository configuracionRepository;

	@Value("${sakura.abono.porcentaje}")
	private int porcentajeInicial;

	public Configuracion obtener() {
		return configuracionRepository.findAll().stream().findFirst().orElseGet(this::crearInicial);
	}

	public Configuracion actualizar(Configuracion datos) {
		if (datos.getPorcentajeAbono() < 0 || datos.getPorcentajeAbono() > 100) {
			throw new ReglaNegocioException("El porcentaje de abono debe estar entre 0 y 100");
		}
		if (datos.getDiasAnticipacionMaxima() < 1) {
			throw new ReglaNegocioException("Los días de anticipación deben ser al menos 1");
		}
		if (datos.getHorasAnticipacionMinima() < 0) {
			throw new ReglaNegocioException("Las horas mínimas no pueden ser negativas");
		}
		Configuracion actual = obtener();
		actual.setPorcentajeAbono(datos.getPorcentajeAbono());
		actual.setDiasAnticipacionMaxima(datos.getDiasAnticipacionMaxima());
		actual.setHorasAnticipacionMinima(datos.getHorasAnticipacionMinima());
		return configuracionRepository.save(actual);
	}

	private Configuracion crearInicial() {
		Configuracion configuracion = new Configuracion();
		configuracion.setPorcentajeAbono(porcentajeInicial);
		configuracion.setDiasAnticipacionMaxima(30);
		configuracion.setHorasAnticipacionMinima(2);
		return configuracionRepository.save(configuracion);
	}

}
