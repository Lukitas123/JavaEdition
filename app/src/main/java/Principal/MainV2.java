package Principal;
import com.google.gson.Gson;

import entidades.cofres.Cofre;
import entidades.cofres.CofreDePuntos;
import entidades.monstruos.Errante;
import entidades.monstruos.Monstruo;
import estructuras.Lista;
import generador.GeneradorDeLaberinto;
import generador.GeneradorJavaEdition;
import modelo.Agenda;
import modelo.Conexion;
import modelo.Configuracion;
import modelo.ColocadorDeConexiones;
import modelo.Direccion;
import modelo.EntradaDeRanking;
import modelo.EstadoPartida;
import modelo.EventoProgramado;
import modelo.Heroe;
import modelo.Mundo;
import modelo.ParametrosDeDificultad;
import modelo.Plano;
import modelo.Posicion;
import modelo.Ranking;
import modelo.Terreno;
import modelo.Ubicacion;
import persistencia.LectorConfiguracion;
import persistencia.PersistenciaRanking;
import vista.Vista;
import vista.VistaBmp;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Random;
import java.util.Scanner;


public class MainV2 {

    private static final String CONFIG_POR_DEFECTO = "Configuracion.json";
    private static final String RANKING_POR_DEFECTO = "ranking.json";
    private static final int INTENTOS_DE_GENERACION = 8;

    private record ParametrosGenerador(int version, String dificultad, int filas, int columnas,
                                       Long semilla, int cofres, int monstruos, boolean conSalida) {
    }


    private record PlanoGenerado(Posicion entrada, Posicion salida, String[] plano) {
    }

    public static void main(String[] args) {
        boolean guardarBmp = contiene(args, "--bmp");
        String rutaConfig = rutaConfiguracion(args);
        Scanner teclado = new Scanner(System.in);

        Configuracion config;
        try {
            config = LectorConfiguracion.cargarConfiguracion(rutaConfig);
        } catch (RuntimeException e) {
            System.out.println("No se pudo leer la configuracion: " + e.getMessage());
            System.out.println("Busque '" + rutaConfig + "' desde: " + Path.of("").toAbsolutePath());
            return;
        }

        System.out.println("=== JavaEdition - La fuga de la mazmorra ===");
        String dificultad = elegirDificultad(teclado);
        if (dificultad == null) {
            return;
        }
        ParametrosDeDificultad parametros = parametrosDe(config, dificultad);

        Mundo mundo;
        try {
            mundo = construirMundo(config, parametros, dificultad);
        } catch (RuntimeException e) {
            System.out.println("No se pudo generar la mazmorra: " + e.getMessage());
            return;
        }

        Heroe heroe = new Heroe(config.energiaInicial(), mundo.getInicio());
        Agenda agenda = new Agenda();
        mundo.planoEn(heroe.getUbicacion()).marcarVisitada(heroe.getUbicacion().posicion());

        System.out.println("Mundo: " + parametros.tableros() + " tablero(s) x "
                + parametros.pisos() + " piso(s).");
        System.out.println("Movimientos: w/a/s/d + Enter | q para salir"
                + (guardarBmp ? " | guardando BMP en salida/" : ""));

        jugar(mundo, heroe, agenda, config, dificultad, rutaDeRanking(rutaConfig), teclado, guardarBmp);
    }

    // ---------- Armado del mundo ----------

    private static Mundo construirMundo(Configuracion config, ParametrosDeDificultad parametros,
                                        String dificultad) {
        RuntimeException ultimoError = null;
        Gson gson = new Gson();
        GeneradorDeLaberinto generador = new GeneradorJavaEdition();

        for (int intento = 1; intento <= INTENTOS_DE_GENERACION; intento++) {
            try {
                Mundo mundo = new Mundo(parametros.tableros(), parametros.pisos());

                for (int tablero = 1; tablero <= parametros.tableros(); tablero++) {
                    for (int piso = 1; piso <= parametros.pisos(); piso++) {
                        boolean conSalida = tablero == parametros.tableros()
                                && piso == parametros.pisos();
                        Long semillaPlano = semillaDe(config.semilla(), tablero, piso, intento);

                        ParametrosGenerador params = new ParametrosGenerador(
                                1, dificultad, parametros.filas(), parametros.columnas(),
                                semillaPlano, parametros.cofres(), parametros.monstruos(), conSalida);

                        PlanoGenerado generado = gson.fromJson(
                                generador.generar(gson.toJson(params)), PlanoGenerado.class);

                        Plano plano = construirPlano(generado, randomDe(semillaPlano));
                        mundo.agregarPlano(tablero, piso, plano);
                        plano.marcarVisitada(plano.getEntrada());
                    }
                }

                Long semillaConexiones = semillaDe(config.semilla(), 99, 99, intento);
                new ColocadorDeConexiones(randomDe(semillaConexiones)).conectarTodo(mundo);
                return mundo;
            } catch (RuntimeException e) {
                ultimoError = e;
            }
        }
        throw new IllegalStateException("No se pudo generar un mundo valido despues de "
                + INTENTOS_DE_GENERACION + " intentos: " + ultimoError.getMessage(), ultimoError);
    }

