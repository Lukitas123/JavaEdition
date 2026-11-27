package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import entidades.cofres.*;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.monstruos.Fantasma;
import entidades.monstruos.Monstruo;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;

public class ConversorPlanoTest {

    private static Catalogo<Cofre> pruebaCofre(){
        return CatalogosDePrueba.cofres();
    }

    private static Catalogo<Monstruo> pruebaMonstruos(){
        return CatalogosDePrueba.monstruos();
    }

    private static Terreno[][] terrenoDePrueba() {
        Terreno p = Terreno.PARED;
        Terreno o = Terreno.PISO;
        Terreno a = Terreno.AGUA;
        return new Terreno[][] {
            {p, p, p, p},
            {p, o, a, p},
            {p, o, o, p},
            {p, p, p, p}
        };
    }

    /** Pasa un plano por JSON y lo reconstruye agregando sus entidades agregadas al final */
    private static Plano idaYVuelta(Plano original) {
        Gson gson = new Gson();
        String json = gson.toJson(ConversorPlano.aGuardado(original));
        PlanoGuardado leido = gson.fromJson(json, PlanoGuardado.class);
        Plano restaurado = ConversorPlano.aPlano(leido);
        ConversorPlano.agregarEntidades(restaurado, leido, pruebaMonstruos(), pruebaCofre() );
        return restaurado;
    }

    /** Se conservan el terreno, las visitadas, la entrada y la salida  */
    @Test
    void planoConservaTerrenoYVisitadas() {
        Plano original = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        original.marcarVisitada(new Posicion(2, 2));

        Plano restaurado = idaYVuelta(original);

        assertEquals(Terreno.AGUA, restaurado.terrenoEn(new Posicion(2, 3)));
        assertTrue(restaurado.estaVisitada(new Posicion(2, 2)));
        assertFalse(restaurado.estaVisitada(new Posicion(3, 2)));
        assertEquals(original.getEntrada(), restaurado.getEntrada());
        assertEquals(original.getSalida(), restaurado.getSalida());
    }

    /** Un fantasma dentro de una pared se puede guardar y cargar */
    @Test
    void fantasmaEnUnaParedSobrevive() {
        Plano original = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        Fantasma fantasma = new Fantasma(new Posicion(2, 3), new Random(1));
        original.agregarMonstruo(fantasma);
        fantasma.restaurarPosicion(new Posicion(1, 3));   // la celda (1,3) es pared

        Plano restaurado = idaYVuelta(original);

        Monstruo monstruo = restaurado.getMonstruos().obtener(0);
        assertEquals(new Posicion(1, 3), monstruo.getPosicion());
        assertEquals(new Posicion(2, 3), monstruo.getPosicionInicial());
    }

    /** Un cofre conserva su tipo, su posicion y su daño */
    @Test
    void trampaConservaSuDanio() {
        Plano original = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        original.agregarCofre(new TrampaDeEnergia(new Posicion(3, 2), 25));

        Plano restaurado = idaYVuelta(original);

        assertEquals(1, restaurado.getCofres().cantidad());
        Cofre cofre = restaurado.getCofres().obtener(0);
        assertEquals("TrampaDeEnergia", cofre.getTipo());
        assertEquals(new Posicion(3, 2), cofre.getPosicion());
        assertEquals(25, cofre.getDanio());
    }

    @Test
    void cofresZapatosYDagaConservanTipoYPosicion() {
        Plano original = new Plano(
                terrenoDePrueba(), new Posicion(2, 2), null);
        original.agregarCofre(new CofreDeDaga(new Posicion(3, 2)));
        original.agregarCofre(new CofreZapatosDeAgua(new Posicion(3, 3)));

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(CofreDeDaga.class, restaurado.cofreEn(new Posicion(3, 2)));
        assertInstanceOf(CofreZapatosDeAgua.class,
                restaurado.cofreEn(new Posicion(3, 3)));
    }
    @Test
    void cofreDeEscudoConservaTipoYPosicion() {
        Plano original = new Plano(
                terrenoDePrueba(), new Posicion(2, 2), null);
        original.agregarCofre(new CofreDeEscudo(new Posicion(3, 2)));

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(
                CofreDeEscudo.class, restaurado.cofreEn(new Posicion(3, 2)));
    }
    @Test
    void cofreDeBrujulaConservaTipoYPosicion() {
        Plano original = new Plano(
                terrenoDePrueba(), new Posicion(2, 2), null);
        original.agregarCofre(new CofreDeBrujula(new Posicion(3, 2)));

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(
                CofreDeBrujula.class, restaurado.cofreEn(new Posicion(3, 2)));
    }

