package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import entidades.cofres.CofreDeDaga;
import entidades.monstruos.Errante;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

class DagaTest {

    private final Posicion posicionHeroe = new Posicion(2, 2);
    private final Heroe heroe = new Heroe(
            100, new Ubicacion(1, 1, posicionHeroe));
    private final Plano plano = new Plano(new Terreno[][] {
            {Terreno.PISO, Terreno.PISO, Terreno.PISO},
            {Terreno.PISO, Terreno.PISO, Terreno.PISO},
            {Terreno.PISO, Terreno.PISO, Terreno.PISO}
    }, posicionHeroe, null);
    private final Agenda agenda = new Agenda();

    @Test
    void elCofreEntregaUnaDaga() {
        CofreDeDaga cofre = new CofreDeDaga(posicionHeroe);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(Daga.ID_OBJETO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicionHeroe));
    }

    @Test
    void eliminaUnMonstruoAdyacente() {
        Errante monstruo = new Errante(new Posicion(2, 3), new Random(1),30);
        plano.agregarMonstruo(monstruo);

        boolean usada = new Daga(new Random(1)).usar(heroe, plano, agenda).seConsume();

        assertTrue(usada);
        assertNull(plano.monstruoEn(new Posicion(2, 3)));
    }

    @Test
    void noAlcanzaUnMonstruoEnDiagonal() {
        Errante diagonal = new Errante(new Posicion(3, 3), new Random(1),30);
        plano.agregarMonstruo(diagonal);

        boolean usada = new Daga(new Random(1)).usar(heroe, plano, agenda).seConsume();

        assertFalse(usada);
        assertSame(diagonal, plano.monstruoEn(new Posicion(3, 3)));
    }

    @Test
    void eligeSoloUnoCuandoHayVariosAdyacentes() {
        Errante arriba = new Errante(new Posicion(1, 2), new Random(1),30);
        Errante derecha = new Errante(new Posicion(2, 3), new Random(2),30);
        plano.agregarMonstruo(arriba);
        plano.agregarMonstruo(derecha);

        boolean usada = new Daga(new Random(1)).usar(heroe, plano, agenda).seConsume();

        assertTrue(usada);
        assertEquals(1, plano.getMonstruos().cantidad());
        assertTrue(plano.monstruoEn(new Posicion(1, 2)) == arriba
                || plano.monstruoEn(new Posicion(2, 3)) == derecha);
    }
}