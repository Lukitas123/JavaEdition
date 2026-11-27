package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import entidades.monstruos.Cazador;
import entidades.monstruos.Dormilon;
import entidades.monstruos.Monstruo;
import modelo.*;
import modelo.eventos.DespertarDormilones;
import modelo.eventos.FinEfectoHeroe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;

public class PersistenciaPartidaTest {

    private static Catalogo<Cofre> pruebaCofre(){
        return CatalogosDePrueba.cofres();
    }

    private static Terreno[][] terrenoDePrueba() {
        Terreno p = Terreno.PARED;
        Terreno o = Terreno.PISO;
        return new Terreno[][] {
            {p, p, p, p},
            {p, o, o, p},
            {p, o, o, p},
            {p, p, p, p}
        };
    }

/** Una partida guardada en un archivo y cargada de nuevo conserva todo su estado */
    @Test
    void mazmorraGuardadaYCargadaConservaElEstado(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();

        Mundo mundo = new Mundo(1, 1);
        Plano plano = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        plano.agregarCofre(new CofreDePuntos(new Posicion(3, 2)));
        mundo.agregarPlano(1, 1, plano);

        Heroe heroe = new Heroe(100, mundo.getInicio());
        heroe.perderEnergia(15);
        heroe.agregarObjeto("pocion");

        Agenda agenda = new Agenda();
        agenda.avanzarPaso();

        Mazmorra original = new Mazmorra(Dificultad.FACIL, mundo, heroe, agenda,
                new Costo(1, 3, 8), EstadoPartida.GANADA);

        PersistenciaMazmorra.guardar(ConversorMazmorra.aGuardada(original), ruta);
        Mazmorra restaurada = ConversorMazmorra.aMazmorra(PersistenciaMazmorra.cargar(ruta),
                CatalogosDePrueba.monstruos(), CatalogosDePrueba.cofres());

        assertEquals(Dificultad.FACIL, restaurada.getDificultad());
        assertEquals(EstadoPartida.GANADA, restaurada.getEstado());
        assertEquals(3, restaurada.getCosto().agua());
        assertEquals(85, restaurada.getHeroe().getEnergia());
        assertEquals(1, restaurada.getHeroe().cantidadDe("pocion"));
        assertEquals(1, restaurada.getAgenda().getPasoActual());
        assertEquals(1, restaurada.planoActual().getCofres().cantidad());
}

    /** Cargar sin archivo tira error */
    @Test
    void cargarSinArchivoRechaza() {
        assertThrows(IllegalArgumentException.class, () -> PersistenciaMazmorra.cargar("no_existe.json"));
    }
    @Test
    void cazadorConservaLosCostosAlGuardarYCargar(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();
        Costo costos = new Costo(1, 3, 2);
        Posicion inicio = new Posicion(1, 1);
        Posicion destino = new Posicion(1, 3);

        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.LAVA, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        plano.agregarMonstruo(new Cazador(inicio, costos));

        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, destino));

        MazmorraGuardada guardada = ConversorMazmorra.aGuardada(new Mazmorra(Dificultad.FACIL, mundo,
                heroe, new Agenda(), costos, EstadoPartida.EN_CURSO));
        PersistenciaMazmorra.guardar(guardada, ruta);
        MazmorraGuardada leida = PersistenciaMazmorra.cargar(ruta);
        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                leida, CatalogosDePrueba.monstruos(leida.costos()), pruebaCofre());

        Plano planoRestaurado = restaurada.getMundo().planoEn(1, 1);
        Monstruo monstruo = planoRestaurado.getMonstruos().obtener(0);

        assertEquals(costos, restaurada.getCosto());
        assertInstanceOf(Cazador.class, monstruo);
        assertEquals(new Posicion(1, 2),
                monstruo.decidirMovimiento(planoRestaurado, destino));
    }

    @Test
    void dormilonPendienteSeGuardaConSuEvento(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();
        Costo costos = new Costo(1, 3, 8);
        Posicion inicio = new Posicion(1, 1);

        Plano plano = new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, inicio, null);
        Dormilon dormilon = new Dormilon(inicio, costos);
        plano.agregarMonstruo(dormilon);

        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(1, 3)));
        Agenda agenda = new Agenda();

        dormilon.actuar(plano, heroe, agenda);


        MazmorraGuardada guardada = ConversorMazmorra.aGuardada( new Mazmorra
                (Dificultad.FACIL, mundo, heroe, agenda, costos, EstadoPartida.EN_CURSO));
        PersistenciaMazmorra.guardar(guardada, ruta);
        MazmorraGuardada leida = PersistenciaMazmorra.cargar(ruta);
        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                leida, CatalogosDePrueba.monstruos(leida.costos()), pruebaCofre());
        Plano planoRestaurado = restaurada.getMundo().planoEn(1, 1);
        Dormilon restaurado = assertInstanceOf(
                Dormilon.class, planoRestaurado.getMonstruos().obtener(0));

        assertFalse(restaurado.estaDespierto());
        assertEquals(1, restaurada.getAgenda().cantidadDeEventos());

        restaurada.getAgenda().avanzarPaso();
        EventoProgramado evento = restaurada.getAgenda().sacarProximo();
        assertEquals(new DespertarDormilones(1, 1), evento.evento());

        restaurado.despertarSiCorresponde(restaurada.getAgenda().getPasoActual());
        restaurado.actuar(planoRestaurado, restaurada.getHeroe(), restaurada.getAgenda());
        assertTrue(restaurado.estaDespierto());
        assertEquals(new Posicion(1, 2), restaurado.getPosicion());

    }
}
