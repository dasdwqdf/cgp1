package utils;

import game.battle.Battle;
import game.battle.BattleStatistics;

import java.util.ArrayList;
import java.util.List;

public class BattleStatisticsService {
    public static void runBattleSimulations(int numberOfBattles) {
        // Listas para armazenar as estatísticas de cada batalha
        List<BattleStatistics> battleStatsList = new ArrayList<>();

        for (int i = 0; i < numberOfBattles; i++) {
            Battle battle = new Battle(true);
            BattleStatistics stats =  battle.startAiBattle();
            battleStatsList.add(stats);

            System.out.println("BATALHA " + String.valueOf(i + 1) + ": " + stats);
            System.out.println(stats);
        }

        // Calculando as médias
        double totalTurns = 0;
        double totalFirstAiPower = 0;
        double totalSecondAiPower = 0;
        double totalFirstAiMovements = 0;
        double totalSecondAiMovements = 0;
        double totalFirstAiUnusedMana = 0;
        double totalSecondAiUnusedMana = 0;
        double totalFirstAiAverageMovementsPerTurn = 0;
        double totalSecondAiAverageMovementsPerTurn = 0;
        int firstAiWins = 0;
        int secondAiWins = 0;

        for (BattleStatistics stats : battleStatsList) {
            totalTurns += stats.getTotalTurns();
            totalFirstAiPower += stats.getFirstAiTotalPower();
            totalSecondAiPower += stats.getSecondAiTotalPower();
            totalFirstAiMovements += stats.getFirstAiNuMovements();
            totalSecondAiMovements += stats.getSecondAiNuMovements();
            totalFirstAiUnusedMana += stats.getFirstAiAverageUnusedMana();
            totalSecondAiUnusedMana += stats.getSecondAiAverageUnusedMana();
            totalFirstAiAverageMovementsPerTurn += stats.getFirstAiAverageMovementsPerTurn();
            totalSecondAiAverageMovementsPerTurn += stats.getSecondAiAverageMovementsPerTurn();

            // Contando as vitórias de cada IA
            if (stats.getWinner().equals("IA Focada na Maximização de Poder")) {
                firstAiWins++;
            } else {
                secondAiWins++;
            }
        }

        // Calculando as médias
        double averageTurns = totalTurns / numberOfBattles;
        double averageFirstAiPower = totalFirstAiPower / numberOfBattles;
        double averageSecondAiPower = totalSecondAiPower / numberOfBattles;
        double averageFirstAiMovements = totalFirstAiMovements / numberOfBattles;
        double averageSecondAiMovements = totalSecondAiMovements / numberOfBattles;
        double averageFirstAiUnusedMana = totalFirstAiUnusedMana / numberOfBattles;
        double averageSecondAiUnusedMana = totalSecondAiUnusedMana / numberOfBattles;

        // Exibindo os resultados médios
        System.out.println("===== Resultados Médios =====");
        System.out.println("- Turnos Médios: " + averageTurns);
        System.out.println("- Poder Médio da IA Focada na Maximização de Poder: " + averageFirstAiPower);
        System.out.println("- Poder Médio da IA Focada na Conservação de Mana: " + averageSecondAiPower);
        System.out.println("- Movimentos Médios da IA Focada na Maximização de Poder: " + averageFirstAiMovements);
        System.out.println("- Movimentos Médios da IA Focada na Conservação de Mana: " + averageSecondAiMovements);
        System.out.println("- Mana Média Não Utilizada da IA Focada na Maximização de Poder: " + averageFirstAiUnusedMana);
        System.out.println("- Mana Média Não Utilizada da IA Focada na Conservação de Mana: " + averageSecondAiUnusedMana);
        System.out.println("- Vitórias da IA Focada na Maximização de Poder: " + firstAiWins);
        System.out.println("- Vitórias da IA Focada na Conservação de Mana: " + secondAiWins);
    }
}
