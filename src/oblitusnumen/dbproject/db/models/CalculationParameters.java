package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.DBManager;

public class CalculationParameters {
    public String m_s = null;
    public double D_1 = Double.NaN;
    public double N = Double.NaN;
    public double n_1 = Double.NaN;
    public double n_2 = Double.NaN;
    public double u = Double.NaN;
    public double xi = Double.NaN;
    public double D_2 = Double.NaN;
    public double vr = Double.NaN;
    public String type = null;
    public String speed = null;
    public double a_min = Double.NaN;
    public double a = Double.NaN;
    public String m_l = null;
    public double L = Double.NaN;
    public double i_max = Double.NaN;
    public double i = Double.NaN;
    public double L_min = Double.NaN;
    public double Lr = Double.NaN;
    public double L_diff = Double.NaN;
    public double D_cp = Double.NaN;
    public double lambda = Double.NaN;
    public double delta = Double.NaN;
    public double sigma_1 = Double.NaN;
    public double v = Double.NaN;
    public double D_1_r = Double.NaN;
    public double D_2_r = Double.NaN;
    public String mat = null;
    public double width = Double.NaN;
    public double thick = Double.NaN;
    public String bType = null;
    public boolean hasLayer;
    public int layerNumber;

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
        Wheel bWheel = new Wheel();
        bWheel.n = n_2;
        bWheel.d = D_2;
    }
}
