package NoAgent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import com.sun.org.apache.bcel.internal.Repository;

import com.sun.xml.internal.ws.org.objectweb.asm.ClassReader;
import javassist.*;
import jdk.internal.org.objectweb.asm.commons.Method;
import org.benf.cfr.reader.CfrDriverImpl;
import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;
import org.benf.cfr.reader.util.getopt.OptionsImpl;


public class decompile {
    public static void main(String[] args) throws IOException {
       // Long time = cfr("decompiler.jar", "./cfr_output_jar");
        //System.out.println(String.format("decompiler time: %dms", time));
        // decompiler time: 11655ms
    }
    static class DataSourcewithJavaAssist implements ClassFileSource {
        @Override
        public void informAnalysisRelativePathDetail(String usePath, String classFilePath) {
        }

        @Override
        public Collection<String> addJar(String jarPath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPossiblyRenamedPath(String s) {
            return s;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String classname) throws IOException {
            classname = classname.replace("/",".");
            classname = classname.substring(0,classname.length()-6);
            ClassLoader cl =null;
            try {
                cl = Class.forName(classname).getClassLoader();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(cl));


            CtClass string_clazz = null;
            try {
                string_clazz = pool.get(classname);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            byte data[]= new byte[0];
            try {
                data = string_clazz.toBytecode();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
            return Pair.make(data, classname);
        }
    }

    static class DataSourcefromtransform implements ClassFileSource {

        @Override
        public void informAnalysisRelativePathDetail(String s, String s1) {

        }

        @Override
        public Collection<String> addJar(String s) {
            return null;
        }

        @Override
        public String getPossiblyRenamedPath(String s) {
            return s;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String classname) throws IOException {
            classname = classname.replace("/",".");
            classname = classname.substring(0,classname.length()-6);
            try {
                detect.addtransform.invoke(detect.insn,new Object[]{new retransformer(),true});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            Class c = null;


            try {
                c = Class.forName(classname);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Class<?>[] cc = new Class<?>[]{c};
                detect.retransform.invoke(detect.insn, (Object) cc);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return Pair.make(retransformer.code,classname);
        }
    }

    static class DataSourcewithASM implements ClassFileSource{

        @Override
        public void informAnalysisRelativePathDetail(String s, String s1) {

        }

        @Override
        public Collection<String> addJar(String s) {
            return null;
        }

        @Override
        public String getPossiblyRenamedPath(String s) {
            return s;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String classname) throws IOException {
            classname = classname.replace("/",".");
            classname = classname.substring(0,classname.length()-6);
            ClassReader classReader = new ClassReader(classname);
            byte[] data = classReader.b;
            //classname = classname.replace("/",".");
            return Pair.make(data,classname);
        }
    }

    static class DataSourcewithBCEL implements ClassFileSource {


        @Override
        public void informAnalysisRelativePathDetail(String s, String s1) {

        }

        @Override
        public Collection<String> addJar(String s) {
            return null;
        }

        @Override
        public String getPossiblyRenamedPath(String s) {
            return s;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String classname) throws IOException {
            classname = classname.replace("/",".");
            classname = classname.substring(0,classname.length()-6);
            byte[] data = new byte[0];
            try {
                data = Repository.lookupClass(Class.forName(classname)).getBytes();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(data==null||data.length==0)
            {
                data = Repository.lookupClass(detect.c).getBytes();
            }
            return Pair.make(data,classname);
        }
    }

    public static String  cfr(String classname,String outputdir) throws IOException {
        Long start = System.currentTimeMillis();
        // target dir
        HashMap<String, String> outputMap = new HashMap<>();
       // outputMap.put("outputdir", targetPath);
        codecheck codecheck = new codecheck();
        OutputSinkFactory mySink = new OutputSinkFactory() {
            @Override
            public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                // I only understand how to sink strings, regardless of what you have to give me.
                return Collections.singletonList(SinkClass.STRING);
            }

            @Override
            public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                return sinkType == SinkType.JAVA ? codecheck::setjava : ignore -> {};
            }
        };


       // ClassFileSource source = new DataSourcewithBCEL();
       //ClassFileSource source = new DataSourcewithJavaAssist();
        //ClassFileSource source = new DataSourcewithASM();
        //ClassFileSource source = new DataSourcefromtransform();//(调用instrument使用transform配合retransform，未实现)
        OptionsImpl options = new OptionsImpl(outputMap);
       // CfrDriver cfrDriver = new CfrDriver.Builder().withBuiltOptions(options).build();
       // CfrDriver cfrDriver = new CfrDriver.Builder().build();
       // CfrDriver cfrDriver = new CfrDriver.Builder().withOutputSink(mySink).withClassFileSource(source).build();
        CfrDriver cfrDriver = new CfrDriver.Builder().withBuiltOptions(options).withOutputSink(mySink).build();
        cfrDriver.analyse(Collections.singletonList(outputdir +"/"+ classname));
        Long end = System.currentTimeMillis();
        //System.out.println(codecheck.java);
        return codecheck.java;
    }

}
