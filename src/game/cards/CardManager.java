package game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardManager {

    List<Card> deck;
    List<Card> hand;
    List<Card> discard;

    public final static int handMaxSize = 6;

    public CardManager() {
        deck = new ArrayList<Card>();
        hand = new ArrayList<Card>();
        discard = new ArrayList<Card>();
    }

    public void initDeck(String deckName)  {
        CardParser parser = new CardParser();
        parser.loadDeckFile(this, deckName);
    }

    public List<Card> getDeck() {
        return deck;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getDiscard() {
        return discard;
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    public void shuffleDeck() {
        if (!deck.isEmpty()) {
            Collections.shuffle(deck);
        }
    }

    public void addCardToDeck(Card card) {
        deck.add(card);
    }

    public void removeCardFromDeck(Card card) {
        if (!deck.isEmpty()) {
            deck.remove(card);
        }
    }

    public int drawCards(int numCards) {
        int drawnCards = 0;
        while (!isDeckEmpty() && numCards > 0) {
            Card card = deck.remove(0);
            hand.add(card);
            drawnCards++;
            numCards--;
        }
        return drawnCards;
    }


    public int discardCards(int numCards) {
        int numDiscardedCards = 0;

        while(numDiscardedCards < numCards && !hand.isEmpty()) {
            discard.add(hand.remove(0));
            numDiscardedCards++;
        }

        return numDiscardedCards;
    }

    public void useCard(Card card) {
        hand.remove(card);
        discard.add(card);
    }

    public void reset() {
        deck.clear();
        hand.clear();
        discard.clear();
    }

    @Override
    public String toString() {
        return "CardManager{" +
                "deck=" + deck +
                ", hand=" + hand +
                ", discard=" + discard +
                ", handMaxCards=" + handMaxSize +
                '}';
    }
}
