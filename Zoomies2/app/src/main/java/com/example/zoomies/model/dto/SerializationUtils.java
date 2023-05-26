package com.example.zoomies.model.dto;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class SerializationUtils {

    public static List<GeneralDTO> castTransfer(String message) {
        GeneralDTO[] arr = new Gson().fromJson(message, GeneralDTO[].class);
        return Arrays.asList(arr);
    }
}
