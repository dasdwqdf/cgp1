package game.entity;

import game.card.Card;
import game.card.CardEffect;
import game.card.CardType;

import java.util.*;

public class AiEntity extends PlayerEntity {

    public enum AiEntityType {
        AI_TYPE_1,
        AI_TYPE_2
    }

    AiEntityType aiType;

    public AiEntity(String name, int hp, int mana, String deckName) {
        super(name, hp, mana, deckName);
        aiType = AiEntityType.AI_TYPE_1;
    }

    public AiEntity(String name, int hp, int mana, String deckName, AiEntityType aiType) {
        super(name, hp, mana, deckName);
        this.aiType = aiType;
    }

    @Override
    public Card selectBestCard() {
        Card bestCard = null;

        for (Card card : getCardManager().getHand()) {
            if (card.getCardType().equals(CardType.POWER)) {
                if (bestCard == null || card.getPower() > bestCard.getPower()) {
                    bestCard = card;
                }
            }
        }

        return bestCard;
    }

    public List<Card> selectMovementsAi() {
        List<Card> currentHand = getCardManager().getHand();

        // Mão vazia, não temos movimentos para realizar
        if (currentHand.isEmpty()) {
            return null;
        }

        // Criamos a lista de movimentos
        List<Card> movements = new ArrayList<>();

        // Separamos a mão da IA por powerCards e effectCards
        List<Card> powerCards = currentHand.stream()
                .filter(card -> card.getCardType().equals(CardType.POWER))
                .sorted(Comparator.comparingInt(Card::getPower).reversed()) // Ordena as cartas de Poder por Power decrescente
                .toList();

        List<Card> effectCards = new ArrayList<>(currentHand.stream()
                .filter(card -> card.getCardType().equals(CardType.EFFECT))
                .toList());

        // Separamos as cartas por efeito
        HashMap<CardEffect, List<Card>> hashMapEffectCards = new HashMap<>();
        // Inicializamos o HashMap a partir das cartas de feito disponíveis
        for (Card card : effectCards) {
            CardEffect cardEffect = card.getCardEffect();

            if (!hashMapEffectCards.containsKey(cardEffect)) {
                hashMapEffectCards.put(cardEffect, new ArrayList<>());
            }
            hashMapEffectCards.get(cardEffect).add(card);
        }

        // Atributos atuais do jogador
        int currentMana = getMana();
        int currentHp =  getHp();

        // Maximiza a Mana da IA
        List<Card> manaCards = hashMapEffectCards.get(CardEffect.MANA);
        if (manaCards != null) {
            Iterator<Card> iterator = manaCards.iterator();  // Cria um iterator para a lista
            while (currentMana < maxMana && iterator.hasNext()) {
                Card card = iterator.next();  // Obtém o próximo card
                movements.add(card);          // Adiciona o card aos movimentos
                iterator.remove();            // Remove o card da lista usando o iterator
                currentMana += card.getEffectArg();  // Atualiza a mana com o efeito do card
            }
        }

        // Prioridade para redraw de cartas caso a mão esteja praticamente vazia
        List<Card> redrawCards = hashMapEffectCards.get(CardEffect.REDRAW);

        if (redrawCards != null && cardManager.getDeck().size() <3 && cardManager.getDeck().size() > 6) {
            for (Card card : redrawCards) {
                movements.add(card);
                return movements;
            }
        }

        // Dá prioridade para a compra de cartas caso sua mão esteja com poucas opções
        List<Card> drawCards = hashMapEffectCards.get(CardEffect.DRAW);
        if (drawCards != null && cardManager.getHand().size() < 5) {
            for (Card card : drawCards) {
                movements.add(card);
                return movements;
            }
        }

        // Dá prioridade para a recuperação de vida caso a vida esteja baixa
        List<Card> healCards = hashMapEffectCards.get(CardEffect.HEAL);
        if (healCards != null && currentHp < 3) {
            healCards.sort(Comparator.comparingInt(Card::getEffectArg).reversed()); // Ordenamos as cartas de HEAL por potência
            Iterator<Card> iterator = healCards.iterator();

            while (currentHp < 3 && iterator.hasNext()) {
                Card card = iterator.next(); // Obtém a próxima card
                if (card.getManaCost() <= currentMana) {
                    movements.add(card);
                    currentMana -= card.getManaCost();
                    currentHp += card.getEffectArg();
                }
            }
        }

        switch (aiType) {
            case AI_TYPE_1 -> selectMovementsAiType1(movements, powerCards, hashMapEffectCards, currentMana);
            case AI_TYPE_2 -> selectMovementsAiType2(movements, powerCards, hashMapEffectCards, currentMana);
        }

        return movements;
    }

