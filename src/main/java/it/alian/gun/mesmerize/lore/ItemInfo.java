package it.alian.gun.mesmerize.lore;

public class ItemInfo {

    String soulbound;

    double unbreakable;
    int levelCap;

    public int getLevelCap() {
        return levelCap;
    }

    public String getSoulbound() {
        return soulbound;
    }

    public double getUnbreakable() {
        return unbreakable;
    }

    public static ItemInfo empty() {
        return new ItemInfo();
    }
}
