package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.ColumnName;
import oblitusnumen.dbproject.db.DBManager;

public class AssemblyUnit {
    @ColumnName("Метод определения диаметра меньшего шкива")
    public String m_s = null;
    @ColumnName("Мощность")
    public Double N = null;
    @ColumnName("Метод определения длины ремня")
    public String m_l = null;
    @ColumnName("Быстроходность передачи")
    public String speed = null;
    @ColumnName("Передаточное число")
    public Double u = null;
    @ColumnName("Минимальное межосевое расстояние")
    public Double a_min = null;
    @ColumnName("Межосевое расстояние")
    public Double a = null;
    @ColumnName("Максимальная частота пробега ремня в секунду")
    public Double i_max = null;
    @ColumnName("Частота пробега ремня в секунду")
    public Double i = null;
    @ColumnName("Средний диаметр шкивов")
    public Double D_cp = null;
    @ColumnName("Разница в размерах шкивов")
    public Double delta = null;
    @ColumnName("Угол обхвата на меньшем шкиве")
    public Double sigma_1 = null;
    @ColumnName("Вид передачи")
    public String type = null;
    @ColumnName("ID меньшего шкива")
    public Integer lId = null;
    @ColumnName("ID большего шкива")
    public Integer bId = null;
    @ColumnName("ID ремня")
    public Integer beltId = null;

    public CalculationParameters toCalculationParameters(DBManager dbManager) {
        CalculationParameters parameters = new CalculationParameters();
        parameters.m_s = m_s;
        parameters.N = N;
        parameters.m_l = m_l;
        parameters.speed = speed;
        parameters.u = u;
        parameters.a_min = a_min;
        parameters.a = a;
        parameters.i_max = i_max;
        parameters.i = i;
        parameters.D_cp = D_cp;
        parameters.delta = delta;
        parameters.sigma_1 = sigma_1;
        parameters.type = type;
        Belt belt = Belt.ofPart(dbManager.getById("parts", beltId));
        Wheel lWheel = Wheel.ofPart(dbManager.getById("parts", lId));
        Wheel bWheel = Wheel.ofPart(dbManager.getById("parts", beltId));
        parameters.v = belt.v;
        parameters.mat = belt.mat;
        parameters.thick = belt.thick;
        parameters.width = belt.width;
        parameters.xi = belt.xi;
        parameters.L = belt.L;
        parameters.bType = belt.bType;
        parameters.hasLayer = belt.hasLayer;
        parameters.layerNumber = belt.layerNumber;
        parameters.L_min = belt.L_min;
        parameters.lambda = belt.lambda;
        parameters.vr = belt.vr;
        parameters.L_diff = belt.L_diff;
        parameters.Lr = belt.Lr;
        parameters.setLWheel(lWheel);
        parameters.setBWheel(bWheel);
        return parameters;
    }
}
