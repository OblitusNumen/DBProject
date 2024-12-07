package oblitusnumen.dbproject;

import oblitusnumen.dbproject.db.DBManager;
import oblitusnumen.dbproject.db.models.CalculationParameters;
import oblitusnumen.dbproject.db.models.Parameters;
import oblitusnumen.dbproject.db.models.staticmodels.Gost;
import oblitusnumen.dbproject.ui.TableWindow;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    DBManager dbManager = new DBManager();
    Map<String, TableWindow<?>> tableMonitors = new HashMap<>();
    CalculationParameters currentCalculationParameters;
    Console console;

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
        String table = console.getChoice("Выберите таблицу для просмотра:", dbManager.tables().toArray(new String[0]));
        if (tableMonitors.containsKey(table)) {
            tableMonitors.get(table).toTop();
        } else {
            tableMonitors.put(table, new TableWindow<>(this, dbManager, table));
        }
        System.out.println("Таблица не найдена. Попробуйте ещё раз.");
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
        String[] options = {"По межосевому расстоянию", "Из условий сравнительной долговечности"};
        int choiceIndex = console.getChoiceIndex("Выберите метод определения длины ремня:", options);
        currentCalculationParameters.m_l = options[choiceIndex];
            switch (choiceIndex) {
                case 0 -> currentCalculationParameters.L = 2 * currentCalculationParameters.a + Math.PI
                        * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2) / 2
                        + Math.pow(currentCalculationParameters.D_2 - currentCalculationParameters.D_1, 2) / (4 * currentCalculationParameters.a);
                case 1 -> {
                    currentCalculationParameters.i_max = currentCalculationParameters.speed.equals("Быстроходная") ? 50 : 5;
                    System.out.println("Введите частоту пробега ремня в секунду. Максимальное значение " + currentCalculationParameters.i_max);
                    currentCalculationParameters.i = console.nextDouble();
                    currentCalculationParameters.L_min = currentCalculationParameters.vr / currentCalculationParameters.i;
                    currentCalculationParameters.Lr = 2 * currentCalculationParameters.a + Math.PI * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2)
                            / 2 + Math.pow(currentCalculationParameters.D_2 - currentCalculationParameters.D_1, 2) / (4 * currentCalculationParameters.a);
                    currentCalculationParameters.L_diff = currentCalculationParameters.L_min - currentCalculationParameters.Lr;
                    if (currentCalculationParameters.L_diff <= 0)
                        currentCalculationParameters.L = currentCalculationParameters.Lr;
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
                }
            }
        //z45
        currentCalculationParameters.sigma_1 = 180 - ((currentCalculationParameters.D_2 - currentCalculationParameters.D_1) / currentCalculationParameters.a) * 57;
        System.out.println(currentCalculationParameters);
        TableWindow<Parameters> results = new TableWindow<>("Результаты расчёта", Parameters.class, () -> List.of(currentCalculationParameters.getParameters()), () -> {
        });
        results.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        System.out.println("Хотите сохранить данные расчёта? (y/n)");
        l:
        while (true) {
            String s = console.nextString();
            switch (s) {
                case "y" -> {
                    saveParams();
                    break l;
                }
                case "n" -> {
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
        String[] options = {"Быстроходная", "Среднескоростная"};
        int choiceIndex = console.getChoiceIndex("Выберите быстроходность передачи:", options);
        currentCalculationParameters.speed = options[choiceIndex];
        currentCalculationParameters.a_min = (choiceIndex == 0 ? 1.5 : 2) * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2);
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
        String[] options = {"По формуле М.А. Саверина", "Исходя из ориентировочной скорости", "На основании конструктивных соображений", "При ограниченном сортаменте"};
        switch (console.getChoiceIndex("Выберите один из методов расчёта диаметра меньшего шкива:", options)) {
            case 0 -> {
                currentCalculationParameters.m_s = "По формуле М.А. Саверина";
                formulaD_1();
            }
            case 1 -> {
                currentCalculationParameters.m_s = "Исходя из ориентировочной скорости";
                chooseSpeedFromTable();
                currentCalculationParameters.v = console.nextDouble();
                //z72
                currentCalculationParameters.D_1_r = currentCalculationParameters.v * 60000 / (Math.PI * currentCalculationParameters.n_1);
            }
            case 2 -> {
                currentCalculationParameters.m_s = "На основании конструктивных соображений";
                System.out.println("Введите диаметр меньшего шкива");
                currentCalculationParameters.D_1_r = console.nextDouble();
            }
            case 3 -> {
                currentCalculationParameters.m_s = "При ограниченном сортаменте";
                // TODO: 12/5/24
                currentCalculationParameters.D_1_r = 500;// FIXME: 12/6/24 from table 1.2
            }
        }
        z52();
    }

    /**
     * z71
     */
    private void chooseSpeedFromTable() {
        try (Connection connection = dbManager.getConnection()) {
            List<String> materials = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement("select distinct \"type\" from \"speed\"")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        materials.add(resultSet.getString(1));
                    }
                }
            }
            String choice = currentCalculationParameters.mat = console.getChoice("Выберите материал ремня:", materials.toArray(new String[0]));
            System.out.println("Выберите ширину ремня в пределах одного из диапазонов:");
            try (PreparedStatement statement = connection.prepareStatement("select distinct \"minW\", \"maxW\" from \"speed\" where \"type\" = ?")) {
                statement.setString(1, choice);
                try (ResultSet resultSet = statement.executeQuery()) {
                    int i = 1;
                    while (resultSet.next()) {
                        System.out.println(i + ". " + resultSet.getDouble(1) + "-" + resultSet.getDouble(2));
                        i++;
                    }
                }
            }
            double width;
            while (true) {
                width = currentCalculationParameters.width = console.nextDouble();
                try (PreparedStatement statement = connection.prepareStatement("select distinct \"minT\", \"maxT\"" +
                        " from \"speed\"" +
                        " where \"type\" = ? and ? between \"minW\" and \"maxW\"")) {
                    statement.setString(1, choice);
                    statement.setString(2, String.valueOf(width));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        int i = 1;
                        while (resultSet.next()) {
                            if (i == 1) System.out.println("Выберите толщину ремня в пределах одного из диапазонов:");
                            System.out.println(i + ". " + resultSet.getDouble(1) + "-" + resultSet.getDouble(2));
                            i++;
                        }
                        if (i != 1) break;
                        System.out.println("Не найден диапазон для значения " + width + ". Попробуйте ещё раз.");
                    }
                }
            }double thick;
            while (true) {
                thick = currentCalculationParameters.thick = console.nextDouble();
                try (PreparedStatement statement = connection.prepareStatement("select distinct \"recSpeed\"" +
                        " from \"speed\"" +
                        " where \"type\" = ? and ? between \"minW\" and \"maxW\" and ? between \"minT\" and \"maxT\"")) {
                    statement.setString(1, choice);
                    statement.setString(2, String.valueOf(width));
                    statement.setString(3, String.valueOf(thick));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            System.out.println("Выберите скорость ремня. Рекомендованная наибольшая скорость ремня: "
                                    + resultSet.getDouble(1));
                            break;
                        }
                    }
                    System.out.println("Не найден диапазон для значения " + thick + ". Попробуйте ещё раз.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * z53
     */
    private void computeD2() {
        String[] options = {"Повышающая", "Понижающая"};
        int choiceIndex = console.getChoiceIndex("Выберите вид передачи:", options);
        currentCalculationParameters.type = options[choiceIndex];
        System.out.println("Введите коэффициент скольжения ремня");
        currentCalculationParameters.xi = console.nextDouble();
        currentCalculationParameters.D_2_r = currentCalculationParameters.D_1_r * (choiceIndex == 0 ?
                currentCalculationParameters.u / (1 - currentCalculationParameters.xi)
                : currentCalculationParameters.u * (1 - currentCalculationParameters.xi));
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
        currentCalculationParameters.D_1_r = 120 * Math.pow(N * 1000 / currentCalculationParameters.n_1, 1. / 3);
    }
}