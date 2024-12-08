package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.Utils;
import oblitusnumen.dbproject.db.ColumnName;

public class CalculationParametersWithId extends CalculationParameters {
    @ColumnName("№")
    public int id;

    public CalculationParametersWithId(Object init, int id) {
        this.id = id;
        Utils.copyFields(this, init);
    }
}