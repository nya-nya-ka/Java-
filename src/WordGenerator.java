import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordGenerator {
    private List<String> words;
    private List<String> usedWords;
    private Random random;

    public WordGenerator(String difficulty) {
        usedWords = new ArrayList<>();
        random = new Random();

        switch (difficulty) {
            case "Easy":
                words = new ArrayList<>(Arrays.asList(
                    "System.out.println(i);",
                    "while(i < 10){\n\tdo();\n}",
                    "public class Cat{\n\tdo();\n}",
                    "boolean flag;\nDate d;\n",
                    "float a = 1.234f;\nfloat b = 1;",
                    "String x = \"aa\" + \"bb\";",
                    "array = new double[5];",
                    "array = new int[]{1, 2, 3};",
                    "import java.io.*;",
                    "if(a == b){\n\tcontinue;\n}",
                    "random = new Random();",
                    "Bgm.play(\"music.wav\");",
                    "frame.setSize(800, 800);"
                ));
                break;
            case "Medium":
                words = new ArrayList<>(Arrays.asList(
                    "while(condition){\n\tdoSomething();\n}",
                    "public void method(){\n\t//code\n}",
                    "public static void main(String[] args){\n\t//code\n}",
                    "do{\n\tfunction();\n}while(a == b);",
                    "for(int num: array){\n\t//code\n}",
                    "Caluculator calc = new Caluculator;",
                    "for(int i = 0; i < 10; i++){\n\t//code\n}"
                ));
                break;
            case "Hard":
                words = new ArrayList<>(Arrays.asList(
                    "switch(value){\n\tcase 1: break;\n\tdefault: break;\n}",
                    "public static void main(String[] args){\n\t// code\n}",
                    "if(a == b){\n\treturn true;\n}else{\n\treturn false;\n}",
                    "for(int i = 0; i < 10; i++){\n\tSystem.out.println(i);\n}",
                    "for(int num: arr){\n\tSystem.out.println(num);\n}",
                    "for(int i = 0; i < 10; i++){\n\tSystem.out.println(i);\n}",
                    "try{\n\t//code\n}catch(Exception e){\n\te.printStackTrace();\n}"
                ));
                break;
        }

        // Shuffle the words list to add randomness
        Collections.shuffle(words);
    }

    public String generateWord() {
        if (words.isEmpty()) {
            return "No more words!";
        }

        String word = words.remove(0);
        usedWords.add(word);
        return word;
    }
}
