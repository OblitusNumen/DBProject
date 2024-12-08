package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class DiametersByBelt {
    @ColumnName("Тип ремня")
    public String bType = null;
    @ColumnName("Толщина, мм")
    public Double thick = null;
    @ColumnName("Минимальный допускаемый диаметр шкива, мм")
    public Double minD = null;
    @ColumnName("Наличие прослоек")
    public Boolean hasLayer = null;
    @ColumnName("Минимальный рекомендуемый диаметр шкива, мм")
    public Double recD = null;
    @ColumnName("Число прокладок")
    public Integer layerNumber = null;
}
