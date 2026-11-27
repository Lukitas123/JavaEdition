package entidades.cofres;

import modelo.*;
import modelo.eventos.FinPuente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CofreDePuenteTest {

    @Test
    void puenteCubreTerrenosYSeAcumula() {
        Posicion inicio = new Posicion(1, 1);
        Posicion agua = new Posicion(1, 2);
        Posicion lava = new Posicion(1, 3);
        Posicion lavaNueva = new Posicion(1, 4);

        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.AGUA, Terreno.LAVA, Terreno.PISO}
        }, inicio, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, inicio));
        Agenda agenda = new Agenda();

        CofreDePuente primero = new CofreDePuente(inicio);
        plano.agregarCofre(primero);
        primero.abrir(heroe, plano, agenda);

        assertTrue(primero.estaAbierto());
        assertNull(plano.cofreEn(inicio));
        assertEquals(1, heroe.getCofresAbiertos());
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));
        assertEquals(Terreno.PISO, plano.terrenoEn(lava));
        assertEquals(Integer.valueOf(16), plano.getEfectos().getFinPuente());

        // La lava creada después queda visible.
        plano.cambiarTerreno(lavaNueva, Terreno.LAVA);
        assertEquals(Terreno.LAVA, plano.terrenoEn(lavaNueva));

        agenda.avanzarPaso();
        CofreDePuente segundo = new CofreDePuente(inicio);
        plano.agregarCofre(segundo);
        segundo.abrir(heroe, plano, agenda);

        assertEquals(Terreno.PISO, plano.terrenoEn(lavaNueva));
        assertEquals(Integer.valueOf(31), plano.getEfectos().getFinPuente());
        assertEquals(2, agenda.cantidadDeEventos());

        while (agenda.getPasoActual() < 16) {
            agenda.avanzarPaso();
        }
        EventoProgramado primerEvento = agenda.sacarProximo();
        assertEquals(new FinPuente(1, 1), primerEvento.evento());
        assertEquals(16, primerEvento.pasoDeVencimiento());

        plano.getEfectos().finalizarPuenteSiVencio(agenda.getPasoActual());
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));
        assertEquals(Terreno.PISO, plano.terrenoEn(lavaNueva));

        while (agenda.getPasoActual() < 31) {
            agenda.avanzarPaso();
        }
        EventoProgramado segundoEvento = agenda.sacarProximo();
        assertEquals(new FinPuente(1, 1), segundoEvento.evento());

        plano.getEfectos().finalizarPuenteSiVencio(agenda.getPasoActual());
        assertNull(plano.getEfectos().getFinPuente());
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
        assertEquals(Terreno.LAVA, plano.terrenoEn(lava));
        assertEquals(Terreno.LAVA, plano.terrenoEn(lavaNueva));
    }
}
