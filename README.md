# 📄 ATS Resume Analyzer Pro

A desktop application built with **Java Swing** that analyzes your resume 
against ATS (Applicant Tracking System) keyword scoring — helping you 
improve your resume before applying for jobs.

![Java](https://img.shields.io/badge/Java-16%2B-orange)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Mac%20%7C%20Linux-lightgrey)

---

## 🖥️ Preview
<img width="1911" height="1080" alt="Screenshot 2026-04-10 111033" src="https://github.com/user-attachments/assets/0a4da969-d780-4ce7-8f00-13d2bcbc33bf" />
<img width="1911" height="1080" alt="Screenshot 2026-04-10 111013" src="https://github.com/user-attachments/assets/710dc9a7-1946-45c7-bce9-1fb0ce0d4a7c" />




---

## ✨ Features

- ✅ Supports **6 job roles** with role-specific keyword banks
- ✅ **Weighted keyword scoring** — high-value skills score more points
- ✅ **Job Description Boost** — paste JD to boost matching keywords by 1.5×
- ✅ **Fresher mode** — tailored recommendations for 0–1 year experience
- ✅ **Missing keywords list** — shows exactly what to add to your resume
- ✅ **Visual score bar** — instant progress bar with percentage
- ✅ **Light and Dark theme** — switchable from menu bar
- ✅ **Export report** — save full analysis report as `.txt` file
- ✅ **Import fields** — load resume data from `.txt` file
- ✅ **Copy to clipboard** — one click full report copy
- ✅ **Sample data** — load demo resume instantly for testing
- ✅ **Keyboard shortcuts** — Ctrl+Enter to analyze, Ctrl+Backspace to clear

---

## 🎯 Supported Roles

| Role | Sample Keywords |
|------|----------------|
| 💻 Software Developer | Java, Python, Docker, Spring, React, AWS, Kubernetes... |
| 🔐 Cybersecurity Analyst | Pen Testing, SIEM, Wireshark, Metasploit, Nmap... |
| 🌐 Network Engineer | Cisco, BGP, OSPF, TCP/IP, VPN, Firewall... |
| 📊 Data Analyst | Tableau, Power BI, Pandas, SQL, Machine Learning... |
| 🖥️ Web Developer | HTML, CSS, React, Node.js, MongoDB, REST API... |
| ⚙️ DevOps Engineer | Docker, Kubernetes, CI/CD, Terraform, Jenkins... |

---

## 🚀 How to Run

### Requirements
- Java **16 or higher**
- No external libraries needed — pure Java Swing

### Steps

**1. Clone the repository:**
```bash
git clone https://github.com/Pratikjpg/ATSResumePro.git
cd ATSResumePro
```

**2. Compile:**
```bash
javac ATSResumePro.java
```

**3. Run:**
```bash
java ATSResumePro
```

---

## 📖 How to Use

1. Fill in **Name, Email, Phone, Experience, Education**
2. Select your **Role** from the dropdown
3. Enter your **Skills** (comma separated)
4. Describe your **Projects**
5. *(Optional)* Paste **Job Description** and check **JD Boost**
6. Click **▶ Analyze** or press **Ctrl + Enter**
7. View your **score, matched keywords, missing keywords**
8. **Export** or **Copy** the report

---

## ⌨️ Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + Enter` | Analyze resume |
| `Ctrl + Backspace` | Clear all fields |

---

## 📊 Scoring System

| Step | Formula |
|------|---------|
| Base score | `min(occurrences, 3) × keyword weight` |
| JD Boost | `weight × 1.5` for JD-matched keywords |
| Final % | `totalScore / maxPossibleScore × 100` |
| Cap | Maximum **100%** |

Keyword weights range from **5 to 10** based on industry importance.

---

## 📁 Project Structure
ATSResumePro/
├── ATSResumePro.java    # Single file — complete application
├── README.md            # Project documentation
├── .gitignore           # Java gitignore
└── LICENSE              # MIT License

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| Java 16+ | Core language |
| Java Swing | GUI framework |
| SwingWorker | Background analysis thread |
| GridBagLayout | Sidebar form layout |
| JFileChooser | Export / Import file dialogs |
| HashMap / LinkedHashMap | Keyword storage and scoring |

---

## 🐛 Bugs Fixed

| Bug | Fix Applied |
|-----|-------------|
| Score exceeded 100% with JD Boost ON | Capped at 100% with corrected maxPossibleScore |
| Duplicate keywords due to case mismatch | Case-insensitive deduplication using LinkedHashMap |
| Full JD sentences appearing in missing keywords | Token length + stopword filtering on JD input |

---

## 👨‍💻 Author

**Pratik Jani**
B.Tech Computer Science

[![GitHub](https://img.shields.io/badge/GitHub-Pratikjpg-black?logo=github)](https://github.com/Pratikjpg)

---

## 📜 License

This project is licensed under the **MIT License** — see the
[LICENSE](LICENSE) file for details.

---

## ⭐ Support

If you found this project useful, please consider giving it a **⭐ star** on GitHub!
