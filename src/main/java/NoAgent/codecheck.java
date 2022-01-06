package NoAgent;

public class codecheck {
    public String java;

    public void setjava(String code) {
        java = code;
    }

    public <T> void setjava(T t) {
        java = (String) t;
    }

    public static boolean check(String code) {
        String[] blacklist = new String[]{"getRuntime", "defineClass", "invoke", "shell", "javax.crypto", "ProcessBuilder","exec"};
        for (String b : blacklist) {
            if (code.contains(b)) {
                return true;
            }
      }
        return false;
    }
}
