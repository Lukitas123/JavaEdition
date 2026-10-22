package generador;

import com.google.gson.Gson;
import estructuras.Cola;
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

        if (!ValidarDimensionesR1(dto)){
            throw new IllegalArgumentException("Falla R1: las dimensiones del plano no son validas.");
        }
        if (!ValidarSimbolosR2(dto)){
            throw new IllegalArgumentException("Falla R2: el plano contiene simbolos invalidos.");
        }
        if (!ValidarBordesR3(dto)){
            throw new IllegalArgumentException("Falla R3: los bordes del plano deben ser todos pared (#).");
        }
        if (!ValidarEntradaSalidaR4(dto)){
            throw new IllegalArgumentException("Falla R4: la entrada o la salida no son validas.");
        }
        if (!ValidarConexidadR5(dto)){
            throw new IllegalArgumentException("Falla R5: el plano no es conexo.");
        }
        if (!ValidarBifurcacionesR6(dto)){
            throw new IllegalArgumentException("Falla R6: el plano no tiene al menos un 10% de bifurcaciones.");
        }
        if (!ValidarProporcionR7(dto)){
            throw new IllegalArgumentException("Falla R7: la proporcion de celdas transitables debe estar entre 30% y 60%.");
        }
        if (!ValidarCantidadesR8(dto)){
            throw new IllegalArgumentException("Falla R8: la cantidad de cofres o monstruos no coincide con los parametros.");
        }
        if (!ValidarSalidaMasLejanaR9(dto)){
            throw new IllegalArgumentException("Falla R9: la salida no esta en la celda mas lejana a la entrada.");
        }
        if (!ValidarAguaYLavaR10(dto)){
            throw new IllegalArgumentException("Falla R10: el agua y la lava superan el 25% de las celdas transitables.");
        }
        if (!ValidarDistanciaMonstruosR11(dto)){
            throw new IllegalArgumentException("Falla R11: hay un monstruo a distancia Manhattan menor a 3 de la entrada.");
        }
    }
    public boolean ValidarDimensionesR1 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int columnas = dto.parametros().columnas();
        int filas = dto.parametros().filas();
       //primero valido que cada cadena tenga el tamaño de columnas
            for (String cadena : plano){
                if(cadena.length() != columnas){
                    return false;
                }
            }
        //despues valido que el tamaño de las filas coincida con la cantidad de cadenas del plano
        if (plano.length != filas){
            return false;
        }
        //por ultimo valido que el minimo de filas y columnas sea 5.
        if (!(columnas >= 5 && filas >= 5)){
            return false;
        }
        return true;
    }

    public boolean ValidarSimbolosR2 (PlanoDto dto){
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

    public boolean ValidarBordesR3 (PlanoDto dto){
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

    public boolean ValidarEntradaSalidaR4 (PlanoDto dto){
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
        if (cantidadEntradas != 1 || dto.entrada() == null || !(posEntradaEncontrada.equals(dto.entrada()))){
            return false;
        }

        //si lleva salida, valido que haya una sola S y coincida con el JSON; si no lleva, que no haya ninguna
        if (conSalida){
            if (cantidadSalidas != 1 || dto.salida() == null || !(posSalidaEncontrada.equals(dto.salida()))){
                return false;
            }
        } else {
            if (cantidadSalidas != 0 || dto.salida() != null){
                return false;
            }
        }

        return true;
    }

    public boolean ValidarConexidadR5 (PlanoDto dto){
        //guardo las variables a usar
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        int totalTransitables = 0;

        //cuento cuantas celdas transitables (distintas de '#') hay en total
        for (String cadena : plano){
            for (char simbolo : cadena.toCharArray()){
                if (simbolo != '#'){
                    totalTransitables++;
                }
            }
        }

        //calculo las distancias desde la entrada con BFS y cuento cuantas celdas se alcanzaron
        int[][] distancias = calcularDistanciasBfs(dto);
        int alcanzadas = 0;
        for (int f = 0; f < filas; f++){
            for (int c = 0; c < columnas; c++){
                if (distancias[f][c] != -1){
                    alcanzadas++;
                }
            }
        }

        //si las alcanzadas coinciden con el total de transitables, el plano es conexo
        if (alcanzadas != totalTransitables){
            return false;
        }
        return true;
    }

    public boolean ValidarBifurcacionesR6 (PlanoDto dto){
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
        if (!((conTresOMasVecinos * 100) >= (transitables * 10))){
            return false;
        }
        return true;
    }

    public boolean ValidarProporcionR7 (PlanoDto dto){
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

    public boolean ValidarCantidadesR8 (PlanoDto dto){
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

    public boolean ValidarSalidaMasLejanaR9 (PlanoDto dto){
        //si el plano no lleva salida, esta regla se cumple directo
        if (!dto.parametros().conSalida()){
            return true;
        }
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        int[][] distancias = calcularDistanciasBfs(dto);

        //busco cual es la distancia maxima alcanzada en todo el plano
        int distanciaMaxima = 0;
        for (int f = 0; f < filas; f++){
            for (int c = 0; c < columnas; c++){
                if (distancias[f][c] > distanciaMaxima){
                    distanciaMaxima = distancias[f][c];
                }
            }
        }

        //compruebo que la distancia de la salida S sea igual a la distancia maxima del BFS
        Posicion salida = dto.salida();
        int distanciaSalida = distancias[salida.fila() - 1][salida.columna() - 1];
        if (distanciaSalida != distanciaMaxima){
            return false;
        }
        return true;
    }

    public boolean ValidarAguaYLavaR10 (PlanoDto dto){
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

    public boolean ValidarDistanciaMonstruosR11 (PlanoDto dto){
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

    private int[][] calcularDistanciasBfs (PlanoDto dto){
        String[] plano = dto.plano();
        int filas = dto.parametros().filas();
        int columnas = dto.parametros().columnas();
        int[][] distancias = new int[filas][columnas];

        //inicializo todas las distancias en -1 (no visitado)
        for (int f = 0; f < filas; f++){
            for (int c = 0; c < columnas; c++){
                distancias[f][c] = -1;
            }
        }

        //creo la cola propia y encolo la posicion de entrada E con distancia 0
        Cola<Posicion> cola = new Cola<>();
        Posicion entrada = dto.entrada();
        distancias[entrada.fila() - 1][entrada.columna() - 1] = 0;
        cola.encolar(entrada);

        //mientras haya posiciones en la cola, desencolo y visito los 4 vecinos transitables
        while (!cola.estaVacia()){
            Posicion actual = cola.desencolar();
            int distActual = distancias[actual.fila() - 1][actual.columna() - 1];

            for (Direccion dir : Direccion.values()){
                Posicion vecino = actual.mover(dir);
                int fVecino = vecino.fila() - 1;
                int cVecino = vecino.columna() - 1;

                //si esta dentro de los limites, no es pared y todavia no fue visitado, anoto su distancia y lo encolo
                if (fVecino >= 0 && fVecino < filas && cVecino >= 0 && cVecino < columnas){
                    if (plano[fVecino].charAt(cVecino) != '#' && distancias[fVecino][cVecino] == -1){
                        distancias[fVecino][cVecino] = distActual + 1;
                        cola.encolar(vecino);
                    }
                }
            }
        }

        return distancias;
    }
}
