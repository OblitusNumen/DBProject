package oblitusnumen.dbproject;

import oblitusnumen.dbproject.db.DBManager;
import oblitusnumen.dbproject.db.models.Parameters;
import oblitusnumen.dbproject.ui.TableWindow;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    DBManager dbManager = new DBManager();
    List<TableWindow> tableMonitors = new ArrayList<>();
    Parameters currentParameters;
    Console console;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Main() throws SQLException {
        console = new Console(this);
    }

    public static void main(String[] args) throws SQLException {
        new Main().start();
    }

    private void start() {
        console.start();
    }

    public void showTable() {
        // TODO: 12/5/24
    }

    /**
     * z32
     */
    public void compute() {
        currentParameters = new Parameters();
        computeD1();
        computeD2();
        //z54
        currentParameters.D_1 = round_GOST(currentParameters.D_1);
        //z55
        currentParameters.D_2 = round_GOST(currentParameters.D_2);
        //z42
        currentParameters.vr = Math.PI * currentParameters.D_1 * currentParameters.n_1 / 60000;
        //z43
        spaceBetween();
        //z44
        System.out.println("Выберите метод определения длины ремня:");
        System.out.println("1. По межосевому расстоянию");
        System.out.println("2. Из условий сравнительной долговечности");
        int i;
        l:
        while (true) {
            i = console.nextInt();
            switch (i) {
                case 1 -> {
                    currentParameters.m_l = "По межосевому расстоянию";
                    currentParameters.L = 2 * currentParameters.a + Math.PI * (currentParameters.D_1 + currentParameters.D_2)
                            / 2 + Math.pow(currentParameters.D_2 - currentParameters.D_1, 2) / (4 * currentParameters.a);
                    break l;
                }
                case 2 -> {
                    currentParameters.m_l = "Из условий сравнительной долговечности";
                    currentParameters.i_max = currentParameters.speed.equals("Быстроходная") ? 50 : 5;
                    System.out.println("Введите частоту пробега ремня в секунду. Максимальное значение " + currentParameters.i_max);
                    currentParameters.i = console.nextDouble();
                    currentParameters.L_min = currentParameters.vr / currentParameters.i;
                    currentParameters.Lr = 2 * currentParameters.a + Math.PI * (currentParameters.D_1 + currentParameters.D_2)
                            / 2 + Math.pow(currentParameters.D_2 - currentParameters.D_1, 2) / (4 * currentParameters.a);
                    currentParameters.L_diff = currentParameters.L_min - currentParameters.Lr;
                    if (currentParameters.L_diff <= 0) currentParameters.L = currentParameters.Lr;
                    else {
                        //z84
                        System.out.println("Введите длину ремня. Минимальное значение " + currentParameters.L_min);
                        currentParameters.L = console.nextDouble();
                    }
                    //z6.11
                    currentParameters.D_cp = (currentParameters.D_1 + currentParameters.D_2) / 2;
                    currentParameters.lambda = currentParameters.L - Math.PI * currentParameters.D_cp;
                    currentParameters.delta = (currentParameters.D_1 - currentParameters.D_2) / 2;
                    currentParameters.a = (currentParameters.lambda + Math.pow(Math.pow(currentParameters.lambda, 2)
                            - 8 * Math.pow(currentParameters.delta, 2), 1. / 2)) / 4;
                    break l;
                }
            }
        }
        //z45
        currentParameters.sigma_1 = 180 - ((currentParameters.D_2 - currentParameters.D_1) / currentParameters.a) * 57;
        System.out.println(currentParameters);
        System.out.println("Хотите сохранить данные расчёта? (д/н)");
        l:while (true) {
            String s = console.nextString();
            switch (s) {
                case "д" -> {
                    saveParams();
                    break l;
                }
                case "н" -> {
                    break l;
                }
            }
        }
        currentParameters = null;
    }

    private void saveParams() {
        // TODO: 12/5/24
        System.out.println("Данные сохранены");
    }

    /**
     * z43
     */
    private void spaceBetween() {
        System.out.println("Выберите быстроходность передачи:");
        System.out.println("1. Быстроходная");
        System.out.println("2. Среднескоростная");
        int i;
        l:while (true) {
            i = console.nextInt();
            switch (i) {
                case 1 -> {
                    currentParameters.speed = "Быстроходная";
                    break l;
                }
                case 2 -> {
                    currentParameters.speed = "Среднескоростная";
                    break l;
                }
            }
        }
        currentParameters.a_min = (i == 1 ? 1.5 : 2) * (currentParameters.D_1 + currentParameters.D_2);
        //z75
        System.out.println("Введите межосевое расстояние. Минимальное значение " + currentParameters.a_min);
        currentParameters.a = console.nextDouble();
    }

    /**
     * z51
     */
    private void computeD1() {
        System.out.println("Введите частоту вращения меньшего шкива, мин^-1");
        currentParameters.n_1 = console.nextDouble();
        System.out.println("Выберите один из методов расчёта диаметра меньшего шкива:");
        System.out.println("1. По формуле М.А. Саверина");
        System.out.println("2. Исходя из ориентировочной скорости");
        System.out.println("3. На основании конструктивных соображений");
        System.out.println("4. При ограниченном сортаменте");
        l:while (true) {
            int i = console.nextInt();
            switch (i) {
                case 1 -> {
                    currentParameters.m_s = "По формуле М.А. Саверина";
                    formulaD_1();
                    break l;
                }
                case 2 -> {
                    currentParameters.m_s = "Исходя из ориентировочной скорости";
                    // TODO: 12/5/24
                    currentParameters.v = 5;// FIXME: 12/6/24 from table 1.1
                    currentParameters.D_1 = currentParameters.v * 60000 / (Math.PI * currentParameters.n_1);
                    break l;
                }
                case 3 -> {
                    currentParameters.m_s = "На основании конструктивных соображений";
                    System.out.println("Введите диаметр меньшего шкива");
                    currentParameters.D_1 = console.nextDouble();
                    break l;
                }
                case 4 -> {
                    currentParameters.m_s = "При ограниченном сортаменте";
                    // TODO: 12/5/24
                    currentParameters.D_1 = 500;// FIXME: 12/6/24 from table 1.2
                    break l;
                }
            }
        }
        z52();
    }

    /**
     * z53
     */
    private void computeD2() {
        System.out.println("Выберите вид передачи:");
        System.out.println("1. Повышающая");
        System.out.println("2. Понижающая");
        int i;
        l:
        while (true) {
            i = console.nextInt();
            switch (i) {
                case 1 -> {
                    currentParameters.type = "Повышающая";
                    break l;
                }
                case 2 -> {
                    currentParameters.type = "Понижающая";
                    break l;
                }
            }
        }
        System.out.println("Введите коэффициент скольжения ремня");
        currentParameters.xi = console.nextDouble();
        currentParameters.D_2 = currentParameters.D_1 * (i == 1 ? currentParameters.u / (1 - currentParameters.xi) : currentParameters.u * (1 - currentParameters.xi));
    }

    private double round_GOST(double d) {// TODO: 12/5/24 table 13.18
        return d;
    }

    private void z52() {
        System.out.println("Введите частоту вращения большего шкива, мин^-1");
        double n_2 = console.nextDouble();
        currentParameters.n_2 = n_2;
        currentParameters.u = currentParameters.n_1 / n_2;
    }

    /**
     * z61
     */
    private void formulaD_1() {
        System.out.println("Введите мощность, кВт");
        double N = console.nextDouble();
        currentParameters.N = N;
        currentParameters.D_1 = 120 * Math.pow(N * 1000 / currentParameters.n_1, 1./3);
    }
}