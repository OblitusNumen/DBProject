package oblitusnumen.dbproject.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
            String[] table = s.split("[\t,]");
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
                )) {
                    statement.execute();
                    System.out.println("created table " + table);
                }
                String[] rows;
                try (InputStream inputStream = getClass().getResourceAsStream("/" + table + ".csv")) {
                    rows = new String(inputStream.readAllBytes()).split("\n");
                }
                StringBuilder valueFormat = new StringBuilder("?, ");
                for (int i = 0; i < typedFields.length; i++) {
                    valueFormat.append("?");
                    if (i < typedFields.length - 1) valueFormat.append(", ");
                }
                int rowsNumber = 0;
                for (String row : rows) {
                    if (row.isEmpty()) continue;
                    try (PreparedStatement statement = connection.prepareStatement("insert into \"" + table + "\" values (" + valueFormat + ")")) {
                        String[] values = row.split("[\t,]");
                        statement.setString(1, String.valueOf(rowsNumber + 1));
                        for (int i = 0; i < values.length; i++) {
                            statement.setString(i + 2, values[i].equals("null") ? null : values[i]);
                        }
                        statement.execute();
                    }
                    rowsNumber++;
                }
                System.out.printf("inserted %d entries%n", rowsNumber);
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public <Model> List<Model> getAll(String table) {
        if (!tableModels.containsKey(table)) throw new IllegalArgumentException("Unknown table");
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from \"" + table + "\"")) {
                return (List<Model>) executeQuery(statement, tableModels.get(table));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <Model> Model getById(String table, int id) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from \"" + table + "\" where id = ?")) {
                statement.setString(1, String.valueOf(id));
                return (Model) executeQuery(statement, tableModels.get(table)).stream().findFirst().orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <Model> List<Model> executeQuery(PreparedStatement statement, Class<Model> model) throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Model> result = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            TypedField[] typedFields = modelFields.get(model);
            Constructor<Model> modelConstrurtor = model.getConstructor();
            while (rs.next()) {
                Model modelInstance = modelConstrurtor.newInstance();
                for (TypedField typedField : typedFields) {
                    typedField.field.set(modelInstance, typedField.type.get(rs, typedField.field.getName()));
                }
                result.add(modelInstance);
            }
        }
        return result;
    }

    public <Model> int insertInto(String table, Model value) {
            Class<Model> model = (Class<Model>) tableModels.get(table);
            TypedField[] typedFields = modelFields.get(model);
        Object[] parameters = new Object[typedFields.length];
                StringBuilder columnFormat = new StringBuilder();
            for (int i = 0; i < typedFields.length; i++) {
                TypedField typedField = typedFields[i];
                parameters[i] = typedField.field.getName();
                try {
                    parameters[i] = typedField.field.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                columnFormat.append("\"").append(typedField.field.getName()).append("\"");
                if (i < typedFields.length - 1) columnFormat.append(", ");
            }
                StringBuilder valueFormat = new StringBuilder();
                for (int i = 0; i < typedFields.length; i++) {
                    valueFormat.append("?");
                    if (i < typedFields.length - 1) valueFormat.append(", ");
                }
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into \"" + table + "\" (" + columnFormat + ") values (" + valueFormat + ")")) {
                // Set parameter values
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
                // Execute the insert statement
                int affectedRows = statement.executeUpdate();
                // Check if the insert was successful
                if (affectedRows > 0) {
                    // Retrieve the generated key
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Get the first column (ID)
                        } else throw new RuntimeException("No ID was returned.");
                    }
                } else throw new RuntimeException("Insertion failed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getTableModel(String tableName) {
        return tableModels.get(tableName);
    }

    public Set<String> tables() {
        return tableModels.keySet();
    }

    public enum Type {
        STRING("class java.lang.String", "TEXT") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                return rs.getString(name);
            }
        },
        INTEGER("class java.lang.Integer", "INTEGER") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                super.get(rs, name);
                return rs.wasNull() ? null : rs.getInt(name);
            }
        },
        DOUBLE("class java.lang.Double", "DOUBLE") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                super.get(rs, name);
                return rs.wasNull() ? null : rs.getDouble(name);
            }
        },
        BOOLEAN("class java.lang.Boolean", "INTEGER") {
            @Override
            public Object get(ResultSet rs, String name) throws SQLException {
                super.get(rs, name);
                return rs.wasNull() ? null : rs.getInt(name) != 0;
            }
        };

        private final String type;
        private final String sqlType;

        Type(String type, String sqlType) {
            this.type = type;
            this.sqlType = sqlType;
        }

        public Object get(ResultSet rs, String name) throws SQLException {
            return rs.getObject(name);
        }

        @Override
        public String toString() {
            return sqlType;
        }
    }

    public record TypedField(Field field, Type type) {
    }
}
