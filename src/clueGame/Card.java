
package clueGame;

import java.util.Objects;

public class Card {

    private String cardName;
    private CardType cardType;

    public Card(String cardName, CardType cardType) {
        this.cardName = cardName;
        this.cardType = cardType;
    }

    public String getCardName() {
        return cardName;
    }

    public CardType getCardType() {
        return cardType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Card card = (Card) other;

        return cardName.equals(card.cardName)
                && cardType == card.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName, cardType);
    }
}
