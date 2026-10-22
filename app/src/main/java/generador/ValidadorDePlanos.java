package generador;

import com.google.gson.Gson;
import modelo.Direccion;
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

    public boolean validarBifurcacionesR6 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        int transitables = 0;
        int conTresOMasVecinos = 0;

        //recorro las celdas internas buscando cuales son transitables (todo lo que no sea '#')
        for (int f = 1; f < filas - 1; f++){
            for (int c = 1; c < columnas - 1; c++){
                if (plano[f].charAt(c) != '#'){
                    transitables++;
                    int vecinosTransitables = 0;
                    //miro sus 4 vecinos usando Direccion (arriba, abajo, izquierda, derecha)
                    for (Direccion dir : Direccion.values()){
                        int filaVecino = f + dir.getDeltaFila();
                        int colVecino = c + dir.getDeltaColumna();
                        if (plano[filaVecino].charAt(colVecino) != '#'){
                            vecinosTransitables++;
                        }
                    }
                    //si tiene 3 o mas vecinos transitables es una bifurcacion
                    if (vecinosTransitables >= 3){
                        conTresOMasVecinos++;
                    }
                }
            }
        }

        //al menos el 10% de las transitables deben tener 3 o mas vecinos transitables
        if (transitables == 0){
            return false;
        }
        return (conTresOMasVecinos * 100) >= (transitables * 10);
    }

    public boolean validarProporcionR7 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        int totalCeldas = filas * columnas;
        int transitables = 0;

        //cuento cuantas celdas transitables hay en todo el plano
        for (String cadena : plano){
            for (char simbolo : cadena.toCharArray()){
                if (simbolo != '#'){
                    transitables++;
                }
            }
        }

        //valido que las transitables esten entre el 30% y el 60% del total de celdas
        double proporcion = (double) transitables / totalCeldas;
        if (proporcion < 0.30 || proporcion > 0.60){
            return false;
        }
        return true;
    }

    public boolean validarCantidadesR8 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int cofresEsperados = dto.parametros().cofres();
        int monstruosEsperados = dto.parametros().monstruos();
        if (cofresEsperados < 0 || monstruosEsperados < 0){
            return false;
        }

        int cofresContados = 0;
        int monstruosContados = 0;

        //recorro cada cadena y cuento las C y las M
        for (String cadena : plano){
            for (char simbolo : cadena.toCharArray()){
                if (simbolo == 'C'){
                    cofresContados++;
                }
                if (simbolo == 'M'){
                    monstruosContados++;
                }
            }
        }

        //compruebo que coincidan exactamente con lo que dicen los parametros
        if (cofresContados != cofresEsperados || monstruosContados != monstruosEsperados){
            return false;
        }
        return true;
    }

    public boolean validarAguaYLavaR10 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int transitables = 0;
        int aguaYLava = 0;

        //cuento las celdas transitables y cuantas de ellas son agua (~) o lava (^)
        for (String cadena : plano){
            for (char simbolo : cadena.toCharArray()){
                if (simbolo != '#'){
                    transitables++;
                }
                if (simbolo == '~' || simbolo == '^'){
                    aguaYLava++;
                }
            }
        }

        //entre las dos deben ser como mucho el 25% de las transitables
        if (transitables == 0){
            return false;
        }
        double proporcion = (double) aguaYLava / transitables;
        if (proporcion > 0.25){
            return false;
        }
        return true;
    }

    public boolean validarDistanciaMonstruosR11 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        Posicion entrada = dto.entrada();

        //recorro el plano buscando cada M y calculo su distancia Manhattan a la entrada E
        for (int f = 0; f < filas; f++){
            String cadena = plano[f];
            for (int c = 0; c < columnas; c++){
                if (cadena.charAt(c) == 'M'){
                    Posicion posMonstruo = new Posicion(f + 1, c + 1);
                    if (posMonstruo.distanciaManhattan(entrada) < 3){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
