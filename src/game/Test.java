package game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {

  public static void main(String[] args) {
    String tempLanguage = "de";
    String language="en";
          try {
            InputStream inp = ClassLoader.getSystemClassLoader().getResourceAsStream("res/localization/language_"+tempLanguage+".properties");
            BufferedReader rd = new BufferedReader(new InputStreamReader(inp));
            language = tempLanguage; // if file exists it reaches this line
          } catch (Exception ex){ /* if file does not exist keep it default */ }
  System.out.println(language);
  }
}