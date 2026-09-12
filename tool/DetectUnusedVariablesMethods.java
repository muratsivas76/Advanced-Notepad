import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DetectUnusedVariablesMethods {

    // Dokunulmaz metodlar dizisi - Bunlar kullanılmıyor görünse de silinmemeli
    private static final String[] BLACKLIST = {
        "main", "actionPerformed", "itemStateChanged", "stateChanged",
        "undoableEditHappened", "resetUndo", 
        "mousePressed", "mouseReleased", "mouseClicked", "mouseEntered", "mouseExited",
        "keyTyped", "keyPressed", "keyReleased", "focusGained", "focusLost",
        "windowOpened", "windowClosing", "windowClosed", "windowIconified", "windowDeiconified", "windowActivated", "windowDeactivated",
        "onStart", "onCreate", "onResume", "onPause", "onStop", "onDestroy", "onRestart", // Android
        "onClick", "onLongClick", "onTouch", "onLongPress", "onScroll" // View Listeners
    };

    private static class ItemInfo {
        int lineNum;
        String type;
        ItemInfo(int l, String t) { this.lineNum = l; this.type = t; }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Kullanim: java DetectUnusedVariablesMethods <DosyaYolu.java>");
            return;
        }

        File inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            System.out.println("Dosya bulunamadi!");
            return;
        }

        try {
            processFile(inputFile);
        } catch (Exception e) {
            System.out.println("Hata olustu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isBlacklisted(String name) {
        for (String s : BLACKLIST) {
            if (s.equals(name)) return true;
        }
        return false;
    }

    public static void processFile(File file) throws IOException {
        List<String> allLines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line;
        while ((line = reader.readLine()) != null) {
            allLines.add(line);
        }
        reader.close();

        Map<String, ItemInfo> definitions = new LinkedHashMap<String, ItemInfo>();
        
        // Regex patterns (Gelistirilmis)
        Pattern methodPattern = Pattern.compile("(public|protected|private|static|void|String|int|boolean|double|Object)\\s+([\\w\\d_]+)\\s*\\(");
        Pattern varPattern = Pattern.compile("(public|protected|private|static|final)\\s+[\\w\\d_\\<\\>\\[\\]]+\\s+([\\w\\d_]+)\\s*[;=]");

        // Step 1: Scanner
        for (int i = 0; i < allLines.size(); i++) {
            String l = allLines.get(i).trim();
            if (l.startsWith("//") || l.startsWith("*") || l.startsWith("/*") || l.isEmpty()) continue;

            // Metotlari yakala
            Matcher mMethod = methodPattern.matcher(l);
            if (mMethod.find()) {
                String name = mMethod.group(2);
                if (!isBlacklisted(name) && !name.equals(file.getName().replace(".java", ""))) {
                    definitions.put(name, new ItemInfo(i + 1, "METHOD"));
                }
            }

            // Degiskenleri yakala (Local degiskenler regex'e gore filtrelenir)
            Matcher mVar = varPattern.matcher(l);
            if (mVar.find()) {
                String name = mVar.group(2);
                if (!isBlacklisted(name)) {
                    definitions.put(name, new ItemInfo(i + 1, "VARIABLE"));
                }
            }
        }

        // Step 2: Raporlama
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("reportUnusual.txt"), "UTF-8"));
        writer.println("--- DETAILED UNUSED ITEMS REPORT ---");
        writer.println("Target File: " + file.getName());
        writer.println("----------------------------------------------");

        int unusedCount = 0;
        for (String name : definitions.keySet()) {
            ItemInfo info = definitions.get(name);
            int count = 0;
            // Kelime siniri kullanarak (\\b) tam ismi ara
            Pattern usagePattern = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");

            for (String l : allLines) {
                Matcher m = usagePattern.matcher(l);
                while (m.find()) {
                    count++;
                }
            }

            if (count <= 1) {
                unusedCount++;
                writer.println(String.format("Line %-5d | %-10s | %s", info.lineNum, info.type, name));
            }
        }

        writer.println("----------------------------------------------");
        writer.println("Toplam Supheli Oge: " + unusedCount);
        writer.close();

        System.out.println("Analiz bitti. Rapor: reportUnusual.txt");
    }
    
}
