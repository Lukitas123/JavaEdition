package Principal;

import com.google.gson.Gson;
import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Errante;
import entidades.monstruos.Monstruo;
import generador.GeneradorDeLaberinto;
import generador.GeneradorJavaEdition;
import modelo.Agenda;
import modelo.Configuracion;
import modelo.Direccion;
import modelo.EstadoPartida;
import modelo.EventoProgramado;
import modelo.Heroe;
import modelo.ParametrosDeDificultad;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import persistencia.LectorConfiguracion;

import java.util.Random;
import java.util.Scanner;

/**
 * MAIN PROVISORIO V1(precario)
 * Limitaciones conocidas (se resuelven cuando lleguen las otras piezas):
 *  - Falta Mazmorra: este Main hace de coordinador y juega UN solo plano
 *    (tablero 1, piso 1). Se ignoran tableros y pisos de la configuracion.
 *  - Errante.decidirMovimiento devuelve null, asi que los monstruos no se mueven.
 *  - Sin vista BMP
 *  No se incluyo modelo/mundo
 *
 */
public class MainV1 {

    private static final String RUTA_POR_DEFECTO = "Configuracion.json";

    // Lo que devuelve GeneradorJavaEdition (mismo formato que el JSON del generador).
    private record PlanoGenerado(int version, String generador, Object parametros,
                                 Posicion entrada, Posicion salida, String[] plano) {
    }

    public static void main(String[] args) {
        String ruta = args.length > 0 ? args[0] : buscarConfiguracion();
        Scanner entrada = new Scanner(System.in);

        Configuracion config;
        try {
            config = LectorConfiguracion.cargarConfiguracion(ruta);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Busque '" + ruta + "' desde la carpeta: "
                    + java.nio.file.Path.of("").toAbsolutePath());
            System.out.println("Solucion: copia Configuracion.json a esa carpeta, o pasa la ruta completa "
                    + "en Run > Edit Configurations > Program arguments.");
            return;
        }

        String dificultad = elegirDificultad(entrada);
        if (dificultad == null) {
            return;
        }
        ParametrosDeDificultad p = parametrosDe(config, dificultad);

        // Genera el plano
        String parametrosJson = new Gson().toJson(new ParametrosGenerador(
                1, dificultad, p.filas(), p.columnas(), config.semilla(),
                p.cofres(), p.monstruos(), true));
        GeneradorDeLaberinto generador = new GeneradorJavaEdition();
        PlanoGenerado generado = new Gson().fromJson(generador.generar(parametrosJson), PlanoGenerado.class);

        // Convierte el JSON en un Plano del modelo
        Random random = (config.semilla() != null) ? new Random(config.semilla()) : new Random();
        Plano plano = construirPlano(generado, random);

        // Heroe y agenda
        Heroe heroe = new Heroe(config.energiaInicial(), new Ubicacion(1, 1, plano.getEntrada()));
        Agenda agenda = new Agenda();
        plano.marcarVisitada(plano.getEntrada());

        // juego
        EstadoPartida estado = EstadoPartida.EN_CURSO;
        int pasos = 0;
        System.out.println("Moves: w/a/s/d + Enter, q para salir.");
        while (estado == EstadoPartida.EN_CURSO) {
            dibujar(plano, heroe);
            System.out.println("Energia: " + heroe.getEnergia()
                    + " | Cofres abiertos: " + heroe.getCofresAbiertos()
                    + " | Pasos: " + pasos);
            System.out.print("> ");
            if (!entrada.hasNextLine()) {
                break;
            }
            String comando = entrada.nextLine().trim().toLowerCase();
            if (comando.equals("q")) {
                System.out.println("Saliste de la partida.");
                return;
            }
            Direccion direccion = direccionDe(comando);
            if (direccion == null) {
                System.out.println("Comando invalido.");
                continue;
            }

            if (!moverHeroe(plano, heroe, direccion, config)) {
                System.out.println("No podes ir para ahi.");
                continue;
            }
            pasos++;

            // eventos programados
            agenda.avanzarPaso();
            while (agenda.hayEventoVencido()) {
                EventoProgramado evento = agenda.sacarProximo();
                System.out.println("Evento: " + evento.tipo());
            }

            // abrir cofre si hay uno en la celda
            Posicion pos = heroe.getUbicacion().posicion();
            Cofre cofre = plano.cofreEn(pos);
            if (cofre != null && !cofre.estaAbierto()) {
                cofre.abrir(heroe, plano, agenda);
                System.out.println("Abriste un cofre!");
            }

            // turno de los monstruos
            for (int i = 0; i < plano.getMonstruos().cantidad(); i++) {
                int antes = heroe.getEnergia();
                plano.getMonstruos().obtener(i).actuar(plano, heroe, agenda);
                if (heroe.getEnergia() < antes) {
                    System.out.println("Un monstruo te alcanzo! -" + (antes - heroe.getEnergia()) + " de energia.");
                }
            }

            estado = evaluarEstado(plano, heroe);
        }

