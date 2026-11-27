package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.Test;

import entidades.cofres.CofreDeEscudo;
import entidades.cofres.TrampaDeEnergia;
import entidades.monstruos.Errante;
import entidades.monstruos.Fantasma;

class EscudoTest {

    private final Posicion posicion = new Posicion(1, 1);
    private final Plano plano = new Plano(
            new Terreno[][] {{Terreno.PISO}}, posicion, null);
    private final Heroe heroe = new Heroe(
            100, new Ubicacion(1, 1, posicion));
    private final Agenda agenda = new Agenda();

    @Test
    void elCofreEntregaElEscudoSinActivarlo() {
        CofreDeEscudo cofre = new CofreDeEscudo(posicion);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(Escudo.ID_OBJETO));
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ESCUDO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void otroEscudoSumaDiezPasosYElEventoAnteriorNoLoCancela() {
        Escudo escudo = new Escudo(10);

        avanzar(10);
        assertTrue(escudo.usar(heroe, plano, agenda).seConsume());
        assertEquals(Integer.valueOf(21), heroe.vencimientoDe(EfectoHeroe.ESCUDO));

        avanzar(2);
        escudo.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(31), heroe.vencimientoDe(EfectoHeroe.ESCUDO));

        avanzar(9); // paso 21: vence el primer evento
        assertEquals(new FinEfectoHeroe(EfectoHeroe.ESCUDO),agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(EfectoHeroe.ESCUDO, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(EfectoHeroe.ESCUDO));

        avanzar(10); // paso 31: vence el segundo
        assertEquals(new FinEfectoHeroe(EfectoHeroe.ESCUDO),agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(EfectoHeroe.ESCUDO, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ESCUDO));
    }

    @Test
    void protegeDelDanioDeMonstruosHastaQueVence() {
        Errante errante = new Errante(posicion, new Random(1),30);
        Fantasma fantasma = new Fantasma(posicion, new Random(1),30);

        new Escudo(10).usar(heroe, plano, agenda);

        errante.alcanzarAlHeroe(heroe, agenda);
        fantasma.alcanzarAlHeroe(heroe, agenda);
        assertEquals(100, heroe.getEnergia());

        avanzar(11);
        assertEquals(new FinEfectoHeroe(EfectoHeroe.ESCUDO),agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(EfectoHeroe.ESCUDO, agenda.getPasoActual());

        errante.alcanzarAlHeroe(heroe, agenda);
        assertEquals(70, heroe.getEnergia());
        fantasma.alcanzarAlHeroe(heroe, agenda);
        assertEquals(40, heroe.getEnergia());
    }

    @Test
    void noProtegeDeUnaTrampa() {
        new Escudo(10).usar(heroe, plano, agenda);
        TrampaDeEnergia trampa = new TrampaDeEnergia(posicion, 25);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, agenda);

        assertEquals(75, heroe.getEnergia());
        assertTrue(heroe.tieneEfecto(EfectoHeroe.ESCUDO));
    }

    private void avanzar(int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}
