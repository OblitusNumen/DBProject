package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Wheel {
    @ColumnName("Наименование детали")
    public String type = "Шкив";
    @ColumnName("Частота вращения")
    public Double n = null;
    @ColumnName("Диаметр")
    public Double d = null;
    @ColumnName("Расчётный диаметр")
    public Double dr = null;
}
