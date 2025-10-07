package org.lele.sdtahzl.util;

import lombok.extern.slf4j.Slf4j;
import org.lele.sdtahzl.constant.ServerConstant;
import org.lele.sdtahzl.domain.ClientDTO;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ClientUtil {
    private volatile static ClientDTO clientDTO;

    private static final String PROCESS_NAME = "LeagueClientUx";
    private static final Pattern PORT_PATTERN  = Pattern.compile("--app-port=([0-9]+)");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("--remoting-auth-token=([\\w-]+)");

    // ========================= 外部调用 =========================
    public static ClientDTO getClient() throws Exception {
        if (clientDTO == null) {
            synchronized (ClientUtil.class) {
                if (clientDTO == null) {
                    initClient();
                }
            }
        }
        return clientDTO;
    }

    // ========================= 初始化主流程 =========================
    private static void initClient() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        log.info("Detect OS: {}", os);

        if (os.contains("win")) {
            initOnWindows();
        } else if (os.contains("mac")) {
            initOnMac();
        } else {
            throw new Exception("Unsupported OS: " + os);
        }
    }

    // ========================= Windows 实现（多重兜底） =========================
    private static void initOnWindows() throws Exception {
        // 1) PowerShell (CIM) 读取命令行（Win10/Win11 推荐）
        String cmdLine = tryGetCmdlineViaPowerShellCIM();
        if (!StringUtils.isEmpty(cmdLine)) {
            if (tryParseFromCmdline(cmdLine)) return;
        }

        // 2) 读取 lockfile（最可靠：命令行拿不到时）
        //    2.1 先通过 PowerShell 拿进程路径，再拼 lockfile
        String exePath = tryGetProcessPathViaPowerShell();
        if (!StringUtils.isEmpty(exePath)) {
            File lock = new File(new File(exePath).getParentFile(), "lockfile");
            if (parseFromLockfile(lock)) return;
        }
        //    2.2 常见安装目录兜底
        String userHome = System.getProperty("user.home");
        File[] candidates = new File[] {
                new File("C:\\Riot Games\\League of Legends\\lockfile"),
                new File(userHome + "\\AppData\\Local\\Riot Games\\Riot Client\\lockfile"), // 有些版本在 Riot Client 目录
                new File(userHome + "\\AppData\\Local\\Riot Games\\League of Legends\\lockfile")
        };
        for (File f : candidates) {
            if (parseFromLockfile(f)) return;
        }

        // 3) WMI 老接口（某些老 Win 系统可用）
        cmdLine = tryGetCmdlineViaPowerShellWMI();
        if (!StringUtils.isEmpty(cmdLine)) {
            if (tryParseFromCmdline(cmdLine)) return;
        }

        throw new Exception("Failed to initialize client on Windows: cannot get port/token.");
    }

    // 使用 PowerShell + CIM 获取命令行
    private static String tryGetCmdlineViaPowerShellCIM() {
        String ps = "$ErrorActionPreference='SilentlyContinue';" +
                "[Console]::OutputEncoding=[Text.Encoding]::UTF8;" +
                "(Get-CimInstance Win32_Process -Filter \"Name='" + PROCESS_NAME + ".exe'\") | " +
                "Select-Object -ExpandProperty CommandLine";
        return execPowerShell(ps);
    }

    // 使用 PowerShell + WMI（老接口）获取命令行
    private static String tryGetCmdlineViaPowerShellWMI() {
        String ps = "$ErrorActionPreference='SilentlyContinue';" +
                "[Console]::OutputEncoding=[Text.Encoding]::UTF8;" +
                "(Get-WmiObject Win32_Process -Filter \"Name='" + PROCESS_NAME + ".exe'\") | " +
                "Select-Object -ExpandProperty CommandLine";
        return execPowerShell(ps);
    }

    // 使用 PowerShell 获取进程路径（用于定位 lockfile）
    private static String tryGetProcessPathViaPowerShell() {
        String ps = "$ErrorActionPreference='SilentlyContinue';" +
                "[Console]::OutputEncoding=[Text.Encoding]::UTF8;" +
                "(Get-Process -Name '" + PROCESS_NAME + "' -ErrorAction SilentlyContinue | " +
                "Select-Object -First 1 -ExpandProperty Path)";
        return execPowerShell(ps);
    }

    // 从命令行中解析端口与 token
    private static boolean tryParseFromCmdline(String cmdLine) {
        if (StringUtils.isEmpty(cmdLine)) return false;
        log.info("PS Cmdline: {}", cmdLine.replaceAll("\\s+", " ").trim());

        String port  = extractInfo(cmdLine, PORT_PATTERN);
        String token = extractInfo(cmdLine, TOKEN_PATTERN);
        if (StringUtils.isEmpty(port) || StringUtils.isEmpty(token)) return false;

        buildAndSaveClient(port, token);
        return true;
    }

    // 读取 lockfile 并解析端口/密码
    private static boolean parseFromLockfile(File lockfile) {
        try {
            if (lockfile == null || !lockfile.exists()) return false;
            String content = readAll(lockfile, StandardCharsets.UTF_8).trim();
            // lockfile 形如：LeagueClientUx:PID:PORT:PASSWORD:PROTOCOL
            // 例：LeagueClientUx:29508:51715:7h1s1sp@ssw0rd:https
            String[] parts = content.split(":");
            if (parts.length < 5) return false;
            String port = parts[2];
            String token = parts[3];

            if (!StringUtils.isEmpty(port) && !StringUtils.isEmpty(token)) {
                buildAndSaveClient(port, token);
                log.info("Init from lockfile: {}", lockfile.getAbsolutePath());
                return true;
            }
        } catch (Exception e) {
            log.warn("Read lockfile failed: {}", e.getMessage());
        }
        return false;
    }

    // ========================= macOS 实现（保留原逻辑 + lockfile 兜底） =========================
    private static void initOnMac() throws Exception {
        // 先用 ps 获取命令行
        String cmd = "ps -A | grep " + PROCESS_NAME;
        String output = execSh(cmd, StandardCharsets.UTF_8);
        if (!StringUtils.isEmpty(output) && tryParseFromCmdline(output)) {
            return;
        }

        // mac 常见 lockfile 路径兜底
        File[] candidates = new File[] {
                new File("/Applications/League of Legends.app/Contents/LoL/lockfile"),
                new File(System.getProperty("user.home") + "/Applications/League of Legends.app/Contents/LoL/lockfile"),
                new File(System.getProperty("user.home") + "/Library/Application Support/Riot Games/League of Legends/lockfile") // 某些安装
        };
        for (File f : candidates) {
            if (parseFromLockfile(f)) return;
        }

        throw new Exception("Failed to initialize client on macOS: cannot get port/token.");
    }

    // ========================= 通用工具 =========================
    private static void buildAndSaveClient(String port, String token) {
        String preUrl = ServerConstant.LOCAL_HOST + ":" + port;
        ClientDTO dto = new ClientDTO();
        dto.setPort(port);
        dto.setAuthToken(token);
        dto.setPreUrl(preUrl);
        clientDTO = dto;
        log.info("✅ Client initialized: port={}, token(len={})", port, token.length());
    }

    private static String extractInfo(String input, Pattern pattern) {
        Matcher m = pattern.matcher(input);
        return m.find() ? m.group(1) : null;
    }

    private static String readAll(File f, Charset charset) throws IOException {
        try (InputStream in = new FileInputStream(f);
             Reader reader = new InputStreamReader(in, charset);
             BufferedReader br = new BufferedReader(reader)) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // 以 PowerShell 执行（强制 UTF-8 输出）
    private static String execPowerShell(String psScript) {
        String[] cmd = new String[] {
                "powershell.exe",
                "-NoLogo", "-NoProfile", "-ExecutionPolicy", "Bypass",
                "-Command",
                psScript
        };
        return exec(cmd, StandardCharsets.UTF_8);
    }

    // 以 /bin/sh 执行（mac）
    private static String execSh(String command, Charset charset) {
        String[] cmd = new String[] { "/bin/sh", "-c", command };
        return exec(cmd, charset);
    }

    private static String exec(String[] cmd, Charset charset) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        log.debug("Exec: {}", Arrays.toString(cmd));
        try {
            Process p = pb.start();
            StringBuilder out = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), charset))) {
                String line;
                while ((line = r.readLine()) != null) out.append(line).append('\n');
            }
            p.waitFor(); // 避免僵尸进程
            String result = out.toString().trim();
            log.debug("Exec output: {}", result);
            return result;
        } catch (Exception e) {
            log.warn("Exec failed: {}", e.getMessage());
            return "";
        }
    }
}
