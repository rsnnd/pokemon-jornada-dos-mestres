public class Ataque {
    private final String nome;
    private final Tipo tipo;
    private final int poder;

    public Ataque(String nome, Tipo tipo, int poder) {
        this.nome = nome;
        this.tipo = tipo;
        this.poder = poder;
    }

    public String getNome() { return nome; }
    public Tipo getTipo() { return tipo; }
    public int getPoder() { return poder; }
}
