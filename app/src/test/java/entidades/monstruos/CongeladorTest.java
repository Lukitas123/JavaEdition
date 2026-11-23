package entidades.monstruos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.TipoEvento;
import modelo.Ubicacion;

class CongeladorTest {

    @Test
    void inmovilizaYAcumulaLaDuracionSinQuitarEnergia() {
        Terreno pared = Terreno.PARED;
        Terreno piso = Terreno.PISO;

        Plano plano = new Plano(new Terreno[][] {
                {pared, pared, pared, pared},
                {pared, piso,  piso,  pared},
                {pared, pared, pared, pared}
        }, new Posicion(2, 2), null);

        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(2, 3)));
        Congelador congelador = new Congelador(
                new Posicion(2, 2), new Random(1), 3);
        Agenda agenda = new Agenda();

        avanzar(agenda, 10);
        congelador.actuar(plano, heroe, agenda);

        assertEquals(100, heroe.getEnergia());
        assertEquals(new Posicion(2, 2), congelador.getPosicion());
        assertEquals(Integer.valueOf(14), heroe.vencimientoDe(Congelador.INMOVILIZADO));
        assertEquals(1, agenda.cantidadDeEventos());

        avanzar(agenda, 2); // paso 12
        congelador.actuar(plano, heroe, agenda);

        assertEquals(Integer.valueOf(17), heroe.vencimientoDe(Congelador.INMOVILIZADO));
        assertEquals(2, agenda.cantidadDeEventos());

        avanzar(agenda, 2); // paso 14
        assertEquals(TipoEvento.FIN_INMOVILIZACION, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(
                Congelador.INMOVILIZADO, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(Congelador.INMOVILIZADO));

        avanzar(agenda, 3); // paso 17
        assertEquals(TipoEvento.FIN_INMOVILIZACION, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(
                Congelador.INMOVILIZADO, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(Congelador.INMOVILIZADO));
    }

    private void avanzar(Agenda agenda, int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}