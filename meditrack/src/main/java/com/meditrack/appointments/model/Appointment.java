package com.meditrack.appointments.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una cita medica de MediTrack.
 *
 * Es una clase 100% inmutable:
 * - Es "final": no puede ser heredada, lo que evita que una subclase rompa la inmutabilidad.
 * - Todos los atributos son "private final": se asignan una unica vez en el constructor.
 * - No existen setters.
 * - La lista notifyEmails recibe una copia defensiva en el constructor y el getter
 *   devuelve otra copia envuelta como lista de solo lectura, para que nadie pueda
 *   mutar el estado interno del objeto desde fuera.
 */
public final class Appointment {

    private final String id;
    private final String patientName;
    private final String specialty;
    private final Double costUsd;
    private final List<String> notifyEmails;

    public Appointment(String id, String patientName, String specialty, Double costUsd, List<String> notifyEmails) {
        this.id = id;
        this.patientName = patientName;
        this.specialty = specialty;
        this.costUsd = costUsd;
        // Copia defensiva en el constructor: si el llamador conserva una referencia a la
        // lista original y la modifica despues, ese cambio NO debe afectar a este objeto.
        this.notifyEmails = (notifyEmails == null) ? new ArrayList<>() : new ArrayList<>(notifyEmails);
    }

    public String getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public Double getCostUsd() {
        return costUsd;
    }

    public List<String> getNotifyEmails() {
        // Copia defensiva en el getter + vista de solo lectura: cada llamada devuelve una
        // lista nueva e inmutable, por lo que ni siquiera intentar hacer .add()/.remove()
        // sobre el resultado puede afectar (ni compilar exitosamente en runtime) el estado interno.
        return Collections.unmodifiableList(new ArrayList<>(notifyEmails));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id)
                && Objects.equals(patientName, that.patientName)
                && Objects.equals(specialty, that.specialty)
                && Objects.equals(costUsd, that.costUsd)
                && Objects.equals(notifyEmails, that.notifyEmails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patientName, specialty, costUsd, notifyEmails);
    }

    @Override
    public String toString() {
        return "Appointment{"
                + "id='" + id + '\''
                + ", patientName='" + patientName + '\''
                + ", specialty='" + specialty + '\''
                + ", costUsd=" + costUsd
                + ", notifyEmails=" + notifyEmails
                + '}';
    }
}
