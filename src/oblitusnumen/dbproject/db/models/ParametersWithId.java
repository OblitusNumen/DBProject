package oblitusnumen.dbproject.db.models;

import oblitusnumen.dbproject.Utils;
import oblitusnumen.dbproject.db.ColumnName;

public class ParametersWithId extends Parameters {
    @ColumnName("№")
    public int id;

    public ParametersWithId(Object init, int id) {
        this.id = id;
        Utils.copyFields(this, init);
    }
}