    // Algoritmo abordagem 1, sempre maximizando buscando maximizar o poder da carta em campo
    public void selectMovementsAiType1(List<Card> movements, List<Card> powerCards, HashMap<CardEffect, List<Card>> hashMapEffectCards, int currentMana) {
        // Verificamos se existe alguma carta de poder em campo
        if (fieldCard == null) {
            // Adicionamos a carta de poder mais forte disponível que podemos utilizar com a mana atual
            for (Card powerCard : powerCards) {
                if (powerCard.getManaCost() <= currentMana) {
                    movements.add(powerCard);
                    currentMana -= powerCard.getManaCost(); // Reduzimos a mana disponível
                    break;
                }
            }

            if (fieldCard == null) {return;}
        }

        // Utilizamos o aprimoramento mais poderoso buscando maximizar o poder da carta em campo
        List<Card> powerUpCards = hashMapEffectCards.get(CardEffect.BUFF);
        if (powerUpCards != null) {
            powerUpCards.sort(Comparator.comparingInt(Card::getEffectArg).reversed());
            for (Card card : powerUpCards) {
                if (card.getManaCost() <= currentMana) {
                    movements.add(card);
                    currentMana -= card.getManaCost();
                }
            }
        }
    }

    // Algoritmo abordagem 1, buscando manter mana sempre disponível
    public void selectMovementsAiType2(List<Card> movements, List<Card> powerCards, HashMap<CardEffect, List<Card>> hashMapEffectCards, int currentMana) {

        // Verificamos se existe alguma carta de poder em campo
        if (fieldCard == null) {

            if (powerCards != null && !powerCards.isEmpty()) {
                List<Card> powerCardsCopy = new ArrayList<>(powerCards);

                // Ordenamos as cartas de poder por custo de mana
                powerCardsCopy.sort(Comparator.comparingInt(Card::getManaCost));

                // Adicionamos aos movimentos a carta com menor custo de mana, desde que o bot consiga manter um nível de mana constante
                for (Card powerCard : powerCardsCopy) {
                    if (powerCard.getManaCost() <= currentMana) {
                        movements.add(powerCard);
                        currentMana -= powerCard.getManaCost(); // Reduzimos a mana disponível
                        break;
                    }
                }
            }

            if (fieldCard == null) {return;}
        }

        // A mesma ideia é utilizada para o aprimoramento
        List<Card> powerUpCards = hashMapEffectCards.get(CardEffect.BUFF);

        if (powerUpCards != null && !powerUpCards.isEmpty()) {
            List<Card> powerUpCardsCopy = new ArrayList<>(powerUpCards);
            powerUpCardsCopy.sort(Comparator.comparingInt(Card::getManaCost));
            for (Card card : powerUpCardsCopy) {
                if (card.getManaCost() <= currentMana) {
                    movements.add(card);
                    currentMana -= card.getManaCost();
                }
            }
        }

    }

    @Override
    public Card selectBestDiscardCard() {

        Card bestDiscardCard = cardManager.getHand().get(0);

        for (Card card : cardManager.getHand()) {
            if (card.getManaCost() > bestDiscardCard.getManaCost()) {
                bestDiscardCard = card;
            } else if (card.getManaCost() == bestDiscardCard.getManaCost()) {
                bestDiscardCard = card.getCardType().equals(CardType.EFFECT) ? card : bestDiscardCard;
            }
        }

        return bestDiscardCard;
    }

    public Card getRandomCard() {
        int randomCardIndex = (int) (Math.random() * getCardManager().getHand().size());
        return getCardManager().getHand().get(randomCardIndex);
    }

    @Override
    public String toString() {
        return "AiEntity{" +
                "aiType=" + aiType +
                ", name='" + name + '\'' +
                ", hp=" + hp +
                ", mana=" + mana +
                ", hand=" + cardManager.getHand() +
                ", deckSize=" + cardManager.getHand().size()+
                ", fieldCard=" + fieldCard +
                ", sprite=" + sprite +
                '}';
    }
}
