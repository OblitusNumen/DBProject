package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.db.DBManager;

public class CalculationParameters {
    public String m_s;
    public double D_1;
    public double N;
    public double n_1;
    public double n_2;
    public double u;
    public double xi;
    public double D_2;
    public double vr;
    public String type;
    public String speed;
    public double a_min;
    public double a;
    public String m_l;
    public double L;
    public double i_max;
    public double i;
    public double L_min;
    public double Lr;
    public double L_diff;
    public double D_cp;
    public double lambda;
    public double delta;
    public double sigma_1;
    public double v;
    public double D_1_r;
    public double D_2_r;
    public String mat;
    public double width;
    public double thick;

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
