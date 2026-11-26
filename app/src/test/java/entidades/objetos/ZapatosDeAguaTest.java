
package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.Test;

import entidades.cofres.CofreZapatosDeAgua;

class ZapatosDeAguaTest {

    @Test
    void elCofreEntregaLosZapatosSinActivarlos() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        Agenda agenda = new Agenda();
        CofreZapatosDeAgua cofre = new CofreZapatosDeAgua(posicion);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(ZapatosDeAgua.ID_OBJETO));
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void usarOtroParSumaVeintePasos() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        Agenda agenda = new Agenda();
        ZapatosDeAgua zapatos = new ZapatosDeAgua();

        avanzar(agenda, 10);
        zapatos.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(31), heroe.vencimientoDe(EfectoHeroe.ZAPATOS_DE_AGUA));

        avanzar(agenda, 2); // paso 12
        zapatos.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(51), heroe.vencimientoDe(EfectoHeroe.ZAPATOS_DE_AGUA));

        avanzar(agenda, 19); // paso 31
        assertEquals(new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA), agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(EfectoHeroe.ZAPATOS_DE_AGUA, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));

        avanzar(agenda, 20); // paso 51
        assertEquals(new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA), agenda.sacarProximo().evento());
        heroe.finalizarEfectoSiVencio(EfectoHeroe.ZAPATOS_DE_AGUA, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
    }

    private void avanzar(Agenda agenda, int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}