package com.example.demo5;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.lang.Runtime;
import java.io.IOException;
import java.util.Map;

public class HelloApplication extends Application {

    public static NumberAxis xAxis = new NumberAxis();
    public static NumberAxis yAxis = new NumberAxis();
    public static LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
    public static XYChart.Series<Number, Number> series = new XYChart.Series<>();
    public static XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

    @FXML
    private Button FILE;
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, (java.lang.String[]) null);

    }



    public void start(Stage loginStage) {

        try {
            AnchorPane loginNode = (AnchorPane) FXMLLoader.load(HelloApplication.class.getResource("hello-view.fxml"));

            lineChart.setCreateSymbols(false);

            // Создание графика

            loginNode.getChildren().add(lineChart);
            //loginNode.getChildren().add(FILE);
            xAxis.setLabel("Количество муравьев");
            yAxis.setLabel("Минимальное кол-во переменных");
            xAxis.setTickUnit(1); // Шаг по оси X (целые числа)
            yAxis.setTickUnit(1); // Шаг по оси Y (целые числа)
            // Настроим отображение значений на осях, чтобы они были целыми
            xAxis.setMinorTickCount(0); // Убираем мелкие деления
            yAxis.setMinorTickCount(0); // Убираем мелкие деления
            // Создание серии данных

            series.setName("ACO");
            //series2.getNode().setStyle("-fx-stroke: #00ff00;");
            // Установка ограничений для LineChart внутри AnchorPane
            loginNode.setTopAnchor(lineChart, 10.0);
            loginNode.setRightAnchor(lineChart, 10.0);
            loginNode.setBottomAnchor(lineChart, 50.0);
            loginNode.setLeftAnchor(lineChart, 10.0);

            //loginNode.setBottomAnchor(FILE, 10.0);

            Scene loginScene = new Scene(loginNode);
            loginStage.setTitle("ACO and CNF");
            loginStage.setScene(loginScene);
            loginStage.show();

        } catch (Exception ex) {
            System.out.println("Trouble viewing fxml file:" + ex);
            System.exit(-1);

        }
    }


    public static void add(int i1, int i2){
        series.getData().add(new XYChart.Data<>(i1, i2));

    }
}