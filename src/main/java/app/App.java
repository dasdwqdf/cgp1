package app;

import game.Game;

public class App {

    public static void main(String[] args) {
        // Jogo
        Game game = new Game("2D Card");
        game.start();

        // Simulações
//        BattleStatisticsService.runBattleSimulations(100);
    }
}