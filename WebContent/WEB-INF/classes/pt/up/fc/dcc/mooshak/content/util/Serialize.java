package pt.up.fc.dcc.mooshak.content.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Static methods for custom serialization
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Serialize {

	private static final int BUFFER_SIZE = 1024;

	/**
	 * Marshal path (java.nio.file.Path)
	 * 
	 * @param path java.nio.file.Path (which is not a serializable type)
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writePath(Path path, ObjectOutputStream out)
			throws IOException {
		byte[] buffer;

		if(path == null)
			out.writeInt(-1);
		else {
			buffer = path.toString().getBytes();
			out.writeInt(buffer.length);
			out.write(buffer);
		}

	}

	/**
	 * Unmarshal a path (java.nio.file.Path)
	 * @param in ObjectInputStream
	 * @return Java.nio.file.Path (which is not a serializable type)
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Path readPath(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int len = in.readInt();
		byte[] buffer =  new byte[BUFFER_SIZE];
		Path path;

		if(len == -1)
			path = null;
		else if(len > BUFFER_SIZE)
			throw new IOException("Path serialization exceeds buffer size");
		else {
			in.read(buffer,0,len);
			path = Paths.get(new String(buffer,0,len));
		}

		return path;
	}



	/**
	 * Marshal a String as a sequence of bytes preceded by its size
	 * 
	 * @param text String
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeString(String text, ObjectOutputStream out)
			throws IOException {
		byte[] buffer;

		if(text == null)
			out.writeInt(-1);
		else {
			buffer = text.getBytes();
			out.writeInt(buffer.length);
			out.write(buffer);
		}
		out.flush();
	}

	/**
	 * Unmarshal a String as a sequence of bytes preceded by its size
	 * @param in ObjectInputStream
	 * @return String
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static String readString(ObjectInputStream in)
			throws IOException, ClassNotFoundException {

		int len = in.readInt();
		byte[] buffer =  new byte[BUFFER_SIZE];
		String text;

		if(len == -1)
			text = null;
		else if(len > BUFFER_SIZE)
			throw new IOException("Serialization exceeds buffer size");
		else {
			in.read(buffer,0,len);
			text = new String(buffer,0,len);
		}

		return text;
	}
	
	
	/**
	 * Marshal an integer that may be either a null or an object
	 * @param value Integer
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeInt(Integer value, ObjectOutputStream out) 
			throws IOException {
		
		out.writeBoolean(value == null);
		if(value != null)
			out.writeInt(value);
	}

	/**
	 * Unmarshal an integer that may be either a null or an object
	 * @param in ObjectInputStream
	 * @return Integer (or null)
	 * @throws IOException
	 */
	public static Integer readInt(ObjectInputStream in) throws IOException {
		if(in.readBoolean())
			return null;
		else
			return in.readInt();
	}
	
	/**
	 * Marshal a long integer that may be either a null or an object
	 * @param value Long
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeLong(Long value, ObjectOutputStream out) 
			throws IOException {
		
		out.writeBoolean(value == null);
		if(value != null)
			out.writeLong(value);
	}

	/**
	 * Unmarshal a long integer that may be either a null or an object
	 * @param in ObjectInputStream
	 * @return Long (or null)
	 * @throws IOException
	 */
	public static Long readLong(ObjectInputStream in) throws IOException {
		if(in.readBoolean())
			return null;
		else
			return in.readLong();
	}
	
	/**
	 * Marshal a float that may be either a null or an object
	 * @param value Float
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeFloat(Float value, ObjectOutputStream out) 
			throws IOException {
		
		out.writeBoolean(value == null);
		if(value != null)
			out.writeFloat(value);
	}

	/**
	 * Unmarshal a float that may be either a null or an object
	 * @param in ObjectInputStream
	 * @return Float (or null)
	 * @throws IOException
	 */
	public static Float readFloat(ObjectInputStream in) throws IOException {
		if(in.readBoolean())
			return null;
		else
			return in.readFloat();
	}
	
	/**
	 * Marshal a double that may be either  a null or an object
	 * @param value Double
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeDouble(Double value, ObjectOutputStream out) 
			throws IOException {
		
		out.writeBoolean(value == null);
		if(value != null)
			out.writeDouble(value);
	}

	/**
	 * Unmarshal a double that may  either be a null or an object
	 * @param in ObjectInputStream
	 * @return Double (or null)
	 * @throws IOException
	 */
	public static Double readDouble(ObjectInputStream in) throws IOException {
		if(in.readBoolean())
			return null;
		else
			return in.readDouble();
	}
	
	/**
	 * Marshal a date that may be either either a null or an object
	 * @param value Date
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeDate(Date date, ObjectOutputStream out) 
			throws IOException {
		
		out.writeBoolean(date == null);
		if(date != null)
			out.writeLong(date.getTime());
	}

	/**
	 * Unmarshal a date that may  either be either a null or an object
	 * @param in ObjectInputStream
	 * @return Date (or null)
	 * @throws IOException
	 */
	public static Date readDate(ObjectInputStream in) throws IOException {
		
		if(in.readBoolean())
			return null;
		else
			return new Date(in.readLong());
	}

	/**
	 * Marshal an enumerated value that may be either either a null or an object
	 * @param value Integer
	 * @param out ObjectOutputStream
	 * @throws IOException
	 */
	public static void writeEnum(Enum<?> value, ObjectOutputStream out)
			throws IOException {

		if(value == null)
			writeString(null, out);
		else
			writeString(value.toString(), out);
	}
	
	/**
	 * Unmarshal an enumerated value that may  either be either a null or an object
	 * @param in ObjectInputStream
	 * @return Float (or null)
	 * @throws IOException
	 */
	public static <E extends Enum<E>> 
		E readEnum(Class<E> enumType, ObjectInputStream in)
			throws IOException, ClassNotFoundException {

		String text = readString(in);
		if(text == null)
			return null;
		else
			return Enum.valueOf(enumType, text.replaceAll(" ", "_").toUpperCase());
	}
	
	

}
