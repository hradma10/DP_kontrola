package cz.upol.logicgo.misc.enums;

public enum PreviewType {
    SOLVED("Vyřešené"), UNSOLVED("Nevyřešené"), BOTH("Obě"), CURRENT("Současný stav");


    PreviewType(String name) {
        this.name = name;
    }

    final String name;

    public static PreviewType getInstanceByDescription(String description) {
        for (PreviewType previewType : PreviewType.values()){
            if (previewType.getName().equals(description)){
                return previewType;
            }
        }
        return null;
    }


    public String getName(){
        return name;
    }

}
