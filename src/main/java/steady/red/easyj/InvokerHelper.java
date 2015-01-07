/*
* Copyright 2003-2013 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package steady.red.easyj;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
* A static helper class to make bytecode generation easier and act as a facade over the Invoker
*
* @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
*/
public class InvokerHelper {
    private static final Object[] EMPTY_MAIN_ARGS = new Object[]{new String[0]};
 
    public static final Object[] EMPTY_ARGS = {};
    protected static final Object[] EMPTY_ARGUMENTS = EMPTY_ARGS;
    protected static final Class[] EMPTY_TYPES = {};
 
    public static List asList(Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        }
        if (value instanceof List) {
            return (List) value;
        }
        if (value.getClass().isArray()) {
            return Arrays.asList((Object[]) value);
        }
        if (value instanceof Enumeration) {
            List answer = new ArrayList();
            for (Enumeration e = (Enumeration) value; e.hasMoreElements();) {
                answer.add(e.nextElement());
            }
            return answer;
        }
        // lets assume its a collection of 1
        return Collections.singletonList(value);
    }
 
    public static String toString(Object arguments) {
        if (arguments instanceof Object[])
            return toArrayString((Object[]) arguments);
        if (arguments instanceof Collection)
            return toListString((Collection) arguments);
        if (arguments instanceof Map)
            return toMapString((Map) arguments);
        return format(arguments, false);
    }
 
    public static String inspect(Object self) {
        return format(self, true);
    }
 
    /**
     * Find the right hand regex within the left hand string and return a matcher.
     *
     * @param left  string to compare
     * @param right regular expression to compare the string to
     */
    public static Matcher findRegex(Object left, Object right) {
        String stringToCompare;
        if (left instanceof String) {
            stringToCompare = (String) left;
        } else {
            stringToCompare = toString(left);
        }
        String regexToCompareTo;
        if (right instanceof String) {
            regexToCompareTo = (String) right;
        } else if (right instanceof Pattern) {
            Pattern pattern = (Pattern) right;
            return pattern.matcher(stringToCompare);
        } else {
            regexToCompareTo = toString(right);
        }
        return Pattern.compile(regexToCompareTo).matcher(stringToCompare);
    }
 
    /**
     * Find the right hand regex within the left hand string and return a matcher.
     *
     * @param left  string to compare
     * @param right regular expression to compare the string to
     */
    public static boolean matchRegex(Object left, Object right) {
        if (left == null || right == null) return false;
        Pattern pattern;
        if (right instanceof Pattern) {
            pattern = (Pattern) right;
        } else {
            pattern = Pattern.compile(toString(right));
        }
        String stringToCompare = toString(left);
        Matcher matcher = pattern.matcher(stringToCompare);
 
        return matcher.matches();
    }
 
    public static List createList(Object[] values) {
        List answer = new ArrayList(values.length);
        answer.addAll(Arrays.asList(values));
        return answer;
    }
 
    public static Map createMap(Object[] values) {
        Map answer = new LinkedHashMap(values.length / 2);
        int i = 0;
        while (i < values.length - 1) {
               answer.put(values[i++], values[i++]);
        }
        return answer;
    }
 
    /**
     * Writes an object to a Writer using Groovy's default representation for the object.
     */
    public static void write(Writer out, Object object) throws IOException {
        if (object instanceof String) {
            out.write((String) object);
           
        } else if (object instanceof Object[]) {
            out.write(toArrayString((Object[]) object));
           
        } else if (object instanceof Map) {
            out.write(toMapString((Map) object));
           
        } else if (object instanceof Collection) {
            out.write(toListString((Collection) object));
            
        } else if (object instanceof InputStream || object instanceof Reader) {
            // Copy stream to stream
            Reader reader;
            if (object instanceof InputStream) {
                reader = new InputStreamReader((InputStream) object);
            } else {
                reader = (Reader) object;
            }
            char[] chars = new char[8192];
            int i;
            while ((i = reader.read(chars)) != -1) {
                out.write(chars, 0, i);
            }
            reader.close();
           
        } else {
            out.write(toString(object));
        }
    }
 
    /**
     * Appends an object to an Appendable using Groovy's default representation for the object.
     */
    public static void append(Appendable out, Object object) throws IOException {
        if (object instanceof String) {
            out.append((String) object);
           
        } else if (object instanceof Object[]) {
            out.append(toArrayString((Object[]) object));
           
        } else if (object instanceof Map) {
            out.append(toMapString((Map) object));
           
        } else if (object instanceof Collection) {
            out.append(toListString((Collection) object));
           
        } else if (object instanceof InputStream || object instanceof Reader) {
            // Copy stream to stream
            Reader reader;
            if (object instanceof InputStream) {
                reader = new InputStreamReader((InputStream) object);
            } else {
                reader = (Reader) object;
            }
            char[] chars = new char[8192];
            int i;
            while ((i = reader.read(chars)) != -1) {
                for (int j = 0; j < i; j++) {
                    out.append(chars[j]);
                }
            }
            reader.close();
           
        } else {
            out.append(toString(object));
        }
    }
 
    protected static String format(Object arguments, boolean verbose) {
        return format(arguments, verbose, -1);
    }
 
    public static String format(Object arguments, boolean verbose, int maxSize) {
        if (arguments == null) {
            return "null";
        }
       
        if (arguments.getClass().isArray()) {
            if (arguments instanceof char[]) {
                return new String((char[]) arguments);
            }
            return format(Arrays.asList(arguments), verbose, maxSize);
        }
 
        if (arguments instanceof Collection) {
            return formatList((Collection) arguments, verbose, maxSize);
        }
       
        if (arguments instanceof Map) {
            return formatMap((Map) arguments, verbose, maxSize);
        }
 
        if (arguments instanceof String) {
            if (verbose) {
                String arg = ((String) arguments).replaceAll("\\n", "\\\\n");    // line feed
                arg = arg.replaceAll("\\r", "\\\\r");      // carriage return
                arg = arg.replaceAll("\\t", "\\\\t");      // tab
                arg = arg.replaceAll("\\f", "\\\\f");      // form feed
                arg = arg.replaceAll("'", "\\\\'");    // single quotation mark
                arg = arg.replaceAll("\\\\", "\\\\");      // backslash
                return "\'" + arg + "\'";
            } else {
                return (String) arguments;
            }
        }
 
        return arguments.toString();
    }
 
    private static String formatMap(Map map, boolean verbose, int maxSize) {
        if (map.isEmpty()) {
            return "[:]";
        }
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
        for (Object o : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            if (maxSize != -1 && buffer.length() > maxSize) {
                buffer.append("...");
                break;
            }
            Map.Entry entry = (Map.Entry) o;
            buffer.append(format(entry.getKey(), verbose));
            buffer.append(":");
            if (entry.getValue() == map) {
                buffer.append("(this Map)");
            } else {
                buffer.append(format(entry.getValue(), verbose, sizeLeft(maxSize, buffer)));
            }
        }
        buffer.append("]");
        return buffer.toString();
    }
 
    private static int sizeLeft(int maxSize, StringBuilder buffer) {
        return maxSize == -1 ? maxSize : Math.max(0, maxSize - buffer.length());
    }
 
    private static String formatList(Collection collection, boolean verbose, int maxSize) {
        return formatList(collection, verbose, maxSize, false);
    }
 
    private static String formatList(Collection collection, boolean verbose, int maxSize, boolean safe) {
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
       for (Object item : collection) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            if (maxSize != -1 && buffer.length() > maxSize) {
                buffer.append("...");
                break;
            }
            if (item == collection) {
                buffer.append("(this Collection)");
            } else {
                String str;
                try {
                    str = format(item, verbose, sizeLeft(maxSize, buffer));
                } catch (Exception ex) {
                    if (!safe) throw new RuntimeException(ex);
                    String hash;
                    try {
                        hash = Integer.toHexString(item.hashCode());
                    } catch (Exception ignored) {
                        hash = "????";
                    }
                    str = "<" + item.getClass().getName() + "@" + hash + ">";
                }
                buffer.append(str);
            }
        }
        buffer.append("]");
        return buffer.toString();
    }
 
    /**
     * A helper method to format the arguments types as a comma-separated list.
     *
     * @param arguments the type to process
     * @return the string representation of the type
     */
    public static String toTypeString(Object[] arguments) {
        if (arguments == null) {
            return "null";
        }
        StringBuilder argBuf = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                argBuf.append(", ");
            }
            argBuf.append(arguments[i] != null ? arguments[i].getClass().getName() : "null");
        }
        return argBuf.toString();
    }
 
    /**
     * A helper method to return the string representation of a map with bracket boundaries "[" and "]".
     *
     * @param arg the map to process
     * @return the string representation of the map
     */
    public static String toMapString(Map arg) {
        return toMapString(arg, -1);
    }
 
    /**
     * A helper method to return the string representation of a map with bracket boundaries "[" and "]".
     *
     * @param arg     the map to process
     * @param maxSize stop after approximately this many characters and append '...'
     * @return the string representation of the map
     */
    public static String toMapString(Map arg, int maxSize) {
        return formatMap(arg, false, maxSize);
    }
 
    /**
     * A helper method to return the string representation of a list with bracket boundaries "[" and "]".
     *
     * @param arg the collection to process
     * @return the string representation of the collection
     */
    public static String toListString(Collection arg) {
        return toListString(arg, -1);
    }
 
    /**
     * A helper method to return the string representation of a list with bracket boundaries "[" and "]".
     *
     * @param arg     the collection to process
     * @param maxSize stop after approximately this many characters and append '...'
     * @return the string representation of the collection
     */
    public static String toListString(Collection arg, int maxSize) {
        return toListString(arg, maxSize, false);
    }
 
    /**
     * A helper method to return the string representation of a list with bracket boundaries "[" and "]".
     *
     * @param arg     the collection to process
     * @param maxSize stop after approximately this many characters and append '...'
     * @param safe    whether to use a default object representation for any item in the collection if an exception occurs when generating its toString
     * @return the string representation of the collection
     */
    public static String toListString(Collection arg, int maxSize, boolean safe) {
        return formatList(arg, false, maxSize, safe);
    }
 
    /**
     * A helper method to return the string representation of an array of objects
     * with brace boundaries "[" and "]".
     *
     * @param arguments the array to process
     * @return the string representation of the array
     */
    public static String toArrayString(Object[] arguments) {
        if (arguments == null) {
            return "null";
        }
        String sbdry = "[";
        String ebdry = "]";
        StringBuilder argBuf = new StringBuilder(sbdry);
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                argBuf.append(", ");
            }
            argBuf.append(format(arguments[i], false));
        }
        argBuf.append(ebdry);
        return argBuf.toString();
    }
}

