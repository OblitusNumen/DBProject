package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;

import java.lang.reflect.Field;

public class Belt {
    @ColumnName("Наименование детали")
    public String ND = "Ремень";
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

    public static Belt ofPart(Part part) {
        Belt belt = new Belt();
        for (Field field : Belt.class.getFields()) {
            try {
                field.set(belt, Part.class.getField(field.getName()).get(part));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return belt;
    }

    public Part toPart() {
        Part part = new Part();
        part.ND = ND;
        part.v = v;
        part.mat = mat;
        part.thick = thick;
        part.width = width;
        part.xi = xi;
        part.L = L;
        part.bType = bType;
        part.hasLayer = hasLayer;
        part.layerNumber = layerNumber;
        part.L_min = L_min;
        part.lambda = lambda;
        part.vr = vr;
        part.L_diff = L_diff;
        part.Lr = Lr;
        return part;
    }
}
