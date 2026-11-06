package modelo;

import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.CofreDePuntos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import entidades.monstruos.Errante;
import java.util.Random;

class MundoTest {
    private Mundo mundo;

    /** Plano de 5x5 con borde de pared, entrada en (2,2) y, si se pide, salida en (4,4) */
    private static Plano plano(boolean conSalida) {
        Terreno[][] t = new Terreno[5][5];
        for (int f = 0; f < 5; f++) {
            for (int c = 0; c < 5; c++) {
                boolean borde = f == 0 || f == 4 || c == 0 || c == 4;
                t[f][c] = borde ? Terreno.PARED : Terreno.PISO;
            }
        }
        return new Plano(t, new Posicion(2, 2), conSalida ? new Posicion(4, 4) : null);
    }

    /** Escalera entre el piso 1 y el piso 2 del tablero 1, en la misma celda de ambos */
    private static Conexion escalera(int fila, int columna) {
        return new Conexion(
                new Ubicacion(1, 1, new Posicion(fila, columna)),
                new Ubicacion(1, 2, new Posicion(fila, columna)));
    }

    /** Carga los dos planos del mundo de 1 tablero y 2 pisos: la salida queda en el piso 2 */
    private void cargarPlanos() {
        mundo.agregarPlano(1, 1, plano(false));
        mundo.agregarPlano(1, 2, plano(true));
    }

    @BeforeEach
    void armarMundo() {
        mundo = new Mundo(1, 2);
    }

    // PLANOS -------------------------------------------

    @Test
    void mundoNuevoNoTienePlanosCargados() {
        assertEquals(1, mundo.getTableros());
        assertEquals(2, mundo.getPisos());
        assertThrows(IllegalArgumentException.class, () -> mundo.planoEn(1, 1));
        assertNull(mundo.ubicacionDeSalida());
    }

    @Test
    void dimensionesInvalidasLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Mundo(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Mundo(1, 0));
    }

    @Test
    void agregarYBuscarPlano() {
        Plano p = plano(false);
        mundo.agregarPlano(1, 2, p);
        assertSame(p, mundo.planoEn(1, 2));
        assertSame(p, mundo.planoEn(new Ubicacion(1, 2, new Posicion(2, 2))));
    }

    @Test
    void tableroOPisoFueraDeRangoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(0, 1, plano(false)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(2, 1, plano(false)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(1, 3, plano(false)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(1, 1, null));
    }

    @Test
    void noSePuedenAgregarDosPlanosEnElMismoLugar() {
        mundo.agregarPlano(1, 1, plano(false));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(1, 1, plano(false)));
    }

    @Test
    void soloUnPlanoPuedeTenerLaSalida() {
        mundo.agregarPlano(1, 1, plano(true));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarPlano(1, 2, plano(true)));
    }

    @Test
    void inicioYSalida() {
        cargarPlanos();
        assertEquals(new Ubicacion(1, 1, new Posicion(2, 2)), mundo.getInicio());
        assertEquals(new Ubicacion(1, 2, new Posicion(4, 4)), mundo.ubicacionDeSalida());
    }

    // CONEXIONES --------------------------------

    @Test
    void agregarYBuscarConexionDesdeAmbosExtremos() {
        cargarPlanos();
        Conexion conexion = escalera(3, 3);
        mundo.agregarConexion(conexion);

        assertSame(conexion, mundo.conexionEn(conexion.origen()));
        assertSame(conexion, mundo.conexionEn(conexion.destino()));
        assertNull(mundo.conexionEn(new Ubicacion(1, 1, new Posicion(3, 2))));
        assertEquals(1, mundo.getConexiones().cantidad());
    }

    @Test
    void conexionEnParedLanzaExcepcion() {
        cargarPlanos();
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(1, 1)));
    }

    @Test
    void conexionEnEntradaOSalidaLanzaExcepcion() {
        cargarPlanos();
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(2, 2))); // entrada
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(4, 4))); // salida del piso 2
    }

    @Test
    void conexionEnCofreLanzaExcepcion() {
        cargarPlanos();
        mundo.planoEn(1, 1).agregarCofre(new CofreDePuntos(new Posicion(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(3, 3)));
    }

    @Test
    void conexionEnMonstruoLanzaExcepcion() {
        cargarPlanos();
        mundo.planoEn(1, 1).agregarMonstruo(new Errante(new Posicion(3, 3), new Random(1)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(3, 3)));
    }

    @Test
    void conexionDebeUnirDosPlanosDistintos() {
        cargarPlanos();
        Conexion mismoPlano = new Conexion(
                new Ubicacion(1, 1, new Posicion(3, 3)),
                new Ubicacion(1, 1, new Posicion(3, 2)));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(mismoPlano));
    }

    @Test
    void unaCeldaNoPuedeTenerDosConexiones() {
        cargarPlanos();
        mundo.agregarConexion(escalera(3, 3));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(3, 3)));
    }

    @Test
    void conexionConPlanoSinCargarLanzaExcepcion() {
        mundo.agregarPlano(1, 1, plano(false));
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(escalera(3, 3)));
    }

    @Test
    void nullComoConexionLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> mundo.agregarConexion(null));
    }

    // ALCANZABILIDAD DE LA SALIDA --------------------------

    @Test
    void salidaNoAlcanzableSinConexiones() {
        cargarPlanos();
        assertFalse(mundo.salidaEsAlcanzable());
    }

    @Test
    void salidaAlcanzableConUnaEscalera() {
        cargarPlanos();
        mundo.agregarConexion(escalera(3, 3));
        assertTrue(mundo.salidaEsAlcanzable());
    }

    @Test
    void salidaEnElPlanoDeInicioEsAlcanzable() {
        mundo.agregarPlano(1, 1, plano(true));
        mundo.agregarPlano(1, 2, plano(false));
        assertTrue(mundo.salidaEsAlcanzable());
    }

    @Test
    void sinSalidaCargadaNoEsAlcanzable() {
        mundo.agregarPlano(1, 1, plano(false));
        assertFalse(mundo.salidaEsAlcanzable());
    }

    @Test
    void alcanzableEnCadenaDePlanosYEnAmbosSentidos() {
        Mundo grande = new Mundo(2, 2);
        grande.agregarPlano(1, 1, plano(false));
        grande.agregarPlano(1, 2, plano(false));
        grande.agregarPlano(2, 1, plano(false)); // queda aislado
        grande.agregarPlano(2, 2, plano(true));

        Ubicacion a = new Ubicacion(1, 1, new Posicion(3, 3));
        Ubicacion b = new Ubicacion(1, 2, new Posicion(3, 3));
        Ubicacion c = new Ubicacion(1, 2, new Posicion(3, 2));
        Ubicacion d = new Ubicacion(2, 2, new Posicion(3, 2));

        grande.agregarConexion(new Conexion(a, b));
        assertFalse(grande.salidaEsAlcanzable());

        grande.agregarConexion(new Conexion(d, c)); // cargada al reves: igual se recorre en ambos sentidos
        assertTrue(grande.salidaEsAlcanzable());
    }
}
