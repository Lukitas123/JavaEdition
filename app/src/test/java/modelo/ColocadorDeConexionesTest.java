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

    /** Plano con piso solo en la entrada (2,2). */
    private static Plano soloEntrada() {
        return desdeCadenas(
                "#####",
                "#E###",
                "#####",
                "#####",
                "#####");
    }

    /** Plano con piso solo en el centro (3,3). */
    private static Plano soloCentro() {
        return desdeCadenas(
                "#####",
                "#####",
                "##E##",
                "#####",
                "#####");
    }

    /** Mundo de 2 tableros y 2 pisos, con la salida en el tablero 2, piso 2 */
    private static Mundo mundoCompleto() {
        Mundo grande = new Mundo(2, 2);
        grande.agregarPlano(1, 1, abierto(false));
        grande.agregarPlano(1, 2, abierto(false));
        grande.agregarPlano(2, 1, abierto(false));
        grande.agregarPlano(2, 2, abierto(true));
        return grande;
    }

    private static boolean esBorde(Posicion p) {
        return p.fila() == 1 || p.fila() == 5 || p.columna() == 1 || p.columna() == 5;
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

    // CONSTRUCCION -------------------------------------

    @Test
    void sinGeneradorAleatorioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new ColocadorDeConexiones(null));
    }

    // ESCALERAS ------------------------------------------

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
        // Abajo hay piso en (2,2)E, (2,3), (2,4), (3,2) y (3,4): la unica celda libre es (3,2)
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
    void dosEscalerasNoCompartenLaMismaCelda() {
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

    @Test
    void siNoHayCeldaLibreAbreUnaPared() {
        // Piso abajo: (2,2)E y (2,3). Piso arriba: (2,2)E y (3,2). No hay celda libre en ambos.
        mundo.agregarPlano(1, 1, desdeCadenas(
                "#####",
                "#E.##",
                "#####",
                "#####",
                "#####"));
        mundo.agregarPlano(1, 2, desdeCadenas(
                "#####",
                "#E###",
                "#.###",
                "#####",
                "#####"));

        Conexion escalera = colocador.colocarEscalera(mundo, 1, 1);
        Posicion pos = escalera.origen().posicion();

        assertTrue(pos.equals(new Posicion(2, 3)) || pos.equals(new Posicion(3, 2)));
        assertTrue(mundo.planoEn(1, 1).esTransitable(pos));
        assertTrue(mundo.planoEn(1, 2).esTransitable(pos));
    }

    @Test
    void sinCeldaLibreNiParedParaAbrirLanzaExcepcion() {
        mundo.agregarPlano(1, 1, soloEntrada());
        mundo.agregarPlano(1, 2, soloEntrada());
        assertThrows(IllegalStateException.class, () -> colocador.colocarEscalera(mundo, 1, 1));
    }

    // ---------- Pasillos ----------

    @Test
    void elPasilloUneElBordeDeDosTableros() {
        Mundo dos = new Mundo(2, 1);
        dos.agregarPlano(1, 1, abierto(false));
        dos.agregarPlano(2, 1, abierto(true));

        Conexion pasillo = colocador.colocarPasillo(dos, 1, 2, 1);

        assertEquals(1, pasillo.origen().tablero());
        assertEquals(2, pasillo.destino().tablero());
        assertTrue(esBorde(pasillo.origen().posicion()));
        assertTrue(esBorde(pasillo.destino().posicion()));
        assertEquals(Terreno.PISO, dos.planoEn(1, 1).terrenoEn(pasillo.origen().posicion()));
        assertEquals(Terreno.PISO, dos.planoEn(2, 1).terrenoEn(pasillo.destino().posicion()));
        assertSame(pasillo, dos.conexionEn(pasillo.origen()));
    }

    @Test
    void elPasilloSoloUsaBordesPegadosAUnPisoInterior() {
        // Con piso solo en (2,2), los unicos bordes posibles son (1,2) y (2,1)
        Mundo dos = new Mundo(2, 1);
        dos.agregarPlano(1, 1, soloEntrada());
        dos.agregarPlano(2, 1, soloEntrada());

        Conexion pasillo = colocador.colocarPasillo(dos, 1, 2, 1);

        Posicion a = pasillo.origen().posicion();
        Posicion b = pasillo.destino().posicion();
        assertTrue(a.equals(new Posicion(1, 2)) || a.equals(new Posicion(2, 1)));
        assertTrue(b.equals(new Posicion(1, 2)) || b.equals(new Posicion(2, 1)));
    }

    @Test
    void pasilloEntreElMismoTableroLanzaExcepcion() {
        Mundo dos = new Mundo(2, 1);
        dos.agregarPlano(1, 1, abierto(false));
        dos.agregarPlano(2, 1, abierto(true));
        assertThrows(IllegalArgumentException.class, () -> colocador.colocarPasillo(dos, 1, 1, 1));
    }

    @Test
    void pasilloSinBordePosibleLanzaExcepcionYNoCambiaNada() {
        Mundo dos = new Mundo(2, 1);
        dos.agregarPlano(1, 1, soloCentro());
        dos.agregarPlano(2, 1, abierto(true));

        assertThrows(IllegalStateException.class, () -> colocador.colocarPasillo(dos, 1, 2, 1));
        assertEquals(0, dos.getConexiones().cantidad());
    }

    // ---------- Conectar todo el mundo ----------

    @Test
    void conectarTodoHaceAlcanzableLaSalida() {
        Mundo grande = mundoCompleto();
        colocador.conectarTodo(grande);

        // 2 escaleras (una por tablero) y 1 pasillo
        assertEquals(3, grande.getConexiones().cantidad());
        assertTrue(grande.salidaEsAlcanzable());
    }

    @Test
    void conectarTodoSinSalidaLanzaExcepcion() {
        mundo.agregarPlano(1, 1, abierto(false));
        mundo.agregarPlano(1, 2, abierto(false));
        assertThrows(IllegalStateException.class, () -> colocador.conectarTodo(mundo));
    }

    @Test
    void conLaMismaSemillaColocaLasMismasConexiones() {
        Mundo uno = mundoCompleto();
        Mundo otro = mundoCompleto();
        new ColocadorDeConexiones(new Random(7)).conectarTodo(uno);
        new ColocadorDeConexiones(new Random(7)).conectarTodo(otro);

        assertEquals(uno.getConexiones().cantidad(), otro.getConexiones().cantidad());
        for (int i = 0; i < uno.getConexiones().cantidad(); i++) {
            assertEquals(uno.getConexiones().obtener(i), otro.getConexiones().obtener(i));
        }
    }
}
