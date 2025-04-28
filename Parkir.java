import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Parkir extends JFrame {
    private JComboBox<String> jenisCombo;
    private JTextField inputJam, inputJamMasuk, inputJamKeluar;
    private JButton hitungButton, tambahButton, selesaiButton;
    private JTextArea tempatOutput;
    private JComboBox<String> metodeInputJam;

    private int totalKendaraan = 0;
    private int totalPendapatan = 0;
    private final ArrayList<String> laporanParkir = new ArrayList<>();

    public Parkir() {
        setTitle("Sistem ParkirChan");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.add(new JLabel("Jenis Kendaraan:"));
        String[] jenisKendaraan = {"Motor", "Mobil", "Truk"};
        jenisCombo = new JComboBox<>(jenisKendaraan);
        formPanel.add(jenisCombo);

//        button dan field untuk inputan
        formPanel.add(new JLabel("Metode Input Durasi:"));
        String[] metode = {"Input Jumlah Jam", "Input Jam Masuk & Jam Keluar (24 Jam)"};
        metodeInputJam = new JComboBox<>(metode);
        formPanel.add(metodeInputJam);

        formPanel.add(new JLabel("Jumlah Jam (e. g 2.5):"));
        inputJam = new JTextField();
        formPanel.add(inputJam);

        formPanel.add(new JLabel("Jam Masuk (e.g 12:45):"));
        inputJamMasuk = new JTextField();
        formPanel.add(inputJamMasuk);

        formPanel.add(new JLabel("Jam Keluar (e.g 12:45):"));
        inputJamKeluar = new JTextField();
        formPanel.add(inputJamKeluar);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        hitungButton = new JButton("Hitung Biaya Parkir");
        tambahButton = new JButton("Tambah Kendaraan");
        selesaiButton = new JButton("Selesai & Simpan Laporan");
        buttonPanel.add(hitungButton);
        buttonPanel.add(tambahButton);
        buttonPanel.add(selesaiButton);

        tempatOutput = new JTextArea(15, 50);
        tempatOutput.setEditable(false);
        tempatOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(tempatOutput);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

//      atur layout di form
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        metodeInputJam.addActionListener(e -> toggleInputFields());
        hitungButton.addActionListener(e -> hitungBiayaParkir());
        tambahButton.addActionListener(e -> resetInput());
        selesaiButton.addActionListener(e -> tampilkanRingkasan());
        toggleInputFields();
    }

    private void toggleInputFields() { //fungsi kalau user pilih input jam manual
        boolean manual = metodeInputJam.getSelectedIndex() == 0;
        inputJam.setEnabled(manual);
        inputJamMasuk.setEnabled(!manual);
        inputJamKeluar.setEnabled(!manual);
    }

    private void hitungBiayaParkir() { //fungsi untuk hitung biaya parkir
        try {
            String jenis = (String) jenisCombo.getSelectedItem();

            Kendaraan kendaraan = new Kendaraan(jenis);
            int totalBiaya = 0;
            String lamaParkir = "";

            if (metodeInputJam.getSelectedIndex() == 0) {
                double jam = Double.parseDouble(inputJam.getText());
                totalBiaya = kendaraan.hitungBiayaParkir(jam);

                int jamParkir = (int) jam;
                int menitParkir = (int) ((jam - jamParkir) * 60);
                lamaParkir = String.format("%d Jam %d Menit", jamParkir, menitParkir);

            } else {
                LocalTime jamMasuk = LocalTime.parse(inputJamMasuk.getText(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime jamKeluar = LocalTime.parse(inputJamKeluar.getText(), DateTimeFormatter.ofPattern("HH:mm"));

                if (jamKeluar.isBefore(jamMasuk)) {
                    JOptionPane.showMessageDialog(this, "Jam keluar tidak boleh sebelum jam masuk!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                totalBiaya = kendaraan.hitungBiayaParkir(jamMasuk, jamKeluar);

                int totalMenit = (int) java.time.Duration.between(jamMasuk, jamKeluar).toMinutes();
                int jamParkir = totalMenit / 60;
                int menitParkir = totalMenit % 60;
                lamaParkir = String.format("%d Jam %d Menit", jamParkir, menitParkir);
            }

            String ringkasan = "--- Ringkasan Parkir ---\n" +
                    "Jenis Kendaraan : " + jenis + "\n" +
                    "Lama Parkir     : " + lamaParkir + "\n" +
                    "Total Biaya     : Rp " + totalBiaya + "\n\n";

            tempatOutput.append(ringkasan);

            laporanParkir.add(ringkasan);
            totalKendaraan++;
            totalPendapatan += totalBiaya;

            hitungButton.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format waktu harus HH:mm atau angka desimal untuk jumlah jam.", "WARNING", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetInput() { //untuk menghapus inputan ketika user ingin menambah kendaraan
        jenisCombo.setSelectedIndex(0);
        inputJam.setText("");
        inputJamMasuk.setText("");
        inputJamKeluar.setText("");
        hitungButton.setEnabled(true);
    }

    private void tampilkanRingkasan() { //untuk menampilkan biaya parkir dkk
        String ringkasanAkhir = "\n=== Ringkasan Akhir ===\n" +
                "Jumlah Kendaraan : " + totalKendaraan + "\n" +
                "Total Pendapatan : Rp " + totalPendapatan + "\n";

        tempatOutput.append(ringkasanAkhir);
        laporanParkir.add(ringkasanAkhir);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("laporan_parkir.txt"))) {
            for (String data : laporanParkir) {
                writer.write(data);
            }
            JOptionPane.showMessageDialog(this, "Laporan berhasil disimpan ke laporan_parkir.txt!");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
