package modelo;
/**
 * Toda la informacion que constituye la configuracion
 */
public record Configuracion(
        int energiaInicial,
        Costo costos,
        Long semilla,
        Dificultades dificultades,
        ParametrosEntidades entidades
        ) {
    public Configuracion {
        if (entidades == null) {
            throw new IllegalArgumentException(
                    "Faltan los parametros de entidades");
        }
    }
    
}
