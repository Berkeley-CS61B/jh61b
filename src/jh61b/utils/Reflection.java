package jh61b.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class Reflection {
    public static <T> T getFieldValue(Class clazz, String name, Object o) {
        T result = null;
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not find field " + name + " in class " + clazz.getName());
            return null;
        }
    }

    public static Method getMethod(Class clazz, String name, Class<?>... params) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, params);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail("Could not find method " + name + " in class " + clazz.getName());
            return null;
        }
        return method;
    }

    public static Constructor getConstructor(Class clazz, Class<?>... params) {
        Constructor c = null;
        try {
            c = clazz.getDeclaredConstructor(params);
            c.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail("Could not find constructor " + clazz.getName() + "("
                    + String.join(", ", (String[]) Stream.of(params).map(x -> x.getName()).collect(Collectors.toList()).toArray())
                    + ")" + " in class " + clazz.getName());
            return null;
        }
        return c;
    }

    public static <T> T invoke(Method m, Object... args) {
        T result = null;
        try {
            result = (T) m.invoke(args[0], Arrays.copyOfRange(args, 1, args.length));
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e.getCause());
        }
        return result;
    }

    public static <T> T invokeStatic(Method m, Object... args) {
        T result = null;
        try {
            result = (T) m.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fail(e.getCause());
        }
        return result;
    }

    public static <T> T newInstance(Constructor c, Object... args) {
        T result = null;
        try {
            result = (T) c.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            fail(e.getCause());
        }
        return result;
    }

    public static Stream<Field> getFields(Class clazz) {
        return Stream.of(clazz.getDeclaredFields());
    }

    private static int stringToIntModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "private":
                return Modifier.PRIVATE;
            case "public":
                return Modifier.PUBLIC;
            case "protected":
                return Modifier.PROTECTED;
            case "static":
                return Modifier.STATIC;
            case "final":
                return Modifier.FINAL;
            default:
                fail("Unknown modifier test.");
        }
        /* Should never reach here... */
        return -1;
    }

    public static Predicate<Member> hasModifier(String modifier) {
        return (Member f) -> (f.getModifiers() & stringToIntModifier(modifier)) != 0;
    }

    public static Predicate<Member> doesNotHaveModifier(String modifier) {
        return (Member f) -> (f.getModifiers() & stringToIntModifier(modifier)) == 0;
    }

    public static Predicate<Field> hasType(Class clazz) {
        return (Field f) -> f.getType().equals(clazz);
    }

    public static Predicate<Field> doesNotHaveType(Class clazz) {
        return (Field f) -> !f.getType().equals(clazz);
    }

    public static void assertFieldsLessThan(Class clazz, Class FieldType, int x) {
        assertFieldsLessThan(clazz, null, FieldType, x);
    }

    public static void assertFieldsLessThan(Class clazz, String modifier, int x) {
        assertFieldsLessThan(clazz, modifier, null, x);
    }

    public static void assertFieldsLessThan(Class clazz, Stream<Field> fields, int x) {
        assertFieldsLessThan(clazz, fields, null, null, x);
    }

    public static void assertFieldsLessThan(Class clazz, String modifier, Class FieldType, int x) {
        assertFieldsLessThan(clazz, getFields(clazz), modifier, FieldType, x);
    }

    public static void assertFieldsLessThan(Class clazz, Stream<Field> fields, String modifier, Class FieldType, int x) {
        if (modifier != null) {
            fields = fields.filter(hasModifier(modifier)).filter(doesNotHaveModifier("static"));
        }
        if (FieldType != null) {
            fields = fields.filter(hasType(FieldType));
        }

        if (fields.count() >= x) {
            fail(clazz.getName() + " has too many fields" + (modifier != null ? " with modifier " + modifier : "") + ""
                    + (FieldType != null ? " of type " + FieldType.getName() : ""));
        }
    }

    public static void assertFieldsGreaterThan(Class clazz, Class FieldType, int x) {
        assertFieldsGreaterThan(clazz, null, FieldType, x);
    }

    public static void assertFieldsGreaterThan(Class clazz, String modifier, int x) {
        assertFieldsGreaterThan(clazz, modifier, null, x);
    }

    public static void assertFieldsGreaterThan(Class clazz, Stream<Field> fields, int x) {
        assertFieldsGreaterThan(clazz, fields, null, null, x);
    }

    public static void assertFieldsGreaterThan(Class clazz, String modifier, Class FieldType, int x) {
        assertFieldsGreaterThan(clazz, getFields(clazz), modifier, FieldType, x);
    }

    public static void assertFieldsGreaterThan(Class clazz, Stream<Field> fields, String modifier, Class FieldType,
                                               int x) {
        if (modifier != null) {
            fields = fields.filter(hasModifier(modifier));
        }
        if (FieldType != null) {
            fields = fields.filter(hasType(FieldType));
        }

        if (fields.count() <= x) {
            fail(clazz.getName() + " has too few fields" + (modifier != null ? " with modifier " + modifier : "") + " "
                    + (FieldType != null ? " of type " + FieldType.getName() : ""));
        }
    }

    public static void assertFieldsEqualTo(Class clazz, Class FieldType, int x) {
        assertFieldsEqualTo(clazz, null, FieldType, x);
    }

    public static void assertFieldsEqualTo(Class clazz, String modifier, int x) {
        assertFieldsEqualTo(clazz, modifier, null, x);
    }

    public static void assertFieldsEqualTo(Class clazz, Stream<Field> fields, int x) {
        assertFieldsEqualTo(clazz, fields, null, null, x);
    }

    public static void assertFieldsEqualTo(Class clazz, String modifier, Class FieldType, int x) {
        assertFieldsEqualTo(clazz, getFields(clazz), modifier, FieldType, x);
    }

    public static void assertFieldsEqualTo(Class clazz, Stream<Field> fields, String modifier, Class FieldType, int x) {
        if (modifier != null) {
            fields = fields.filter(hasModifier(modifier));
        }
        if (FieldType != null) {
            fields = fields.filter(hasType(FieldType));
        }

        if (fields.count() != x) {
            fail(clazz.getName() + " has the wrong number of fields" + (modifier != null ? " with modifier " + modifier : "")
                    + " " + (FieldType != null ? " of type " + FieldType.getName() : ""));
        }
    }

    public static void assertNoPublicFields(Class clazz) {
        assertFieldsEqualTo(clazz, getFields(clazz).filter(doesNotHaveModifier("static")), "public", null, 0);
    }

    public static void assertNoProtectedFields(Class clazz) {
        assertFieldsEqualTo(clazz, getFields(clazz).filter(doesNotHaveModifier("static")), "protected", null, 0);
    }

    public static void assertFieldModifiers(Class clazz) {
        List<Field> fields = getFields(clazz).collect(Collectors.toList());
        for (Field f : fields) {
            int m = f.getModifiers();
            assertTrue(Modifier.isPrivate(m) || Modifier.isProtected(m) || Modifier.isPublic(m),
                    "Field \"" + f.getName() + "\" does not have one of {public, private, protected}");
        }
    }

    public static Field getFieldByName(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            fail(clazz.getName() + " should have a field named '" + name + "', but does not.");
            // Should not reach here!
            return null;
        }
    }

    public static Field getNonStaticFieldByType(Class clazz, Class FieldType) {
        Stream<Field> fields = getFields(clazz).filter(hasType(FieldType)).filter(doesNotHaveModifier("static"));
        List<Field> fieldsList = fields.collect(Collectors.toList());
        if (fieldsList.isEmpty()) {
            fail(clazz.getName() + " should have a field with the type '" + FieldType.getName() + "', but does not.");
            // Should not reach here!
            return null;
        }

        if (fieldsList.size() > 1) {
            fail(clazz.getName() + " should only have one field with the type '" + FieldType.getName() + "', but has more.");
            // Should not reach here
            return null;
        }

        return fieldsList.get(0);
    }

    public static Field getFieldByType(Class clazz, Class FieldType) {
        Stream<Field> fields = getFields(clazz).filter(hasType(FieldType));
        List<Field> fieldsList = fields.collect(Collectors.toList());
        if (fieldsList.isEmpty()) {
            fail(clazz.getName() + " should have a field with the type '" + FieldType.getName() + "', but does not.");
            // Should not reach here!
            return null;
        }

        if (fieldsList.size() > 1) {
            fail(clazz.getName() + " should only have one field with the type '" + FieldType.getName() + "', but has more.");
            // Should not reach here
            return null;
        }

        return fieldsList.get(0);
    }

    public static Field getFieldByModifiers(Class clazz, String modifier) {
        return getFieldByModifiers(clazz, List.of(modifier));
    }

    public static Field getFieldByModifiers(Class clazz, List<String> modifiers) {
        Stream<Field> fields = getFields(clazz);
        for (String m : modifiers) {
            fields = fields.filter(hasModifier(m));
        }
        List<Field> fieldsList = fields.collect(Collectors.toList());
        if (fieldsList.isEmpty()) {
            fail(clazz.getName() + " should have a field with the modifiers '" + String.join(", ", modifiers)
                    + "', but does not.");
            // Should not reach here!
            return null;
        }

        if (fieldsList.size() > 1) {
            fail(clazz.getName() + " should only have one field with the modifiers '" + String.join(", ", modifiers)
                    + "', but has more.");
            // Should not reach here
            return null;
        }

        return fieldsList.get(0);
    }

    public static void checkFieldModifiers(Field f, String modifier) {
        checkFieldModifiers(f, List.of(modifier));
    }

    public static void checkFieldModifiers(Field f, List<String> modifiers) {
        if (!modifiers.stream().allMatch(m -> hasModifier(m).test(f))) {
            fail(f.getName() + " is missing at least one of the modifiers: " + String.join(", ", modifiers));
        }
    }

    public static void assertPublicInterface(Class clazz, List<String> methods) {
        SortedSet<String> expected = new TreeSet<>(methods);
        SortedSet<String> actual = new TreeSet<>(Stream.of(clazz.getDeclaredMethods()).filter(hasModifier("public"))
                .map(x -> x.getName()).collect(Collectors.toList()));
        if (!expected.equals(actual)) {
            String diff = "expected: " + expected + "\nactual:  " + actual;
            fail("The public interface of " + clazz.getName() + " has incorrect functionality.\n" + diff);
        }
    }

    public static void assertMethodCorrectlyOverridden(Class clazz, String method, Class<?>... params) {
        Method studentc = getMethod(clazz, method, params);
        Method superc = getMethod(clazz.getSuperclass(), method, params);
        if (!studentc.getReturnType().equals(superc.getReturnType())) {
            fail("You should be overriding the " + method + "method, but your signature wasn't correct.");
        }
    }
}
