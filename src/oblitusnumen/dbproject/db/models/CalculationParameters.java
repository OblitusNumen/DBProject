package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;
import oblitusnumen.dbproject.db.DBManager;

public class CalculationParameters {
    @ColumnName("Метод определения диаметра меньшего шкива")
    public String m_s = null;
    @ColumnName("Диаметр меньшего шкива")
    public Double D_1 = null;
    @ColumnName("Мощность")
    public Double N = null;
    @ColumnName("Частота вращения меньшего шкива")
    public Double n_1 = null;
    @ColumnName("Частота вращения большего шкива")
    public Double n_2 = null;
    @ColumnName("Передаточное число")
    public Double u = null;
    @ColumnName("Коэффициент скольжения ремня")
    public Double xi = null;
    @ColumnName("Диаметр большего шкива")
    public Double D_2 = null;
    @ColumnName("Расчётная скорость ремня")
    public Double vr = null;
    @ColumnName("Вид передачи")
    public String type = null;
    @ColumnName("Быстроходность передачи")
    public String speed = null;
    @ColumnName("Минимальное межосевое расстояние")
    public Double a_min = null;
    @ColumnName("Межосевое расстояние")
    public Double a = null;
    @ColumnName("Метод определения длины ремня")
    public String m_l = null;
    @ColumnName("Длина ремня")
    public Double L = null;
    @ColumnName("Максимальная частота пробега ремня в секунду")
    public Double i_max = null;
    @ColumnName("Частота пробега ремня в секунду")
    public Double i = null;
    @ColumnName("Минимальная длина ремня")
    public Double L_min = null;
    @ColumnName("Расчётная длина ремня")
    public Double Lr = null;
    @ColumnName("Разница L_min и L")
    public Double L_diff = null;
    @ColumnName("Средний диаметр шкивов")
    public Double D_cp = null;
    @ColumnName("Разница между длиной ремня и длиной окружности")
    public Double lambda = null;
    @ColumnName("Разница в размерах шкивов")
    public Double delta = null;
    @ColumnName("Угол обхвата на меньшем шкиве")
    public Double sigma_1 = null;
    @ColumnName("Скорость ремня")
    public Double v = null;
    @ColumnName("Расчётный диаметр меньшего шкива")
    public Double D_1_r = null;
    @ColumnName("Расчётный диаметр большего шкива")
    public Double D_2_r = null;
    @ColumnName("Материал ремня")
    public String mat = null;
    @ColumnName("Ширина ремня")
    public Double width = null;
    @ColumnName("Толщина ремня")
    public Double thick = null;
    @ColumnName("Тип ремня")
    public String bType = null;
    @ColumnName("Наличие прослоек")
    public Boolean hasLayer = null;
    @ColumnName("Количество прокладок")
    public Integer layerNumber = null;

    public Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.D_1 = D_1;
        parameters.D_2 = D_2;
        parameters.a = a;
        parameters.L = L;
        parameters.sigma_1 = sigma_1;
        return parameters;
    }

    public void saveAll(DBManager dbManager) {
        Wheel lWheel = new Wheel();
        lWheel.n = n_1;
        lWheel.d = D_1;
        lWheel.dr = D_1_r;
        Wheel bWheel = new Wheel();
        bWheel.n = n_2;
        bWheel.d = D_2;
        bWheel.dr = D_2_r;
        Belt belt = new Belt();
        belt.v = v;
        belt.mat = mat;
        belt.thick = thick;
        belt.width = width;
        belt.xi = xi;
        belt.L = L;
        belt.bType = bType;
        belt.hasLayer = hasLayer;
        belt.layerNumber = layerNumber;
        belt.L_min = L_min;
        belt.lambda = lambda;
        belt.vr = vr;
        belt.L_diff = L_diff;
        belt.Lr = Lr;
        AssemblyUnit unit = new AssemblyUnit();
        unit.m_s = m_s;
        unit.N = N;
        unit.m_l = m_l;
        unit.speed = speed;
        unit.u = u;
        unit.a_min = a_min;
        unit.a = a;
        unit.i_max = i_max;
        unit.i = i;
        unit.D_cp = D_cp;
        unit.delta = delta;
        unit.sigma_1 = sigma_1;
        unit.type = type;
        unit.lId = dbManager.insertInto("parts", lWheel.toPart());
        unit.bId = dbManager.insertInto("parts", bWheel.toPart());
        unit.beltId = dbManager.insertInto("parts", belt.toPart());
        dbManager.insertInto("assembly-units", unit);
    }

    public void setLWheel(Wheel lWheel) {
        n_1 = lWheel.n;
        D_1 = lWheel.d;
        D_1_r = lWheel.dr;
    }

    public void setBWheel(Wheel lWheel) {
        n_2 = lWheel.n;
        D_2 = lWheel.d;
        D_2_r = lWheel.dr;
    }
}
