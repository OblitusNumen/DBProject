package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

public class Part {
    @ColumnName("Наименование детали")
    public String type = null;
    @ColumnName("Скорость")
    public Double v = null;
    @ColumnName("Материал")
    public String mat = null;
    @ColumnName("Толщина")
    public Double thick = null;
    @ColumnName("Ширина")
    public Double width = null;
    @ColumnName("Коэффициент скольжения")
    public Double xi = null;
    @ColumnName("Длина")
    public Double L = null;
    @ColumnName("Тип")
    public String bType = null;
    @ColumnName("Наличие прослоек")
    public Boolean hasLayer = null;
    @ColumnName("Число прокладок")
    public Integer layerNumber = null;
    @ColumnName("Минимальная длина")
    public Double L_min = null;
    @ColumnName("Разница между длиной ремня и длиной окружности")
    public Double lambda = null;
    @ColumnName("Расчётная скорость")
    public Double vr = null;
    @ColumnName("Разница L_min и L")
    public Double L_diff = null;
    @ColumnName("Расчётная длина ремня")
    public Double Lr = null;
}
