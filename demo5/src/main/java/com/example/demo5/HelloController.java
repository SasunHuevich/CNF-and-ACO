package com.example.demo5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class HelloController {

    String currentWorkingDirectory = System.getProperty("user.dir");
    File parameter_file = new File(currentWorkingDirectory + "/resources/txt.txt");
    File knf_file = new File(currentWorkingDirectory + "/resources/knf.txt");

    //лист скобок
    ArrayList<Trio <Integer, Integer, ArrayList<Integer>>> expression = new ArrayList<Trio<Integer, Integer, ArrayList<Integer>>>();

    //лист с вхождениями переменных
    ArrayList<Trio <Integer, Integer, ArrayList<Integer>>> variable = new ArrayList<Trio<Integer, Integer, ArrayList<Integer>>>();

    // Лист со всеми перемнными
    HashMap<Integer, Integer>  all_variables = new HashMap<>();
//    x1 ∨ x2 ∨ x4
//    <1, 3, <1, 2, 4>>
//
//    <1, 3, <1, 2, 3>>

    public void Reading(String s){
        int var = 0; // Порядковый номер выражения

        for(int i = 0 ; i < s.length(); i++){
            if(s.charAt(i) == '('){
                this.expression.add(new Trio<>(var, 0, new ArrayList<>()));
                ArrayList<Integer> list = new ArrayList<>();
                while(s.charAt(i) != ')'){
                    if(s.charAt(i) == '¬' || s.charAt(i) == '!' || s.charAt(i) == '-'){
                        i += 2;
                        StringBuffer buf = new StringBuffer();
                        while(s.charAt(i) >= '0' && s.charAt(i) <= '9'){
                            buf.append(s.charAt(i));
                            i++;
                        }
                        int i1 = Integer.parseInt(buf.toString()) * -1;
                        list.add(i1);

                        if(!this.all_variables.containsKey(i1 * -1)){
                            all_variables.put(i1 * -1, -1);
                        }
                        i--;

                        int j = 0;
                        int j1 = 0;

                        for(Trio T : variable){
                            if((int) T.getKey() == i1){
                                j1 = 1;
                                break;
                            }
                            j++;
                        }


                        if(j1 == 0){
                            this.variable.add(new Trio<>(i1, 1, new ArrayList<>()));
                            this.variable.get(j).getValue2().add(var);
                        }
                        else{
                            this.variable.get(j).setValue1(this.variable.get(j).getValue1() + 1);
                            this.variable.get(j).getValue2().add(var);
                        }
                    }
                    else if(s.charAt(i) == 'x' || s.charAt(i) == 'X'){
                        i += 1;
                        StringBuffer buf = new StringBuffer();
                        while(s.charAt(i) >= '0' && s.charAt(i) <= '9'){
                            buf.append(s.charAt(i));
                            i++;
                        }
                        int i1 = Integer.parseInt(buf.toString());
                        list.add(i1);

                        if(!this.all_variables.containsKey(i1)){
                            all_variables.put(i1, -1);
                        }

                        i--;

                        int j = 0;
                        int j1 = 0;

                        for(Trio T : variable){
                            if((int) T.getKey() == i1){
                                j1 = 1;
                                break;
                            }
                            j++;
                        }


                        if(j1 == 0){
                            this.variable.add(new Trio<>(i1, 1, new ArrayList<>()));
                            this.variable.get(j).getValue2().add(var);
                        }
                        else{
                            this.variable.get(j).setValue1(this.variable.get(j).getValue1() + 1);
                            this.variable.get(j).getValue2().add(var);
                        }
                    }
                    i++;
                }

                this.expression.get(var).setValue1(list.size());
                this.expression.get(var).setValue2(new ArrayList<>(list));
                var++;
            }
        }
    }


    public void sort_variable(){
        Collections.sort(this.variable, (p1, p2) -> p2.getValue1().compareTo(p1.getValue1()));
    }


    public void sort_expression(){
        Collections.sort(this.expression, (p1, p2) -> p2.getValue1().compareTo(p1.getValue1()));
    }


    // Добавить обработку строку вида x1 + -x1

    public static int phero_random_variable(HashMap<Integer, Double> Jik) {
        Random rand = new Random();
        int result = -1;

        // Вычисляем сумму всех феромонов
        double totalPheromone = 0.0;
        for (double pheromone : Jik.values()) {
            totalPheromone += pheromone;
        }

        // Генерируем случайное число в пределах totalPheromone
        double randValue = rand.nextDouble() * totalPheromone;
        double cumulative = 0.0;

        // Проходим по мапе и выбираем вершину
        for (Map.Entry<Integer, Double> entry : Jik.entrySet()) {
            cumulative += entry.getValue();
            if (cumulative >= randValue) {
                result = entry.getKey(); // Выбрали вершину
                break; // Выходим из цикла, чтобы не перезаписывать result
            }
        }

        System.out.println(result);
        return result;
    }



    public void ACO() throws FileNotFoundException {
        Scanner in = new Scanner(parameter_file);
        int var = Integer.parseInt(in.nextLine()); // Кол-во итераций
        double k_phero = Double.parseDouble(in.nextLine());
        double min_phero = Double.parseDouble(in.nextLine()); // должно быть больше нуля
        double mod_phero = Double.parseDouble(in.nextLine()); // изменение фермоона при пробеге муравья
        double minus_phero = Double.parseDouble(in.nextLine()); // изменение фермоона если муравей не прошел по переменной

        // Массив с распределением феромона
        HashMap<Integer, Double> phero = new HashMap<>();

        // Массив с числом на которое изменится феромон в каждой переменной после итерации
        HashMap<Integer, Double> phero_mod = new HashMap<>();

        // Массив с лучшим решением
        Pair<Integer, HashMap<Integer, Integer>> best = new Pair<>(this.all_variables.size() + 1, new HashMap<>());

        // Забиваем массивы некоторым начальным значением
        for(int i = 0; i < this.variable.size(); i++){
            phero.put(this.variable.get(i).getKey(), k_phero);
            phero_mod.put(this.variable.get(i).getKey(), 0.0);

        }
        int i1 = 0;
        // Цикл по кол-ву итераций
        for(int i = 0; i < var; i++){
            System.out.println("RUN: " + i);

            // Итерация для каждого отедльного муравьишки
            for(int j = 0; j < this.variable.size(); j++){

                // Массив текущего решения
                HashMap<Integer, Integer> cont = new HashMap<>(this.all_variables);

                // Переменные, возможные для посещения (Отрицательные значения тоже включены)
                HashMap<Integer, Double> Jik = new HashMap<>(phero);

                // Создаем копии изначальный листов для каждого муравьишки
                ArrayList<Trio <Integer, Integer, ArrayList<Integer>>> local_expression = new ArrayList<>(this.expression);


                //HashMap<Integer, ArrayList<Integer>> local_expression = new HashMap<>();
                ArrayList<Trio <Integer, Integer, ArrayList<Integer>>> local_variable = new ArrayList<>(this.variable);

                // Удаляем переменныую в который поместили муравьишку
                System.out.println("");
                System.out.println("NEW ANT");
                System.out.println("ant variable: " + this.variable.get(j).getKey());
                Jik.remove(this.variable.get(j).getKey());
                Jik.remove(this.variable.get(j).getKey() * -1);

                //записали значение переменной
                if(this.variable.get(j).getKey() < 0){
                    cont.put(this.variable.get(j).getKey() * -1, 0);
                }
                else{
                    cont.put(this.variable.get(j).getKey(), 1);
                }

                // Удаляем выражения содрежащие эту переменную
                int targetValue = this.variable.get(j).getKey();

                Iterator<Trio<Integer, Integer, ArrayList<Integer>>> iterator = local_expression.iterator();
                while (iterator.hasNext()) {
                    Trio<Integer, Integer, ArrayList<Integer>> trio = iterator.next();

                    // Проверяем, содержит ли список внутри Trio нужное значение
                    if (trio.getValue2().contains(targetValue)) {
                        iterator.remove(); // Удаляем элемент безопасно
                    }
                }

                System.out.println("LOCAL_EXPRESSION");
                for(Trio T : local_expression){
                    System.out.println(T.getKey() + " " + T.getValue1() + " " + T.getValue2());
                }
                boolean flag = false;
                while(!Jik.isEmpty() && flag != true){
                    targetValue = phero_random_variable(Jik);
                    System.out.println("ant variable: " + targetValue);
                    Jik.remove(targetValue);
                    Jik.remove(targetValue * -1);

                    //записали значение переменной
                    if(targetValue < 0){
                        cont.put(targetValue * -1, 0);
                    }
                    else{
                        cont.put(targetValue, 1);
                    }

                    Iterator<Trio<Integer, Integer, ArrayList<Integer>>> iterator1 = local_expression.iterator();
                    while (iterator1.hasNext()) {
                        Trio<Integer, Integer, ArrayList<Integer>> trio = iterator1.next();

                        // Проверяем, содержит ли список внутри Trio нужное значение
                        if (trio.getValue2().contains(targetValue)) {
                            iterator1.remove(); // Удаляем элемент безопасно
                        }
                    }


                    System.out.println("LOCAL_EXPRESSION");
                    for(Trio T : local_expression){
                        System.out.println(T.getKey() + " " + T.getValue1() + " " + T.getValue2());
                    }


                    if(local_expression.isEmpty()){
                        for (Map.Entry<Integer, Integer> entry : cont.entrySet()) {
                            System.out.println("var " + entry.getKey() + " = " + entry.getValue());
                        }
                        System.out.println("KNF = 1");
                        flag = true;

                        int sum = 0;

                        for(Integer key : cont.keySet()) {
                            Integer value = cont.get(key);
                            if(value != -1){
                                sum++;
                                if(value == 0) {
                                    phero.put(key * -1, phero.get(key * -1) + mod_phero);
                                }
                                else {
                                    phero.put(key, phero.get(key) + mod_phero);
                                }
                            }
                            else {
                                if(phero.containsKey(key)) {
                                    phero.put(key, phero.get(key) - minus_phero);
                                    if(phero.get(key) < min_phero) {
                                        phero.put(key, min_phero);
                                    }
                                }

                                if(phero.containsKey(key * -1)) {
                                    phero.put(key * -1, phero.get(key * -1) - minus_phero);
                                    if(phero.get(key * -1) < min_phero) {
                                        phero.put(key * -1, min_phero);
                                    }
                                }
                            }
                        }
                        if(best.getKey() > sum) {
                            best = new Pair<>(sum, new HashMap<>(cont));
                        }
                        HelloApplication.add(i1, sum);
                        i1++;
                    }
                }
                if(!flag) {
                    HelloApplication.add(i1, all_variables.size() + 1);
                    i1++;
                }
            }

        }
        HelloApplication.lineChart.getData().add(HelloApplication.series);
        if(best.getKey() != all_variables.size() + 1) {
            System.out.println("Best combination: ");
            System.out.println("count variables: " + best.getKey());
            System.out.println("Variables:");
            for (Map.Entry<Integer, Integer> entry : best.getValue().entrySet()) {
                if(entry.getValue() != -1){
                    System.out.println("x" + entry.getKey() + " = " + entry.getValue());
                }
                else{
                    System.out.println("x" + entry.getKey() + " = (1 || 0)");
                }

            }
        }
        else{
            System.out.println("Solution not found.");
        }

    }

    @FXML
    void RUN_BUTTON(ActionEvent event) throws IOException {
        HelloApplication.series.getData().clear();

        this.variable.clear();
        this.expression.clear();

        Scanner in = new Scanner(knf_file);
        String s = in.nextLine();
        Reading(s);
        sort_variable();
        sort_expression();

        /////////////////////////////////////////////////////////////////////////////////////////////
        System.out.println("EXPRESSION");
        for(Trio T : this.expression){
            System.out.println(T.getKey() + " " + T.getValue1() + " " + T.getValue2());
        }

        System.out.println("VARIABLE");
        for(Trio T : this.variable){
            System.out.println(T.getKey() + " " + T.getValue1() + " " + T.getValue2());
        }

        System.out.println("ALL_VARIABLES");
        for(Map.Entry<Integer, Integer> entry : this.all_variables.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        ACO();
    }




    @FXML
    void CLEAN_BUTTON(ActionEvent event) throws IOException {


    }


    @FXML
    void FILE_BUTTON(ActionEvent event) throws IOException {

    }



    @FXML
    private void initialize(){

    }
}
