package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import entidades.monstruos.Cazador;
import entidades.monstruos.Dormilon;
import entidades.monstruos.Monstruo;
import modelo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import entidades.Catalogo;
import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Monstruo;

public class PersistenciaPartidaTest {

    private static Catalogo<Cofre> pruebaCofre(){
        return CatalogosDePrueba.cofres();
    }

    private static Catalogo<Monstruo> pruebaMonstruos(){
        return CatalogosDePrueba.monstruos();
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
    void partidaGuardadaYCargadaConservaElEstado(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();

        Mundo mundo = new Mundo(1, 1);
        Plano plano = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        plano.agregarCofre(new CofreDePuntos(new Posicion(3, 2)));
        mundo.agregarPlano(1, 1, plano);

        Heroe heroe = new Heroe(100, mundo.getInicio());
        heroe.perderEnergia(15);
        heroe.agregarObjeto("pocion");
        heroe.activarEfecto("zapatos", 6);

        Agenda agenda = new Agenda();
        agenda.avanzarPaso();
        agenda.programar(5, TipoEvento.FIN_ZAPATOS_DE_AGUA, null);
        MazmorraGuardada original = ConversorMazmorra.aGuardada(
                Dificultad.FACIL, EstadoPartida.EN_CURSO, mundo, heroe, agenda, new Costo(1,3,8));
        PersistenciaMazmorra.guardar(original, ruta);

        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                PersistenciaMazmorra.cargar(ruta), pruebaMonstruos(), pruebaCofre());

        assertEquals(Dificultad.FACIL, restaurada.dificultad());
        assertEquals(EstadoPartida.EN_CURSO, restaurada.estado());
        assertEquals(85, restaurada.heroe().getEnergia());
        assertEquals(heroe.getUbicacion(), restaurada.heroe().getUbicacion());
        assertEquals(1, restaurada.heroe().cantidadDe("pocion"));
        assertTrue(restaurada.heroe().tieneEfecto("zapatos"));
        assertEquals(1, restaurada.agenda().getPasoActual());
        assertEquals(1, restaurada.agenda().cantidadDeEventos());
        Plano planoActual = restaurada.mundo().planoEn(restaurada.heroe().getUbicacion());
        assertEquals(1, planoActual.getCofres().cantidad());
        assertEquals(Integer.valueOf(6), restaurada.heroe().vencimientoDe("zapatos"));
    }

    /** Cargar sin archivo tira error */
    @Test
    void cargarSinArchivoRechaza() {
        assertThrows(IllegalArgumentException.class, () -> PersistenciaMazmorra.cargar("no_existe.json"));
    }
    @Test
    void cazadorConservaLosCostosAlGuardarYCargar(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();
        Costo costos = new Costo(1, 3, 8);
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

        MazmorraGuardada guardada = ConversorMazmorra.aGuardada(
                Dificultad.FACIL, EstadoPartida.EN_CURSO,
                mundo, heroe, new Agenda(), costos);
        PersistenciaMazmorra.guardar(guardada, ruta);

        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                PersistenciaMazmorra.cargar(ruta));
        Plano planoRestaurado = restaurada.mundo().planoEn(1, 1);
        Monstruo monstruo = planoRestaurado.getMonstruos().obtener(0);

        assertEquals(costos, restaurada.costos());
        assertInstanceOf(Cazador.class, monstruo);
        assertEquals(new Posicion(2, 1),
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

        MazmorraGuardada guardada = ConversorMazmorra.aGuardada(
                Dificultad.FACIL, EstadoPartida.EN_CURSO,
                mundo, heroe, agenda, costos);
        PersistenciaMazmorra.guardar(guardada, ruta);

        Mazmorra restaurada = ConversorMazmorra.aMazmorra(
                PersistenciaMazmorra.cargar(ruta));
        Plano planoRestaurado = restaurada.mundo().planoEn(1, 1);
        Dormilon restaurado = assertInstanceOf(
                Dormilon.class, planoRestaurado.getMonstruos().obtener(0));

        assertFalse(restaurado.estaDespierto());
        assertEquals(1, restaurada.agenda().cantidadDeEventos());

        restaurada.agenda().avanzarPaso();
        EventoProgramado evento = restaurada.agenda().sacarProximo();
        assertEquals(TipoEvento.DESPERTAR_DORMILON, evento.tipo());
        assertEquals("1,1", evento.dato());

        restaurado.despertarSiCorresponde(restaurada.agenda().getPasoActual());
        restaurado.actuar(planoRestaurado, restaurada.heroe(), restaurada.agenda());
        assertTrue(restaurado.estaDespierto());
        assertEquals(new Posicion(1, 2), restaurado.getPosicion());
    }
}
