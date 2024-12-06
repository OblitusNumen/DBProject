package oblitusnumen.dbproject.ui;

import oblitusnumen.dbproject.Main;
import oblitusnumen.dbproject.db.ColumnName;
import oblitusnumen.dbproject.db.DBManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class TableWindow<Model> extends JFrame {
    private final DefaultTableModel tableModel;
    private final Field[] fields;
    private final Supplier<Iterable<Model>> updater;
    private final JTable table;
    private JPanel pane;
    private final Runnable onDispose;

    public TableWindow(String title, Class<Model> model, Supplier<Iterable<Model>> updater, Runnable onDispose) {
        super(title);
        fields = model.getFields();
        this.updater = updater;
        this.onDispose = onDispose;
        String[] columnNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            columnNames[i] = field.getName();
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof ColumnName columnName) {
                    columnNames[i] = columnName.value();
                    break;
                }
            }
        }
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
//        table.getTableHeader().setEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        // Set table
        GridBagConstraints gbc = new GridBagConstraints();
        // Set properties for GridBagConstraints (row, column, grid width, grid height)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        // Add the component with its constraints
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setMinimumSize(new Dimension(800, 450));
        pane.add(scrollPane, gbc);
        setContentPane(pane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        update();
    }

    public TableWindow(Main main, DBManager dbManager, String tableName) {
        this("Таблица " + tableName, (Class<Model>) dbManager.getTableModel(tableName), () -> dbManager.getAll(tableName), () -> main.closeMonitor(tableName));
    }

    public void update() {
        update(updater.get());
    }

    public void update(Iterable<Model> rows) {// FIXME: 12/5/24
        while (tableModel.getRowCount() > 0) tableModel.removeRow(0);
        try {
            Object[] objects = new Object[fields.length];
            for (Model row : rows) {
                for (int i = 0; i < fields.length; i++) {
                    objects[i] = fields[i].get(row);
                }
                tableModel.addRow(objects);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        repaint();
        pack();
        alignCentered();
    }

    @Override
    public void dispose() {
        onDispose.run();
        super.dispose();
    }

    public void alignCentered() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setBounds((int) (width / 2 - (double) getWidth() / 2), (int) (height / 2 - (double) getHeight() / 2),
                getWidth(), getHeight());
    }

    public void toTop() {
        setState(JFrame.NORMAL);
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }
}
