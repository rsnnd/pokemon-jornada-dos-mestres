import java.io.IOException;
/** Adaptador do formato Properties; Main conhece apenas RepositorioPartida. */
public class RepositorioProperties implements RepositorioPartida {
    @Override public boolean existe() { return Persistencia.existeSave(); }
    @Override public void salvar(Treinador treinador, Jornada jornada) throws IOException {
        Persistencia.salvar(treinador, jornada);
    }
    @Override public DadosPartida carregar(Combate combate, Leitor leitor) throws IOException {
        return Persistencia.carregar(combate, leitor);
    }
}
