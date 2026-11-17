package entidades.objetos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogoObjetosTest {

    @Test
    void registraYRecuperaUnObjeto() {
        CatalogoObjetos catalogoObjetos = new CatalogoObjetos();
        PocionDeEnergia pocion = new PocionDeEnergia(25);

        catalogoObjetos.registrar(PocionDeEnergia.id, pocion);

        assertSame(pocion, catalogoObjetos.obtener(PocionDeEnergia.id));
    }

    @Test
    void rechazaUnIdDesconocidoODuplicado() {
        CatalogoObjetos catalogoObjetos = new CatalogoObjetos();
        catalogoObjetos.registrar(PocionDeEnergia.id, new PocionDeEnergia(25));

        assertThrows(IllegalArgumentException.class,
                () -> catalogoObjetos.obtener("desconocido"));
        assertThrows(IllegalArgumentException.class,
                () -> catalogoObjetos.registrar(
                        PocionDeEnergia.id, new PocionDeEnergia(10)));
    }
}