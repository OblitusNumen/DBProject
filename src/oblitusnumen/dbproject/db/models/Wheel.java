package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Wheel {
    @ColumnName("Наименование детали")
    public String type = "Шкив";
    @ColumnName("Частота вращения")
    public double n;
    @ColumnName("Диаметр")
    public double d;
}
