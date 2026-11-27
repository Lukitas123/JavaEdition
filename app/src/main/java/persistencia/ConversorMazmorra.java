package persistencia;

import entidades.Catalogo;
import entidades.CatalogosEntidades;
import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import modelo.*;

import java.util.Random;

/**
 *Convierte una Mazmorra a una MazmorraGuardada y viceversa
*/
public class ConversorMazmorra {
    
    /**Copia una mazmorra/partida */
    public static  MazmorraGuardada aGuardada(Mazmorra mazmorra){
        if (mazmorra == null) {
            throw new IllegalArgumentException("La partida no puede ser null");
        }
        return new MazmorraGuardada(
                mazmorra.getDificultad(),
                mazmorra.getEstado(),
                ConversorMundo.aGuardado(mazmorra.getMundo()),
                ConversorHeroe.aGuardado(mazmorra.getHeroe()),
                ConversorAgenda.aGuardada(mazmorra.getAgenda()),
                mazmorra.getCosto(),
                mazmorra.getParametrosEntidades());
    }

    /** Crea una Mazmorra a partir de una MazmorraGuardada */
    public static Mazmorra aMazmorra(MazmorraGuardada guardado, Random azar) {
        if (guardado == null) {
            throw new IllegalArgumentException("La partida guardada no puede ser null");
        }
        if (guardado.parametrosEntidades() == null) {
            throw new IllegalArgumentException(
                    "Guardado incompatible: faltan los parametros de entidades. "
                            + "Los guardados anteriores no se pueden cargar.");
        }
        if (guardado.costos() == null || azar == null) {
            throw new IllegalArgumentException(
                    "Los costos y el generador aleatorio son obligatorios");
        }
        ParametrosEntidades parametros = guardado.parametrosEntidades();

        Mundo mundo = ConversorMundo.aMundo(
                guardado.mundo(),
                CatalogosEntidades.monstruos(
                        guardado.costos(), parametros, azar),
                CatalogosEntidades.cofres(parametros));

        return new Mazmorra(
                guardado.dificultad(),
                mundo,
                ConversorHeroe.aHeroe(guardado.heroe()),
                ConversorAgenda.aAgenda(guardado.agenda()),
                guardado.costos(),
                parametros,
                guardado.estado(),
                CatalogosEntidades.objetos(parametros, azar));
    }

}
