import java.io.IOException;
/** Contrato exclusivo para persistir e recuperar partidas (ISP, DIP). */
public interface RepositorioPartida {
    boolean existe();
    void salvar(Treinador treinador, Jornada jornada) throws IOException;
    DadosPartida carregar(Combate combate, Leitor leitor) throws IOException;
}
