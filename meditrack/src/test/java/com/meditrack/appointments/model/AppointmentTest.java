package com.meditrack.appointments.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class AppointmentTest {

    @Test
    public void getters_alCrearAppointment_debenDevolverLosMismosValoresDelConstructor() {
        // Arrange
        List<String> emails = Arrays.asList("correo1@mail.com", "correo2@mail.com");

        // Act
        Appointment appointment = new Appointment("A1", "Ana Torres", "Cardiologia", 45.0, emails);

        // Assert
        assertEquals("A1", appointment.getId());
        assertEquals("Ana Torres", appointment.getPatientName());
        assertEquals("Cardiologia", appointment.getSpecialty());
        assertEquals(45.0, appointment.getCostUsd(), 0.0001);
        assertEquals(emails, appointment.getNotifyEmails());
    }

    @Test
    public void getNotifyEmails_alModificarListaOriginalDespuesDeCrear_noDebeAfectarElEstadoInterno() {
        // Arrange: lista mutable que el "llamador" conserva despues de crear el objeto
        List<String> emailsOriginales = new ArrayList<>(Arrays.asList("correo1@mail.com"));
        Appointment appointment = new Appointment("A2", "Luis Paredes", "Pediatria", 30.0, emailsOriginales);

        // Act: se muta la lista original que se paso al constructor
        emailsOriginales.add("correo-intruso@mail.com");

        // Assert: el tamano interno del Appointment no debe haber cambiado (copia defensiva
        // en el constructor)
        assertEquals(1, appointment.getNotifyEmails().size());
    }

    @Test
    public void getNotifyEmails_alLlamarloDosVeces_debeDevolverInstanciasDistintasDeLaListaOriginal() {
        // Arrange
        List<String> emails = Arrays.asList("correo1@mail.com");
        Appointment appointment = new Appointment("A3", "Maria Gomez", "Dermatologia", 20.0, emails);

        // Act
        List<String> resultadoGetter = appointment.getNotifyEmails();

        // Assert: la referencia devuelta por el getter nunca es la misma que la lista
        // original que se paso al constructor (copia defensiva tambien en el getter)
        assertNotSame(emails, resultadoGetter);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getNotifyEmails_alIntentarModificarLaListaDevuelta_debeLanzarExcepcion() {
        // Arrange
        Appointment appointment = new Appointment(
                "A4", "Carlos Ruiz", "Traumatologia", 60.0, Arrays.asList("correo1@mail.com"));

        // Act: se intenta mutar la lista de solo lectura devuelta por el getter
        appointment.getNotifyEmails().add("otro@mail.com");

        // Assert: la anotacion @Test(expected = ...) valida que se lanzo la excepcion esperada
    }
}