        dibujar(plano, heroe);
        if (estado == EstadoPartida.GANADA) {
            System.out.println("GANASTE! Cofres abiertos: " + heroe.getCofresAbiertos()
                    + " | Energia restante: " + heroe.getEnergia() + " | Pasos: " + pasos);
        } else if (estado == EstadoPartida.PERDIDA) {
            System.out.println("PERDISTE: te quedaste sin energia.");
        }
    }

    /**
     * Busca Configuracion.json en la carpeta actual y en app.
     */
    private static String buscarConfiguracion() {
        String[] candidatas = {RUTA_POR_DEFECTO, "app/" + RUTA_POR_DEFECTO,
                "../" + RUTA_POR_DEFECTO, "../app/" + RUTA_POR_DEFECTO};
        for (String candidata : candidatas) {
            if (java.nio.file.Files.exists(java.nio.file.Path.of(candidata))) {
                return candidata;
            }
        }
        return RUTA_POR_DEFECTO;
    }



    private record ParametrosGenerador(int version, String dificultad, int filas, int columnas,
                                       Long semilla, int cofres, int monstruos, boolean conSalida) {
    }

    private static Plano construirPlano(PlanoGenerado g, Random random) {
        int filas = g.plano().length;
        int columnas = g.plano()[0].length();
        Terreno[][] terreno = new Terreno[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                terreno[f][c] = Terreno.desdeSimbolo(g.plano()[f].charAt(c));
            }
        }
        Plano plano = new Plano(terreno, g.entrada(), g.salida());
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Posicion pos = new Posicion(f + 1, c + 1);
                char simbolo = g.plano()[f].charAt(c);
                if (simbolo == 'M') {
                    plano.agregarMonstruo(new Errante(pos, random));
                } else if (simbolo == 'C') {
                    plano.agregarCofre(new CofreDePuntos(pos));
                }
            }
        }
        return plano;
    }

    private static ParametrosDeDificultad parametrosDe(Configuracion config, String dificultad) {
        return switch (dificultad) {
            case "FACIL" -> config.dificultades().FACIL();
            case "MEDIO" -> config.dificultades().MEDIO();
            default -> config.dificultades().DIFICIL();
        };
    }

    // ---------- reglas del turno ----------

    /**
     * Mueve al heroe si la celda destino es transitable y cobra el costo del terreno.
     */
    private static boolean moverHeroe(Plano plano, Heroe heroe, Direccion direccion, Configuracion config) {
        Ubicacion actual = heroe.getUbicacion();
        Posicion destino = actual.posicion().mover(direccion);
        if (!plano.esTransitable(destino)) {
            return false;
        }
        heroe.irA(new Ubicacion(actual.tablero(), actual.piso(), destino));
        plano.marcarVisitada(destino);
        heroe.perderEnergia(costoDe(plano.terrenoEn(destino), config));
        return true;
    }

    private static int costoDe(Terreno terreno, Configuracion config) {
        return switch (terreno) {
            case AGUA -> config.costos().agua();
            case LAVA -> config.costos().lava();
            default -> config.costos().piso();
        };
    }

    private static EstadoPartida evaluarEstado(Plano plano, Heroe heroe) {
        if (plano.tieneSalida() && heroe.getUbicacion().posicion().equals(plano.getSalida())) {
            return EstadoPartida.GANADA;
        }
        if (heroe.estaSinEnergia()) {
            return EstadoPartida.PERDIDA;
        }
        return EstadoPartida.EN_CURSO;
    }

    // Se ejecuta por consola;

    private static String elegirDificultad(Scanner entrada) {
        while (true) {
            System.out.print("Dificultad (1=FACIL, 2=MEDIO, 3=DIFICIL, q=salir): ");
            if (!entrada.hasNextLine()) {
                return null;
            }
            switch (entrada.nextLine().trim().toLowerCase()) {
                case "1":
                    return "FACIL";
                case "2":
                    return "MEDIO";
                case "3":
                    return "DIFICIL";
                case "q":
                    return null;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static Direccion direccionDe(String comando) {
        return switch (comando) {
            case "w" -> Direccion.ARRIBA;
            case "s" -> Direccion.ABAJO;
            case "a" -> Direccion.IZQUIERDA;
            case "d" -> Direccion.DERECHA;
            default -> null;
        };
    }

    /**
     * H = heroe
     * M = monstruo
     * C = cofre cerrado
     * c = cofre abierto
     * S = salida
     * E = entrada
     */
    private static void dibujar(Plano plano, Heroe heroe) {
        Posicion posHeroe = heroe.getUbicacion().posicion();
        StringBuilder sb = new StringBuilder();
        for (int f = 1; f <= plano.getFilas(); f++) {
            for (int c = 1; c <= plano.getColumnas(); c++) {
                Posicion pos = new Posicion(f, c);
                char simbolo = plano.terrenoEn(pos).getSimbolo();
                Monstruo monstruo = plano.monstruoEn(pos);
                Cofre cofre = plano.cofreEn(pos);
                if (pos.equals(posHeroe)) {
                    simbolo = 'H';
                } else if (monstruo != null) {
                    simbolo = 'M';
                } else if (cofre != null) {
                    simbolo = cofre.estaAbierto() ? 'c' : 'C';
                } else if (pos.equals(plano.getSalida())) {
                    simbolo = 'S';
                } else if (pos.equals(plano.getEntrada())) {
                    simbolo = 'E';
                }
                sb.append(simbolo);
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }
}