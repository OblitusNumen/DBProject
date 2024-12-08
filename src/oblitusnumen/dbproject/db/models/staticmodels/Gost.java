package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class Gost {
    @ColumnName("Диаметр")
    public Double d = null;
    @ColumnName("Предельное отклонение")
    public Double delta = null;
}
