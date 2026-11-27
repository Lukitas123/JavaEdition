package modelo;

import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.CofreDePuntos;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.Test;

import java.util.List;

class LogPartidaTest {

    @Test
    void empiezaVacioYRegistraEnOrdenCronologico() {
        LogPartida log = new LogPartida();
        assertTrue(log.estaVacio());
        assertEquals(0, log.cantidad());

        log.registrar(1, "Heroe avanza");
        log.registrar(2, "Cofre abierto");

        assertFalse(log.estaVacio());
        assertEquals(2, log.cantidad());
        assertEquals(
                List.of("[Paso 1] Heroe avanza", "[Paso 2] Cofre abierto"),
                log.getEntradas());
    }

    @Test
    void ultimasDevuelveLasMasRecientesEnOrden() {
        LogPartida log = new LogPartida();
        log.registrar(1, "Uno");
        log.registrar(2, "Dos");
        log.registrar(3, "Tres");

        assertEquals(List.of(), log.ultimas(0));
        assertEquals(List.of("[Paso 2] Dos", "[Paso 3] Tres"), log.ultimas(2));
        assertEquals(log.getEntradas(), log.ultimas(10));
    }

    @Test
    void rechazaParametrosInvalidos() {
        LogPartida log = new LogPartida();
        assertThrows(IllegalArgumentException.class, () -> log.registrar(-1, "Paso invalido"));
        assertThrows(IllegalArgumentException.class, () -> log.registrar(0, null));
        assertThrows(IllegalArgumentException.class, () -> log.registrar(0, "   "));
        assertThrows(IllegalArgumentException.class, () -> log.ultimas(-1));
    }

    @Test
    void mazmorraRegistraMovimientosCofreYVictoriaEnSuLog() {
        Terreno[][] terreno = {
                {Terreno.PISO, Terreno.PISO, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        };
        Plano plano = new Plano(terreno, new Posicion(1, 1), new Posicion(1, 3));
        plano.agregarCofre(new CofreDePuntos(new Posicion(1, 2)));
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);
        Heroe heroe = new Heroe(50, mundo.getInicio());
        Mazmorra mazmorra = new Mazmorra(
                Dificultad.FACIL, mundo, heroe, new Agenda(), new Costo(1, 3, 5));

        mazmorra.esperar();
        assertTrue(mazmorra.moverHeroe(Direccion.DERECHA));
        assertTrue(mazmorra.moverHeroe(Direccion.DERECHA));

        List<String> entradas = mazmorra.getLog().getEntradas();
        assertEquals(5, entradas.size());
        assertTrue(entradas.get(0).contains("[Paso 1] Heroe espera"));
        assertTrue(entradas.get(1).contains("[Paso 2] Heroe se mueve DERECHA"));
        assertTrue(entradas.get(2).contains("[Paso 2] Cofre abierto: CofreDePuntos"));
        assertTrue(entradas.get(3).contains("[Paso 3] Heroe se mueve DERECHA"));
        assertTrue(entradas.get(4).contains("Partida ganada"));
    }

    @Test
    void mazmorraRegistraLaDerrotaEnSuLog() {
        Mazmorra mazmorra = MundosDePrueba.mazmorraDe(MundosDePrueba.abierto(false), 1);

        mazmorra.moverHeroe(Direccion.DERECHA);

        List<String> entradas = mazmorra.getLog().getEntradas();
        assertEquals(2, entradas.size());
        assertTrue(entradas.get(0).contains("[Paso 1] Heroe se mueve DERECHA"));
        assertTrue(entradas.get(1).contains("[Paso 1] Heroe sin energia. Partida perdida."));
    }

    @Test
    void mazmorraRegistraElUsoDeUnaConexionEnSuLog() {
        Mazmorra mazmorra = MundosDePrueba.mazmorraDe(MundosDePrueba.mundoConEscalera(), 50);

        mazmorra.moverHeroe(Direccion.ABAJO);
        mazmorra.moverHeroe(Direccion.DERECHA);

        List<String> entradas = mazmorra.getLog().getEntradas();
        assertEquals(3, entradas.size());
        assertTrue(entradas.get(2).contains("[Paso 2] Conexion usada hacia tablero 1, piso 2"));
    }

    @Test
    void mazmorraRegistraLosEventosEjecutadosEnSuLog() {
        Mazmorra mazmorra = MundosDePrueba.mazmorraDe(MundosDePrueba.abierto(false), 50);
        mazmorra.getAgenda().programar(1, new FinEfectoHeroe(EfectoHeroe.ESCUDO));

        mazmorra.esperar();

        List<String> entradas = mazmorra.getLog().getEntradas();
        assertEquals(2, entradas.size());
        assertTrue(entradas.get(0).contains("[Paso 1] Evento ejecutado: FinEfectoHeroe"));
        assertTrue(entradas.get(1).contains("[Paso 1] Heroe espera"));
    }
}
