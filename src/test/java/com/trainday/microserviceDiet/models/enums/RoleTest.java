package com.trainday.microserviceDiet.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {
    @Test
    void fromValueAcceptsEnumNameIgnoringCase() {
        assertEquals(Role.NUTRITIONIST, Role.fromValue("nutritionist"));
    }

    @Test
    void fromValueAcceptsDisplayValueIgnoringCase() {
        assertEquals(Role.ATHLETE, Role.fromValue("atleta"));
        assertEquals(Role.PERSONAL_TRAINER, Role.fromValue("educador_fisico"));
    }

    @Test
    void fromValueRejectsUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> Role.fromValue("UNKNOWN"));
    }

    @Test
    void fromValueRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> Role.fromValue(null));
    }

    @Test
    void getStateReturnsDisplayValue() {
        assertEquals("NUTRICIONISTA", Role.NUTRITIONIST.getState());
    }
}
