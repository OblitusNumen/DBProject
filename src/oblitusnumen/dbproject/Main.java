package oblitusnumen.dbproject;

import oblitusnumen.dbproject.db.DBManager;
import oblitusnumen.dbproject.db.models.staticmodels.Gost;
import oblitusnumen.dbproject.db.models.CalculationParameters;
import oblitusnumen.dbproject.db.models.Parameters;
import oblitusnumen.dbproject.ui.TableWindow;

import javax.swing.*;
import java.util.*;

public class Main {
    DBManager dbManager = new DBManager();
    Map<String, TableWindow<?>> tableMonitors = new HashMap<>();
    CalculationParameters currentCalculationParameters;
    Console console;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Main() throws Exception {
        console = new Console(this);
    }

    public static void main(String[] args) throws Exception {
        new Main().start();
    }

    private void start() {
        console.start();
        for (TableWindow<?> tableMonitor : tableMonitors.values().toArray(new TableWindow[0])) {
            tableMonitor.dispose();
        }
        System.exit(0);
    }

    public void closeMonitor(String table) {
        tableMonitors.remove(table);
    }

    public void showTable() {
        System.out.println("Выберите таблицу для просмотра:");
        String[] tables = dbManager.tables().toArray(new String[0]);
        for (int i = 0; i < tables.length; i++) {
            String table = tables[i];
            System.out.println((i + 1) + ". " + table);
        }
        int i;
        while (true) {
            i = console.nextInt();
            if (i <= tables.length && i >= 1) {
                String table = tables[i - 1];
                if (tableMonitors.containsKey(table)) {
                    tableMonitors.get(table).toTop();
                } else {
                    tableMonitors.put(table, new TableWindow(this, dbManager, table));
                }
                break;
            }
            System.out.println("Таблица не найдена. Попробуйте ещё раз.");
        }
    }

