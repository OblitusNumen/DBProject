package oblitusnumen.dbproject;

import oblitusnumen.dbproject.db.DBManager;
import oblitusnumen.dbproject.db.models.AssemblyUnit;
import oblitusnumen.dbproject.db.models.CalculationParameters;
import oblitusnumen.dbproject.db.models.Parameters;
import oblitusnumen.dbproject.db.models.staticmodels.Gost;
import oblitusnumen.dbproject.ui.TableWindow;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String table = console.getChoice("Выберите таблицу для просмотра:", dbManager.tables().toArray(new String[0]));
        if (tableMonitors.containsKey(table)) {
            tableMonitors.get(table).toTop();
        } else {
            tableMonitors.put(table, new TableWindow<>(this, dbManager, table));
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
        String[] options = {"По межосевому расстоянию", "Из условий сравнительной долговечности"};
        int choiceIndex = console.getChoiceIndex("Выберите метод определения длины ремня:", options);
        currentCalculationParameters.m_l = options[choiceIndex];
        switch (choiceIndex) {
            case 0 -> currentCalculationParameters.L = 2 * currentCalculationParameters.a + Math.PI
                    * (currentCalculationParameters.D_1 + currentCalculationParameters.D_2) / 2
                    + Math.pow(currentCalculationParameters.D_2 - currentCalculationParameters.D_1, 2) / (4 * currentCalculationParameters.a);
            case 1 -> {
                currentCalculationParameters.i_max = (double) (currentCalculationParameters.speed.equals("Быстроходная") ? 50 : 5);
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
        currentCalculationParameters.saveAll(dbManager);
        System.out.println("Данные сохранены");
        for (TableWindow<?> value : tableMonitors.values()) {
            value.update();
        }
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
        int choiceIndex = console.getChoiceIndex("Выберите один из методов расчёта диаметра меньшего шкива:", options);
        currentCalculationParameters.m_s = options[choiceIndex];
        switch (choiceIndex) {
            case 0 -> formulaD_1();
            case 1 -> {
                chooseSpeedFromTable();
                currentCalculationParameters.v = console.nextDouble();
                //z72
                currentCalculationParameters.D_1_r = currentCalculationParameters.v * 60000 / (Math.PI * currentCalculationParameters.n_1);
            }
            case 2 -> {
                System.out.println("Введите диаметр меньшего шкива");
                currentCalculationParameters.D_1_r = console.nextDouble();
            }
            case 3 -> {
                chooseDiameterByBelt();
                currentCalculationParameters.D_1_r = console.nextDouble();
            }
        }
        z52();
    }

    /**
     * z64
     */
    private void chooseDiameterByBelt() {
        try (Connection connection = dbManager.getConnection()) {
            String bType = getbType(connection);
            Double thick = getThick(connection, bType);
            if (thick != null) currentCalculationParameters.thick = thick;
            Boolean hasLayer = getHasLayer(connection, thick, bType);
            if (hasLayer != null) currentCalculationParameters.hasLayer = hasLayer;
            Integer layerNumber = getLayerNumber(connection, thick, hasLayer, bType);
            if (layerNumber != null) currentCalculationParameters.layerNumber = layerNumber;
            try (PreparedStatement statement = connection.prepareStatement("select distinct \"minD\", \"recD\"" +
                    " from \"diameters-by-belt\"" +
                    " where \"bType\" = ?" +
                    " and \"thick\" " + (thick != null ? "= ?" : "is null") +
                    " and \"hasLayer\" " + (hasLayer != null ? "= ?" : "is null") +
                    " and \"layerNumber\" " + (layerNumber != null ? "= ?" : "is null"))) {
                statement.setString(1, bType);
                int i = 2;
                if (thick != null) statement.setObject(i++, thick);
                if (hasLayer != null) statement.setObject(i++, hasLayer);
                if (layerNumber != null) statement.setObject(i, layerNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        double minD = resultSet.getDouble(1);
                        resultSet.getObject(2);
                        System.out.println("Введите диаметр меньшего шкива. Минимальный допускаемый диаметр: " + minD + "." +
                                (resultSet.wasNull() ? "" : " Минимальный рекомендуемый диаметр: " + resultSet.getDouble(2) + "."));
                    } else throw new RuntimeException("Something went wrong. Probably tables are empty.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer getLayerNumber(Connection connection, Double thick, Boolean hasLayer, String bType) throws SQLException {
        List<Integer> numberLayers = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select distinct \"layerNumber\"" +
                " from \"diameters-by-belt\"" +
                " where \"bType\" = ?" +
                " and \"thick\" " + (thick != null ? "= ?" : "is null") +
                " and \"hasLayer\" " + (hasLayer != null ? "= ?" : "is null"))) {
            statement.setString(1, bType);
            int i = 2;
            if (thick != null) statement.setObject(i++, thick);
            if (hasLayer != null) statement.setObject(i, hasLayer);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    resultSet.getObject(1);
                    numberLayers.add(resultSet.wasNull() ? null : resultSet.getInt(1));
                }
            }
        }
        Integer layerNumber;
        if (numberLayers.size() > 1) {
            layerNumber = console.getChoice("Выберите количество прокладок ремня:", numberLayers.toArray(new Integer[0]));
        } else layerNumber = numberLayers.size() == 1 ? numberLayers.getFirst() : null;
        return layerNumber;
    }

    private Boolean getHasLayer(Connection connection, Double thick, String bType) throws SQLException {
        List<Boolean> hasLayers = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select distinct \"hasLayer\"" +
                " from \"diameters-by-belt\"" +
                " where \"bType\" = ? and \"thick\" " + (thick != null ? "= ?" : "is null"))) {
            statement.setString(1, bType);
            if (thick != null) statement.setObject(2, thick);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    resultSet.getObject(1);
                    hasLayers.add(resultSet.wasNull() ? null : resultSet.getInt(1) != 0);
                }
            }
        }
        Boolean hasLayer;
        if (hasLayers.size() > 1) {
            hasLayer = console.getChoiceIndex("Выберите наличие прослоек:", "Есть", "Нет") == 0;
        } else hasLayer = hasLayers.size() == 1 ? hasLayers.getFirst() : null;
        return hasLayer;
    }

    private Double getThick(Connection connection, String bType) throws SQLException {
        List<Double> thickness = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select distinct \"thick\" from \"diameters-by-belt\" where \"bType\" = ?")) {
            statement.setString(1, bType);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    resultSet.getObject(1);
                    thickness.add(resultSet.wasNull() ? null : resultSet.getDouble(1));
                }
            }
        }
        Double thick;
        if (thickness.size() > 1) {
            thick = console.getChoice("Выберите толщину ремня:", thickness.toArray(new Double[0]));
        } else thick = thickness.size() == 1 ? thickness.getFirst() : null;
        return thick;
    }

    private String getbType(Connection connection) throws SQLException {
        List<String> types = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select distinct \"bType\" from \"diameters-by-belt\"")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    types.add(resultSet.getString(1));
                }
            }
        }
        return currentCalculationParameters.bType = console.getChoice("Выберите тип ремня:", types.toArray(new String[0]));
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
            }
            double thick;
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
                            System.out.println("Введите скорость ремня. Рекомендованная наибольшая скорость ремня: "
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
        diameter.d = (double) -1000;
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

    public void openData() {
        List<CalculationParameters> calculationParameters = new ArrayList<>();
        for (Object o : dbManager.getAll("assembly-units")) {
            AssemblyUnit assemblyUnit = (AssemblyUnit) o;
            calculationParameters.add(assemblyUnit.toCalculationParameters(dbManager));
        }
        new TableWindow<>("Параметры расчётов", CalculationParameters.class, () -> calculationParameters, () -> {
        });
    }
}