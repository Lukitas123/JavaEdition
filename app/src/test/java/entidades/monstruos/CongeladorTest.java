package entidades.monstruos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.Test;

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
        assertEquals(Integer.valueOf(14), heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO));
        assertEquals(1, agenda.cantidadDeEventos());

        avanzar(agenda, 2); // paso 12
        congelador.actuar(plano, heroe, agenda);

        assertEquals(Integer.valueOf(17), heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO));
        assertEquals(2, agenda.cantidadDeEventos());

        avanzar(agenda, 2); // paso 14
        assertEquals(new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO), agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(
                EfectoHeroe.INMOVILIZADO, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(EfectoHeroe.INMOVILIZADO));

        avanzar(agenda, 3); // paso 17
        assertEquals(new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO), agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(
                EfectoHeroe.INMOVILIZADO, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(EfectoHeroe.INMOVILIZADO));
    }

    private void avanzar(Agenda agenda, int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}