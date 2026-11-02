package generador;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import estructuras.Pila;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import modelo.Direccion;
import modelo.Posicion;

public class GeneradorJavaEdition implements GeneradorDeLaberinto {
    private static final String NOMBRE_GENERADOR = "JavaEdition";

    private record ParametrosDto(Integer version, String dificultad, Integer filas, Integer columnas,
                                 Long semilla, Integer cofres, Integer monstruos, Boolean conSalida) {}

    private record PlanoDto(int version, String generador, ParametrosDto parametros,
                            Posicion entrada, Posicion salida, String[] plano) {}

    @Override
    public String generar(String parametrosJson) {
        //compruebo que el JSON recibido no sea nulo ni vacio
        if (parametrosJson == null || parametrosJson.isBlank()) {
            throw new IllegalArgumentException("El JSON de parametros no puede ser nulo ni vacio.");
        }

        Gson gson = new Gson();
        ParametrosDto params;
        try {
            params = gson.fromJson(parametrosJson, ParametrosDto.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("El JSON de parametros tiene formato invalido.", e);
        }

        validarParametros(params);

        Random random = (params.semilla() != null) ? new Random(params.semilla()) : new Random();
        char[][] grilla = construirLaberintoBase(params, random);

        throw new UnsupportedOperationException("Pendiente de decorar plano con entrada, salida, agua, lava y entidades.");
    }

    private void validarParametros(ParametrosDto params) {
        //verifico que el objeto y todos los campos obligatorios esten presentes
        if (params == null
                || params.version() == null
                || params.dificultad() == null || params.dificultad().isBlank()
                || params.filas() == null
                || params.columnas() == null
                || params.cofres() == null
                || params.monstruos() == null
                || params.conSalida() == null) {
            throw new IllegalArgumentException("Faltan campos obligatorios en los parametros del generador.");
        }

        int filas = params.filas();
        int columnas = params.columnas();
        int cofres = params.cofres();
        int monstruos = params.monstruos();

        //R1: las dimensiones minimas son 5x5
        if (filas < 5 || columnas < 5) {
            throw new IllegalArgumentException("Las filas y columnas deben ser al menos 5.");
        }

        //R8: cantidades no negativas
        if (cofres < 0 || monstruos < 0) {
            throw new IllegalArgumentException("La cantidad de cofres y monstruos no puede ser negativa.");
        }

        //R7 establece que como maximo el 60% del total de celdas puede ser transitable
        int maxTransitablesPorRegla = (int) Math.floor(filas * columnas * 0.60);
        int maxInterior = (filas - 2) * (columnas - 2);
        int maxTransitables = Math.min(maxTransitablesPorRegla, maxInterior);

        int celdasNecesarias = 1 + (params.conSalida() ? 1 : 0) + cofres + monstruos;
        if (celdasNecesarias > maxTransitables) {
            throw new IllegalArgumentException("No entran tantos cofres y monstruos en las celdas transitables del plano.");
        }
    }

    private char[][] construirLaberintoBase(ParametrosDto params, Random random) {
        int filas = params.filas();
        int columnas = params.columnas();
        char[][] grilla = new char[filas][columnas];

        //1) todas las celdas empiezan como pared '#'
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                grilla[f][c] = '#';
            }
        }

        int totalCeldas = filas * columnas;
        int minPorRegla = (int) Math.ceil(totalCeldas * 0.30);
        int maxPorRegla = (int) Math.floor(totalCeldas * 0.60);
        int minNecesarias = 1 + (params.conSalida() ? 1 : 0) + params.cofres() + params.monstruos() + 6;
        int objetivoMinimo = Math.min(maxPorRegla, Math.max(minPorRegla, minNecesarias));
        //dejamos margen debajo del 60% para poder abrir bifurcaciones sin pasarnos de R7
        int topeBacktracking = Math.max(objetivoMinimo, (int) Math.floor(totalCeldas * 0.48));

        //2) algoritmo de backtracking con nuestra Pila propia a distancia 2
        Pila<Posicion> pila = new Pila<>();
        Posicion inicio = new Posicion(1, 1);
        grilla[inicio.fila()][inicio.columna()] = '.';
        int transitables = 1;
        pila.apilar(inicio);

