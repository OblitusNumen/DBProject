package oblitusnumen.dbproject.ui;

import oblitusnumen.dbproject.db.ColumnName;
import oblitusnumen.dbproject.db.DBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TableWindow<Model> extends JFrame {
    private final DBManager dbManager;
    private final String tableName;
    private final DefaultTableModel tableModel;
    private final Field[] fields;
    private JPanel pane;

    public TableWindow(DBManager dbManager, String tableName) {
        super("Таблица " + tableName);
        this.dbManager = dbManager;
        this.tableName = tableName;
        fields = dbManager.getTableModel(tableName).getFields();
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
        JTable table = new JTable(tableModel);
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
        update();
    }

    public void update() {
        update(dbManager.getAll(tableName));
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

    public void alignCentered() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setBounds((int) (width / 2 - (double) getWidth() / 2), (int) (height / 2 - (double) getHeight() / 2),
                getWidth(), getHeight());
    }
}
