package NoAgent;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.tools.jcore.ClassDump;

import java.io.File;
import java.lang.reflect.Method;


public class dump {


    public static void run(String pattern, boolean sensitive, boolean noStat, boolean classLoaderPrefix, String outputDir, int pid) throws Exception {
        ClassLoader classLoader = dump.class.getClassLoader();

        DumpWrapperFilterConfig.setPattern(pattern);
        if (outputDir != null) {
            DumpWrapperFilterConfig.setOutputDirectory(outputDir);
        }
        DumpWrapperFilterConfig.setSensitive(sensitive);

        if (!noStat) {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    // print stat
                    System.out.println("Dumped classes counter: " + DumpWrapperFilterConfig.getDumpedCounter());
                    System.out.println("Output directory: "
                            + new File(DumpWrapperFilterConfig.getOutputDirectory()).getAbsolutePath());
                    if (!DumpWrapperFilterConfig.duplicateClasses.isEmpty()) {
                        System.out.println("DuplicateClasses size: " + DumpWrapperFilterConfig.duplicateClasses.size());
                        if (!classLoaderPrefix) {
                            System.out.println(
                                    "Dumped Classes contain duplicate classes, please add --classLoaderPrefix command option, otherwise the class with the same name will be overwritten.");
                            System.out.println("Dumped Classes: ");
                            for (String clazz : DumpWrapperFilterConfig.duplicateClasses) {
                                System.out.println(clazz);
                            }
                        }
                    }

                    if (!DumpWrapperFilterConfig.overWriteFiles.isEmpty()) {
                        System.out.println("Over write files size: " + DumpWrapperFilterConfig.overWriteFiles.size());
                        System.out.println("Over write files: ");
                        for (String file : DumpWrapperFilterConfig.overWriteFiles) {
                            System.out.println(file);
                        }
                    }
                }
            }));
        }

        System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "io.github.hengyunabc.dumpclass.DumpWrapperFilter");

        Method mainMethod = classLoader.loadClass("sun.jvm.hotspot.tools.jcore.ClassDump").getMethod("main",
                String[].class);

        // sun.jvm.hotspot.tools.jcore.ClassDump.main(new String[] { pid });
        mainMethod.invoke(null, new Object[] { new String[] { "" + pid } });
    }
}
