package NoAgent;

import javassist.*;
import sun.misc.Unsafe;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class detect {
    private static String className = "getJPSAgent";
    private static String methodName = "print";
    public static Object insn;
    public static Method addtransform;
    public static Method retransform;
    public static ClassLoader cl;
    public static Class c;
    public native  long caloffset();

    public  int libb(){
        System.load("C:\\Users\\xyy\\source\\repos\\NOAgent\\x64\\Release\\NOAgent.dll");
        return 1;
    }
    public int print(int i) {
        System.out.println("i: " + i);
        return i + 2;
    }
    public Class<?>[] listclass(Object insn,Class instrument_clazz){
        try {
            Method getAllLoadedClasses = instrument_clazz.getMethod("getAllLoadedClasses");
            Class<?>[] clazzes = (Class<?>[]) getAllLoadedClasses.invoke(insn);
           // Method[] methods = instrument_clazz.getDeclaredMethods();
            for(Class cls:clazzes){
                System.out.println(cls.getName());
            }

            return clazzes;

        }catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }
    public void change(Object insn,Class instrument_clazz){
        try {
            Method addTransformer = instrument_clazz.getMethod("addTransformer", ClassFileTransformer.class,boolean.class);
            addTransformer.invoke(insn,new Object[]{new TestTransformer(className, methodName),true});
            try {
                List<Class> needRetransFormClasses = new LinkedList<>();
                Class[] loadedClass = listclass(insn,instrument_clazz);
                for (Class c : loadedClass) {
                    //System.out.println(loadedClass[i].getName());
                    if (c.getName().equals(className)) {
                        System.out.println("---find!!!---");
                        Method[] methods = c.getDeclaredMethods();
                        for(Method method : methods)
                        {System.out.println(method.getName());}
                        Method retransformClasses = instrument_clazz.getMethod("retransformClasses");
                        retransformClasses.invoke(insn,new Object[]{c});
                    }
                }


            } catch (Exception e) {

            }
        }catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    public void redefine(Object insn,Class instrument_clazz) throws IOException, CannotCompileException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass string_clazz = null;
        string_clazz = pool.get("java.io.RandomAccessFile");
        CtMethod method_getname = string_clazz.getDeclaredMethod("getFD");
        method_getname.insertBefore("System.out.println(\"hi, from java instrucment api defineClass\");");
        //CtClass ctClass = pool.makeClass(new FileInputStream("D:\\内存马\\java-agent\\java.io.RandomAccessFile.txt\\java\\io\\RandomAccessFile.class"));
        byte[] bytes = string_clazz.toBytecode();
        ClassDefinition definition = new ClassDefinition(Class.forName("java.io.RandomAccessFile"), bytes);
        Method redefineClazz = instrument_clazz.getMethod("redefineClasses", ClassDefinition[].class);
        redefineClazz.invoke(insn, new Object[] {
                new ClassDefinition[] {
                        definition
                }
        });

    }
    public void run() {
        System.load("C:\\Users\\xyy\\source\\repos\\NOAgent\\x64\\Release\\NOAgent.dll");
        int i = 1;
        while (true) {
            i = print(i);

            try {
                Thread.sleep(1000);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public detect(){
        System.out.println("Hello World");
        //System.load("C:\\Program Files\\Apache Software Foundation\\Tomcat 8.5\\webapps\\memshell\\WEB-INF\\lib\\NOAgent.dll");
    }

    public Object genImp(String dlladdress,detect getJPSAgent) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        System.load(dlladdress);
       // System.out.println(Long.toHexString(getJPSAgent.caloffset()));
        long native_jvmtienv = getJPSAgent.caloffset();
        //getJPSAgent.run();
        Unsafe unsafe = null;
        try {    Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);}
        catch (Exception e) {
            throw new AssertionError(e);}
        long JPLISAgent = unsafe.allocateMemory(0x100000);
        //unsafe.putLong(jvmtiStackAddr,jvmtiAddress);
        unsafe.putLong(native_jvmtienv+8,0x30010100000071eel);
        unsafe.putLong(native_jvmtienv+0x168,0x9090909000000200l);//实现redefineClass
        System.out.println("long:"+Long.toHexString(native_jvmtienv+0x168));
        unsafe.putLong(JPLISAgent,unsafe.getLong(native_jvmtienv) -0x9D6760);

        unsafe.putLong(JPLISAgent + 8, native_jvmtienv);//实现retransform,mNormalEnvironment.mJVMTIEnv;
        unsafe.putLong(JPLISAgent + 0x10, JPLISAgent);// mNormalEnvironment.mAgent;
        unsafe.putLong(JPLISAgent + 0x18, 0x00730065006c0000l);//mNormalEnvironment.mIsRetransformer; 决定是否可以retransform
        //make retransform env
        unsafe.putLong(JPLISAgent + 0x20, native_jvmtienv);//mRetransformEnvironment.mJVMTIEnv
        unsafe.putLong(JPLISAgent + 0x28, JPLISAgent);//mRetransformEnvironment.mAgent
        unsafe.putLong(JPLISAgent + 0x30, 0x0038002e00310001l);//mRetransformEnvironment.mIsRetransformer
        unsafe.putLong(JPLISAgent + 0x38,  0);//jobject                 mInstrumentationImpl;
        unsafe.putLong(JPLISAgent + 0x40, 0);// jmethodID               mPremainCaller;
        unsafe.putLong(JPLISAgent + 0x48, 0);//jmethodID               mAgentmainCaller;
        unsafe.putLong(JPLISAgent + 0x50, 0);//jmethodID               mTransform;
        unsafe.putLong(JPLISAgent + 0x58, 0x0072007400010001l);
        /*    jboolean                mRedefineAvailable;     /* cached answer to "does this agent support redefine"
        jboolean                mRedefineAdded;         /* indicates if can_redefine_classes capability has been added
        jboolean                mNativeMethodPrefixAvailable; /* cached answer to "does this agent support prefixing"
        jboolean                mNativeMethodPrefixAdded;     /* indicates if can_set_native_method_prefix capability has been added */
        unsafe.putLong(JPLISAgent + 0x60, JPLISAgent + 0x68);// char const *            mAgentClassName;        /* agent class name */
        unsafe.putLong(JPLISAgent + 0x68, 0x0041414141414141l);// char const *            mOptionsString;         /* -javaagent options string */
        Class<?> instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
        Constructor<?> constructor = instrument_clazz.getDeclaredConstructor(long.class, boolean.class, boolean.class);
        constructor.setAccessible(true);
        Object insn = constructor.newInstance(JPLISAgent, true, false);
        return  insn;
    }

    public boolean classFileIsExists(Class clazz){
        if(clazz == null){
            return false;
        }
        URL is;
        String className = clazz.getName();
        String classNamePath = className.replace(".", "/") + ".class";
        if(clazz.getClassLoader()==null)
        {
            is = ClassLoader.getSystemClassLoader().getResource(classNamePath);
        }
        else
        {is = clazz.getClassLoader().getResource(classNamePath);}
        if(is == null){
            return false;
        }else{
            return true;
        }
    }

    public String  codecheckanddump(String classname,String outputdir) throws IOException {
       String javaPath = classname.replace(".","\\") + ".java";
       String classPath = classname.replace(".","\\") + ".class";
       String code = decompile.cfr(classPath,outputdir);
       if(code!=null)
       {if(codecheck.check(code)){//检测代码中是否有恶意代码
           code = "Dangerous" + code;
            file.createjavafile(outputdir+"\\java\\"+ javaPath);
            file.bufferedWriterMethod(outputdir + "\\java\\" + javaPath,code);
            //classes.dump(classname,outputdir+"\\classes");
           //code = "Dangerous" + code;
           return code;
        }}
       return code;
    }

    public void dumpclass(String pattern, boolean sensitive, boolean noStat, boolean classLoaderPrefix, String outputDir, int pid) throws Exception {
        dump.run(pattern, sensitive,noStat,classLoaderPrefix, outputDir,pid);
    }


    public void dump(String classname,String outputdir) throws IOException {
        String javaPath = classname.replace(".","\\") + ".java";
        String classPath = classname.replace(".","\\") + ".class";
        file.createjavafile("java\\"+ javaPath);
        String code = decompile.cfr(classname,outputdir);
        file.bufferedWriterMethod("java\\" + javaPath,code);
        classes.dump(classname,"classes");
    }


    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, CannotCompileException, NoSuchFieldException, NotFoundException {
        //System.load("C:\\Users\\xyy\\source\\repos\\NOAgent\\x64\\Release\\NOAgent.dll");

        /*detect getJPSAgent = new detect();
        getJPSAgent.libb();
        System.out.println(Long.toHexString(getJPSAgent.caloffset()));
        long native_jvmtienv = getJPSAgent.caloffset();
        //getJPSAgent.run();
        Unsafe unsafe = null;
        try {    Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);}
        catch (Exception e) {
            throw new AssertionError(e);}
        long JPLISAgent = unsafe.allocateMemory(0x1000);
        unsafe.putLong(JPLISAgent + 8, native_jvmtienv);
        unsafe.putByte(native_jvmtienv + 361, (byte) 2);
        Class<?> instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
        Constructor<?> constructor = instrument_clazz.getDeclaredConstructor(long.class, boolean.class, boolean.class);
        constructor.setAccessible(true);
        Object insn = constructor.newInstance(JPLISAgent, true, false);*/
        detect getJPSAgent = new detect();
        insn = getJPSAgent.genImp("C:\\Users\\xyy\\source\\repos\\NOAgent\\x64\\Release\\NOAgent.dll",getJPSAgent);//生成instrument对象
        Class<?> instrument_clazz = Class.forName("sun.instrument.InstrumentationImpl");
        Field[] fields = insn.getClass().getDeclaredFields();
        addtransform = instrument_clazz.getMethod("addTransformer", ClassFileTransformer.class, boolean.class);//利用反射获取addtransform与retransform方法
        retransform = instrument_clazz.getMethod("retransformClasses",java.lang.Class[].class);

        for(Field field:fields)
        {
            System.out.println(field.getName());

        }

       /*Field mEnvironmentSupportsRetransformClasses =  insn.getClass().getDeclaredField("mEnvironmentSupportsRetransformClasses");
        Field mEnvironmentSupportsRetransformClassesKnown =  insn.getClass().getDeclaredField("mEnvironmentSupportsRetransformClassesKnown");
        mEnvironmentSupportsRetransformClasses.setAccessible(true);
        mEnvironmentSupportsRetransformClasses.setBoolean(insn,true);
        mEnvironmentSupportsRetransformClassesKnown.setAccessible(true);
        mEnvironmentSupportsRetransformClassesKnown.setBoolean(insn,true);
       //getJPSAgent.listclass(insn,instrument_clazz);
       // getJPSAgent.change(insn,instrument_clazz);*/
        //getJPSAgent.listclass(insn,instrument_clazz);
        getJPSAgent.redefine(insn,instrument_clazz);
        FileDescriptor fd = null;
        RandomAccessFile fos = null;
        fos=new RandomAccessFile("D:\\内存马\\java-agent\\123.txt","r");
        fos.getFD();
        String classname = "java.io.RandomAccessFile";
        /*Class c = null;
        c = Class.forName(classname);
        Class<?>[] cc = new Class<?>[]{c};
        detect.addtransform.invoke(detect.insn,new Object[]{new retransformer(),true});
        detect.retransform.invoke(detect.insn, (Object) cc);*/
        //getJPSAgent.codecheckanddump(classname);


        //getJPSAgent.run();
    }
}

