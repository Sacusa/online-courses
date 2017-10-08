package library.staff;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import library.SmallLibrary;

public class SmallLibraryTest {

    @Test
    public void testUsesTheGivenRep() {
        Field[] fields = SmallLibrary.class.getDeclaredFields();
        Map<String, Class<?>> expectedFields = new HashMap<>();
        expectedFields.put("inLibrary", Set.class);
        expectedFields.put("checkedOut", Set.class);
        for (Field field : fields) {
            String name = field.getName();
            Class<?> type = field.getType();
            if (name.startsWith("$")) {
                continue; // internal Java runtime field, ignore it
            }
            assertTrue("SmallLibrary rep has an extra field named " + name,
                       expectedFields.containsKey(name));
            assertTrue("SmallLibrary rep field " + name + " should have type " + expectedFields.get(name),
                       expectedFields.get(name).equals(type));
            expectedFields.remove(name);
        }
        if (expectedFields.size() > 0) {
            fail("SmallLibrary rep is missing fields " + expectedFields.keySet());
        }
    }

}
