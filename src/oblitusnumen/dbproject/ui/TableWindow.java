package oblitusnumen.dbproject.ui;

import oblitusnumen.dbproject.Main;
import oblitusnumen.dbproject.Utils;
import oblitusnumen.dbproject.db.ColumnName;
import oblitusnumen.dbproject.db.DBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class TableWindow<Model> extends JFrame {
    private final DefaultTableModel tableModel;
    private final Field[] fields;
    private final Supplier<Iterable<Model>> updater;
    private final Runnable onDispose;
    private JPanel pane;

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
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        adjustColumnWidths(table);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
//        table.getTableHeader().setEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        // Set table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setSize(new Dimension(800, 450));
        pane.add(scrollPane, BorderLayout.CENTER);
        setContentPane(pane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        update();
        Utils.alignCentered(this);
    }

    public TableWindow(Main main, DBManager dbManager, String tableName) {
        this("Таблица " + tableName, (Class<Model>) dbManager.getTableModel(tableName), () -> dbManager.getAll(tableName), () -> main.closeMonitor(tableName));
    }

    // Adjust column widths to fit the header title
    private static void adjustColumnWidths(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        JTableHeader header = table.getTableHeader();
        FontMetrics metrics = header.getFontMetrics(header.getFont());

        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            String headerValue = column.getHeaderValue().toString();
            int width = metrics.stringWidth(headerValue) + 10; // Add padding
            column.setPreferredWidth(width);
        }
    }

    public void update() {
        update(updater.get());
    }

    public void update(Iterable<Model> rows) {
        while (tableModel.getRowCount() > 0) tableModel.removeRow(0);
        try {
            Object[] row = new Object[fields.length];
            for (Model model : rows) {
                for (int i = 0; i < fields.length; i++) {
                    row[i] = fields[i].get(model);
                }
                tableModel.addRow(row);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dispose() {
        onDispose.run();
        super.dispose();
    }

    public void toTop() {
        setState(JFrame.NORMAL);
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }
}
