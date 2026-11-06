package modelo;

import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Errante;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColocadorDeConexionesTest {
    private Mundo mundo;
    private ColocadorDeConexiones colocador;

    /** Arma un plano desde cadenas. La E es la entrada y la S, si hay, la salida */
    private static Plano desdeCadenas(String... filas) {
        Terreno[][] t = new Terreno[filas.length][filas[0].length()];
        Posicion entrada = null;
        Posicion salida = null;
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length(); c++) {
                char simbolo = filas[f].charAt(c);
                t[f][c] = Terreno.desdeSimbolo(simbolo);
                if (simbolo == 'E') {
                    entrada = new Posicion(f + 1, c + 1);
                }
                if (simbolo == 'S') {
                    salida = new Posicion(f + 1, c + 1);
                }
            }
        }
        return new Plano(t, entrada, salida);
    }

    /** Plano de 5x5 con todo el interior libre. Con salida, la S queda en (4,4) */
    private static Plano abierto(boolean conSalida) {
        return desdeCadenas(
                "#####",
                "#E..#",
                "#...#",
                conSalida ? "#..S#" : "#...#",
                "#####");
    }

    private void cargarPlanosAbiertos() {
        mundo.agregarPlano(1, 1, abierto(false));
        mundo.agregarPlano(1, 2, abierto(true));
    }

    @BeforeEach
    void armar() {
        mundo = new Mundo(1, 2);
        colocador = new ColocadorDeConexiones(new Random(1));
    }

    @Test
    void sinGeneradorAleatorioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new ColocadorDeConexiones(null));
    }

    @Test
    void laEscaleraUsaLaMismaCeldaEnAmbosPisos() {
        cargarPlanosAbiertos();
        Conexion escalera = colocador.colocarEscalera(mundo, 1, 1);

        assertEquals(1, escalera.origen().tablero());
        assertEquals(1, escalera.origen().piso());
        assertEquals(2, escalera.destino().piso());
        assertEquals(escalera.origen().posicion(), escalera.destino().posicion());
        assertSame(escalera, mundo.conexionEn(escalera.origen()));
    }

    @Test
    void laEscaleraEvitaEntradaCofreYMonstruo() {
        // abajo solo hay piso en (2,2)E, (2,3), (2,4), (3,2) y (3,4): la unica celda libre es (3,2)
        Plano abajo = desdeCadenas(
                "#####",
                "#E..#",
                "#.#.#",
                "#####",
                "#####");
        abajo.agregarCofre(new CofreDePuntos(new Posicion(2, 3)));
        abajo.agregarMonstruo(new Errante(new Posicion(2, 4), new Random(1)));
        abajo.agregarCofre(new CofreDePuntos(new Posicion(3, 4)));
        mundo.agregarPlano(1, 1, abajo);
        mundo.agregarPlano(1, 2, abierto(true));

        Conexion escalera = colocador.colocarEscalera(mundo, 1, 1);

        assertEquals(new Posicion(3, 2), escalera.origen().posicion());
    }

    @Test
    void dosEscalerasNoComparteLaMismaCelda() {
        cargarPlanosAbiertos();
        Conexion primera = colocador.colocarEscalera(mundo, 1, 1);
        Conexion segunda = colocador.colocarEscalera(mundo, 1, 1);

        assertNotEquals(primera.origen(), segunda.origen());
        assertEquals(2, mundo.getConexiones().cantidad());
    }

    @Test
    void escaleraHaciaUnPisoInexistenteLanzaExcepcion() {
        cargarPlanosAbiertos();
        assertThrows(IllegalArgumentException.class, () -> colocador.colocarEscalera(mundo, 1, 2));
    }
}
