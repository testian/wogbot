package core;
import java.io.*;
public class FileClassLoader extends ClassLoader {
private String path;
public static final String extension=".class";
public FileClassLoader(String path)
{
	
super();
if (path.length()>0 && path.charAt(path.length()-1) != File.separatorChar) {path = path + File.separatorChar;}
this.path=path;

}
	
	public Class<?> findClass(String className) throws ClassNotFoundException
	{
		File classFile = new File(path + className.replace('.',File.separatorChar) + extension);
		try {
		FileInputStream readByteCode = new FileInputStream(classFile);
		byte[] byteCode = new byte[(int)classFile.length()];
		try {
		readByteCode.read(byteCode);
		return defineClass(className, byteCode,0,byteCode.length);
		} catch (IOException ex ) {throw new ClassNotFoundException();}
		}catch (FileNotFoundException ex) {throw new ClassNotFoundException();}
	}
}
