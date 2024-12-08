package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Parameters {
    @ColumnName("Диаметр меньшего шкива")
    public Double D_1 = null;
    @ColumnName("Диаметр большего шкива")
    public Double D_2 = null;
    @ColumnName("Межосевое расстояние")
    public Double a = null;
    @ColumnName("Длина ремня")
    public Double L = null;
    @ColumnName("Угол обхвата на меньшем шкиве")
    public Double sigma_1 = null;
}
