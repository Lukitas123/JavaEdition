package modelo;

import static org.junit.jupiter.api.Assertions.*;

import entidades.monstruos.Monstruo;
import entidades.objetos.Brujula;
import entidades.objetos.CatalogoObjetos;
import entidades.objetos.Daga;
import entidades.objetos.Escudo;
import entidades.objetos.PocionDeEnergia;
import entidades.objetos.Sueno;
import entidades.objetos.ZapatosDeAgua;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Regla 2: usar un objeto del inventario (tecla u) */
class MazmorraUsarObjetoTest {
    /** Monstruo quieto que cuenta cuántas veces decidió y, al alcanzar al héroe, le hace 30 de daño */
    private static class MonstruoDePrueba extends Monstruo {
        private int decisiones = 0;

        MonstruoDePrueba(Posicion posicion) {
            super(posicion);
        }

        @Override
        public Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe) {
            decisiones++;
            return getPosicion();
        }

        @Override
        public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
            daniarAlHeroe(heroe, 30);
        }
    }

    /** Arma un plano desde cadenas, la E es la entrada y la S, si hay, la salida */
    private static Plano plano(String... filas) {
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

    /** Plano de 5x5 con el interior libre y la entrada en (2,2) */
    private static Plano abierto() {
        return plano(
                "#####",
                "#E..#",
                "#...#",
                "#...#",
                "#####");
    }

    /** Catálogo con los seis objetos del juego */
    private static CatalogoObjetos catalogo() {
        CatalogoObjetos catalogo = new CatalogoObjetos();
        catalogo.registrar(PocionDeEnergia.id, new PocionDeEnergia(30));
        catalogo.registrar(ZapatosDeAgua.ID_OBJETO, new ZapatosDeAgua());
        catalogo.registrar(Escudo.ID_OBJETO, new Escudo());
        catalogo.registrar(Daga.ID_OBJETO, new Daga(new Random(1)));
        catalogo.registrar(Brujula.ID_OBJETO, new Brujula());
        catalogo.registrar(Sueno.ID_OBJETO, new Sueno());
        return catalogo;
    }

    private static Mazmorra partida(Plano plano, int energia) {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);
        return new Mazmorra(Dificultad.FACIL, mundo, new Heroe(energia, mundo.getInicio()),
                new Agenda(), new Costo(1, 3, 8), catalogo());
    }

    private static boolean logContiene(Mazmorra mazmorra, String texto) {
        for (String entrada : mazmorra.getLog().getEntradas()) {
            if (entrada.contains(texto)) {
                return true;
            }
        }
        return false;
    }

    // POCION ------------------------------------------------

    @Test
    void laPocionDevuelveEnergiaYSeGasta() {
        Mazmorra mazmorra = partida(abierto(), 50);
        mazmorra.getHeroe().agregarObjeto(PocionDeEnergia.id);

        assertTrue(mazmorra.usarObjeto(PocionDeEnergia.id));

        assertEquals(80, mazmorra.getHeroe().getEnergia());
        assertFalse(mazmorra.getHeroe().tieneObjeto(PocionDeEnergia.id));
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void siHayVariasUnidadesSeGastaSoloUna() {
        Mazmorra mazmorra = partida(abierto(), 50);
        mazmorra.getHeroe().agregarObjeto(PocionDeEnergia.id);
        mazmorra.getHeroe().agregarObjeto(PocionDeEnergia.id);

        mazmorra.usarObjeto(PocionDeEnergia.id);

        assertEquals(1, mazmorra.getHeroe().cantidadDe(PocionDeEnergia.id));
    }

    // ZAPATOS DE AGUA -----------------------------------

    @Test
    void conZapatosElAguaCuestaComoPiso() {
        Mazmorra mazmorra = partida(plano(
                "#####",
                "#E~.#",
                "#####"), 100);
        mazmorra.getHeroe().agregarObjeto(ZapatosDeAgua.ID_OBJETO);

        mazmorra.usarObjeto(ZapatosDeAgua.ID_OBJETO);
        assertTrue(mazmorra.getHeroe().tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        mazmorra.moverHeroe(Direccion.DERECHA); // el agua

        assertEquals(99, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void losZapatosDuranVeintePasosContandoElDeUso() {
        Mazmorra mazmorra = partida(abierto(), 100);
        mazmorra.getHeroe().agregarObjeto(ZapatosDeAgua.ID_OBJETO);
        mazmorra.usarObjeto(ZapatosDeAgua.ID_OBJETO); // paso 1

        for (int i = 0; i < 19; i++) {
            mazmorra.esperar();
        }
        assertEquals(20, mazmorra.getAgenda().getPasoActual());
        assertTrue(mazmorra.getHeroe().tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));

        mazmorra.esperar();
        assertFalse(mazmorra.getHeroe().tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
    }

    // ESCUDO -----------------------------------------

    @Test
    void conEscudoLosMonstruosNoQuitanEnergia() {
        Plano plano = abierto();
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 3)));
        Mazmorra mazmorra = partida(plano, 100);
        mazmorra.getHeroe().agregarObjeto(Escudo.ID_OBJETO);

        mazmorra.usarObjeto(Escudo.ID_OBJETO);
        mazmorra.moverHeroe(Direccion.DERECHA); // pisa al monstruo

        assertEquals(99, mazmorra.getHeroe().getEnergia()); // solo el costo del piso
    }

    @Test
    void sinEscudoElMismoMonstruoLeQuitaTreinta() {
        Plano plano = abierto();
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 3)));
        Mazmorra mazmorra = partida(plano, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(69, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void elEscudoDuraDiezPasosContandoElDeUso() {
        Mazmorra mazmorra = partida(abierto(), 100);
        mazmorra.getHeroe().agregarObjeto(Escudo.ID_OBJETO);
        mazmorra.usarObjeto(Escudo.ID_OBJETO); // paso 1

        for (int i = 0; i < 9; i++) {
            mazmorra.esperar();
        }
        assertEquals(10, mazmorra.getAgenda().getPasoActual());
        assertTrue(mazmorra.getHeroe().tieneEfecto(EfectoHeroe.ESCUDO));

        mazmorra.esperar();
        assertFalse(mazmorra.getHeroe().tieneEfecto(EfectoHeroe.ESCUDO));
    }

    // DAGA ----------------------------------------

    @Test
    void laDagaEliminaAlMonstruoDeAlLadoYSeGasta() {
        Plano plano = abierto();
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 3)));
        Mazmorra mazmorra = partida(plano, 100);
        mazmorra.getHeroe().agregarObjeto(Daga.ID_OBJETO);

        assertTrue(mazmorra.usarObjeto(Daga.ID_OBJETO));

        assertEquals(0, plano.getMonstruos().size());
        assertFalse(mazmorra.getHeroe().tieneObjeto(Daga.ID_OBJETO));
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void laDagaSinMonstruoCercaNoSeUsaYNoCuentaComoPaso() {
        Plano plano = abierto();
        MonstruoDePrueba lejano = new MonstruoDePrueba(new Posicion(4, 4));
        plano.agregarMonstruo(lejano);
        Mazmorra mazmorra = partida(plano, 100);
        mazmorra.getHeroe().agregarObjeto(Daga.ID_OBJETO);

        assertFalse(mazmorra.usarObjeto(Daga.ID_OBJETO));

        assertTrue(mazmorra.getHeroe().tieneObjeto(Daga.ID_OBJETO));
        assertEquals(1, plano.getMonstruos().size());
        assertEquals(0, mazmorra.getAgenda().getPasoActual());
        assertEquals(0, lejano.decisiones); // los monstruos no actuaron
    }

    // BRÚJULA --------------------------------------------

    @Test
    void laBrujulaIndicaLaDireccionDeLaSalidaYQuedaEnElLog() {
        Mazmorra mazmorra = partida(plano(
                "#####",
                "#E..#",
                "#...#",
                "#..S#",
                "#####"), 100);
        mazmorra.getHeroe().agregarObjeto(Brujula.ID_OBJETO);

        assertTrue(mazmorra.usarObjeto(Brujula.ID_OBJETO));

        assertTrue(logContiene(mazmorra, "La salida esta hacia abajo a la derecha"));
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void laBrujulaAvisaSiLaSalidaNoEstaEnElPlano() {
        Mazmorra mazmorra = partida(abierto(), 100);
        mazmorra.getHeroe().agregarObjeto(Brujula.ID_OBJETO);

        mazmorra.usarObjeto(Brujula.ID_OBJETO);

        assertTrue(logContiene(mazmorra, "La salida no esta en este plano."));
    }

    // SUEÑO ---------------------------------------------------

    @Test
    void elSuenoDuermeALosMonstruosSietePasosContandoElDeUso() {
        Plano plano = abierto();
        MonstruoDePrueba monstruo = new MonstruoDePrueba(new Posicion(2, 4));
        plano.agregarMonstruo(monstruo);
        Mazmorra mazmorra = partida(plano, 100);
        mazmorra.getHeroe().agregarObjeto(Sueno.ID_OBJETO);

        mazmorra.usarObjeto(Sueno.ID_OBJETO); // paso 1
        assertTrue(plano.monstruosDormidos());

        for (int i = 0; i < 6; i++) {
            mazmorra.esperar();
        }
        assertEquals(7, mazmorra.getAgenda().getPasoActual());
        assertTrue(plano.monstruosDormidos());
        assertEquals(0, monstruo.decisiones); // durmieron todo ese tiempo

        mazmorra.esperar(); // paso 8: se despiertan y actuan
        assertFalse(plano.monstruosDormidos());
        assertEquals(1, monstruo.decisiones);
    }

    // LO Q NO SE PUEDE USAR --------------------------------------

    @Test
    void usarUnObjetoQueNoTieneNoHaceNada() {
        Mazmorra mazmorra = partida(abierto(), 100);

        assertFalse(mazmorra.usarObjeto(PocionDeEnergia.id));

        assertEquals(100, mazmorra.getHeroe().getEnergia());
        assertEquals(0, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void unObjetoQueNoEstaEnElCatalogoLanzaExcepcion() {
        Mazmorra mazmorra = partida(abierto(), 100);
        mazmorra.getHeroe().agregarObjeto("objeto_raro");

        assertThrows(IllegalArgumentException.class, () -> mazmorra.usarObjeto("objeto_raro"));
    }

    @Test
    void unaPartidaSinCatalogoNoPuedeUsarObjetos() {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, abierto());
        Mazmorra sinCatalogo = new Mazmorra(Dificultad.FACIL, mundo,
                new Heroe(100, mundo.getInicio()), new Agenda(), new Costo(1, 3, 8));
        sinCatalogo.getHeroe().agregarObjeto(PocionDeEnergia.id);

        assertThrows(IllegalArgumentException.class, () -> sinCatalogo.usarObjeto(PocionDeEnergia.id));
    }

    @Test
    void elIdNuloOVacioLanzaExcepcion() {
        Mazmorra mazmorra = partida(abierto(), 100);

        assertThrows(IllegalArgumentException.class, () -> mazmorra.usarObjeto(null));
        assertThrows(IllegalArgumentException.class, () -> mazmorra.usarObjeto(" "));
    }

    @Test
    void conLaPartidaTerminadaLanzaExcepcion() {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, abierto());
        Mazmorra terminada = new Mazmorra(Dificultad.FACIL, mundo, new Heroe(100, mundo.getInicio()),
                new Agenda(), new Costo(1, 3, 8), EstadoPartida.GANADA, catalogo());

        assertThrows(IllegalStateException.class, () -> terminada.usarObjeto(PocionDeEnergia.id));
    }

    // USAR UN OBJETO ES UN PASO --------------------------

    @Test
    void despuesDeUsarUnObjetoActuanLosMonstruos() {
        Plano plano = abierto();
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 2))); // sobre el heroe
        Mazmorra mazmorra = partida(plano, 50);
        mazmorra.getHeroe().agregarObjeto(PocionDeEnergia.id);

        mazmorra.usarObjeto(PocionDeEnergia.id);

        // +30 de la pocion y -30 del monstruo
        assertEquals(50, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void siUnMonstruoLoDejaSinEnergiaTrasUsarUnObjetoSePierde() {
        Plano plano = abierto();
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 2)));
        Mazmorra mazmorra = partida(plano, 30);
        mazmorra.getHeroe().agregarObjeto(Brujula.ID_OBJETO);

        mazmorra.usarObjeto(Brujula.ID_OBJETO);

        assertEquals(EstadoPartida.PERDIDA, mazmorra.getEstado());
    }
}
