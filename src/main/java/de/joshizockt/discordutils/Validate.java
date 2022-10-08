package de.joshizockt.discordutils;

public class Validate {

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean notNull(Object object) {
        return notNull(object, "Object cannot be null!");
    }

    public static boolean notNull(Object object, String message) {
        if(object == null) {
            throw new NullPointerException(message);
        }
        return true;
    }

}
