package sudokuosman.model;

import java.util.HashMap;
import java.util.Map;

public class SudokuOption {
    public enum ColorPalettName{
        Blue,
        Green,
        Peach,
        Lavender,
        Red,
        Beige,
        Sanda
    }

    public enum DifficultyLevel{
        Easy,
        Medium,
        Difficult,
        Impossible
    }

    static public ColorPalettName colorPalett = ColorPalettName.Green;

    static public DifficultyLevel difficultyLevel = DifficultyLevel.Easy;

    static public boolean isDarkMode = false;

    static public Map<ColorPalettName, ColorPalett> colorPaletts;

    public static int getNbEmptyCell(){
        switch (difficultyLevel){
            case Easy -> { return 35; }
            case Medium -> { return 42; }
            case Difficult -> { return 50; }
            case Impossible -> { return 90; }
        }
        return 1;
    }

    static {
        colorPaletts = new HashMap<>();
        colorPaletts.put(ColorPalettName.Blue, new ColorPalett("D0E7F9", "A9C9EB", "91BED4", "6497B1", "375D81"));
        colorPaletts.put(ColorPalettName.Beige, new ColorPalett("FFEDE6", "F9CBB7", "DFAF94", "F4A87C", "BFA086"));
        colorPaletts.put(ColorPalettName.Green, new ColorPalett("D9F0E6", "B7D7C5", "A7D3B1", "81B29A", "3C6E47"));
        colorPaletts.put(ColorPalettName.Red, new ColorPalett("F8D7DA", "F1A2A6", "D94F4F", "B23A3A", "6E1E1E"));
        colorPaletts.put(ColorPalettName.Peach, new ColorPalett("FFEDE7", "F9CBBE", "F7D2C7", "F2B8A0", "A66C4F"));
        colorPaletts.put(ColorPalettName.Lavender, new ColorPalett("E9E6F7", "CAC6E3", "C4C1E0", "A7A1C1", "6D5D7A"));
        colorPaletts.put(ColorPalettName.Sanda, new ColorPalett("FAEAED", "FFC0CB", "D1A1A9", "A5747C", "46032A"));
    }

    static public Color getBGColor(){
        if (isDarkMode){
            return colorPaletts.get(colorPalett).color5;
        }
        return colorPaletts.get(colorPalett).color1;
    }

    static public Color getNumberColor(){
        if (isDarkMode){
            return colorPaletts.get(colorPalett).color1;
        }
        return colorPaletts.get(colorPalett).color5;
    }

    static public Color getSelectedColor(){
        if (isDarkMode){
            return colorPaletts.get(colorPalett).color2;
        }
        return colorPaletts.get(colorPalett).color4;
    }

    static public Color getSameNumberColor(){
        return colorPaletts.get(colorPalett).color3;
    }

    static public Color getselectedZoneColor(){
        if (isDarkMode){
            return colorPaletts.get(colorPalett).color4;
        }
        return colorPaletts.get(colorPalett).color2;
    }

    public static class Color{
        int r, g, b;
        Color(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
        }
        Color(int c){
            this(c, c, c);
        }
        Color(){
            this(0);
        }

        @Override
        public String toString() {
            return "rgb("+r+","+g+","+b+")";
        }

        public int getR() { return r; }

        public int getG() { return g; }

        public void setG(int g) { this.g = g; }

        public int getB() { return b; }

        public void setB(int b) { this.b = b; }

        public void setR(int r) { this.r = r; }
    }

    public static class ColorPalett{
        public Color color1;
        public Color color2;
        public Color color3;
        public Color color4;
        public Color color5;

        public static int[] hexToRGB(String hex) {
            // Supprimer le # si présent
            hex = hex.startsWith("#") ? hex.substring(1) : hex;

            if (hex.length() != 6) {
                throw new IllegalArgumentException("La couleur doit être au format #RRGGBB");
            }

            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            return new int[]{r, g, b};
        }

        ColorPalett(String c1, String c2, String c3, String c4, String c5){
            int[] rgb = hexToRGB(c1);
            color1 = new Color(rgb[0], rgb[1], rgb[2]);
            rgb = hexToRGB(c2);
            color2 = new Color(rgb[0], rgb[1], rgb[2]);
            rgb = hexToRGB(c3);
            color3 = new Color(rgb[0], rgb[1], rgb[2]);
            rgb = hexToRGB(c4);
            color4 = new Color(rgb[0], rgb[1], rgb[2]);
            rgb = hexToRGB(c5);
            color5 = new Color(rgb[0], rgb[1], rgb[2]);
        }
    }
}
