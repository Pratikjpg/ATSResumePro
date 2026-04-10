import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ATSResumePro extends JFrame {

    enum Theme { LIGHT, DARK }

    private Theme currentTheme = Theme.LIGHT;

    private JPanel sidebarPanel, reportPanel;
    private JTextField nameField, emailField, phoneField, expField, eduField;
    private JTextArea skillsArea, projectsArea, jdArea;
    private JCheckBox fresherCheck, jdBoostCheck;
    private JButton analyzeBtn, clearBtn, loadSampleBtn, copyBtn, exportBtn, importBtn;
    private JTextArea reportArea;
    private JProgressBar progressBar;
    private JComboBox<String> roleCombo;

    private Map<String, List<String>> keywordsMap;
    private Map<String, Integer> keywordWeights;

    public ATSResumePro() {
        super("ATS Resume Analyzer Pro");
        loadKeywords();
        setLayout(new BorderLayout());
        createSidebar();
        createReportPanel();
        initMenu();
        add(sidebarPanel, BorderLayout.WEST);
        add(reportPanel, BorderLayout.CENTER);
        setSize(1300, 800);
        setMinimumSize(new Dimension(1100, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        applyTheme(currentTheme);
        addHotkeys();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HOTKEYS
    // ─────────────────────────────────────────────────────────────────────────
    private void addHotkeys() {
        KeyStroke analyzeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK);
        sidebarPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(analyzeKey, "analyzeAction");
        sidebarPanel.getActionMap().put("analyzeAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { analyzeResume(); }
        });

        KeyStroke clearKey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_DOWN_MASK);
        sidebarPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(clearKey, "clearAction");
        sidebarPanel.getActionMap().put("clearAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { clearAllFields(); }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MENU
    // ─────────────────────────────────────────────────────────────────────────
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Theme");
        JMenuItem light = new JMenuItem("Light");
        light.addActionListener(e -> { currentTheme = Theme.LIGHT; applyTheme(currentTheme); });
        JMenuItem dark = new JMenuItem("Dark");
        dark.addActionListener(e -> { currentTheme = Theme.DARK; applyTheme(currentTheme); });
        themeMenu.add(light);
        themeMenu.add(dark);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // THEME
    // ─────────────────────────────────────────────────────────────────────────
    private void applyTheme(Theme theme) {
        Color bg, fg, inputBg;
        if (theme == Theme.DARK) {
            bg      = new Color(45, 45, 45);
            fg      = Color.WHITE;
            inputBg = new Color(70, 70, 70);
        } else {
            bg      = Color.WHITE;
            fg      = Color.BLACK;
            inputBg = Color.WHITE;
        }
        sidebarPanel.setBackground(bg);
        reportPanel.setBackground(bg);
        for (Component c : sidebarPanel.getComponents())  setComponentTheme(c, bg, fg, inputBg);
        for (Component c : reportPanel.getComponents())   setComponentTheme(c, bg, fg, inputBg);
    }

    private void setComponentTheme(Component c, Color bg, Color fg, Color inputBg) {
        if (c instanceof JPanel) {
            c.setBackground(bg);
            for (Component cc : ((JPanel) c).getComponents())
                setComponentTheme(cc, bg, fg, inputBg);
        } else if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            sp.setBackground(bg);
            sp.getViewport().setBackground(bg);
            Component view = sp.getViewport().getView();
            if (view != null) setComponentTheme(view, bg, fg, inputBg);
        } else if (c instanceof JTextField) {
            c.setBackground(inputBg); c.setForeground(fg);
        } else if (c instanceof JTextArea) {
            c.setBackground(inputBg); c.setForeground(fg);
        } else if (c instanceof JLabel) {
            c.setForeground(fg);
        } else if (c instanceof JButton) {
            // colored buttons keep their own bg
        } else if (c instanceof JComboBox) {
            c.setBackground(inputBg); c.setForeground(fg);
        } else if (c instanceof JCheckBox) {
            c.setBackground(bg); c.setForeground(fg);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────────────────────────────────
    private void createSidebar() {
        Font labelFont = new Font("Segoe UI", Font.BOLD,  13);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 13);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        gc.gridx   = 0;

        int row = 0;

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Candidate Name:", labelFont), gc);
        gc.gridy  = row++; gc.insets = new Insets(0, 0, 6, 0);
        nameField = styledField(inputFont);
        formPanel.add(nameField, gc);

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Email:", labelFont), gc);
        gc.gridy  = row++; gc.insets = new Insets(0, 0, 6, 0);
        emailField = styledField(inputFont);
        formPanel.add(emailField, gc);

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Phone Number:", labelFont), gc);
        gc.gridy  = row++; gc.insets = new Insets(0, 0, 6, 0);
        phoneField = styledField(inputFont);
        formPanel.add(phoneField, gc);

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        JPanel expEduLabelRow = transparentPanel(new GridLayout(1, 2, 10, 0));
        expEduLabelRow.add(styledLabel("Years of Experience:", labelFont));
        expEduLabelRow.add(styledLabel("Highest Education Level:", labelFont));
        formPanel.add(expEduLabelRow, gc);

        gc.gridy  = row++; gc.insets = new Insets(0, 0, 6, 0);
        JPanel expEduRow = transparentPanel(new GridLayout(1, 2, 10, 0));
        expField = styledField(inputFont);
        eduField = styledField(inputFont);
        expEduRow.add(expField);
        expEduRow.add(eduField);
        formPanel.add(expEduRow, gc);

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Role Applying For:", labelFont), gc);
        gc.gridy  = row++; gc.insets = new Insets(0, 0, 6, 0);
        String[] roles = {"Software Developer", "Cybersecurity Analyst", "Network Engineer",
                          "Data Analyst", "Web Developer", "DevOps Engineer"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(inputFont);
        roleCombo.setPreferredSize(new Dimension(0, 36));
        formPanel.add(roleCombo, gc);

        gc.gridy  = row++; gc.insets = new Insets(10, 0, 10, 0);
        JPanel checkRow = transparentPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        fresherCheck  = styledCheck("Fresher (0–1 years exp)", inputFont);
        jdBoostCheck  = styledCheck("Use Job Description Boost", inputFont);
        checkRow.add(fresherCheck);
        checkRow.add(jdBoostCheck);
        formPanel.add(checkRow, gc);

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Skills (comma separated):", labelFont), gc);
        gc.gridy   = row++; gc.insets = new Insets(0, 0, 6, 0);
        gc.weighty = 0.25; gc.fill = GridBagConstraints.BOTH;
        skillsArea = styledTextArea(inputFont, 5);
        formPanel.add(styledScroll(skillsArea, 90), gc);
        gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Projects Description:", labelFont), gc);
        gc.gridy   = row++; gc.insets = new Insets(0, 0, 6, 0);
        gc.weighty = 0.35; gc.fill = GridBagConstraints.BOTH;
        projectsArea = styledTextArea(inputFont, 7);
        formPanel.add(styledScroll(projectsArea, 120), gc);
        gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridy  = row++; gc.insets = new Insets(8, 0, 2, 0);
        formPanel.add(styledLabel("Paste Job Description Keywords (optional):", labelFont), gc);
        gc.gridy   = row++; gc.insets = new Insets(0, 0, 6, 0);
        gc.weighty = 0.25; gc.fill = GridBagConstraints.BOTH;
        jdArea = styledTextArea(inputFont, 5);
        formPanel.add(styledScroll(jdArea, 90), gc);
        gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;

        JScrollPane formScroll = new JScrollPane(formPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);

        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);
        analyzeBtn    = makeBtn("▶  Analyze",        new Color(37,  99, 235), Color.WHITE, btnFont);
        clearBtn      = makeBtn("✕  Clear All",       new Color(220, 53,  69), Color.WHITE, btnFont);
        loadSampleBtn = makeBtn("⟳  Sample Data",    new Color(40, 167,  69), Color.WHITE, btnFont);
        importBtn     = makeBtn("⬆  Import",          new Color(108,117, 125), Color.WHITE, btnFont);
        exportBtn     = makeBtn("⬇  Export Report",  new Color(108,117, 125), Color.WHITE, btnFont);
        copyBtn       = makeBtn("⧉  Copy Report",    new Color(23, 162, 184), Color.WHITE, btnFont);

        analyzeBtn.addActionListener(e    -> analyzeResume());
        clearBtn.addActionListener(e      -> clearAllFields());
        loadSampleBtn.addActionListener(e -> loadSampleData());
        importBtn.addActionListener(e     -> importFields());
        exportBtn.addActionListener(e     -> exportReport());
        copyBtn.addActionListener(e       -> copyReportToClipboard());

        JPanel btnRow1 = transparentPanel(new GridLayout(1, 3, 8, 0));
        btnRow1.add(analyzeBtn); btnRow1.add(clearBtn); btnRow1.add(loadSampleBtn);

        JPanel btnRow2 = transparentPanel(new GridLayout(1, 3, 8, 0));
        btnRow2.add(importBtn); btnRow2.add(exportBtn); btnRow2.add(copyBtn);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(8, 14, 10, 14));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRow1);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        btnPanel.add(btnRow2);

        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(480, 800));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(210, 210, 210)));
        sidebarPanel.add(formScroll, BorderLayout.CENTER);
        sidebarPanel.add(btnPanel,   BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REPORT PANEL
    // ─────────────────────────────────────────────────────────────────────────
    private void createReportPanel() {
        reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "ATS Resume Analysis Report"));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setMargin(new Insets(10, 12, 10, 12));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportPanel.add(scrollPane, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setPreferredSize(new Dimension(0, 28));
        reportPanel.add(progressBar, BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // KEYWORDS DATA
    // ─────────────────────────────────────────────────────────────────────────
    private void loadKeywords() {
        keywordsMap    = new HashMap<>();
        keywordWeights = new HashMap<>();

        keywordsMap.put("Software Developer", Arrays.asList(
            "Java", "Python", "C++", "Git", "SQL", "OOP", "REST", "Spring", "Hibernate",
            "Docker", "Kubernetes", "Agile", "JavaScript", "React", "Node.js",
            "Microservices", "AWS", "Azure", "TDD", "JUnit"
        ));
        keywordsMap.put("Cybersecurity Analyst", Arrays.asList(
            "Penetration Testing", "Vulnerability Assessment", "SIEM", "Firewalls", "IDS",
            "IPS", "SOC", "Malware Analysis", "Cryptography", "Risk Assessment",
            "Incident Response", "Threat Hunting", "Python", "Linux", "Networking",
            "MITRE ATT&CK", "Nmap", "Wireshark", "Burp Suite", "Metasploit"
        ));
        keywordsMap.put("Network Engineer", Arrays.asList(
            "Cisco", "Routing", "Switching", "TCP/IP", "VPN", "BGP", "OSPF", "Firewall",
            "Wireshark", "Ethernet", "Load Balancer", "Network Security", "QoS",
            "IPv4", "IPv6", "MPLS", "LAN", "WAN"
        ));
        keywordsMap.put("Data Analyst", Arrays.asList(
            "Excel", "Tableau", "Power BI", "SQL", "Python", "R", "Data Cleaning",
            "Data Visualization", "Statistics", "Machine Learning", "Pandas", "NumPy",
            "Data Mining", "Reporting", "Dashboard", "Analytics"
        ));
        keywordsMap.put("Web Developer", Arrays.asList(
            "HTML", "CSS", "JavaScript", "React", "Node.js", "Express", "MongoDB", "SQL",
            "REST API", "Git", "Bootstrap", "Angular", "Vue.js", "Webpack", "Responsive Design"
        ));
        keywordsMap.put("DevOps Engineer", Arrays.asList(
            "Docker", "Kubernetes", "Jenkins", "CI/CD", "Terraform", "AWS", "Azure",
            "Linux", "Monitoring", "Ansible", "CloudFormation", "Nagios", "Git",
            "Scripting", "Python", "Bash"
        ));

        String[][] weights = {
            {"Java","10"},{"Python","10"},{"C++","8"},{"Git","7"},{"SQL","8"},
            {"OOP","7"},{"REST","7"},{"Spring","7"},{"Hibernate","7"},{"Docker","9"},
            {"Kubernetes","9"},{"Agile","6"},{"JavaScript","9"},{"React","9"},
            {"Node.js","8"},{"Microservices","7"},{"AWS","9"},{"Azure","9"},
            {"TDD","6"},{"JUnit","6"},{"Penetration Testing","10"},
            {"Vulnerability Assessment","9"},{"SIEM","8"},{"Firewalls","9"},
            {"IDS","8"},{"IPS","8"},{"SOC","7"},{"Malware Analysis","9"},
            {"Cryptography","8"},{"Risk Assessment","8"},{"Incident Response","9"},
            {"Threat Hunting","8"},{"Linux","7"},{"Networking","7"},
            {"MITRE ATT&CK","7"},{"Nmap","6"},{"Wireshark","7"},{"Burp Suite","7"},
            {"Metasploit","7"},{"Cisco","8"},{"Routing","7"},{"Switching","7"},
            {"TCP/IP","8"},{"VPN","7"},{"BGP","6"},{"OSPF","6"},{"Firewall","8"},
            {"Ethernet","6"},{"Load Balancer","6"},{"Network Security","8"},
            {"QoS","5"},{"IPv4","5"},{"IPv6","5"},{"MPLS","5"},{"LAN","6"},
            {"WAN","6"},{"Excel","8"},{"Tableau","9"},{"Power BI","9"},{"R","7"},
            {"Data Cleaning","7"},{"Data Visualization","8"},{"Statistics","7"},
            {"Machine Learning","8"},{"Pandas","7"},{"NumPy","7"},
            {"Data Mining","7"},{"Reporting","6"},{"Dashboard","7"},
            {"Analytics","7"},{"HTML","8"},{"CSS","8"},{"Express","7"},
            {"Bootstrap","7"},{"Angular","7"},{"Vue.js","7"},{"Webpack","6"},
            {"Responsive Design","7"},{"MongoDB","7"},{"REST API","7"},
            {"Jenkins","8"},{"CI/CD","9"},{"Terraform","8"},{"Monitoring","7"},
            {"Ansible","7"},{"CloudFormation","7"},{"Nagios","6"},
            {"Scripting","7"},{"Bash","6"}
        };
        for (String[] w : weights)
            keywordWeights.put(w[0], Integer.parseInt(w[1]));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ANALYZE  — all 3 bugs fixed
    // ─────────────────────────────────────────────────────────────────────────
    private void analyzeResume() {
        if (!validateInputs()) return;

        progressBar.setValue(0);
        progressBar.setString("Starting...");
        reportArea.setText("");

        SwingWorker<String, Integer> worker = new SwingWorker<>() {

            @Override
            protected String doInBackground() throws Exception {
                publish(10);

                String name          = nameField.getText().trim();
                String email         = emailField.getText().trim();
                String phone         = phoneField.getText().trim();
                String experienceStr = expField.getText().trim();
                String education     = eduField.getText().trim();
                String skillsInput   = skillsArea.getText().toLowerCase();
                String projectsInput = projectsArea.getText().toLowerCase();
                String jdInput       = jdArea.getText().toLowerCase();
                String role          = (String) roleCombo.getSelectedItem();
                boolean isFresher    = fresherCheck.isSelected();
                boolean jdBoost      = jdBoostCheck.isSelected();

                int experience = 0;
                try { experience = Integer.parseInt(experienceStr); } catch (Exception ignored) {}

                String aggregateText = skillsInput + " " + projectsInput;

                Set<String>          foundKeywords   = new LinkedHashSet<>();
                Set<String>          missingKeywords = new LinkedHashSet<>();
                Map<String, Integer> keywordScoreMap = new LinkedHashMap<>();

                // ── FIX 1 & 2: Build keyword list, filter JD, then deduplicate ──
                List<String> roleKeywords = new ArrayList<>(
                    keywordsMap.getOrDefault(role, new ArrayList<>()));

                if (jdBoost && !jdInput.isEmpty()) {
                    // FIX 3: Filter out full sentences — only accept short keyword phrases
                    List<String> jdKeywords = Arrays.stream(jdInput.split("[,;\\n\\r]+"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .filter(s -> s.length() <= 40)
                        .filter(s -> s.split("\\s+").length <= 4)
                        .collect(Collectors.toList());
                    roleKeywords.addAll(jdKeywords);
                }

                // FIX 2: Deduplicate case-insensitively — keep first (title-cased) occurrence
                Map<String, String> seen = new LinkedHashMap<>();
                for (String kw : roleKeywords) {
                    seen.putIfAbsent(kw.toLowerCase(), kw);
                }
                roleKeywords = new ArrayList<>(seen.values());

                publish(30);

                // FIX 1: maxPossibleScore calculated AFTER dedup, WITH boost factored in
                int maxPossibleScore = roleKeywords.stream()
                    .mapToInt(k -> {
                        int w = keywordWeights.getOrDefault(k, 5);
                        return (jdBoost && !jdInput.isEmpty() && jdInput.contains(k.toLowerCase()))
                            ? (int) Math.round(w * 1.5) : w;
                    }).sum();

                int totalScore = 0;
                for (String keyword : roleKeywords) {
                    String kwLower = keyword.toLowerCase();
                    int count = countOccurrences(aggregateText, kwLower);
                    if (count > 0) {
                        foundKeywords.add(keyword);
                        int weight = keywordWeights.getOrDefault(keyword, 5);
                        if (jdBoost && jdInput.contains(kwLower))
                            weight = (int) Math.round(weight * 1.5);
                        int score = Math.min(count, 3) * weight;
                        keywordScoreMap.put(keyword, score);
                        totalScore += score;
                    } else {
                        missingKeywords.add(keyword);
                    }
                }

                publish(60);

                final int expFinal = experience;
                if (isFresher && expFinal > 1) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(ATSResumePro.this,
                            "Fresher selected but years of experience > 1. Please correct.",
                            "Warning", JOptionPane.WARNING_MESSAGE));
                }

                // FIX 1: Cap score at 100%
                int scorePercent = maxPossibleScore > 0
                    ? Math.min(100, (int)((totalScore * 100.0) / maxPossibleScore)) : 0;

                // ── Build report ──────────────────────────────────────────────
                StringBuilder sb = new StringBuilder();
                sb.append("══════════════════════════════════════════\n");
                sb.append("   ATS RESUME ANALYSIS REPORT\n");
                sb.append("══════════════════════════════════════════\n\n");

                sb.append("  Candidate  : ").append(name).append("\n");
                sb.append("  Email      : ").append(email).append("\n");
                sb.append("  Phone      : ").append(phone).append("\n");
                sb.append("  Role       : ").append(role).append("\n");
                sb.append("  Experience : ").append(experienceStr).append(" year(s)\n");
                sb.append("  Education  : ").append(education).append("\n\n");

                sb.append("──────────────────────────────────────────\n");
                sb.append("  KEYWORD MATCH SCORE\n");
                sb.append("──────────────────────────────────────────\n");
                sb.append(String.format("  Total : %d / %d  (%d%%)\n\n",
                    totalScore, maxPossibleScore, scorePercent));

                int bars = scorePercent / 5;
                sb.append("  [");
                for (int i = 0; i < 20; i++) sb.append(i < bars ? "█" : "░");
                sb.append("]  ").append(scorePercent).append("%\n\n");

                sb.append("──────────────────────────────────────────\n");
                sb.append("  MATCHED KEYWORDS\n");
                sb.append("──────────────────────────────────────────\n");
                if (foundKeywords.isEmpty()) {
                    sb.append("  (none matched)\n");
                } else {
                    for (Map.Entry<String, Integer> e : keywordScoreMap.entrySet())
                        sb.append(String.format("  ✔  %-28s %d pts\n", e.getKey(), e.getValue()));
                }

                sb.append("\n──────────────────────────────────────────\n");
                sb.append("  MISSING KEYWORDS  (add these to resume)\n");
                sb.append("──────────────────────────────────────────\n");
                if (missingKeywords.isEmpty()) {
                    sb.append("  Great! No missing keywords.\n");
                } else {
                    for (String kw : missingKeywords)
                        sb.append("  ✘  ").append(kw).append("\n");
                }

                sb.append("\n──────────────────────────────────────────\n");
                sb.append("  RECOMMENDATIONS\n");
                sb.append("──────────────────────────────────────────\n");
                if (foundKeywords.isEmpty()) {
                    sb.append("  • No relevant keywords found.\n");
                    sb.append("    Enhance your skills and projects section.\n");
                } else {
                    sb.append("  • Highlight these top skills in your resume:\n");
                    foundKeywords.stream().limit(8).forEach(kw ->
                        sb.append("    – ").append(kw).append("\n"));
                }
                if (isFresher)
                    sb.append("  • As a fresher, emphasize projects and internships.\n");
                if (expFinal < 1 && !isFresher)
                    sb.append("  • Experience < 1 year may not suit mid-level roles.\n");
                if (jdBoost && !jdInput.isEmpty())
                    sb.append("  • JD Boost active: keywords weighted for provided JD.\n");

                sb.append("\n══════════════════════════════════════════\n");

                publish(100);
                return sb.toString();
            }

            @Override
            protected void process(List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                if      (latest < 30)  progressBar.setString("Reading inputs...");
                else if (latest < 60)  progressBar.setString("Matching keywords...");
                else if (latest < 100) progressBar.setString("Building report...");
            }

            @Override
            protected void done() {
                try {
                    reportArea.setText(get());
                    progressBar.setValue(100);
                    progressBar.setString("Analysis Complete ✔");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ATSResumePro.this,
                        "Analysis failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS — UI factories
    // ─────────────────────────────────────────────────────────────────────────
    private JLabel styledLabel(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(Font font) {
        JTextField f = new JTextField();
        f.setFont(font);
        f.setPreferredSize(new Dimension(0, 36));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }

    private JTextArea styledTextArea(Font font, int rows) {
        JTextArea ta = new JTextArea(rows, 20);
        ta.setFont(font);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setMargin(new Insets(6, 8, 6, 8));
        return ta;
    }

    private JScrollPane styledScroll(JTextArea ta, int minHeight) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setMinimumSize(new Dimension(0, minHeight));
        sp.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        return sp;
    }

    private JCheckBox styledCheck(String text, Font font) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(font);
        cb.setOpaque(false);
        return cb;
    }

    private JPanel transparentPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        return p;
    }

    private JButton makeBtn(String text, Color bg, Color fg, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS — logic
    // ─────────────────────────────────────────────────────────────────────────
    private int countOccurrences(String text, String keyword) {
        int count = 0, idx = 0;
        while ((idx = text.indexOf(keyword, idx)) != -1) { count++; idx += keyword.length(); }
        return count;
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty())
            { showError("Candidate name cannot be empty."); return false; }
        if (!emailField.getText().trim().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
            { showError("Invalid email address."); return false; }
        if (!phoneField.getText().trim().matches("^[0-9]{7,15}$"))
            { showError("Invalid phone number. Use digits only, min 7 digits."); return false; }
        try { if (Integer.parseInt(expField.getText().trim()) < 0) throw new Exception(); }
        catch (Exception e) { showError("Years of experience must be a valid number (0 or more)."); return false; }
        return true;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ACTIONS
    // ─────────────────────────────────────────────────────────────────────────
    private void clearAllFields() {
        nameField.setText(""); emailField.setText(""); phoneField.setText("");
        expField.setText("");  eduField.setText("");   skillsArea.setText("");
        projectsArea.setText(""); jdArea.setText(""); reportArea.setText("");
        fresherCheck.setSelected(false); jdBoostCheck.setSelected(false);
        progressBar.setValue(0); progressBar.setString("");
        roleCombo.setSelectedIndex(0);
    }

    private void loadSampleData() {
        nameField.setText("John Doe");
        emailField.setText("john.doe@example.com");
        phoneField.setText("9876543210");
        expField.setText("2");
        eduField.setText("Bachelor of Technology in Computer Science");
        skillsArea.setText("Java, Python, Git, Docker, Kubernetes, REST API, Agile");
        projectsArea.setText("Developed a Spring Boot microservices application.\n"
            + "Created DevOps pipelines using Jenkins and Docker.\n"
            + "Built REST APIs for e-commerce platform using Java and Spring.");
        jdArea.setText("Java, Kubernetes, Docker, CI/CD, Jenkins");
        fresherCheck.setSelected(false);
        jdBoostCheck.setSelected(true);
        roleCombo.setSelectedItem("Software Developer");
        reportArea.setText("");
        progressBar.setValue(0);
        progressBar.setString("");
    }

    private void copyReportToClipboard() {
        String report = reportArea.getText();
        if (report.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report to copy.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new StringSelection(report), null);
        JOptionPane.showMessageDialog(this, "Report copied to clipboard.");
    }

    private void exportReport() {
        String report = reportArea.getText();
        if (report.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Report");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt"))
                file = new File(file.getParentFile(), file.getName() + ".txt");
            try {
                Files.write(file.toPath(), report.getBytes(StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(this, "Report exported to:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importFields() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Fields");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String> lines = Files.readAllLines(
                    chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
                Map<String, String> data = new HashMap<>();
                for (String line : lines) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) data.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
                if (data.containsKey("name"))            nameField.setText(data.get("name"));
                if (data.containsKey("email"))           emailField.setText(data.get("email"));
                if (data.containsKey("phone"))           phoneField.setText(data.get("phone"));
                if (data.containsKey("experience"))      expField.setText(data.get("experience"));
                if (data.containsKey("education"))       eduField.setText(data.get("education"));
                if (data.containsKey("skills"))          skillsArea.setText(data.get("skills"));
                if (data.containsKey("projects"))        projectsArea.setText(data.get("projects"));
                if (data.containsKey("job description")) jdArea.setText(data.get("job description"));
                JOptionPane.showMessageDialog(this, "Fields imported successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ATSResumePro().setVisible(true));
    }
}