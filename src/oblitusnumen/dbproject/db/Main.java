package oblitusnumen.dbproject.db;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class Main {
    static final Map<String, DBManager.Type> typeAdaptation = getStringTypeMap();

    public static void main(String[] args) throws Exception {
        System.out.println("""
                <?xml version="1.0" encoding="utf-8" ?>
                <!-- SQL XML created by WWW SQL Designer, https://github.com/ondras/wwwsqldesigner/ -->
                <!-- Active URL: https://sql.toad.cz/ -->
                <sql>
                <datatypes db="mysql">
                	<group label="Numeric" color="rgb(238,238,170)">
                		<type label="Integer" length="0" sql="INTEGER" quote=""/>
                	 	<type label="TINYINT" length="0" sql="TINYINT" quote=""/>
                	 	<type label="SMALLINT" length="0" sql="SMALLINT" quote=""/>
                	 	<type label="MEDIUMINT" length="0" sql="MEDIUMINT" quote=""/>
                	 	<type label="INT" length="0" sql="INT" quote=""/>
                		<type label="BIGINT" length="0" sql="BIGINT" quote=""/>
                		<type label="Decimal" length="1" sql="DECIMAL" re="DEC" quote=""/>
                		<type label="Single precision" length="0" sql="FLOAT" quote=""/>
                		<type label="Double precision" length="0" sql="DOUBLE" re="DOUBLE" quote=""/>
                	</group>
                
                	<group label="Character" color="rgb(255,200,200)">
                		<type label="Char" length="1" sql="CHAR" quote="'"/>
                		<type label="Varchar" length="1" sql="VARCHAR" quote="'"/>
                		<type label="Text" length="0" sql="MEDIUMTEXT" re="TEXT" quote="'"/>
                		<type label="Binary" length="1" sql="BINARY" quote="'"/>
                		<type label="Varbinary" length="1" sql="VARBINARY" quote="'"/>
                		<type label="BLOB" length="0" sql="BLOB" re="BLOB" quote="'"/>
                	</group>
                
                	<group label="Date &amp; Time" color="rgb(200,255,200)">
                		<type label="Date" length="0" sql="DATE" quote="'"/>
                		<type label="Time" length="0" sql="TIME" quote="'"/>
                		<type label="Datetime" length="0" sql="DATETIME" quote="'"/>
                		<type label="Year" length="0" sql="YEAR" quote=""/>
                		<type label="Timestamp" length="0" sql="TIMESTAMP" quote="'"/>
                	</group>
                
                	<group label="Miscellaneous" color="rgb(200,200,255)">
                		<type label="ENUM" length="1" sql="ENUM" quote=""/>
                		<type label="SET" length="1" sql="SET" quote=""/>
                		<type label="Bit" length="0" sql="bit" quote=""/>
                	</group>
                </datatypes>""");
        List<String> input = new ArrayList<>();
        try (InputStream inputStream = Main.class.getResourceAsStream("/.tables")) {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNext()) {
                    input.add(scanner.next());
                }
            }
        }
        String[] split = input.toArray(new String[0]);
        for (String s : split) {
            if (s.isEmpty()) continue;
            String[] table = s.split("[\t,]");
            printTable(table[0], Class.forName(table[1]));
        }
        System.out.println("</sql>");
    }

    private static void printTable(String table, Class<?> clazz) {
        System.out.printf("""
                <table x="124" y="243" name="%s">
                """, table);
        System.out.printf("""
                <row name="%s" null="1" autoincrement="1">
                <datatype>%s</datatype>
                <default>NULL</default></row>
                """, "id", "INTEGER");
        for (Field field : clazz.getFields()) {
            String cn = field.getName();
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof ColumnName columnName) {
                    cn = columnName.value();
                    break;
                }
            }
            System.out.printf("""
                    <row name="%s" null="1" autoincrement="1">
                    <datatype>%s</datatype>
                    <default>NULL</default></row>
                    """, cn + ", " + field.getName(), typeAdaptation.get(field.getType().toString()));
        }
        System.out.println("""
                <key type="PRIMARY" name="">
                <part>id</part>
                </key>""");
        System.out.println("</table>");
    }

    private static Map<String, DBManager.Type> getStringTypeMap() {
        Map<String, DBManager.Type> typeAdaptation = new HashMap<>();
        for (DBManager.Type value : DBManager.Type.values()) {
            typeAdaptation.put(value.type, value);
        }
        return typeAdaptation;
    }
}
