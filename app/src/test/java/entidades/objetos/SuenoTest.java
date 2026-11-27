package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import modelo.eventos.FinSueno;
import org.junit.jupiter.api.Test;

import entidades.cofres.CofreDeSueno;
import entidades.monstruos.Errante;
import modelo.Agenda;
import modelo.EventoProgramado;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

class SuenoTest {
    private final Posicion posicion = new Posicion(1, 1);
    private final Plano plano = new Plano(
            new Terreno[][] {{Terreno.PISO}}, posicion, null);
    private final Heroe heroe = new Heroe(
            100, new Ubicacion(2, 3, posicion));
    private final Agenda agenda = new Agenda();

    @Test
    void elCofreEntregaElObjetoSinActivarlo() {
        CofreDeSueno cofre = new CofreDeSueno(posicion);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(Sueno.ID_OBJETO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
        assertFalse(plano.getEfectos().monstruosDormidos());
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void duermeMonstruosYAcumulaSinQueElEventoAnteriorLosDespierte() {
        Errante monstruo = new Errante(posicion, new Random(1));
        plano.agregarMonstruo(monstruo);
        Sueno sueno = new Sueno();

        avanzar(10);
        assertTrue(sueno.usar(heroe, plano, agenda).seConsume());
        assertEquals(Integer.valueOf(18), plano.getEfectos().getFinSuenoMonstruos());

        monstruo.actuar(plano, heroe, agenda);
        assertEquals(100, heroe.getEnergia());

        avanzar(2);
        sueno.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(25), plano.getEfectos().getFinSuenoMonstruos());

        avanzar(6); // paso 18
        EventoProgramado primero = agenda.sacarProximo();
        assertEquals(new FinSueno(2, 3), primero.evento());
        assertEquals(18, primero.pasoDeVencimiento());
        plano.getEfectos().despertarMonstruosSiVencio(agenda.getPasoActual());
        assertTrue(plano.getEfectos().monstruosDormidos());

        avanzar(7); // paso 25
        EventoProgramado segundo = agenda.sacarProximo();
        assertEquals(new FinSueno(2, 3), segundo.evento());
        plano.getEfectos().despertarMonstruosSiVencio(agenda.getPasoActual());
        assertFalse(plano.getEfectos().monstruosDormidos());

        monstruo.actuar(plano, heroe, agenda);
        assertEquals(70, heroe.getEnergia());
    }

    private void avanzar(int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}