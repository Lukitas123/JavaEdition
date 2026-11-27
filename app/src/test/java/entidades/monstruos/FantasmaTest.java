package entidades.monstruos;

import java.util.Random;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FantasmaTest {

    private Plano crearPlano(Terreno... terrenos) {
        return new Plano(
                new Terreno[][] {terrenos},
                new Posicion(1, 1),
                null);
    }

    private Heroe crearHeroe(int energia, Posicion posicion) {
        return new Heroe(energia, new Ubicacion(1, 1, posicion));
    }

    @Test
    void puedeMoverseAUnaCasillaDePared() {
        Plano plano = crearPlano(
                Terreno.PISO, Terreno.PARED, Terreno.PISO);
        Posicion inicio = new Posicion(1, 1);
        Fantasma fantasma = new Fantasma(inicio, new Random(1),30);
        Heroe heroe = crearHeroe(100, new Posicion(1, 3));

        fantasma.actuar(plano, heroe, new Agenda());

        assertEquals(new Posicion(1, 2), fantasma.getPosicion());
        assertEquals(Terreno.PARED, plano.terrenoEn(fantasma.getPosicion()));
        assertEquals(100, heroe.getEnergia());
    }

    @Test
    void seMueveUnTurnoSiYOtroNoYPermaneceDentroDelPlano() {
        Plano plano = crearPlano(
                Terreno.PISO, Terreno.PISO, Terreno.PISO, Terreno.PISO);
        Fantasma fantasma =
                new Fantasma(new Posicion(1, 1), new Random(1),30);
        Heroe heroe = crearHeroe(100, new Posicion(1, 4));
        Agenda agenda = new Agenda();

        fantasma.actuar(plano, heroe, agenda);
        Posicion despuesDelPrimerTurno = fantasma.getPosicion();

        assertEquals(new Posicion(1, 2), despuesDelPrimerTurno);
        assertTrue(plano.estaDentro(despuesDelPrimerTurno));

        fantasma.actuar(plano, heroe, agenda);

        assertEquals(despuesDelPrimerTurno, fantasma.getPosicion());

        fantasma.actuar(plano, heroe, agenda);

        assertNotEquals(despuesDelPrimerTurno, fantasma.getPosicion());
        assertTrue(plano.estaDentro(fantasma.getPosicion()));

        Posicion despuesDelTercerTurno = fantasma.getPosicion();
        fantasma.actuar(plano, heroe, agenda);

        assertEquals(despuesDelTercerTurno, fantasma.getPosicion());
        assertTrue(plano.estaDentro(fantasma.getPosicion()));
    }

    @Test
    void alAlcanzarAlHeroeLeQuitaEnergiaYVuelveAlInicio() {
        Plano plano = crearPlano(Terreno.PISO, Terreno.PISO);
        Posicion inicio = new Posicion(1, 1);
        Fantasma fantasma = new Fantasma(inicio, new Random(1),30);
        Heroe heroe = crearHeroe(100, new Posicion(1, 2));

        fantasma.actuar(plano, heroe, new Agenda());

        assertEquals(70, heroe.getEnergia());
        assertEquals(inicio, fantasma.getPosicion());
    }

    @Test
    void estarInactivoNoCambiaLaAlternanciaDeMovimiento() {
        Plano plano = crearPlano(
                Terreno.PISO, Terreno.PISO, Terreno.PISO, Terreno.PISO);
        Posicion inicio = new Posicion(1, 1);
        Fantasma fantasma = new Fantasma(inicio, new Random(1),30);
        Heroe heroe = crearHeroe(100, new Posicion(1, 4));
        Agenda agenda = new Agenda();

        fantasma.desactivar();
        fantasma.actuar(plano, heroe, agenda);

        assertEquals(inicio, fantasma.getPosicion());

        fantasma.activar();
        fantasma.actuar(plano, heroe, agenda);

        assertEquals(new Posicion(1, 2), fantasma.getPosicion());
    }

    @Test
    void rechazaArgumentosNulos() {
        Plano plano = crearPlano(Terreno.PISO, Terreno.PISO);
        Fantasma fantasma =
                new Fantasma(new Posicion(1, 1), new Random(1),30);

        assertThrows(IllegalArgumentException.class,
                () -> fantasma.decidirMovimiento(null, new Posicion(1, 2)));
        assertThrows(IllegalArgumentException.class,
                () -> fantasma.decidirMovimiento(plano, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Fantasma(new Posicion(1, 1), null,30));
    }
}