package entidades.monstruos;

import java.util.Random;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErranteTest {

    private Plano crearPasillo() {
        return new Plano(
                new Terreno[][] {
                        {Terreno.PISO, Terreno.PISO, Terreno.PISO}
                },
                new Posicion(1, 1),
                null);
    }

    private Heroe crearHeroe(int energia, Posicion posicion) {
        return new Heroe(energia, new Ubicacion(1, 1, posicion));
    }

    @Test
    void decidirMovimientoNoCambiaLaPosicion() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));

        Posicion destino = errante.decidirMovimiento(
                plano, new Posicion(1, 3));

        assertEquals(new Posicion(1, 2), destino);
        assertEquals(inicio, errante.getPosicion());
    }

    @Test
    void actuarActualizaLaPosicionSinDanarSiNoHayEncuentro() {
        Plano plano = crearPasillo();
        Errante errante = new Errante(new Posicion(1, 1), new Random(1));
        Heroe heroe = crearHeroe(100, new Posicion(1, 3));

        errante.actuar(plano, heroe, new Agenda());

        assertEquals(new Posicion(1, 2), errante.getPosicion());
        assertEquals(100, heroe.getEnergia());
    }

    @Test
    void alAlcanzarAlHeroePierdeEnergiaYElErranteVuelveAlInicio() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));
        Heroe heroe = crearHeroe(100, new Posicion(1, 2));
        Agenda agenda = new Agenda();

        errante.actuar(plano, heroe, agenda);

        assertEquals(70, heroe.getEnergia());
        assertEquals(inicio, errante.getPosicion());
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void resuelveElEncuentroAntesDeMoverseYSinEnergiaNegativa() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));
        Heroe heroe = crearHeroe(20, inicio);

        errante.actuar(plano, heroe, new Agenda());

        assertEquals(0, heroe.getEnergia());
        assertTrue(heroe.estaSinEnergia());
        assertEquals(inicio, errante.getPosicion());
    }

    @Test
    void inactivoNoSeMueveNiAtaca() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));
        Heroe heroe = crearHeroe(100, inicio);
        errante.desactivar();

        errante.actuar(plano, heroe, new Agenda());

        assertEquals(inicio, errante.getPosicion());
        assertEquals(100, heroe.getEnergia());
        assertFalse(errante.estaActivo());
    }

    @Test
    void sinVecinosSeQuedaEnSuPosicion() {
        Posicion inicio = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, inicio, null);
        Errante errante = new Errante(inicio, new Random(1));

        assertEquals(inicio, errante.decidirMovimiento(plano, inicio));
    }

    @Test
    void rechazaDatosInicialesNulos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Errante(null, new Random(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new Errante(new Posicion(1, 1), null));
    }

    @Test
    void rechazaArgumentosNulosSinModificarElEstado() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));
        Heroe heroe = crearHeroe(100, new Posicion(1, 3));
        Agenda agenda = new Agenda();

        assertThrows(IllegalArgumentException.class,
                () -> errante.actuar(null, heroe, agenda));
        assertThrows(IllegalArgumentException.class,
                () -> errante.actuar(plano, null, agenda));
        assertThrows(IllegalArgumentException.class,
                () -> errante.actuar(plano, heroe, null));

        assertThrows(IllegalArgumentException.class,
                () -> errante.decidirMovimiento(null, inicio));
        assertThrows(IllegalArgumentException.class,
                () -> errante.decidirMovimiento(plano, null));

        assertEquals(inicio, errante.getPosicion());
        assertEquals(100, heroe.getEnergia());
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void alReactivarseVuelveAMoverse() {
        Plano plano = crearPasillo();
        Posicion inicio = new Posicion(1, 1);
        Errante errante = new Errante(inicio, new Random(1));
        Heroe heroe = crearHeroe(100, new Posicion(1, 3));
        Agenda agenda = new Agenda();

        errante.desactivar();
        errante.actuar(plano, heroe, agenda);

        assertEquals(inicio, errante.getPosicion());

        errante.activar();
        errante.actuar(plano, heroe, agenda);

        assertTrue(errante.estaActivo());
        assertEquals(new Posicion(1, 2), errante.getPosicion());
        assertEquals(100, heroe.getEnergia());
    }

    @Test
    void eligeEntreVecinosValidosSinSalirDelPlanoNiPisarParedes() {
        Posicion inicio = new Posicion(1, 2);
        Plano plano = new Plano(
                new Terreno[][] {
                        {Terreno.PISO, Terreno.PISO, Terreno.AGUA},
                        {Terreno.PISO, Terreno.PARED, Terreno.PISO}
                },
                inicio,
                null);

        Errante errante = new Errante(inicio, new Random(1));

        for (int intento = 0; intento < 50; intento++) {
            Posicion destino = errante.decidirMovimiento(
                    plano, new Posicion(2, 1));

            assertTrue(
                    destino.equals(new Posicion(1, 1))
                            || destino.equals(new Posicion(1, 3)),
                    "El destino debe ser uno de los dos vecinos validos");

            assertEquals(inicio, errante.getPosicion());
        }
    }
}