        while (!pila.estaVacia()) {
            Posicion actual = pila.obtenerCima();
            List<Direccion> direccionesPosibles = new ArrayList<>();

            if (transitables + 2 <= topeBacktracking) {
                for (Direccion dir : Direccion.values()) {
                    int nf = actual.fila() + dir.getDeltaFila() * 2;
                    int nc = actual.columna() + dir.getDeltaColumna() * 2;
                    if (nf >= 1 && nf <= filas - 2 && nc >= 1 && nc <= columnas - 2) {
                        if (grilla[nf][nc] == '#') {
                            direccionesPosibles.add(dir);
                        }
                    }
                }
            }

            if (!direccionesPosibles.isEmpty()) {
                Direccion elegida = direccionesPosibles.get(random.nextInt(direccionesPosibles.size()));
                int fMedio = actual.fila() + elegida.getDeltaFila();
                int cMedio = actual.columna() + elegida.getDeltaColumna();
                int fDestino = actual.fila() + elegida.getDeltaFila() * 2;
                int cDestino = actual.columna() + elegida.getDeltaColumna() * 2;

                grilla[fMedio][cMedio] = '.';
                grilla[fDestino][cDestino] = '.';
                transitables += 2;
                pila.apilar(new Posicion(fDestino, cDestino));
            } else {
                pila.desapilar();
            }
        }

        //3) abrimos paredes internas entre celdas de piso para asegurar R6 (al menos 10% de bifurcaciones) y R7 (>= 30%)
        asegurarProporcionYBifurcaciones(grilla, filas, columnas, objetivoMinimo, maxPorRegla, random);

        return grilla;
    }

    private void asegurarProporcionYBifurcaciones(char[][] grilla, int filas, int columnas,
                                                  int minTransitables, int maxTransitables, Random random) {
        int transitables = contarTransitables(grilla, filas, columnas);

        while (transitables < maxTransitables
                && (transitables < minTransitables || contarBifurcaciones(grilla, filas, columnas) * 100 < transitables * 10)) {
            List<Posicion> paredesEntrePisos = new ArrayList<>();
            List<Posicion> paredesAdyacentes = new ArrayList<>();

            for (int f = 1; f < filas - 1; f++) {
                for (int c = 1; c < columnas - 1; c++) {
                    if (grilla[f][c] == '#') {
                        int vecinos = contarVecinosTransitables(grilla, f, c);
                        if (vecinos >= 2) {
                            paredesEntrePisos.add(new Posicion(f, c));
                        } else if (vecinos == 1) {
                            paredesAdyacentes.add(new Posicion(f, c));
                        }
                    }
                }
            }

            Posicion elegida = null;
            if (!paredesEntrePisos.isEmpty()) {
                elegida = paredesEntrePisos.get(random.nextInt(paredesEntrePisos.size()));
            } else if (!paredesAdyacentes.isEmpty()) {
                elegida = paredesAdyacentes.get(random.nextInt(paredesAdyacentes.size()));
            } else {
                break;
            }

            grilla[elegida.fila()][elegida.columna()] = '.';
            transitables++;
        }
    }

    private int contarTransitables(char[][] grilla, int filas, int columnas) {
        int total = 0;
        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (grilla[f][c] != '#') {
                    total++;
                }
            }
        }
        return total;
    }

    private int contarBifurcaciones(char[][] grilla, int filas, int columnas) {
        int conTresOMas = 0;
        for (int f = 1; f < filas - 1; f++) {
            for (int c = 1; c < columnas - 1; c++) {
                if (grilla[f][c] != '#' && contarVecinosTransitables(grilla, f, c) >= 3) {
                    conTresOMas++;
                }
            }
        }
        return conTresOMas;
    }

    private int contarVecinosTransitables(char[][] grilla, int f, int c) {
        int vecinos = 0;
        for (Direccion dir : Direccion.values()) {
            int nf = f + dir.getDeltaFila();
            int nc = c + dir.getDeltaColumna();
            if (grilla[nf][nc] != '#') {
                vecinos++;
            }
        }
        return vecinos;
    }
}
