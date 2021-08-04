package com.example.calorietracker.Model;

import java.util.Map;

public class Stats2 {

    String food_name,calories;
    String  time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Stats2(String food_name, String calories,String time) {
        this.food_name = food_name;
        this.calories = calories;
        this.time=time;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }



}
