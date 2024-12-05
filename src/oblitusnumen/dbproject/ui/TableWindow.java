package oblitusnumen.dbproject.ui;

import oblitusnumen.dbproject.Main;
import oblitusnumen.dbproject.db.DBManager;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Enumeration;

public class TableWindow extends JFrame {
    private final DBManager dbManager;
    private final String tableName;
    private final int tableWidth;
    private JPanel pane;
    private JTable table;

    public TableWindow(DBManager dbManager, String tableName) {
        super("Таблица " + tableName);
        this.dbManager = dbManager;
        this.tableName = tableName;
        tableWidth = dbManager.getTableWidth(tableName);
        setContentPane(pane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void update() {// FIXME: 12/5/24
//        table.set();
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
