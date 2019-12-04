package timer;

import java.time.Duration;
import spigot.file.configuration.FileConfiguration;

public class Pausa {

    int veces = 0;
    int segundos = 0, minutos = 0, horas = 0;
    long totaltime = 0;

    public Pausa(String key, FileConfiguration config) {
        if (config.contains(key)) {
            veces = config.getInt(key + "veces");
            segundos = config.getInt(key + "segundos");
            minutos = config.getInt(key + "minutos");
            horas = config.getInt(key + "horas");
            totaltime = config.getInt(key + "totaltime");
        }
    }

    public void addTime(long time) {
        totaltime += time;
        Duration duration = Duration.ofMillis(totaltime);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long millis = duration.toMillis();
        long seconds = millis / 1000;

        segundos = (int) seconds;
        minutos = (int) minutes;
        horas = (int) hours;
        //System.out.println(String.format("%02d:%02d:%02d", hours, minutes, seconds, millis));
    }
}
