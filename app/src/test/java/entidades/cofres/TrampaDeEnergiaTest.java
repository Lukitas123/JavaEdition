package entidades.cofres;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrampaDeEnergiaTest {

    @Test
    void alAbrirseAplicaElDanioYDesaparece() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        Agenda agenda = new Agenda();
        TrampaDeEnergia trampa = new TrampaDeEnergia(posicion, 20);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, agenda);

        assertEquals(80, heroe.getEnergia());
        assertEquals(1, heroe.getCofresAbiertos());
        assertTrue(trampa.estaAbierto());
        assertNull(plano.cofreEn(posicion));
        assertEquals(0, agenda.cantidadDeEventos());

        // Abrirla otra vez no debe causar daño ni contar otro cofre.
        trampa.abrir(heroe, plano, agenda);

        assertEquals(80, heroe.getEnergia());
        assertEquals(1, heroe.getCofresAbiertos());
    }

    @Test
    void laEnergiaNoBajaDeCero() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(15, new Ubicacion(1, 1, posicion));
        TrampaDeEnergia trampa = new TrampaDeEnergia(posicion, 20);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, new Agenda());

        assertEquals(0, heroe.getEnergia());
        assertTrue(heroe.estaSinEnergia());
    }

    @Test
    void rechazaDanioCeroONegativo() {
        Posicion posicion = new Posicion(1, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new TrampaDeEnergia(posicion, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new TrampaDeEnergia(posicion, -1));
    }
}