package main;

import game.Game;
import utils.BattleStatisticsService;

public class Main {

    public static void main(String[] args) {
        // Jogo
        Game game = new Game("2D Card");
        game.start();

        // Simulações
//        BattleStatisticsService.runBattleSimulations(100);
    }
}