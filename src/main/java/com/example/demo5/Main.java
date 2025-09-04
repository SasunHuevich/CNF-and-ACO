package com.example.demo5;

import javafx.application.Platform;

public class Main {
    public static void main(String[] args) {
        initializeJavaFXToolkit();
        HelloApplication.main(args);
    }

    private static void initializeJavaFXToolkit() {
        Platform.startup(() -> {
            // Пустой блок, здесь можно добавить дополнительные действия при инициализации Toolkit
        });
    }
}