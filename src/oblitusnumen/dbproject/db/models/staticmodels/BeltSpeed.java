package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class BeltSpeed {
    @ColumnName("Тип ремня")
    public String type = null;
    @ColumnName("Минимальная ширина")
    public Double minW = null;
    @ColumnName("Максимальная ширина")
    public Double maxW = null;
    @ColumnName("Минимум толщина")
    public Double minT = null;
    @ColumnName("Максимальная толщина")
    public Double maxT = null;
    @ColumnName("Рекомендованная наибольшая скорость, мс")
    public Double recSpeed = null;
}