    @Test
    void conservaCofreYFinDeSueno() {
        Plano original = new Plano(
                terrenoDePrueba(), new Posicion(2, 2), null);
        original.agregarCofre(new CofreDeSueno(new Posicion(3, 2)));
        original.getEfectos().dormirMonstruos(10, 7);

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(CofreDeSueno.class,
                restaurado.cofreEn(new Posicion(3, 2)));
        assertTrue(restaurado.getEfectos().monstruosDormidos());
        assertEquals(Integer.valueOf(18), restaurado.getEfectos().getFinSuenoMonstruos());
    }
    @Test
    void conservaPuenteYTerrenosAlGuardarYCargar() {
        Posicion inicio = new Posicion(1, 1);
        Posicion agua = new Posicion(1, 2);
        Posicion lavaCubierta = new Posicion(1, 3);
        Posicion lavaNueva = new Posicion(1, 4);

        Plano original = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.AGUA, Terreno.LAVA, Terreno.PISO}
        }, inicio, null);

        original.agregarCofre(new CofreDePuente(inicio));
        original.getEfectos().activarPuente(10, 15);
        original.cambiarTerreno(lavaNueva, Terreno.LAVA);

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(CofreDePuente.class, restaurado.cofreEn(inicio));
        assertEquals(Integer.valueOf(26), restaurado.getEfectos().getFinPuente());
        assertEquals(Terreno.PISO, restaurado.terrenoEn(agua));
        assertEquals(Terreno.PISO, restaurado.terrenoEn(lavaCubierta));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(lavaNueva));

        restaurado.getEfectos().finalizarPuenteSiVencio(26);
        assertEquals(Terreno.AGUA, restaurado.terrenoEn(agua));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(lavaCubierta));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(lavaNueva));
    }


    @Test
    void conservaLavaActivaYVencimientos() {
        Posicion inicio = new Posicion(1, 1);
        Posicion aguaConLava = new Posicion(1, 2);
        Posicion pisoConLava = new Posicion(1, 3);
        Posicion aguaConPuente = new Posicion(1, 4);

        Plano original = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.AGUA, Terreno.PISO, Terreno.AGUA}
        }, inicio, null);

        original.agregarCofre(new TrampaDeLava(inicio));
        original.getEfectos().activarPuente(0, 15);
        original.getEfectos().activarLava(aguaConLava, 1, 10);
        original.getEfectos().activarLava(pisoConLava, 3, 10);

        Plano restaurado = idaYVuelta(original);

        assertInstanceOf(TrampaDeLava.class, restaurado.cofreEn(inicio));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(aguaConLava));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(pisoConLava));
        assertEquals(Terreno.PISO, restaurado.terrenoEn(aguaConPuente));

        restaurado.getEfectos().finalizarLavaSiVencio(12);
        assertEquals(Terreno.AGUA, restaurado.terrenoEn(aguaConLava));
        assertEquals(Terreno.LAVA, restaurado.terrenoEn(pisoConLava));

        restaurado.getEfectos().finalizarLavaSiVencio(14);
        assertEquals(Terreno.PISO, restaurado.terrenoEn(pisoConLava));

        restaurado.getEfectos().finalizarPuenteSiVencio(16);
        assertEquals(Terreno.AGUA, restaurado.terrenoEn(aguaConPuente));
    }

    @Test
    void rechazaLavaYpuenteEnLaMismaCelda() {
        Posicion inicio = new Posicion(1, 1);
        Posicion agua = new Posicion(1, 2);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO, Terreno.AGUA}}, inicio, null);
        plano.getEfectos().activarPuente(0, 15);

        PlanoGuardado guardado = ConversorPlano.aGuardado(plano);
        PlanoGuardado invalido = new PlanoGuardado(
                guardado.terreno(), guardado.visitadas(),
                guardado.entrada(), guardado.salida(),
                guardado.monstruos(), guardado.cofres(),
                guardado.finSuenoMonstruos(), guardado.finPuente(),
                guardado.cubiertasPorPuente(),
                new LavaGuardada[] {new LavaGuardada(agua, 11)});

        assertThrows(IllegalArgumentException.class,
                () -> ConversorPlano.aPlano(invalido));
    }

}