public class Inventario {
    private int pokebolas;
    private int pocoes;

    public Inventario(int pokebolas, int pocoes) {
        if (pokebolas < 0 || pocoes < 0) throw new IllegalArgumentException("Itens invalidos.");
        this.pokebolas = pokebolas;
        this.pocoes = pocoes;
    }

    public int getPokebolas() { return pokebolas; }
    public int getPocoes() { return pocoes; }

    public boolean usarPokebola() {
        if (pokebolas == 0) return false;
        pokebolas--;
        return true;
    }

    public boolean usarPocao(Pokemon pokemon) {
        if (pocoes == 0 || pokemon.estaDerrotado() || pokemon.getVidaAtual() == pokemon.getVidaMaxima()) {
            return false;
        }
        pocoes--;
        int recuperado = pokemon.curar(20);
        System.out.println(pokemon.getNome() + " recuperou " + recuperado + " HP!");
        return true;
    }

    public void adicionarItens(int novasPokebolas, int novasPocoes) {
        if (novasPokebolas < 0 || novasPocoes < 0) throw new IllegalArgumentException("Recompensa invalida.");
        pokebolas += novasPokebolas;
        pocoes += novasPocoes;
    }

    public void mostrar() {
        System.out.println("Pokebolas: " + pokebolas + " | Pocoes: " + pocoes + " (curam 20 HP)");
    }
}
