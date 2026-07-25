package com.meditrack.appointments.service;

import com.meditrack.appointments.model.Appointment;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

public class AppointmentServiceTest {

    @Test
    public void getValidAppointments_debeEmitirSoloLasTresValidas() {
        // Arrange
        AppointmentService service = new AppointmentService();

        // Act
        Flux<Appointment> flujo = service.getValidAppointments();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getValidAppointments_alEmitirCitasValidas_debenTenerLaEspecialidadEnMayusculas() {
        // Arrange
        AppointmentService service = new AppointmentService();

        // Act
        Flux<Appointment> flujo = service.getValidAppointments();

        // Assert: verificamos que el operador map efectivamente transformo las 3 citas validas
        StepVerifier.create(flujo)
                .expectNextMatches(a -> a.getSpecialty().equals(a.getSpecialty().toUpperCase()))
                .expectNextMatches(a -> a.getSpecialty().equals(a.getSpecialty().toUpperCase()))
                .expectNextMatches(a -> a.getSpecialty().equals(a.getSpecialty().toUpperCase()))
                .verifyComplete();
    }

    @Test
    public void filterValidAppointments_conTodasLasCitasInvalidas_debeEmitirSoloLaCitaPorDefecto() {
        // Arrange
        AppointmentService service = new AppointmentService();
        Flux<Appointment> todasInvalidas = Flux.just(
                new Appointment("X1", "Paciente Uno", "cardiologia", 0.0, Arrays.asList("x1@mail.com")),
                new Appointment("X2", "Paciente Dos", "pediatria", 30.0, Collections.emptyList()));

        // Act
        Flux<Appointment> flujo = service.filterValidAppointments(todasInvalidas);

        // Assert: al no quedar ninguna cita valida, defaultIfEmpty debe emitir la generica
        StepVerifier.create(flujo)
                .expectNextMatches(a -> "DEFAULT".equals(a.getId()) && "GENERAL".equals(a.getSpecialty()))
                .verifyComplete();
    }

    @Test
    public void findById_conIdExistente_debeEmitirLaCitaCorrespondiente() {
        // Arrange
        AppointmentService service = new AppointmentService();

        // Act
        Mono<Appointment> mono = service.findById("A1");

        // Assert
        StepVerifier.create(mono)
                .expectNextMatches(a -> "A1".equals(a.getId()))
                .verifyComplete();
    }

    @Test
    public void findById_conIdInexistente_debeTerminarEnError() {
        // Arrange
        AppointmentService service = new AppointmentService();

        // Act
        Mono<Appointment> mono = service.findById("NO-EXISTE");

        // Assert
        StepVerifier.create(mono)
                .expectError(NoSuchElementException.class)
                .verify();
    }
}
