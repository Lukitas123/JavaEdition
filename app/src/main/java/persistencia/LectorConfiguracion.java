package persistencia;

import modelo.Configuracion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

//Lee el archivo json con los datos de Configuracion
public class LectorConfiguracion {
    /**metodo de lectura. Devuelve comentario junto con el error si falla**/
    public static String leerConfiguracion(String ruta) {
        try {
            return Files.readString(Path.of(ruta));
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer: " + ruta + 
            ".  Asegúrese de que el archivo .json se encuentre en la misma carpeta que el programa" , e);
        }
    }
    /**metodo para cargar la Configuracion como objeto**/
    public static Configuracion cargarConfiguracion(String ruta){
        //Guardo el texto en json a una variable y paso la informacion a un nuevo Objeto de Configuracion
        String json=leerConfiguracion(ruta);

        Gson gson=new Gson();
        Configuracion c = gson.fromJson(json,Configuracion.class);

        return c;
    }
}
