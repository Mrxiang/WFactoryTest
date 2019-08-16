package com.waterworld.factorytest;

import android.app.Activity;
import android.content.Intent;

public abstract class FactoryActivity extends Activity{
      public abstract  void setResultBeforeFinish(int status);
}
