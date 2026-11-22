package entidades.monstruos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import modelo.Agenda;
import modelo.Costo;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

class CazadorTest {

    @Test
    void eligeElDesvioMasBaratoYSeAdaptaAlCambioDeTerreno() {
        Posicion inicio = new Posicion(1, 1);
        Posicion destino = new Posicion(1, 3);
        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.LAVA, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        Cazador cazador = new Cazador(inicio, new Costo(1, 3, 8));

        assertEquals(new Posicion(2, 1),
                cazador.decidirMovimiento(plano, destino));
        assertEquals(inicio, cazador.getPosicion());

        plano.cambiarTerreno(new Posicion(1, 2), Terreno.PISO);
        assertEquals(new Posicion(1, 2),
                cazador.decidirMovimiento(plano, destino));
    }

    @Test
    void sinCaminoSeQuedaEnSuPosicion() {
        Posicion inicio = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PARED, Terreno.PISO}
        }, inicio, null);
        Cazador cazador = new Cazador(inicio, new Costo(1, 3, 8));

        assertEquals(inicio,
                cazador.decidirMovimiento(plano, new Posicion(1, 3)));
    }

    @Test
    void alAlcanzarAlHeroeHaceDanioYVuelveAlInicio() {
        Posicion inicio = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        Heroe heroe = new Heroe(
                100, new Ubicacion(1, 1, new Posicion(1, 3)));
        Cazador cazador = new Cazador(inicio, new Costo(1, 3, 8));
        Agenda agenda = new Agenda();

        cazador.actuar(plano, heroe, agenda);
        assertEquals(new Posicion(1, 2), cazador.getPosicion());
        assertEquals(100, heroe.getEnergia());

        cazador.actuar(plano, heroe, agenda);
        assertEquals(70, heroe.getEnergia());
        assertEquals(inicio, cazador.getPosicion());
    }
}