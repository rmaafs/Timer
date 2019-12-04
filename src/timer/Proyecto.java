package timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import spigot.file.configuration.FileConfiguration;

class Proyecto {

    int numero;
    int anio = 2019;
    String nombre;
    String ambiente;
    String link;
    String ticket;
    List<Actividad> actividades = new ArrayList<>();

    public Proyecto(int numero, String nombre) {
        this.numero = numero;
        this.nombre = nombre;
    }

    public void cargar(HashMap<String, Actividad> hashAct, JComboBox<String> cbActividades, FileConfiguration config) {
        cbActividades.removeAllItems();
        actividades.clear();
        String key = numero + ".";
        numero = config.getInt(key + "numero");
        anio = config.getInt(key + "anio");
        nombre = config.getString(key + "nombre");
        ambiente = config.getString(key + "ambiente");
        link = config.getString(key + "link");
        ticket = config.getString(key + "ticket");
        if (config.contains(key + "actividades")) {
            for (String act : config.getConfigurationSection(key + "actividades").getKeys(false)) {
                key = numero + ".";
                key = key + "actividades." + act + ".";
                try {
                    Actividad a = new Actividad(key, act, config);
                    System.out.println("Actividad " + a.id + " cargada.");
                    actividades.add(a);
                    hashAct.put(a.id + "", a);
                    cbActividades.addItem(a.id + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "ATENCION: Este proyecto no tiene actividades.");
        }
    }

    public void nuevaActividad(HashMap<String, Actividad> hashAct, JComboBox<String> cbActividades, FileConfiguration config) {
        String key = numero + ".";
        for (int i = 0; i < 1000; i++) {
            if (!config.contains(key + "actividades." + i)) {
                try {
                    Actividad a = new Actividad(key + "actividades." + i + ".", i, config);
                    hashAct.put(a.id + "", a);
                    actividades.add(a);
                    cbActividades.addItem(a.id + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void eliminarActividad(Actividad a, HashMap<String, Actividad> hashAct, FileConfiguration config) {
        String key = numero + ".actividades";
        hashAct.remove(a.id + "");
        config.set(key + "." + a.id, null);
    }

    public String getFormato() {
        return numero + " " + nombre;
    }

    public String getFullFormato() {
        return anio + "/" + ambiente + " " + numero + " " + nombre;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }
}
