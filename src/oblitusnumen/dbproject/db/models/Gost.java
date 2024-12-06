package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Gost {
    @ColumnName("Диаметр")
    public double d;
    @ColumnName("Предельное отклонение")
    public double delta;
}
