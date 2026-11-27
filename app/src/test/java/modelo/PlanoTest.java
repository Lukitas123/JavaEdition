package modelo;

import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Errante;
import entidades.monstruos.Monstruo;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlanoTest {
    private Plano plano;

    private static Terreno[][] desdeCadenas(String... filas) {
        Terreno[][] t = new Terreno[filas.length][filas[0].length()];
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length(); c++) {
                t[f][c] = Terreno.desdeSimbolo(filas[f].charAt(c));
            }
        }
        return t;
    }

    @BeforeEach
    void armarPlano() {
        plano = new Plano(desdeCadenas(
                "#####",
                "#E.~#",
                "#.#.#",
                "#...#",
                "#####"),
                new Posicion(2, 2), null);
    }

    // ---------- Terreno ----------

    @Test
    void dimensionesYEntrada() {
        assertEquals(5, plano.getFilas());
        assertEquals(5, plano.getColumnas());
        assertEquals(new Posicion(2, 2), plano.getEntrada());
        assertFalse(plano.tieneSalida());
    }

    @Test
    void lasPosicionesVanDeUnoAN() {
        assertEquals(Terreno.PARED, plano.terrenoEn(new Posicion(1, 1)));
        assertEquals(Terreno.PISO, plano.terrenoEn(new Posicion(2, 2)));
        assertEquals(Terreno.AGUA, plano.terrenoEn(new Posicion(2, 4)));
        assertEquals(Terreno.PARED, plano.terrenoEn(new Posicion(5, 5)));
    }

    @Test
    void posicionFueraDelPlanoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> plano.terrenoEn(new Posicion(0, 1)));
        assertThrows(IllegalArgumentException.class, () -> plano.terrenoEn(new Posicion(6, 1)));
    }

    @Test
    void transitableEsTodoLoQueNoEsPared() {
        assertFalse(plano.esTransitable(new Posicion(1, 1)));
        assertTrue(plano.esTransitable(new Posicion(2, 3)));
        assertTrue(plano.esTransitable(new Posicion(2, 4))); // agua
        assertFalse(plano.esTransitable(new Posicion(0, 0))); // fuera
    }

    @Test
    void vecinosTransitablesIgnoraParedes() {
        assertEquals(2, plano.vecinosTransitables(new Posicion(2, 2)).size());
    }

    @Test
    void cambiarTerrenoModificaLaCelda() {
        plano.cambiarTerreno(new Posicion(2, 4), Terreno.PISO);
        assertEquals(Terreno.PISO, plano.terrenoEn(new Posicion(2, 4)));
    }

    @Test
    void marcarVisitada() {
        Posicion p = new Posicion(3, 2);
        assertFalse(plano.estaVisitada(p));
        plano.marcarVisitada(p);
        assertTrue(plano.estaVisitada(p));
    }

    @Test
    void entradaEnParedLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new Plano(desdeCadenas("###", "#.#", "###"), new Posicion(1, 1), null));
    }

    // ---------- Monstruos ----------

    @Test
    void agregarYBuscarMonstruo() {
        Monstruo monstruo = new Errante(new Posicion(2, 3), new Random(1));
        plano.agregarMonstruo(monstruo);

        assertEquals(1, plano.getMonstruos().size());
        assertSame(monstruo, plano.monstruoEn(new Posicion(2, 3)));
        assertNull(plano.monstruoEn(new Posicion(4, 2)));
    }

    @Test
    void quitarMonstruo() {
        Monstruo monstruo = new Errante(new Posicion(2, 3), new Random(1));
        plano.agregarMonstruo(monstruo);

        assertTrue(plano.quitarMonstruo(monstruo));
        assertEquals(0, plano.getMonstruos().size());
        assertFalse(plano.quitarMonstruo(monstruo));
    }

    @Test
    void monstruoEnParedLanzaExcepcion() {
        Monstruo enPared = new Errante(new Posicion(1, 1), new Random(1));
        assertThrows(IllegalArgumentException.class, () -> plano.agregarMonstruo(enPared));
        assertThrows(IllegalArgumentException.class, () -> plano.agregarMonstruo(null));
    }

    // ---------- Cofres ----------

    @Test
    void agregarYBuscarCofre() {
        Cofre cofre = new CofreDePuntos(new Posicion(4, 2));
        plano.agregarCofre(cofre);

        assertEquals(1, plano.getCofres().size());
        assertSame(cofre, plano.cofreEn(new Posicion(4, 2)));
        assertNull(plano.cofreEn(new Posicion(2, 3)));
    }

    @Test
    void quitarCofre() {
        Cofre cofre = new CofreDePuntos(new Posicion(4, 2));
        plano.agregarCofre(cofre);

        assertTrue(plano.quitarCofre(cofre));
        assertEquals(0, plano.getCofres().size());
        assertFalse(plano.quitarCofre(cofre));
    }

    @Test
    void cofreEnParedLanzaExcepcion() {
        Cofre enPared = new CofreDePuntos(new Posicion(1, 1));
        assertThrows(IllegalArgumentException.class, () -> plano.agregarCofre(enPared));
        assertThrows(IllegalArgumentException.class, () -> plano.agregarCofre(null));
    }

    @Test
    void lavaPosteriorAlPuenteLoCancelaEnLaCelda() {
        Posicion inicio = new Posicion(1, 1);
        Posicion agua = new Posicion(1, 2);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA}}, inicio, null);

        plano.activarPuente(0, 15);
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));

        plano.activarLava(agua, 1, 10);
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));
        assertEquals(Terreno.AGUA, plano.terrenoBaseEn(agua));

        // Otra trampa reinicia el vencimiento de esta celda.
        plano.activarLava(agua, 3, 10);
        plano.finalizarLavaSiVencio(12);
        assertEquals(Terreno.LAVA, plano.terrenoEn(agua));

        plano.finalizarLavaSiVencio(14);
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
        assertEquals(Integer.valueOf(16), plano.getFinPuente());
    }

    @Test
    void puentePosteriorCancelaLaLavaTemporal() {
        Posicion inicio = new Posicion(1, 1);
        Posicion agua = new Posicion(1, 2);
        Posicion piso = new Posicion(1, 3);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA, Terreno.PISO}},
                inicio, null);

        plano.activarLava(agua, 0, 10);
        plano.activarLava(piso, 0, 10);
        plano.activarPuente(1, 15);

        assertEquals(Terreno.PISO, plano.terrenoEn(agua));
        assertEquals(Terreno.PISO, plano.terrenoEn(piso));

        plano.finalizarLavaSiVencio(11);
        assertEquals(Terreno.PISO, plano.terrenoEn(agua));

        plano.finalizarPuenteSiVencio(17);
        assertEquals(Terreno.AGUA, plano.terrenoEn(agua));
        assertEquals(Terreno.PISO, plano.terrenoEn(piso));
    }
}
