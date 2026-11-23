package persistencia;

import estructuras.Lista;
import modelo.Conexion;
import modelo.Costo;
import modelo.Mundo;
import entidades.Catalogo;
import entidades.monstruos.Monstruo;
import entidades.cofres.Cofre;

/** Convierte un Mundo en MundoGuardado y viceversa */
public class ConversorMundo {

    /** Copia todos los planos y las conexiones del mundo */
    public static MundoGuardado aGuardado(Mundo mundo){
        PlanoGuardado[][] planos = new PlanoGuardado[mundo.getTableros()][mundo.getPisos()];

        for (int i = 1; i <= mundo.getTableros(); i++){
            for (int j = 1; j <= mundo.getPisos(); j++){
                planos[i-1][j-1] = ConversorPlano.aGuardado(mundo.planoEn(i,j));
            }
        }

        Lista<Conexion> lista = mundo.getConexiones();
        Conexion[] conexiones = new Conexion[lista.cantidad()];
        for (int i = 0; i < conexiones.length; i++){
            conexiones[i] = lista.obtener(i);
        }
        return new MundoGuardado(planos, conexiones);
    }

    /** Reconstruye el mundo. Primero planos, despues conexiones y por ultimo entidades */
    public static Mundo aMundo(MundoGuardado guardado, Costo costos){
        return reconstruir(guardado, (plano, guardadoPlano) ->
                ConversorPlano.agregarEntidades(plano, guardadoPlano, costos));
    }

    public static Mundo aMundo(MundoGuardado guardado, Catalogo<Monstruo> monstruos,
        Catalogo<Cofre> cofres){
        return reconstruir(guardado, (plano, guardadoPlano) ->
                ConversorPlano.agregarEntidades(
                        plano, guardadoPlano, monstruos, cofres));
    }

    private static Mundo reconstruir(
            MundoGuardado guardado,
            java.util.function.BiConsumer<modelo.Plano, PlanoGuardado> agregarEntidades) {
        int tableros = guardado.planos().length;
        int pisos = guardado.planos()[0].length;
        Mundo mundo = new Mundo(tableros, pisos);

        for (int i = 1; i <= tableros; i++){
            for (int j = 1; j <= pisos; j++){
                mundo.agregarPlano(i, j, ConversorPlano.aPlano(guardado.planos()[i - 1][j - 1]));
            }
        }
        for (int i = 0; i < guardado.conexiones().length; i++){
            mundo.agregarConexion(guardado.conexiones()[i]);
        }
        for (int i = 1; i <= tableros; i++){
            for (int j = 1; j <= pisos; j++){
                agregarEntidades.accept(
                        mundo.planoEn(i, j), guardado.planos()[i - 1][j - 1]);
            }
        }
        return mundo;
    }
}
