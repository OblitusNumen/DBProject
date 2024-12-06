package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class Gost {
    @ColumnName("Диаметр")
    public double d;
    @ColumnName("Предельное отклонение")
    public double delta;
}
