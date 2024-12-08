package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.DBManager;

public class CalculationParameters {
    public String m_s = null;
    public Double D_1 = null;
    public Double N = null;
    public Double n_1 = null;
    public Double n_2 = null;
    public Double u = null;
    public Double xi = null;
    public Double D_2 = null;
    public Double vr = null;
    public String type = null;
    public String speed = null;
    public Double a_min = null;
    public Double a = null;
    public String m_l = null;
    public Double L = null;
    public Double i_max = null;
    public Double i = null;
    public Double L_min = null;
    public Double Lr = null;
    public Double L_diff = null;
    public Double D_cp = null;
    public Double lambda = null;
    public Double delta = null;
    public Double sigma_1 = null;
    public Double v = null;
    public Double D_1_r = null;
    public Double D_2_r = null;
    public String mat = null;
    public Double width = null;
    public Double thick = null;
    public String bType = null;
    public Boolean hasLayer = null;
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
    }
}
