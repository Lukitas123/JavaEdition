package entidades;

import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Errante;
import java.util.Random;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntidadesIntegracionTest {

    @Test
    void abrirCofreYRecibirAtaqueConservaElEstadoCoherente() {
        Posicion inicioHeroe = new Posicion(1, 1);
        Posicion posicionCofre = new Posicion(1, 2);
        Posicion inicioErrante = new Posicion(1, 3);

        Plano plano = new Plano(
                new Terreno[][] {
                        {Terreno.PISO, Terreno.PISO, Terreno.PISO}
                },
                inicioHeroe,
                null);

        Heroe heroe = new Heroe(
                100, new Ubicacion(1, 1, inicioHeroe));
        Agenda agenda = new Agenda();
        CofreDePuntos cofre = new CofreDePuntos(posicionCofre);
        Errante errante = new Errante(inicioErrante, new Random(1));

        plano.agregarCofre(cofre);
        plano.agregarMonstruo(errante);

        heroe.irA(new Ubicacion(1, 1, posicionCofre));
        plano.cofreEn(posicionCofre).abrir(heroe, plano, agenda);
        errante.actuar(plano, heroe, agenda);

        assertEquals(posicionCofre, heroe.getUbicacion().posicion());
        assertEquals(1, heroe.getCofresAbiertos());
        assertEquals(70, heroe.getEnergia());
        assertTrue(cofre.estaAbierto());
        assertNull(plano.cofreEn(posicionCofre));
        assertSame(errante, plano.monstruoEn(inicioErrante));
        assertEquals(0, agenda.cantidadDeEventos());
    }
}