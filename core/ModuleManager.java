package core;
import java.io.*;
import staticmodules.*;
public class ModuleManager {
private String path;
//FileClassLoader loader;
SessionCommandManager commandManager;
public ModuleManager(String path, SessionCommandManager commandManager)
{
	if (path.length()>0 && path.charAt(path.length()-1) != File.separatorChar) {path = path + File.separatorChar;}
this.path=path;
this.commandManager=commandManager;


}
synchronized public int reload(String subdirectory)
{
File directory = new File(path + subdirectory);
String[] fileList = directory.list();
if (fileList == null){return 0;}
int loadCount=0;

commandManager.clear(); //Delete all references to the BotCommand Modules

commandManager.add(new ReloadCommand()); //Load all static modules

System.gc();
System.gc();//TODO: Twice because "someone" said so -> weird, (check if it works with a single call, later)
//It doesn't even need a single call - I wonder if reloading is dependable in that case
FileClassLoader loader = new FileClassLoader(path);

for (int i = 0; i < fileList.length;i++)
{
if (fileList[i].length()>=FileClassLoader.extension.length() &&
fileList[i].substring(fileList[i].length()-FileClassLoader.extension.length()).equals(FileClassLoader.extension)		
){
	String className = subdirectory.replace(File.separatorChar,'.') + "." + fileList[i].substring(0,fileList[i].length()-FileClassLoader.extension.length());
	try {	

	Class newClass = loader.findClass(className);
	//System.out.println("class name: " + className);
	Object module = newClass.newInstance();
	if (module instanceof SessionCommand)
    {
		//commandManager.remove(((BotCommand)module).getName());
		commandManager.add((SessionCommand)module);
		loadCount++;
    }
}
catch (ClassNotFoundException ex){System.err.println(ex);}
catch (IllegalAccessException ex){System.err.println(ex);}
catch (InstantiationException ex){System.err.println(ex);}
catch (LinkageError ex){System.err.println(ex);}
}
}
return loadCount;
}
}
