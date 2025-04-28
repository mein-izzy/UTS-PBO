import java.time.Duration;
import java.time.LocalTime;

public class Kendaraan {
    private String jenis;
    private double lamaParkir; // dalam jam (bisa pecahan)
    private static final int tarifMotor = 2000;
    private static final int tarifMobil = 5000;
    private static final int tarifTruk = 8000;

    public Kendaraan(String jenis) {
        this.jenis = jenis;
    }

    public int hitungBiayaParkir(double jamParkir) {
        this.lamaParkir = jamParkir;
        return calculateTotal();
    }

    public int hitungBiayaParkir(LocalTime jamMasuk, LocalTime jamKeluar) {
        Duration durasi = Duration.between(jamMasuk, jamKeluar);
        lamaParkir = durasi.toMinutes() / 60.0;
        return calculateTotal();
    }

    private int calculateTotal() {
        int tarifPerJam = 0;

        switch (jenis.toLowerCase()) {
            case "motor":
                tarifPerJam = tarifMotor;
                break;
            case "mobil":
                tarifPerJam = tarifMobil;
                break;
            case "truk":
                tarifPerJam = tarifTruk;
                break;
        }
        int total = (int) Math.ceil(lamaParkir) * tarifPerJam;
        if (lamaParkir > 5) {
            total = total - (total / 10);
        }
        return total;
    }

}
