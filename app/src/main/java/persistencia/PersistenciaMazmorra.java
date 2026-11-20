package persistencia;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Escribe y lee una partida guardada*/
public class PersistenciaMazmorra {

    /** Guarda la partida en un archivo json*/
    public static void guardar(MazmorraGuardada partida, String ruta){
        String json = new Gson().toJson(partida);
        try {
            Files.writeString(Path.of(ruta), json);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo guardar: " + ruta, e);
        }
    }

    /** Lee una partida guardada */
    public static MazmorraGuardada cargar(String ruta){
        Path destino = Path.of(ruta);
        if (!Files.exists(destino)) {
            throw new IllegalArgumentException("No hay partida guardada en: " + ruta);
        }
        try {
            return new Gson().fromJson(Files.readString(destino), MazmorraGuardada.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer: " + ruta, e);
        }
    }
}
