package com.example.plantscanner;

import java.util.ArrayList;
import java.util.HashMap;

public interface MyCallback {
    void onCallback(HashMap<String, Plant> map, ArrayList<String> names);
}
