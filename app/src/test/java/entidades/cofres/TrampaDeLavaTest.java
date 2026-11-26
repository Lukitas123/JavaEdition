package entidades.cofres;

import modelo.*;
import modelo.eventos.FinLava;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrampaDeLavaTest {

    @Test
    void transformaVecinosYProgramaSuVencimiento() {
        Posicion centro = new Posicion(2, 2);
        Posicion pared = new Posicion(1, 2);
        Posicion agua = new Posicion(2, 1);
        Posicion lava = new Posicion(2, 3);
        Posicion piso = new Posicion(3, 2);

        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PARED, Terreno.PISO},
                {Terreno.AGUA, Terreno.PISO, Terreno.LAVA},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, centro, null);
        Heroe heroe = new Heroe(100, new Ubicacion(2, 3, centro));
        Agenda agenda = new Agenda();
        TrampaDeLava trampa = new TrampaDeLava(centro);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, agenda);

        assertTrue(trampa.estaAbierto());
        assertNull(plano.cofreEn(centro));
        assertEquals(1, heroe.getCofresAbiertos());
        assertEquals(Terreno.PARED, plano.terrenoEn(pared));
        assertEquals(Terreno.PISO, plano.terrenoEn(centro));
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));
        assertEquals(Terreno.LAVA, plano.terrenoEn(lava));
        assertEquals(Terreno.LAVA, plano.terrenoEn(piso));
        assertEquals(1, agenda.cantidadDeEventos());

        trampa.abrir(heroe, plano, agenda);
        assertEquals(1, heroe.getCofresAbiertos());
        assertEquals(1, agenda.cantidadDeEventos());

        while (agenda.getPasoActual() < 11) {
            agenda.avanzarPaso();
        }

        EventoProgramado evento = agenda.sacarProximo();
        assertEquals(new FinLava(2, 3), evento.evento());
        assertEquals(11, evento.pasoDeVencimiento());

        plano.finalizarLavaSiVencio(agenda.getPasoActual());
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
        assertEquals(Terreno.LAVA, plano.terrenoEn(lava));
        assertEquals(Terreno.PISO, plano.terrenoEn(piso));
    }

    @Test
    void sinVecinosTransitablesNoProgramaEvento() {
        Posicion centro = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, centro, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, centro));
        Agenda agenda = new Agenda();
        TrampaDeLava trampa = new TrampaDeLava(centro);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, agenda);

        assertTrue(trampa.estaAbierto());
        assertNull(plano.cofreEn(centro));
        assertEquals(0, agenda.cantidadDeEventos());
    }
}