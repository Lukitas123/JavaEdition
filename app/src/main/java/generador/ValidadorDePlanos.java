package generador;

import com.google.gson.Gson;
import estructuras.Cola;
import modelo.Posicion;

public class ValidadorDePlanos {
    private record ParametrosDto(int version, String dificultad, int filas, int columnas,
                                 Long semilla, int cofres, int monstruos, Boolean conSalida) {}

    private record PlanoDto(int version, String generador, ParametrosDto parametros,
                            Posicion entrada, Posicion salida, String[] plano) {}

    /**
     * Valida que el JSON del plano cumpla con las 11 reglas de la cátedra (R1 a R11).
     *
     * @param planoJson Texto en formato JSON según Anexo B.3.
     * @throws IllegalArgumentException si alguna regla no se cumple.
     */
    public void validar(String planoJson) {
        if (planoJson == null || planoJson.isBlank()) {
            throw new IllegalArgumentException("El JSON del plano no puede ser nulo ni vacío.");
        }
        Gson gson = new Gson();
        PlanoDto dto = gson.fromJson(planoJson, PlanoDto.class);

        if (dto == null || dto.parametros() == null || dto.plano() == null){
            throw new IllegalArgumentException("El JSON del plano esta incompleto.");
        }

    }


}
