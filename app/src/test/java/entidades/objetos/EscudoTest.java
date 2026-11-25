package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import entidades.cofres.CofreDeEscudo;
import entidades.cofres.TrampaDeEnergia;
import entidades.monstruos.Errante;
import entidades.monstruos.Fantasma;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.TipoEvento;
import modelo.Ubicacion;

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
        assertFalse(heroe.tieneEfecto(Escudo.ID_EFECTO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void otroEscudoSumaDiezPasosYElEventoAnteriorNoLoCancela() {
        Escudo escudo = new Escudo();

        avanzar(10);
        assertTrue(escudo.usar(heroe, plano, agenda).seConsume());
        assertEquals(Integer.valueOf(21), heroe.vencimientoDe(Escudo.ID_EFECTO));

        avanzar(2);
        escudo.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(31), heroe.vencimientoDe(Escudo.ID_EFECTO));

        avanzar(9); // paso 21: vence el primer evento
        assertEquals(TipoEvento.FIN_ESCUDO, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(Escudo.ID_EFECTO, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(Escudo.ID_EFECTO));

        avanzar(10); // paso 31: vence el segundo
        assertEquals(TipoEvento.FIN_ESCUDO, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(Escudo.ID_EFECTO, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(Escudo.ID_EFECTO));
    }

    @Test
    void protegeDelDanioDeMonstruosHastaQueVence() {
        Errante errante = new Errante(posicion, new Random(1));
        Fantasma fantasma = new Fantasma(posicion, new Random(1));

        new Escudo().usar(heroe, plano, agenda);

        errante.alcanzarAlHeroe(heroe, agenda);
        fantasma.alcanzarAlHeroe(heroe, agenda);
        assertEquals(100, heroe.getEnergia());

        avanzar(11);
        assertEquals(TipoEvento.FIN_ESCUDO, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(Escudo.ID_EFECTO, agenda.getPasoActual());

        errante.alcanzarAlHeroe(heroe, agenda);
        assertEquals(70, heroe.getEnergia());
        fantasma.alcanzarAlHeroe(heroe, agenda);
        assertEquals(40, heroe.getEnergia());
    }

    @Test
    void noProtegeDeUnaTrampa() {
        new Escudo().usar(heroe, plano, agenda);
        TrampaDeEnergia trampa = new TrampaDeEnergia(posicion, 25);
        plano.agregarCofre(trampa);

        trampa.abrir(heroe, plano, agenda);

        assertEquals(75, heroe.getEnergia());
        assertTrue(heroe.tieneEfecto(Escudo.ID_EFECTO));
    }

    private void avanzar(int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}
