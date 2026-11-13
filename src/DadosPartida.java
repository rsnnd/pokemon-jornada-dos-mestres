/** Agrupa os objetos recuperados do arquivo de salvamento. */
public class DadosPartida {
    private final Treinador treinador;
    private final Jornada jornada;

    public DadosPartida(Treinador treinador, Jornada jornada) {
        this.treinador = treinador;
        this.jornada = jornada;
    }

    public Treinador getTreinador() { return treinador; }
    public Jornada getJornada() { return jornada; }
}
