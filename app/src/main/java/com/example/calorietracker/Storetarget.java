package com.example.calorietracker;

import android.content.Context;
import android.content.SharedPreferences;

public class Storetarget {

    public int getTarget() {
        target=sharedPreferences.getInt("target",0);
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
        sharedPreferences.edit().putInt("target",target).commit();
    }

    private int target;

    Context context;
    SharedPreferences sharedPreferences;

    public Storetarget(Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences("target_details",Context.MODE_PRIVATE);
    }

    public void removeuser()
    {
        sharedPreferences.edit().clear().apply();
    }
}
