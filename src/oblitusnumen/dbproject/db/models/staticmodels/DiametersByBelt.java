package oblitusnumen.dbproject.db.models.staticmodels;

import oblitusnumen.dbproject.db.ColumnName;

public class DiametersByBelt {
    @ColumnName("Тип ремня")
    public String bType;
    @ColumnName("Толщина, мм")
    public double thick;
    @ColumnName("Минимальный допускаемый диаметр шкива, мм")
    public double minD;
    @ColumnName("Наличие прослоек")
    public boolean hasLayer;
    @ColumnName("Минимальный рекомендуемый диаметр шкива, мм")
    public double recD;
    @ColumnName("Число прокладок")
    public int layerNumber;
}
