package com.mukireus.muki_celil_workshop;

public class Message {

  private String mesajText;
  private String gonderici;

  public Message() {
  }

  public Message(String mesajText, String gonderici) {
    this.mesajText = mesajText;
    this.gonderici = gonderici;

  }

  public String getMesajText() {
    return mesajText;
  }

  public void setMesajText(String mesajText) {
    this.mesajText = mesajText;
  }

  public String getGonderici() {
    return gonderici;
  }

  public void setGonderici(String gonderici) {
    this.gonderici = gonderici;
  }

}