    /**
     * z32
     */
    public void compute() {
        currentCalculationParameters = new CalculationParameters();
        computeD1();
        computeD2();
        //z54
        currentCalculationParameters.D_1 = round_GOST(currentCalculationParameters.D_1_r);
        //z55
        currentCalculationParameters.D_2 = round_GOST(currentCalculationParameters.D_2_r);
        //z42
        currentCalculationParameters.vr = Math.PI * currentCalculationParameters.D_1 * currentCalculationParameters.n_1 / 60000;
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
                    currentCalculationParameters.m_l = "По межосевому расстоянию";
                    currentCalculationParameters.L = 2 * currentCalculationParameters.a + Math.PI * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2)
                            / 2 + Math.pow(currentCalculationParameters.D_2 - currentCalculationParameters.D_1, 2) / (4 * currentCalculationParameters.a);
                    break l;
                }
                case 2 -> {
                    currentCalculationParameters.m_l = "Из условий сравнительной долговечности";
                    currentCalculationParameters.i_max = currentCalculationParameters.speed.equals("Быстроходная") ? 50 : 5;
                    System.out.println("Введите частоту пробега ремня в секунду. Максимальное значение " + currentCalculationParameters.i_max);
                    currentCalculationParameters.i = console.nextDouble();
                    currentCalculationParameters.L_min = currentCalculationParameters.vr / currentCalculationParameters.i;
                    currentCalculationParameters.Lr = 2 * currentCalculationParameters.a + Math.PI * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2)
                            / 2 + Math.pow(currentCalculationParameters.D_2 - currentCalculationParameters.D_1, 2) / (4 * currentCalculationParameters.a);
                    currentCalculationParameters.L_diff = currentCalculationParameters.L_min - currentCalculationParameters.Lr;
                    if (currentCalculationParameters.L_diff <= 0) currentCalculationParameters.L = currentCalculationParameters.Lr;
                    else {
                        //z84
                        System.out.println("Введите длину ремня. Минимальное значение " + currentCalculationParameters.L_min);
                        currentCalculationParameters.L = console.nextDouble();
                    }
                    //z6.11
                    currentCalculationParameters.D_cp = (currentCalculationParameters.D_1 + currentCalculationParameters.D_2) / 2;
                    currentCalculationParameters.lambda = currentCalculationParameters.L - Math.PI * currentCalculationParameters.D_cp;
                    currentCalculationParameters.delta = (currentCalculationParameters.D_1 - currentCalculationParameters.D_2) / 2;
                    currentCalculationParameters.a = (currentCalculationParameters.lambda + Math.pow(Math.pow(currentCalculationParameters.lambda, 2)
                            - 8 * Math.pow(currentCalculationParameters.delta, 2), 1. / 2)) / 4;
                    break l;
                }
            }
        }
        //z45
        currentCalculationParameters.sigma_1 = 180 - ((currentCalculationParameters.D_2 - currentCalculationParameters.D_1) / currentCalculationParameters.a) * 57;
        System.out.println(currentCalculationParameters);
        TableWindow<Parameters> results = new TableWindow<>("Результаты расчёта", Parameters.class, () -> List.of(currentCalculationParameters.getParameters()), () -> {
        });
        results.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        results.dispose();
        currentCalculationParameters = null;
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
                    currentCalculationParameters.speed = "Быстроходная";
                    break l;
                }
                case 2 -> {
                    currentCalculationParameters.speed = "Среднескоростная";
                    break l;
                }
            }
        }
        currentCalculationParameters.a_min = (i == 1 ? 1.5 : 2) * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2);
        //z75
        System.out.println("Введите межосевое расстояние. Минимальное значение " + currentCalculationParameters.a_min);
        currentCalculationParameters.a = console.nextDouble();
    }

    /**
     * z51
     */
    private void computeD1() {
        System.out.println("Введите частоту вращения меньшего шкива, мин^-1");
        currentCalculationParameters.n_1 = console.nextDouble();
        System.out.println("Выберите один из методов расчёта диаметра меньшего шкива:");
        System.out.println("1. По формуле М.А. Саверина");
        System.out.println("2. Исходя из ориентировочной скорости");
        System.out.println("3. На основании конструктивных соображений");
        System.out.println("4. При ограниченном сортаменте");
        l:while (true) {
            int i = console.nextInt();
            switch (i) {
                case 1 -> {
                    currentCalculationParameters.m_s = "По формуле М.А. Саверина";
                    formulaD_1();
                    break l;
                }
                case 2 -> {
                    currentCalculationParameters.m_s = "Исходя из ориентировочной скорости";
                    // TODO: 12/5/24
                    currentCalculationParameters.v = 5;// FIXME: 12/6/24 from table 1.1
                    currentCalculationParameters.D_1_r = currentCalculationParameters.v * 60000 / (Math.PI * currentCalculationParameters.n_1);
                    break l;
                }
                case 3 -> {
                    currentCalculationParameters.m_s = "На основании конструктивных соображений";
                    System.out.println("Введите диаметр меньшего шкива");
                    currentCalculationParameters.D_1_r = console.nextDouble();
                    break l;
                }
                case 4 -> {
                    currentCalculationParameters.m_s = "При ограниченном сортаменте";
                    // TODO: 12/5/24
                    currentCalculationParameters.D_1_r = 500;// FIXME: 12/6/24 from table 1.2
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
                    currentCalculationParameters.type = "Повышающая";
                    break l;
                }
                case 2 -> {
                    currentCalculationParameters.type = "Понижающая";
                    break l;
                }
            }
        }
        System.out.println("Введите коэффициент скольжения ремня");
        currentCalculationParameters.xi = console.nextDouble();
        currentCalculationParameters.D_2_r = currentCalculationParameters.D_1_r * (i == 1 ? currentCalculationParameters.u / (1 - currentCalculationParameters.xi) : currentCalculationParameters.u * (1 - currentCalculationParameters.xi));
    }

    private double round_GOST(double d) {
        List<Gost> diameters = dbManager.getAll("gost-diameter");
        diameters.sort((d1, d2) -> (int) (d1.d - d2.d));
        Gost diameter = new Gost();
        diameter.d = -1000;
        for (Gost gost : diameters) {
            if (d > gost.d) diameter = gost;
            else {
                if (diameter.d > d) return diameter.d;
                if (gost.d - d > d - diameter.d) return diameter.d;
                else return gost.d;
            }
        }
        return diameter.d;
    }

    private void z52() {
        System.out.println("Введите частоту вращения большего шкива, мин^-1");
        double n_2 = console.nextDouble();
        currentCalculationParameters.n_2 = n_2;
        currentCalculationParameters.u = currentCalculationParameters.n_1 / n_2;
    }

    /**
     * z61
     */
    private void formulaD_1() {
        System.out.println("Введите мощность, кВт");
        double N = console.nextDouble();
        currentCalculationParameters.N = N;
        currentCalculationParameters.D_1_r = 120 * Math.pow(N * 1000 / currentCalculationParameters.n_1, 1./3);
    }
}