package oblitusnumen.dbproject;

import java.awt.*;
import java.lang.reflect.Field;

public class Utils {
    public static void copyFields(Object to, Object from) {
        try {
            for (Field field : from.getClass().getFields()) {
                to.getClass().getField(field.getName()).set(to, field.get(from));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void alignCentered(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        component.setBounds((int) (width / 2 - (double) component.getWidth() / 2), (int) (height / 2 - (double) component.getHeight() / 2),
                component.getWidth(), component.getHeight());
    }
}
