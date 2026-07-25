package com.meditrack.appointments.service;

import com.meditrack.appointments.model.Appointment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

@Service
public class AppointmentService {

    /**
     * Expone las citas validas de forma reactiva. Internamente separa la
     * generacion de datos (generateAppointments) del procesamiento reactivo
     * (filterValidAppointments) para poder testear cada parte por separado.
     */
    public Flux<Appointment> getValidAppointments() {
        return filterValidAppointments(generateAppointments());
    }

    /**
     * Genera 5 citas en memoria: 3 validas y 2 invalidas segun la regla de
     * negocio (costUsd > 0 y notifyEmails no vacia).
     * Visibilidad de paquete a proposito, para que los tests puedan reutilizar
     * el flujo original si lo necesitan.
     */
    Flux<Appointment> generateAppointments() {
        return Flux.just(
                new Appointment("A1", "Ana Torres", "cardiologia", 45.0, Arrays.asList("ana@mail.com")),
                new Appointment("A2", "Luis Paredes", "pediatria", 30.0,
                        Arrays.asList("luis@mail.com", "mama.luis@mail.com")),
                // invalida: costUsd = 0
                new Appointment("A3", "Maria Gomez", "dermatologia", 0.0, Arrays.asList("maria@mail.com")),
                // invalida: notifyEmails vacia
                new Appointment("A4", "Carlos Ruiz", "traumatologia", 60.0, Collections.emptyList()),
                new Appointment("A5", "Sofia Leon", "cardiologia", 50.0, Arrays.asList("sofia@mail.com")));
    }

    /**
     * Aplica la cadena de operadores reactivos sobre cualquier Flux de citas.
     * Se separa de generateAppointments() para poder probar el caso
     * "todas invalidas -> defaultIfEmpty" con datos de prueba controlados,
     * sin depender de los 5 registros fijos del servicio.
     */
    Flux<Appointment> filterValidAppointments(Flux<Appointment> source) {
        return source
                // filter: deja pasar unicamente las citas que cumplen la regla de negocio
                // (costUsd > 0 y notifyEmails no vacia). Es el punto donde se descartan
                // las citas invalidas antes de que lleguen al cliente.
                .filter(appointment -> appointment.getCostUsd() != null
                        && appointment.getCostUsd() > 0
                        && !appointment.getNotifyEmails().isEmpty())
                // map: transforma cada cita valida, normalizando la especialidad a
                // mayusculas para una presentacion consistente hacia el cliente.
                .map(appointment -> new Appointment(
                        appointment.getId(),
                        appointment.getPatientName(),
                        appointment.getSpecialty().toUpperCase(),
                        appointment.getCostUsd(),
                        appointment.getNotifyEmails()))
                // defaultIfEmpty: si el filtro descarta absolutamente todas las citas,
                // el flujo no debe completar vacio en silencio; se emite una cita
                // generica para que el cliente reciba una respuesta significativa.
                .defaultIfEmpty(defaultAppointment());
    }

    /**
     * Busca una cita por id de forma 100% reactiva y no bloqueante.
     * Prohibido usar block() o inspeccionar un valor ya "desempaquetado":
     * todo el manejo del caso "no encontrado" ocurre dentro de la cadena reactiva.
     */
    public Mono<Appointment> findById(String id) {
        return getValidAppointments()
                // filter: nos quedamos solo con la cita cuyo id coincide con el buscado
                .filter(appointment -> appointment.getId().equals(id))
                // next: toma el primer elemento del Flux ya filtrado y lo convierte en
                // Mono (o en Mono vacio si no hubo coincidencias), sin bloquear el hilo.
                .next()
                // switchIfEmpty: si el Mono anterior esta vacio (id inexistente), se
                // resuelve el caso "no encontrado" emitiendo un error de forma reactiva,
                // en vez de lanzar una excepcion imperativa o devolver null.
                .switchIfEmpty(Mono.error(new NoSuchElementException("No existe una cita con id: " + id)));
    }

    private Appointment defaultAppointment() {
        return new Appointment("DEFAULT", "N/A", "GENERAL", 0.0, Collections.emptyList());
    }
}
