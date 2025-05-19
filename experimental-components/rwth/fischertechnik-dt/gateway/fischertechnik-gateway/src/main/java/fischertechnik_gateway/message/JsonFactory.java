package fischertechnik_gateway.message;

import de.monticore.symboltable.serialization.json.JsonNumber;

public class JsonFactory {
  public static JsonNumber createNumber(float value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }

  public static JsonNumber createNumber(double value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }

  public static JsonNumber createNumber(int value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }

  public static JsonNumber createNumber(short value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }

  public static JsonNumber createNumber(byte value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }

  public static JsonNumber createNumber(long value){
    JsonNumber res = new JsonNumber("0");
    res.setNumber(value);
    return res;
  }
}
