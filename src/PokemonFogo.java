/** Subtipo concreto de Pokemon: intercambiavel com a classe abstrata (LSP). */
public class PokemonFogo extends Pokemon {
    public PokemonFogo(String nome, int vidaMaxima, int nivel, Ataque... ataques) {
        super(nome, Tipo.FOGO, vidaMaxima, nivel, ataques);
    }
    @Override public String getEspecialidade() { return "Especialista em fogo"; }
}
