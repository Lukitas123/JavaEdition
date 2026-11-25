package modelo;

/**
 * unión entre dos planos (escalera o pasillo), se recorre en ambos sentidos,
 * pero se guarda una sola vez con origen y destino
 */
public record Conexion(Ubicacion origen, Ubicacion destino) {

    /** @return el otro extremo de la conexión, o null si la ubicación no es ninguno de los dos */
    public Ubicacion extremoOpuesto(Ubicacion desde) {
        if (desde.equals(origen)) {
            return destino;
        }
        if (desde.equals(destino)) {
            return origen;
        }
        return null;
    }
}