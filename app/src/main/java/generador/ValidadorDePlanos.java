package generador;

import com.google.gson.Gson;
import modelo.Posicion;
import java.util.List;

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
        //compruebo que el JSON tiene contenido
        if (planoJson == null || planoJson.isBlank()) {
            throw new IllegalArgumentException("El JSON del plano no puede ser nulo ni vacío.");
        }
        Gson gson = new Gson();
        PlanoDto dto = gson.fromJson(planoJson, PlanoDto.class);
        //compruebo que esta completo el JSON
        if (dto == null || dto.parametros() == null || dto.plano() == null){
            throw new IllegalArgumentException("El JSON del plano esta incompleto.");
        }

    }

    public boolean validarDimensionesR1 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int columnas = dto.parametros().columnas();
        int filas = dto.parametros().filas();
        //primero valido que el tamaño de las filas coincida con la cantidad de cadenas del plano y el minimo sea 5
        if (plano.length != filas || !(columnas >= 5 && filas >= 5)){
            return false;
        }
        //despues valido que cada cadena tenga el tamaño de columnas
        for (String cadena : plano){
            if(cadena == null || cadena.length() != columnas){
                return false;
            }
        }
        return true;
    }

    public boolean validarSimbolosR2 (PlanoDto dto){
        //creo una lista de caracteres validos
        List<Character> simbolos_validos = List.of('#', '.','~', '^','C', 'M','E', 'S');
        //recorro cada cadena del plano
        for (String cadena : dto.plano()){
        //para cada cadena, recorro cada caracter y me fijo si mi lista de simbolos validos contiene ese caracter, si no es asi, devuelvo false.
            for (char simbolo: cadena.toCharArray()) {
                if (!(simbolos_validos.contains(simbolo))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validarBordesR3 (PlanoDto dto){
        String[] plano = dto.plano();
        int columnas = dto.parametros().columnas();
        int filas = dto.parametros().filas();
        //como hipotesis utilizo que ya esta hecha la validacion R1.
        //primero compruebo que el final y el comienzo de cada fila tenga un #, utilizo la cantidad de columnas como referencia al tamaño de cada fila.
        for (String cadena : plano){
            if (cadena.charAt(0) != '#' || cadena.charAt(columnas-1) != '#'){
                return false;
            }
        }
        //compruebo individualmente que la primera y la ultima fila sean solo de #
        String primeFila = plano[0];
        String ultimaFIla = plano[filas-1];
        //ahora chequeo que solo se compongan de "#"
        if (!(primeFila.matches("#+") && ultimaFIla.matches("#+"))){
            return false;
        }

        return true;
    }

    public boolean validarEntradaSalidaR4 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        Boolean conSalida = dto.parametros().conSalida();
        if (conSalida == null){
            return false;
        }

        int cantidadEntradas = 0;
        int cantidadSalidas = 0;
        Posicion posEntradaEncontrada = null;
        Posicion posSalidaEncontrada = null;

        //recorro la grilla buscando las posiciones de E y S (sumo 1 porque las posiciones van de 1 a n)
        for (int f = 0; f < filas; f++){
            String cadena = plano[f];
            for (int c = 0; c < columnas; c++){
                char simbolo = cadena.charAt(c);
                if (simbolo == 'E'){
                    cantidadEntradas++;
                    posEntradaEncontrada = new Posicion(f + 1, c + 1);
                }
                if (simbolo == 'S'){
                    cantidadSalidas++;
                    posSalidaEncontrada = new Posicion(f + 1, c + 1);
                }
            }
        }

        //valido que haya exactamente una E y coincida con la entrada del JSON
        if (cantidadEntradas != 1 || dto.entrada() == null || !posEntradaEncontrada.equals(dto.entrada())){
            return false;
        }

        //si lleva salida, valido que haya una sola S y coincida con el JSON; si no lleva, que no haya ninguna
        if (conSalida){
            if (cantidadSalidas != 1 || dto.salida() == null || !posSalidaEncontrada.equals(dto.salida())){
                return false;
            }
        } else {
            if (cantidadSalidas != 0 || dto.salida() != null){
                return false;
            }
        }

        return true;
    }
}
