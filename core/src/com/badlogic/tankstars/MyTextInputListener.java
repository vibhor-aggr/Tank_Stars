package com.badlogic.tankstars;

import com.badlogic.gdx.Input.TextInputListener;

public class MyTextInputListener implements TextInputListener {
  private final TankStars ts;

  public MyTextInputListener(final TankStars ts) {
    this.ts = ts;
  }

  @Override
  public void input (String text) {
    ts.setInputText(text);
  }

  @Override
  public void canceled () {
    ts.setInputText("");
  }
}
