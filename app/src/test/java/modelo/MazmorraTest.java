package modelo;

import static modelo.MundosDePrueba.abierto;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MazmorraTest {
    @Test
    void empiezaEnCursoConElHeroeEnLaEntrada() {
        Mazmorra mazmorra = mazmorraDe(abierto(true), 100);

        assertEquals(EstadoPartida.EN_CURSO, mazmorra.getEstado());
        assertFalse(mazmorra.estaTerminada());
        assertEquals(mazmorra.getMundo().getInicio(), mazmorra.getHeroe().getUbicacion());
        assertEquals(new Posicion(2, 2), mazmorra.getHeroe().getUbicacion().posicion());
        assertEquals(Dificultad.FACIL, mazmorra.getDificultad());
    }

    @Test
    void elPlanoActualEsElDondeEstaElHeroe() {
        Plano plano = abierto(false);
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        assertSame(plano, mazmorra.planoActual());
    }

    @Test
    void elPuntajeInicialSoloTieneLaEnergia() {
        Puntaje puntaje = mazmorraDe(abierto(true), 100).calcularPuntaje();

        assertEquals(0, puntaje.cofresAbiertos());
        assertEquals(100, puntaje.energiaRestante());
        assertEquals(0, puntaje.pasos());
        assertEquals(200, puntaje.valor());
    }

    @Test
    void sePuedeCrearConUnEstadoDado() {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, abierto(true));
        Mazmorra mazmorra = new Mazmorra(Dificultad.DIFICIL, mundo,
                new Heroe(50, mundo.getInicio()), new Agenda(), new Costo(1, 3, 8),
                ParametrosDePrueba.ENTIDADES, EstadoPartida.GANADA);

        assertTrue(mazmorra.estaTerminada());
        assertEquals(EstadoPartida.GANADA, mazmorra.getEstado());
    }

    @Test
    void rechazaDatosNulos() {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, abierto(true));
        Heroe heroe = new Heroe(100, mundo.getInicio());
        Agenda agenda = new Agenda();
        Costo costos = new Costo(1, 3, 8);

        assertThrows(IllegalArgumentException.class,
                () -> new Mazmorra(null, mundo, heroe, agenda, costos, ParametrosDePrueba.ENTIDADES));
        assertThrows(IllegalArgumentException.class,
                () -> new Mazmorra(Dificultad.FACIL, null, heroe, agenda, costos, ParametrosDePrueba.ENTIDADES));
        assertThrows(IllegalArgumentException.class,
                () -> new Mazmorra(Dificultad.FACIL, mundo, null, agenda, costos, ParametrosDePrueba.ENTIDADES));
        assertThrows(IllegalArgumentException.class,
                () -> new Mazmorra(Dificultad.FACIL, mundo, heroe, null, costos, ParametrosDePrueba.ENTIDADES));
        assertThrows(IllegalArgumentException.class,
                () -> new Mazmorra(Dificultad.FACIL, mundo, heroe, agenda, null, ParametrosDePrueba.ENTIDADES));
    }
}
