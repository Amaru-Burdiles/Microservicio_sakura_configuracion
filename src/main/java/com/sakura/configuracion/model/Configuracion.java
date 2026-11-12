package com.sakura.configuracion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion")
@Data
@NoArgsConstructor
public class Configuracion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Min(value = 0, message = "El porcentaje no puede ser negativo")
	@Max(value = 100, message = "El porcentaje no puede superar 100")
	@Column(nullable = false)
	private int porcentajeAbono;

	@Min(value = 1, message = "Los días de anticipación deben ser al menos 1")
	@Column(nullable = false)
	private int diasAnticipacionMaxima;

	@Min(value = 0, message = "Las horas mínimas no pueden ser negativas")
	@Column(nullable = false)
	private int horasAnticipacionMinima;

}
