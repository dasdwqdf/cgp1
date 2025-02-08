package game.battle;

public class BattleStatistics {
    private int totalTurns;
    private int firstAiTotalPower;
    private int secondAiTotalPower;
    private int firstAiNuMovements;
    private int secondAiNuMovements;
    private int firstAiUnusedMana;
    private int secondAiUnusedMana;
    private String winner;

    public BattleStatistics(int totalTurns, int firstAiTotalPower, int secondAiTotalPower,
                            int firstAiNuMovements, int secondAiNuMovements,
                            int firstAiUnusedMana, int secondAiUnusedMana, String winner) {
        this.totalTurns = totalTurns;
        this.firstAiTotalPower = firstAiTotalPower;
        this.secondAiTotalPower = secondAiTotalPower;
        this.firstAiNuMovements = firstAiNuMovements;
        this.secondAiNuMovements = secondAiNuMovements;
        this.firstAiUnusedMana = firstAiUnusedMana;
        this.secondAiUnusedMana = secondAiUnusedMana;
        this.winner = winner;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public int getFirstAiTotalPower() {
        return firstAiTotalPower;
    }

    public int getSecondAiTotalPower() {
        return secondAiTotalPower;
    }

    public int getFirstAiNuMovements() {
        return firstAiNuMovements;
    }

    public int getSecondAiNuMovements() {
        return secondAiNuMovements;
    }

    public double getFirstAiAverageMovementsPerTurn() {
        return (double) firstAiNuMovements / totalTurns;
    }

    public double getSecondAiAverageMovementsPerTurn() {
        return (double) secondAiNuMovements / totalTurns;
    }

    public double getFirstAiAverageUnusedMana() {
        return (double) firstAiUnusedMana / totalTurns;
    }

    public double getSecondAiAverageUnusedMana() {
        return (double) secondAiUnusedMana / totalTurns;
    }

    public String getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "===== Resultados =====\n" +
                "- Turnos Jogados: " + totalTurns + "\n" +
                "======================\n" +
                "IA Focada na Maximização de Poder:\n" +
                "- Poder Total obtido: " + firstAiTotalPower + "\n" +
                "- Número de movimentos: " + firstAiNuMovements + "\n" +
                "- Média Movimentos por turno: " + getFirstAiAverageMovementsPerTurn() + "\n" +
                "- Mana Média Não Utilizada: " + getFirstAiAverageUnusedMana() + "\n" +
                "IA Focada na Conservação de Mana:\n" +
                "- Poder Total obtido: " + secondAiTotalPower + "\n" +
                "- Número de movimentos: " + secondAiNuMovements + "\n" +
                "- Média Movimentos por turno: " + getSecondAiAverageMovementsPerTurn() + "\n" +
                "- Mana Média Não Utilizada: " + getSecondAiAverageUnusedMana() + "\n" +
                "Vencedor: " + winner + "\n";
    }
}
