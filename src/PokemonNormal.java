/** Subtipo concreto de Pokemon: intercambiavel com a classe abstrata (LSP). */
public class PokemonNormal extends Pokemon {
    public PokemonNormal(String nome, int vidaMaxima, int nivel, Ataque... ataques) {
        super(nome, Tipo.NORMAL, vidaMaxima, nivel, ataques);
    }
    @Override public String getEspecialidade() { return "Especialista em ataques normais"; }
}