    private static Plano construirPlano(PlanoGenerado generado, Random random) {
        String[] filas = generado.plano();
        Terreno[][] terreno = new Terreno[filas.length][filas[0].length()];

        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length(); c++) {
                terreno[f][c] = Terreno.desdeSimbolo(filas[f].charAt(c));
            }
        }

        Plano plano = new Plano(terreno, generado.entrada(), generado.salida());

        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length(); c++) {
                Posicion posicion = new Posicion(f + 1, c + 1);
                switch (filas[f].charAt(c)) {
                    case 'M' -> plano.agregarMonstruo(new Errante(posicion, random));
                    case 'C' -> plano.agregarCofre(new CofreDePuntos(posicion));
                    default -> { }
                }
            }
        }
        return plano;
    }



    private static void jugar(Mundo mundo, Heroe heroe, Agenda agenda, Configuracion config,
                              String dificultad, String rutaRanking, Scanner teclado, boolean guardarBmp) {
        Vista vista = guardarBmp ? new VistaBmp() : null;
        EstadoPartida estado = EstadoPartida.EN_CURSO;
        int pasos = 0;

        while (estado == EstadoPartida.EN_CURSO) {
            Plano plano = mundo.planoEn(heroe.getUbicacion());
            dibujar(mundo, plano, heroe, pasos);
            System.out.println("Tablero " + heroe.getUbicacion().tablero()
                    + " | Piso " + heroe.getUbicacion().piso()
                    + " | Energia: " + heroe.getEnergia()
                    + " | Cofres: " + heroe.getCofresAbiertos()
                    + " | Pasos: " + pasos);
            System.out.print("> ");

            if (!teclado.hasNextLine()) {
                System.out.println("\nSe cerro la entrada. Partida interrumpida.");
                return;
            }
            String comando = teclado.nextLine().trim().toLowerCase();
            if (comando.equals("q") || comando.equals("salir")) {
                System.out.println("Saliste de la partida.");
                return;
            }

            Direccion direccion = direccionDe(comando);
            if (direccion == null) {
                System.out.println("Comando invalido (usá w/a/s/d o q).");
                continue;
            }
            if (!mover(mundo, heroe, direccion, config)) {
                System.out.println("No podes ir para ahi.");
                continue;
            }
            pasos++;

            agenda.avanzarPaso();
            procesarEventos(agenda, heroe);

            Conexion conexion = mundo.conexionEn(heroe.getUbicacion());
            if (conexion != null) {
                Ubicacion origen = heroe.getUbicacion();
                Ubicacion destino = conexion.extremoOpuesto(origen);
                heroe.irA(destino);
                plano = mundo.planoEn(destino);
                plano.marcarVisitada(destino.posicion());
                System.out.println(cruzarConexion(origen, destino));
            }

            Cofre cofre = plano.cofreEn(heroe.getUbicacion().posicion());
            if (cofre != null && !cofre.estaAbierto()) {
                cofre.abrir(heroe, plano, agenda);
                System.out.println("Abriste un cofre!");
            }

            for (int i = 0; i < plano.getMonstruos().cantidad(); i++) {
                int energiaAntes = heroe.getEnergia();
                Monstruo monstruo = plano.getMonstruos().obtener(i);
                monstruo.actuar(plano, heroe, agenda);
                if (heroe.getEnergia() < energiaAntes) {
                    System.out.println("Un monstruo te alcanzo: -"
                            + (energiaAntes - heroe.getEnergia()) + " de energia.");
                }
            }

            if (vista != null) {
                vista.mostrarPartida(plano, heroe, pasos);
            }
            estado = evaluarEstado(mundo, heroe);
        }

        dibujar(mundo, mundo.planoEn(heroe.getUbicacion()), heroe, pasos);
        if (estado == EstadoPartida.GANADA) {
            System.out.println("GANASTE! Cofres: " + heroe.getCofresAbiertos()
                    + " | Energia: " + heroe.getEnergia() + " | Pasos: " + pasos);
        } else {
            System.out.println("PERDISTE: te quedaste sin energia.");
        }
        registrarEnRanking(heroe, estado, dificultad, pasos, rutaRanking, teclado);
    }

    private static boolean mover(Mundo mundo, Heroe heroe, Direccion direccion, Configuracion config) {
        Ubicacion actual = heroe.getUbicacion();
        Plano plano = mundo.planoEn(actual);
        Posicion destino = actual.posicion().mover(direccion);

        if (!plano.esTransitable(destino)) {
            return false;
        }
        Terreno terreno = plano.terrenoEn(destino);
        heroe.irA(new Ubicacion(actual.tablero(), actual.piso(), destino));
        plano.marcarVisitada(destino);
        heroe.perderEnergia(costoDe(terreno, config));
        return true;
    }

    private static EstadoPartida evaluarEstado(Mundo mundo, Heroe heroe) {
        Ubicacion salida = mundo.ubicacionDeSalida();
        if (salida != null && heroe.getUbicacion().equals(salida)) {
            return EstadoPartida.GANADA;
        }
        if (heroe.estaSinEnergia()) {
            return EstadoPartida.PERDIDA;
        }
        return EstadoPartida.EN_CURSO;
    }

    private static void procesarEventos(Agenda agenda, Heroe heroe) {
        while (agenda.hayEventoVencido()) {
            EventoProgramado evento = agenda.sacarProximo();
            System.out.println("Evento: " + evento.tipo()
                    + (evento.dato() == null ? "" : " (" + evento.dato() + ")"));
            if (evento.tipo() != null && evento.tipo().startsWith("fin-") && evento.dato() != null) {
                heroe.desactivarEfecto(evento.dato());
            }
        }
    }



    private static void registrarEnRanking(Heroe heroe, EstadoPartida estado, String dificultad,
                                           int pasos, String rutaRanking, Scanner teclado) {
        try {
            int puntos = heroe.getCofresAbiertos() * 100 + heroe.getEnergia()
                    + (estado == EstadoPartida.GANADA ? 500 : 0) - pasos;
            puntos = Math.max(0, puntos);

            System.out.print("Nombre para el ranking: ");
            String nombre = teclado.hasNextLine() ? teclado.nextLine().trim() : "";
            if (nombre.isEmpty()) {
                nombre = "Jugador";
            }

            Ranking ranking = PersistenciaRanking.armarRanking(rutaRanking);
            ranking.agregar(new EntradaDeRanking(nombre, puntos,
                    DificultadDe(dificultad), LocalDate.now().toString()));
            PersistenciaRanking.guardarRanking(ranking.totalEntradas(), rutaRanking);

            System.out.println("TOP 10:");
            Lista<EntradaDeRanking> mejores = ranking.top10();
            for (int i = 0; i < mejores.cantidad(); i++) {
                EntradaDeRanking entrada = mejores.obtener(i);
                System.out.println("  " + (i + 1) + ". " + entrada.nombre()
                        + " - " + entrada.puntaje() + " (" + entrada.dificultad() + ")");
            }
        } catch (RuntimeException e) {
            System.out.println("No se pudo actualizar el ranking: " + e.getMessage());
        }
    }



    private static String cruzarConexion(Ubicacion origen, Ubicacion destino) {
        String tipo = origen.piso() == destino.piso() ? "pasillo" : "escalera";
        return "Tomaste un " + tipo + " -> tablero " + destino.tablero()
                + ", piso " + destino.piso();
    }

    private static int costoDe(Terreno terreno, Configuracion config) {
        return switch (terreno) {
            case AGUA -> config.costos().agua();
            case LAVA -> config.costos().lava();
            default -> config.costos().piso();
        };
    }

    private static Direccion direccionDe(String comando) {
        return switch (comando) {
            case "w", "arriba" -> Direccion.ARRIBA;
            case "s", "abajo" -> Direccion.ABAJO;
            case "a", "izquierda" -> Direccion.IZQUIERDA;
            case "d", "derecha" -> Direccion.DERECHA;
            default -> null;
        };
    }

    private static ParametrosDeDificultad parametrosDe(Configuracion config, String dificultad) {
        return switch (dificultad) {
            case "FACIL" -> config.dificultades().FACIL();
            case "DIFICIL" -> config.dificultades().DIFICIL();
            default -> config.dificultades().MEDIO();
        };
    }

    private static modelo.Dificultad DificultadDe(String dificultad) {
        return modelo.Dificultad.valueOf(dificultad);
    }

    private static String elegirDificultad(Scanner teclado) {
        while (true) {
            System.out.print("Dificultad (1=FACIL, 2=MEDIO, 3=DIFICIL, q=salir): ");
            if (!teclado.hasNextLine()) {
                System.out.println("\nSin entrada por consola: uso MEDIO.");
                return "MEDIO";
            }
            String opcion = teclado.nextLine().trim().toUpperCase();
            if (opcion.equals("1") || opcion.equals("FACIL")) {
                return "FACIL";
            }
            if (opcion.equals("2") || opcion.equals("MEDIO") || opcion.isEmpty()) {
                return "MEDIO";
            }
            if (opcion.equals("3") || opcion.equals("DIFICIL")) {
                return "DIFICIL";
            }
            if (opcion.equals("Q") || opcion.equals("SALIR")) {
                return null;
            }
            System.out.println("Opcion invalida.");
        }
    }

    private static Long semillaDe(Long base, int tablero, int piso, int intento) {
        if (base == null) {
            return null;
        }
        return base + (long) intento * 1_000_003L + ((long) tablero * 1000 + piso) * 7919L;
    }

    private static Random randomDe(Long semilla) {
        return semilla == null ? new Random() : new Random(semilla);
    }

    private static boolean contiene(String[] args, String valor) {
        for (String arg : args) {
            if (arg.equals(valor)) {
                return true;
            }
        }
        return false;
    }

    private static String rutaConfiguracion(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                return arg;
            }
        }
        String[] candidatas = {CONFIG_POR_DEFECTO, "MainV2/" + CONFIG_POR_DEFECTO,
                "../" + CONFIG_POR_DEFECTO, "../MainV2/" + CONFIG_POR_DEFECTO};
        for (String candidata : candidatas) {
            if (java.nio.file.Files.exists(Path.of(candidata))) {
                return candidata;
            }
        }
        return CONFIG_POR_DEFECTO;
    }

    private static String rutaDeRanking(String rutaConfig) {
        Path directorio = Path.of(rutaConfig).toAbsolutePath().getParent();
        return directorio == null ? RANKING_POR_DEFECTO
                : directorio.resolve(RANKING_POR_DEFECTO).toString();
    }

    /**
     * H = heroe, M = monstruo, C = cofre cerrado, S = salida, E = entrada,
     * X = escalera, + = pasillo.
     */
    private static void dibujar(Mundo mundo, Plano plano, Heroe heroe, int paso) {
        Posicion posicionHeroe = heroe.getUbicacion().posicion();
        StringBuilder dibujo = new StringBuilder();
        for (int fila = 1; fila <= plano.getFilas(); fila++) {
            for (int columna = 1; columna <= plano.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                char simbolo = plano.terrenoEn(posicion).getSimbolo();

                if (posicion.equals(posicionHeroe)) {
                    simbolo = 'H';
                } else if (plano.monstruoEn(posicion) != null) {
                    simbolo = 'M';
                } else if (plano.cofreEn(posicion) != null) {
                    simbolo = plano.cofreEn(posicion).estaAbierto() ? 'c' : 'C';
                } else if (posicion.equals(plano.getSalida())) {
                    simbolo = 'S';
                } else if (posicion.equals(plano.getEntrada())) {
                    simbolo = 'E';
                } else {
                    Conexion conexion = mundo.conexionEn(
                            new Ubicacion(heroe.getUbicacion().tablero(),
                                    heroe.getUbicacion().piso(), posicion));
                    if (conexion != null) {
                        simbolo = conexion.origen().piso() == conexion.destino().piso() ? '+' : 'X';
                    }
                }
                dibujo.append(simbolo);
            }
            dibujo.append('\n');
        }
        System.out.print(dibujo);
    }
}
