package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

import java.lang.reflect.Field;

public class Wheel {
    @ColumnName("Наименование детали")
    public String ND = "Шкив";
    @ColumnName("Назначение детали")
    public String NAZD = null;
    @ColumnName("Частота вращения")
    public Double n = null;
    @ColumnName("Диаметр")
    public Double d = null;
    @ColumnName("Расчётный диаметр")
    public Double dr = null;

    public static Wheel ofPart(Part part) {
        Wheel wheel = new Wheel();
        for (Field field : Wheel.class.getFields()) {
            try {
                field.set(wheel, Part.class.getField(field.getName()).get(part));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return wheel;
    }

    public Part toPart() {
        Part part = new Part();
        part.ND = ND;
        part.NAZD = NAZD;
        part.n = n;
        part.d = d;
        part.dr = dr;
        return part;
    }
}
