import java.util.Random;

public class Batalha implements Combate {
    private final Leitor leitor;
    private final Random sorteio;
    private final CalculadoraDano calculadoraDano;
    private final RegraCaptura regraCaptura;
    private final RecompensaExperiencia recompensa;

    public Batalha(Leitor leitor, Random sorteio) {
        this(leitor, sorteio, new DanoPorTipo(), new CapturaPorVida(), new RecompensaExperiencia());
    }

    /** Injecao de dependencias: regras substituiveis sem editar Batalha (DIP/OCP). */
    public Batalha(Leitor leitor, Random sorteio, CalculadoraDano calculadoraDano,
                   RegraCaptura regraCaptura, RecompensaExperiencia recompensa) {
        this.leitor = java.util.Objects.requireNonNull(leitor);
        this.sorteio = java.util.Objects.requireNonNull(sorteio);
        this.calculadoraDano = java.util.Objects.requireNonNull(calculadoraDano);
        this.regraCaptura = java.util.Objects.requireNonNull(regraCaptura);
        this.recompensa = java.util.Objects.requireNonNull(recompensa);
    }

    public ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario) {
        return iniciar(treinador, adversario, null, true);
    }

    // Um nome de rival indica batalha de treinador: captura e fuga ficam bloqueadas.
    public ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario, String rival) {
        return iniciar(treinador, adversario, rival, true);
    }

    public ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario, String rival, boolean podeCancelarInicio) {
        boolean selvagem = rival == null;
        System.out.println("\n===================================");
        System.out.println(selvagem ? "     UM POKEMON SELVAGEM APARECEU!" : "     DESAFIO DO TREINADOR " + rival + "!");
        System.out.println("===================================");
        System.out.printf("Adversario: %s | Tipo: %s | Nivel: %d | HP: %d/%d%n",
                adversario.getNome(), adversario.getTipo(), adversario.getNivel(), adversario.getVidaAtual(), adversario.getVidaMaxima());
        System.out.println("Escolha quem vai enfrentar esse Pokemon.");

        Pokemon jogador = escolherPokemon(treinador, null, podeCancelarInicio, false);
        if (jogador == null) {
            System.out.println("Voce voltou ao menu sem iniciar a batalha.");
            return ResultadoBatalha.FUGA;
        }
        System.out.println("\nVai, " + jogador.getNome() + "! A batalha comecou!");

        while (true) {
            if (adversario.estaDerrotado()) {
                System.out.println("Vitoria! " + adversario.getNome() + " desmaiou.");
                int xp = recompensa.vitoria(adversario);
                jogador.ganharExperiencia(xp);
                return ResultadoBatalha.VITORIA;
            }
            if (jogador.estaDerrotado()) {
                System.out.println(jogador.getNome() + " desmaiou!");
                if (!treinador.temPokemonDisponivel()) {
                    System.out.println("Sua equipe inteira desmaiou. Visite o Centro Pokemon!");
                    return ResultadoBatalha.DERROTA;
                }
                System.out.println("Escolha outro Pokemon para continuar!");
                jogador = escolherPokemon(treinador, null, false, false);
                System.out.println("Vai, " + jogador.getNome() + "!");
                continue;
            }

            System.out.printf("\nSEU %s: %d/%d HP  |  RIVAL %s: %d/%d HP%n",
                    jogador.getNome(), jogador.getVidaAtual(), jogador.getVidaMaxima(),
                    adversario.getNome(), adversario.getVidaAtual(), adversario.getVidaMaxima());
            System.out.println("1 - Atacar");
            System.out.println("2 - Usar pocao no Pokemon ativo");
            System.out.println(selvagem ? "3 - Lancar Pokebola" : "3 - Pokebola (indisponivel contra treinadores)");
            System.out.println("4 - Trocar Pokemon");
            System.out.println(selvagem ? "5 - Fugir" : "5 - Fugir (indisponivel contra treinadores)");
            int acao = leitor.opcao(1, 5);
            boolean consomeTurno = false;
            if (acao == 1) {
                Ataque[] ataques = jogador.getAtaques();
                System.out.println("\nEscolha o ataque de " + jogador.getNome() + ":");
                for (int i = 0; i < ataques.length; i++) {
                    System.out.printf("%d - %s (%s, poder %d)%n", i + 1,
                            ataques[i].getNome(), ataques[i].getTipo(), ataques[i].getPoder());
                }
                System.out.println("0 - Voltar ao menu de batalha");
                int ataqueEscolhido = leitor.opcao(0, ataques.length);
                if (ataqueEscolhido == 0) continue;
                executarTurno(jogador, adversario, ataques[ataqueEscolhido - 1]);
                consomeTurno = true;
            } else if (acao == 2) {
                if (!treinador.getInventario().usarPocao(jogador)) {
                    System.out.println("Sem pocoes ou este Pokemon nao pode ser curado agora.");
                } else consomeTurno = true;
            } else if (acao == 3) {
                if (!selvagem) {
                    System.out.println("Nao e possivel capturar Pokemon de outros treinadores!");
                } else if (treinador.equipeCheia()) {
                    System.out.println("Equipe cheia! Limite de 6 Pokemon.");
                } else if (!treinador.getInventario().usarPokebola()) {
                    System.out.println("Voce nao tem Pokebolas.");
                } else {
                    consomeTurno = true;
                    double chance = regraCaptura.chance(adversario);
                    if (sorteio.nextDouble() < chance && treinador.adicionarPokemon(adversario)) {
                        System.out.println("Capturado! " + adversario.getNome() + " entrou na sua equipe!");
                        // Captura concede metade do XP normal, arredondando para baixo.
                        int xpCaptura = recompensa.captura(adversario);
                        System.out.println("Bonus de captura: 50% do XP da vitoria!");
                        jogador.ganharExperiencia(xpCaptura);
                        return ResultadoBatalha.CAPTURA;
                    }
                    System.out.println("Ah, nao! O Pokemon escapou da Pokebola!");
                }
            } else if (acao == 4) {
                Pokemon novo = escolherPokemon(treinador, jogador, true, true);
                if (novo == null) {
                    System.out.println("Troca cancelada. " + jogador.getNome() + " continua lutando.");
                } else {
                    System.out.println("Volte, " + jogador.getNome() + "! Vai, " + novo.getNome() + "!");
                    jogador = novo;
                    consomeTurno = true;
                }
            } else {
                if (selvagem) {
                    System.out.println("Voce fugiu da batalha!");
                    return ResultadoBatalha.FUGA;
                }
                System.out.println("Voce nao pode fugir de uma batalha de treinador!");
            }

            if (adversario.estaDerrotado()) continue;
            if (consomeTurno) {
                Ataque[] ataques = adversario.getAtaques();
                executarTurno(adversario, jogador, ataques[sorteio.nextInt(ataques.length)]);
            }
        }
    }

    // Permite cancelar na escolha inicial ou durante uma troca voluntaria.
    // Quando o Pokemon desmaiou, a escolha de um substituto e obrigatoria.
    private Pokemon escolherPokemon(Treinador treinador, Pokemon atual,
                                    boolean podeCancelar, boolean troca) {
        while (true) {
            System.out.println("\n=== ESCOLHA SEU POKEMON ===");
            treinador.mostrarEquipe();
            System.out.println("Digite o NUMERO do Pokemon que deseja usar:");
            if (podeCancelar) {
                System.out.println("0 - " + (troca ? "Cancelar troca" : "Voltar ao menu principal"));
            }
            int indice = leitor.opcao(podeCancelar ? 0 : 1, treinador.getEquipe().size());
            if (indice == 0) return null;
            Pokemon escolhido = treinador.getEquipe().get(indice - 1);
            if (escolhido.estaDerrotado()) {
                System.out.println("Esse Pokemon esta desmaiado e nao pode lutar. Escolha outro.");
            } else if (escolhido == atual) {
                System.out.println("Esse Pokemon ja esta em batalha. Selecione outro ou cancele.");
            } else {
                System.out.println("Selecionado: " + escolhido.getNome() + " (Nv." + escolhido.getNivel() + ", " +
                        escolhido.getVidaAtual() + "/" + escolhido.getVidaMaxima() + " HP)");
                return escolhido;
            }
        }
    }

    private void executarTurno(Pokemon atacante, Pokemon defensor, Ataque ataque) {
        int dano = defensor.receberDano(calculadoraDano.calcular(atacante, defensor, ataque));
        double efeito = ataque.getTipo().multiplicadorContra(defensor.getTipo());
        System.out.printf("%s usou %s e causou %d de dano!%n", atacante.getNome(), ataque.getNome(), dano);
        if (efeito > 1) System.out.println("Foi super efetivo!");
        if (efeito < 1) System.out.println("Nao foi muito efetivo...");
    }
}
