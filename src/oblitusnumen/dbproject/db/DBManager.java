package oblitusnumen.dbproject.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DBManager {
    private static final String DB_FILENAME = "./database.db";
    public static final String URL = "jdbc:sqlite:" + DB_FILENAME;
    Map<String, Class<?>> tableModels = new HashMap<>();
    Map<Class<?>, TypedField[]> modelFields = new HashMap<>();

    public DBManager() throws Exception {
        File file = new File(DB_FILENAME);
        String[] split;
        try (InputStream inputStream = getClass().getResourceAsStream("/.tables")) {
            split = new String(inputStream.readAllBytes()).split("\n");
        }
        for (String s : split) {
            if (s.isEmpty()) continue;
            String[] table = s.split("\t");
            tableModels.put(table[0], Class.forName(table[1]));
        }
        Map<String, Type> typeAdaptation = new HashMap<>();
        for (Type value : Type.values()) {
            typeAdaptation.put(value.type, value);
        }
        for (Class<?> clazz : tableModels.values()) {
            Field[] fields = clazz.getFields();
            TypedField[] typedFields = new TypedField[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Type type = typeAdaptation.get(field.getType().toString());
                typedFields[i] = new TypedField(field, type);
            }
            modelFields.put(clazz, typedFields);
        }
        if (!file.exists()) {
            init();
        }
    }

    private void init() throws Exception {
        File file = new File(DB_FILENAME);
        if (!file.createNewFile()) throw new RuntimeException("Couldn't create database file");
        createTables();
    }

    private void createTables() throws SQLException, IOException {
        for (String table : tableModels.keySet()) {
            System.out.println("creating table " + table);
            Class<?> model = tableModels.get(table);
            StringBuilder fieldsInfo = new StringBuilder();
            TypedField[] typedFields = modelFields.get(model);
            for (int i = 0; i < typedFields.length; i++) {
                TypedField typedField = typedFields[i];
                fieldsInfo.append(typedField.field.getName()).append(" ").append(typedField.type);
                if (i < typedFields.length - 1) fieldsInfo.append(", ");
            }
            try (Connection connection = getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "create table if not exists \"" + table + "\" (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                fieldsInfo +
                                ");"
                )) {// FIXME: 11/24/24
                    statement.execute();
                    System.out.println("created table " + table);
                }// TODO: 11/24/24 tables
                String[] rows;
                try (InputStream inputStream = getClass().getResourceAsStream("/" + table + ".csv")) {
                    rows = new String(inputStream.readAllBytes()).split("\n");
                }
                StringBuilder valueFormat = new StringBuilder("?,");
                for (int i = 0; i < typedFields.length; i++) {
                    valueFormat.append("?");
                    if (i < typedFields.length - 1) valueFormat.append(", ");
                }
                int rowsNumber = 0;
                for (String row : rows) {
                    if (row.isEmpty()) continue;
                    try (PreparedStatement statement = connection.prepareStatement("insert into \"" + table + "\" values (" + valueFormat + ")")) {// FIXME: 11/24/24
                        String[] values = row.split("\t");
                        statement.setString(1, String.valueOf(rowsNumber + 1));
                        for (int i = 0; i < values.length; i++) {
                            statement.setString(i + 2, values[i]);
                        }
                        statement.execute();
                    }
                    rowsNumber++;
                }
                System.out.println("inserted %d entries".formatted(rowsNumber));
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public int getTableWidth(String tableName) {// FIXME 12/5/24 9:36PM
        throw new RuntimeException();
    }

    public <Model> List<Model> getAll(String table) {
        if (!tableModels.containsKey(table)) throw new IllegalArgumentException("Unknown table");
        Class<Model> model = (Class<Model>) tableModels.get(table);
        List<Model> result = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from \"" + table + "\"")) {// FIXME: 11/24/24
                ResultSet rs = statement.executeQuery();
                TypedField[] typedFields = modelFields.get(model);
                Constructor<Model> modelConstrurtor = model.getConstructor();
                while (rs.next()) {
                    Model modelInstance = modelConstrurtor.newInstance();
                    for (TypedField typedField : typedFields) {
                        typedField.field.set(modelInstance, typedField.type.get(rs, typedField.field.getName()));
                    }
                    result.add(modelInstance);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Class<?> getTableModel(String tableName) {
        return tableModels.get(tableName);
    }

    public Set<String> tables() {
        return tableModels.keySet();
    }

    public enum Type {
        TEXT("class java.lang.String") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                return rs.getString(name);
            }
        },
        INTEGER("int") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                return rs.getInt(name);
            }
        },
        DOUBLE("double") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                return rs.getDouble(name);
            }
        };

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public abstract Object get(ResultSet rs, String name) throws SQLException;
    }

    public record TypedField(Field field, Type type) {
    }
}
