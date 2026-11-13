import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Leitor leitor = new Leitor(entrada);
            Random sorteio = new Random();
            Combate batalha = new Batalha(leitor, sorteio);
            RepositorioPartida repositorio = new RepositorioProperties();
            System.out.println("=====================================");
            System.out.println(" POKEMON: JORNADA DOS MESTRES - EDICAO FINAL");
            System.out.println("=====================================");

            Treinador treinador;
            Jornada jornada;
            while (true) {
                System.out.println("\n=== TELA INICIAL ===");
                System.out.println("1 - Nova jornada");
                System.out.println("2 - Continuar partida salva" + (repositorio.existe() ? "" : " [sem save]"));
                System.out.println("0 - Sair");
                int inicio = leitor.opcao(0, 2);
                if (inicio == 0) return;
                if (inicio == 2) {
                    if (!repositorio.existe()) {
                        System.out.println("Nenhum save encontrado. Escolha Nova jornada.");
                        continue;
                    }
                    try {
                        DadosPartida dados = repositorio.carregar(batalha, leitor);
                        treinador = dados.getTreinador();
                        jornada = dados.getJornada();
                        System.out.println("Partida carregada! Bem-vindo de volta, " + treinador.getNome() + "!");
                        break;
                    } catch (IOException ex) {
                        System.out.println("Nao foi possivel carregar: " + ex.getMessage());
                    }
                } else {
                    if (repositorio.existe()) {
                        System.out.println("Existe uma partida salva. Criar uma nova jornada e salvar depois substituira esse arquivo.");
                        System.out.println("1 - Continuar criando | 0 - Voltar");
                        if (leitor.opcao(0, 1) == 0) continue;
                    }
                    String nome = leitor.lerNome();
                    System.out.println("Escolha seu Pokemon inicial:");
                    System.out.println("1 - Charmander (FOGO)\n2 - Squirtle (AGUA)\n3 - Bulbasaur (PLANTA)");
                    treinador = new Treinador(nome, FabricaPokemon.criar(leitor.opcao(1, 3)));
                    jornada = new Jornada(batalha, leitor);
                    System.out.println("Bem-vindo, " + treinador.getNome() + "! Conquiste 4 insignias e venca a Liga Pokemon!");
                    break;
                }
            }

            boolean jogando = true;
            while (jogando) {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1 - Explorar (Pokemon selvagem)");
                System.out.println("2 - Ver equipe");
                System.out.println("3 - Ver inventario");
                System.out.println("4 - Usar pocao fora da batalha");
                System.out.println("5 - Centro Pokemon");
                System.out.println("6 - Enfrentar treinadores");
                System.out.println("7 - Desafiar ginasios");
                System.out.println("8 - Desafiar Liga Pokemon");
                System.out.println("9 - Ver progresso");
                System.out.println("10 - Salvar partida");
                System.out.println("0 - Salvar e sair");
                int opcao = leitor.opcao(0, 10);
                if (opcao == 0) {
                    salvar(repositorio, treinador, jornada);
                    jogando = false;
                } else if (opcao == 10) {
                    salvar(repositorio, treinador, jornada);
                } else if (opcao == 2) {
                    treinador.mostrarEquipe();
                } else if (opcao == 3) {
                    treinador.getInventario().mostrar();
                } else if (opcao == 5) {
                    treinador.curarEquipe();
                    System.out.println("Centro Pokemon: sua equipe foi completamente recuperada!");
                } else if (opcao == 9) {
                    jornada.mostrarProgresso();
                } else if (!treinador.temPokemonDisponivel() && opcao != 4) {
                    System.out.println("Equipe desmaiada! Visite o Centro Pokemon (opcao 5).");
                } else if (opcao == 1) {
                    batalha.iniciar(treinador, FabricaPokemon.selvagem(sorteio));
                } else if (opcao == 4) {
                    treinador.mostrarEquipe();
                    System.out.println("Qual Pokemon deseja curar? (0 - cancelar)");
                    int indice = leitor.opcao(0, treinador.getEquipe().size());
                    if (indice != 0 && !treinador.getInventario().usarPocao(treinador.getEquipe().get(indice - 1))) {
                        System.out.println("Nao foi possivel usar a pocao.");
                    }
                } else if (opcao == 6) {
                    jornada.desafiarTreinadores(treinador);
                } else if (opcao == 7) {
                    jornada.desafiarGinasio(treinador);
                } else if (opcao == 8) {
                    jornada.desafiarLiga(treinador);
                }
            }
            System.out.println("Obrigado por jogar, " + treinador.getNome() + "!");
        }
    }

    private static void salvar(RepositorioPartida repositorio, Treinador treinador, Jornada jornada) {
        try {
            repositorio.salvar(treinador, jornada);
            System.out.println("Partida salva em " + Persistencia.ARQUIVO + "!");
        } catch (IOException ex) {
            System.out.println("ERRO ao salvar: " + ex.getMessage());
            System.out.println("Seu progresso nao foi gravado. Confira as permissoes da pasta.");
        }
    }
}
