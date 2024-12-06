package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class BeltSpeed {
    @ColumnName("Тип ремня")
    public String type;
    @ColumnName("Минимальная ширина")
    public double minW;
    @ColumnName("Максимальная ширина")
    public double maxW;
    @ColumnName("Минимум толщина")
    public double minT;
    @ColumnName("Максимальная толщина")
    public double maxT;
    @ColumnName("Рекомендованная наибольшая скорость, мс")
    public double recSpeed;
}
