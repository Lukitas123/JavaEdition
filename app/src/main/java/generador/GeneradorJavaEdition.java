package generador;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

        throw new UnsupportedOperationException("Pendiente de implementar generacion del plano.");
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
}
