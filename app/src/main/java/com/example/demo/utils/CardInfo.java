package com.example.demo.utils;



public class CardInfo {

    private String name;

    private String desc;

    private int imageId;

    private CardType cardType;

    public CardInfo(CardType cardType, String name, String desc, int imageId) {
        this.cardType = cardType;
        this.name = name;
        this.desc = desc;
        this.imageId = imageId;
    }

    public CardInfo(String name, String desc, int imageId) {
        this.cardType = CardType.CARD_TYPE_UNKNOWN;
        this.name = name;
        this.desc = desc;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getImageId() {
        return imageId;
    }

    public CardType getCardType() {
        return cardType;
    }
}
