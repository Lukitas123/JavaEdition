package generador;

import static org.junit.jupiter.api.Assertions.*;

import estructuras.Lista;
import modelo.Costo;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GrafoDelPlanoTest {
    private Costo costoEstandar;

    @BeforeEach
    void inicializar() {
        costoEstandar = new Costo(1, 3, 8);
    }

    @Test
    void rechazaPlanoOCostoNulosONegativos() {
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E.S#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        assertThrows(IllegalArgumentException.class, () -> new GrafoDelPlano(null, costoEstandar));
        assertThrows(IllegalArgumentException.class, () -> new GrafoDelPlano(plano, null));
        assertThrows(IllegalArgumentException.class, () -> new GrafoDelPlano(plano, new Costo(-1, 3, 8)));
    }

    @Test
    void construyeVerticesYAristasSoloEnCeldasTransitables() {
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E~S#",
                "#.#.#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        GrafoDelPlano grafo = new GrafoDelPlano(plano, costoEstandar);

        //hay 5 celdas transitables: (2,2), (2,3), (2,4), (3,2) y (3,4)
        assertEquals(5, grafo.cantidadVertices());
        assertTrue(grafo.contienePosicion(new Posicion(2, 2)));
        assertFalse(grafo.contienePosicion(new Posicion(1, 1)));
        assertFalse(grafo.contienePosicion(new Posicion(3, 3)));

        //entrar al agua en (2,3) desde (2,2) cuesta 3, pero volver al piso (2,2) cuesta 1
        assertTrue(grafo.sonAdyacentes(new Posicion(2, 2), new Posicion(2, 3)));
        assertEquals(3, grafo.obtenerCostoMovimiento(new Posicion(2, 2), new Posicion(2, 3)));
        assertEquals(1, grafo.obtenerCostoMovimiento(new Posicion(2, 3), new Posicion(2, 2)));
    }

    @Test
    void dijkstraEsquivaLaLavaCuandoRodearPorPisoEsMasBarato() {
        //camino de arriba pasa por lava '^' en (2,3): costo = 8 + 1 = 9 (2 pasos)
        //camino de abajo rodea por piso '.': (2,2)->(3,2)->(3,3)->(3,4)->(2,4) costo = 1+1+1+1 = 4 (4 pasos)
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E^S#",
                "#...#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        GrafoDelPlano grafo = new GrafoDelPlano(plano, costoEstandar);
        Posicion origen = new Posicion(2, 2);
        Posicion destino = new Posicion(2, 4);

        Lista<Posicion> caminoDijkstra = grafo.caminoMinimo(origen, destino);
        assertEquals(5, caminoDijkstra.cantidad());
        assertEquals(new Posicion(2, 2), caminoDijkstra.obtener(0));
        assertEquals(new Posicion(3, 2), caminoDijkstra.obtener(1));
        assertEquals(new Posicion(3, 3), caminoDijkstra.obtener(2));
        assertEquals(new Posicion(3, 4), caminoDijkstra.obtener(3));
        assertEquals(new Posicion(2, 4), caminoDijkstra.obtener(4));

        assertEquals(4, grafo.costoCaminoMinimo(origen, destino));
    }

    @Test
    void bfsEligeElCaminoConMenosPasosAunqueHayaLava() {
        //mismo plano con lava en el medio: BFS ignora el costo 8 y va directo en 2 pasos (3 celdas)
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E^S#",
                "#...#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        GrafoDelPlano grafo = new GrafoDelPlano(plano, costoEstandar);
        Posicion origen = new Posicion(2, 2);
        Posicion destino = new Posicion(2, 4);

        Lista<Posicion> caminoBfs = grafo.caminoMasCortoEnPasos(origen, destino);
        assertEquals(3, caminoBfs.cantidad());
        assertEquals(new Posicion(2, 2), caminoBfs.obtener(0));
        assertEquals(new Posicion(2, 3), caminoBfs.obtener(1));
        assertEquals(new Posicion(2, 4), caminoBfs.obtener(2));
    }

    @Test
    void caminoHaciaLaMismaPosicionDevuelveUnSoloElementoYCostoCero() {
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E.S#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        GrafoDelPlano grafo = new GrafoDelPlano(plano, costoEstandar);
        Posicion entrada = new Posicion(2, 2);

        Lista<Posicion> camino = grafo.caminoMinimo(entrada, entrada);
        assertEquals(1, camino.cantidad());
        assertEquals(entrada, camino.obtener(0));
        assertEquals(0, grafo.costoCaminoMinimo(entrada, entrada));
    }

    @Test
    void caminoInalcanzableOPosicionFueraDelGrafoDevuelveVacioYMenosUno() {
        //pared en el medio que desconecta (2,2) de (2,4)
        Plano plano = crearPlanoDesdeLineas(new String[]{
                "#####",
                "#E#S#",
                "#####"
        }, new Posicion(2, 2), new Posicion(2, 4));

        GrafoDelPlano grafo = new GrafoDelPlano(plano, costoEstandar);
        Posicion entrada = new Posicion(2, 2);
        Posicion salida = new Posicion(2, 4);
        Posicion pared = new Posicion(1, 1);

        assertTrue(grafo.caminoMinimo(entrada, salida).estaVacia());
        assertEquals(-1, grafo.costoCaminoMinimo(entrada, salida));
        assertTrue(grafo.caminoMasCortoEnPasos(entrada, salida).estaVacia());

        assertTrue(grafo.caminoMinimo(entrada, pared).estaVacia());
        assertEquals(-1, grafo.costoCaminoMinimo(entrada, pared));
    }

    private Plano crearPlanoDesdeLineas(String[] lineas, Posicion entrada, Posicion salida) {
        int filas = lineas.length;
        int columnas = lineas[0].length();
        Terreno[][] grilla = new Terreno[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                grilla[f][c] = Terreno.desdeSimbolo(lineas[f].charAt(c));
            }
        }
        return new Plano(grilla, entrada, salida);
    }
}
