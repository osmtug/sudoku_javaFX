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
        Sanda,
        SandYellow,
        GreenBlue,
        Terracotta,
        ApricotOrange,
        PurpleLila,
        Champagne,
        PinkGrey,
        MossyGreen,
        CoralPink,
        NightBlue,
        GrayPurple,
        Turquoise
    }

    public enum DifficultyLevel{
        TesteMode,
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
            case TesteMode -> { return 5; }
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
        colorPaletts.put(ColorPalettName.SandYellow, new ColorPalett("FFF9E5", "FFF0B3", "F7D774", "EAC264", "9B835C"));
        colorPaletts.put(ColorPalettName.GreenBlue, new ColorPalett("E5F9F7", "B6E2DF", "88CCC6", "55AFA7", "2D5E5E"));
        colorPaletts.put(ColorPalettName.Terracotta, new ColorPalett("FAEFE5", "F3D6C2", "D9A88B", "B87C61", "7B4A3B"));
        colorPaletts.put(ColorPalettName.ApricotOrange, new ColorPalett("FFF4E5", "FFE0B3", "FFC792", "E89A5F", "A15C2E"));
        colorPaletts.put(ColorPalettName.PurpleLila, new ColorPalett("F5EDF8", "D9C6E0", "B49DC8", "8D77A3", "5E4166"));
        colorPaletts.put(ColorPalettName.Champagne, new ColorPalett("FFF9F0", "FCE6C9", "F7D7A8", "D6B27B", "9E8457"));
        colorPaletts.put(ColorPalettName.PinkGrey, new ColorPalett("F5F1F0", "E0D2CF", "C3B1AD", "A78F89", "6D5A58"));
        colorPaletts.put(ColorPalettName.MossyGreen, new ColorPalett("E6F8F1", "C3E7DA", "9CD4C2", "6BB49E", "3E5E53"));
        colorPaletts.put(ColorPalettName.CoralPink, new ColorPalett("FFF3F2", "F9D2CD", "F1A89D", "D77D6F", "924741"));
        colorPaletts.put(ColorPalettName.NightBlue, new ColorPalett("EAF3FA", "C3DDEE", "97C0DD", "5A8FAF", "304B66"));
        colorPaletts.put(ColorPalettName.GrayPurple, new ColorPalett("F4F0F6", "D8CEDD", "B9A9C3", "9B89A5", "5C4E5F"));
        colorPaletts.put(ColorPalettName.Turquoise, new ColorPalett("E6FAF8", "B7EFEA", "80DDD5", "4FC2B9", "1E5D5A"));
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

        @Override
        public String toString() {
            return "rgb("+r+","+g+","+b+")";
        }

        public int getR() { return r; }

        public int getG() { return g; }

        public int getB() { return b; }
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
