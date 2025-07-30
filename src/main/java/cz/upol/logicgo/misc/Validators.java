package cz.upol.logicgo.misc;

public class Validators {

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.matches("^[\\p{L}0-9_-]{3,20}$");
    }

    public static boolean isCountValid(String number, int min, int max) {
        try {
            int num = Integer.parseInt(number);
            return num > min && num < max;
        }catch (NumberFormatException e){
            return false;
        }
    }


}
