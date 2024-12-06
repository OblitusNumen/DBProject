package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Parameters {
    @ColumnName("Диаметр меньшего шкива")
    public double D_1;
    @ColumnName("Диаметр большего шкива")
    public double D_2;
    @ColumnName("Межосевое расстояние")
    public double a;
    @ColumnName("Длина ремня")
    public double L;
    @ColumnName("Угол обхвата на меньшем шкиве")
    public double sigma_1;
}
