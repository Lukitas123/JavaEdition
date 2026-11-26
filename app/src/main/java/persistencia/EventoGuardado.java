package persistencia;

import com.google.gson.JsonElement;

public record EventoGuardado(
        Integer pasoDeVencimiento,
        String tipo,
        JsonElement datos //contempla tablero y piso; o efecto en el caso del heroe
        ) {
}
