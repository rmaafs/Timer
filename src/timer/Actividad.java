package timer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import timer.avances.Archivos;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import spigot.file.configuration.FileConfiguration;
import static timer.Timer.TARIFA;
import static timer.Timer.config;
import static timer.Timer.msg;
import static timer.Timer.textAreaDialog;
import timer.avances.Acciones;
import timer.avances.Datos;
import timer.avances.Procesos;
import timer.avances.Querys;

public class Actividad {

    String key;

    int id;
    Date iniciado;
    String iniciadoDia;
    Date terminado;
    String terminadoDia;
    boolean finalizado = false;
    int segundos = 0;
    int minutos = 0;
    int horas = 0;
    long tiempo = 0;
    List<String> mensaje = new ArrayList<>();
    List<String> descripcion = new ArrayList<>();
    Pausa pausas;
    Archivos archivos;
    Querys querys;
    Procesos procesos;
    Acciones acciones;
    Datos datos;
    float ganancia = 0f;
    String linkPrincipal = "";

    public Actividad(String key, String act, FileConfiguration config) throws Exception {
        crearInstancias(key, act, config);
    }

    public Actividad(String key, int id, FileConfiguration config) throws Exception {
        this.id = id;
        this.key = key;
        String DIA[] = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        iniciado = new Date();
        iniciadoDia = DIA[LocalDate.now().getDayOfWeek().getValue() - 1];

        config.set(key + ".iniciado", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(iniciado));
        config.set(key + ".iniciadoDia", iniciadoDia);
        config.set(key + ".finalizado", finalizado);
        crearInstancias(key, id + "", config);
    }

    private void crearInstancias(String key, String act, FileConfiguration config) throws Exception {
        this.key = key;
        id = Integer.valueOf(act);
        if (config.contains(key + "iniciado")) {
            iniciado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(config.getString(key + "iniciado"));
        }
        if (config.contains(key + "iniciadoDia")) {
            iniciadoDia = config.getString(key + "iniciadoDia");
        }
        if (config.contains(key + "terminado")) {
            terminado = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(config.getString(key + "terminado"));
        }
        if (config.contains(key + "terminadoDia")) {
            terminadoDia = config.getString(key + "terminadoDia");
        }
        if (config.contains(key + "finalizado")) {
            finalizado = config.getBoolean(key + "finalizado");
        }
        if (config.contains(key + "segundos")) {
            segundos = config.getInt(key + "segundos");
        }
        if (config.contains(key + "minutos")) {
            minutos = config.getInt(key + "minutos");
        }
        if (config.contains(key + "horas")) {
            horas = config.getInt(key + "horas");
        }
        if (config.contains(key + "tiempo")) {
            tiempo = config.getLong(key + "tiempo");
        }
        if (config.contains(key + "mensaje")) {
            mensaje = config.getStringList(key + "mensaje");
        }
        if (config.contains(key + "descripcion")) {
            descripcion = config.getStringList(key + "descripcion");
        }
        if (config.contains(key + "ganado")) {
            ganancia = (float) config.getDouble(key + "ganado");
        }

        pausas = new Pausa(key + "pausa.", config);
        archivos = new Archivos(key + "archivo.", config);
        querys = new Querys(key + "query.", config);

        key += "modelado.";
        procesos = new Procesos(key + "proceso.", config);
        acciones = new Acciones(key + "acciones.", config);
        datos = new Datos(key + "datos.", config);
    }

    public void save(FileConfiguration config) {
        config.set(key + "tiempo", tiempo);
    }

    public void addPaused(FileConfiguration config) {
        if (pausas == null) {
            pausas = new Pausa(key + "pausa.", config);
        }
        config.set(key + "pausa.veces", ++pausas.veces);
    }

    public void addTimePaused(long time, FileConfiguration config) {
        if (pausas == null) {
            pausas = new Pausa(key + "pausa.", config);
        }
        pausas.addTime(time);
        config.set(key + "pausa.segundos", pausas.segundos);
        config.set(key + "pausa.minutos", pausas.minutos);
        config.set(key + "pausa.horas", pausas.horas);
        config.set(key + "pausa.totaltime", pausas.totaltime);
    }

    public boolean abrirEditarMensaje() {
        String nuevo = textAreaDialog(null, getFormat(mensaje), "Mensaje");
        if (nuevo != null) {
            mensaje = Arrays.asList(nuevo.split("\n"));
            config.set(key + "mensaje", mensaje);
            //msg("Mensaje guardado con éxito.");
            return true;
        }
        return false;
    }

    public boolean abrirEditarDescripcion() {
        String nuevo = textAreaDialog(null, getFormat(descripcion), "Descripción");
        if (nuevo != null) {
            descripcion = Arrays.asList(nuevo.split("\n"));
            config.set(key + "descripcion", descripcion);
            //msg("Descripción guardado con éxito.");
            return true;
        }
        return false;
    }

    public void finish(FileConfiguration config) {
        finalizado = true;
        if (mensaje.isEmpty() || mensaje.get(0).equals("")) {
            mensaje = Arrays.asList(textAreaDialog(null, "", "Mensaje: " + id).split("\n"));
        }

        if (descripcion.isEmpty() || descripcion.get(0).equals("")) {
            descripcion = Arrays.asList(textAreaDialog(null, "", "Descripción: " + id + "").split("\n"));
        }

        if (mensaje == null || descripcion == null) {
            return;
        }

        String DIA[] = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        terminado = new Date();
        terminadoDia = DIA[LocalDate.now().getDayOfWeek().getValue() - 1];

        Duration duration = Duration.ofMillis(-tiempo);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long millis = duration.toMillis();
        long seconds = millis / 1000;

        segundos = (int) seconds;
        minutos = (int) minutes;
        horas = (int) hours;

        config.set(key + "mensaje", mensaje);
        config.set(key + "descripcion", descripcion);
        if (!config.contains(key + "terminado")) {
            config.set(key + "terminado", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(terminado));
            config.set(key + "terminadoDia", terminadoDia);
        }

        config.set(key + "finalizado", finalizado);
        config.set(key + "segundos", segundos);
        config.set(key + "minutos", minutos);
        config.set(key + "horas", horas);

        float horasp = horas;
        float minutosp = minutos;
        if (minutosp > 30) {
            minutosp = 0;
            horasp++;
        } else {
            minutosp = 30;
        }

        ganancia = TARIFA * (horasp + (minutosp / 60.00f));
        config.set(key + "ganado", ganancia);
    }

    public float getGanancia() {
        return ganancia;
    }

    public String getDocumentacion(Proyecto pActual) {
        Duration duration = Duration.ofMillis(-tiempo);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long millis = duration.toMillis();
        long seconds = millis / 1000;

        segundos = (int) seconds;
        minutos = (int) minutes;
        horas = (int) hours;

        String str = "";
        String barra = "\n\n-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~\n\n";
        if (linkPrincipal.equals("")) {
            linkPrincipal = JOptionPane.showInputDialog("Dame el link principal donde se verán los cambios:");
        }

        for (String s : mensaje) {
            str += s + "\n\n";
        }

        if (!linkPrincipal.equals("") && !linkPrincipal.equals("s")) {
            str += "- " + linkPrincipal;
        } else {
            str = str.substring(0, str.length() - 2);//Eliminamos los saltos de línea
        }

        if (pActual.link != null && !pActual.link.equals("trello.com")) {
            str += "\n\n**Trello:** " + pActual.link;
        }
        
        if (pActual.ticket != null && !pActual.ticket.equals("sipa.tickets")) {
            str += "\n\n**Ticket de SIPA:** " + pActual.ticket;
        }

        str += barra;

        str += "**Descripción:**";
        for (String s : descripcion) {
            str += "\n\n- " + s + "";
        }
        str += barra;

        if (archivos.isModificado()) {
            if (archivos.getCreados() > 0) {
                str += "**Archivos que creé:**";
                str += archivos.getCreadosDocumentacion();
                str += barra;
            }

            if (archivos.getEditados() > 0) {
                str += "**Archivos que edité:**";
                str += archivos.getEditadosDocumentacion();
                str += barra;
            }

            if (archivos.getEliminados() > 0) {
                str += "**Archivos que eliminé:**";
                str += archivos.getEliminadosDocumentacion();
                str += barra;
            }
        }

        if (querys.isModificado()) {
            if (querys.getCreados() > 0) {
                str += "**Querys que creé:**";
                str += querys.getCreadosDocumentacion();
                str += barra;
            }

            if (querys.getEditados() > 0) {
                str += "**Querys que edité:**";
                str += querys.getEditadosDocumentacion();
                str += barra;
            }

            if (querys.getEliminados() > 0) {
                str += "**Querys que eliminé:**";
                str += querys.getEliminadosDocumentacion();
                str += barra;
            }
        }

        if (procesos.isModificado()) {
            if (procesos.getCreados() > 0) {
                str += "**Procesos que creé:**";
                str += procesos.getCreadosDocumentacion();
                str += barra;
            }

            if (procesos.getEditados() > 0) {
                str += "**Procesos que edité:**";
                str += procesos.getEditadosDocumentacion();
                str += barra;
            }

            if (procesos.getEliminados() > 0) {
                str += "**Procesos que eliminé:**";
                str += procesos.getEliminadosDocumentacion();
                str += barra;
            }
        }

        if (acciones.isModificado()) {
            if (acciones.getCreados() > 0) {
                str += "**Acciones que creé:**";
                str += acciones.getCreadosDocumentacion();
                str += barra;
            }

            if (acciones.getEditados() > 0) {
                str += "**Acciones que edité:**";
                str += acciones.getEditadosDocumentacion();
                str += barra;
            }

            if (acciones.getEliminados() > 0) {
                str += "**Acciones que eliminé:**";
                str += acciones.getEliminadosDocumentacion();
                str += barra;
            }
        }

        if (datos.isModificado()) {
            if (datos.getCreados() > 0) {
                str += "**Datos que cree:**";
                str += datos.getCreadosDocumentacion();
                str += barra;
            }

            if (datos.getEditados() > 0) {
                str += "**Datos que edité:**";
                str += datos.getEditadosDocumentacion();
                str += barra;
            }

            if (datos.getEliminados() > 0) {
                str += "**Datos que eliminé:**";
                str += datos.getEliminadosDocumentacion();
                str += barra;
            }
        }

        str += "**Tiempo que tardé en leer explicación, analizar, entender, ejecutar y documentar:**\n\n";
        if (segundos > 0) {
            segundos = 0;
            minutos++;
        }
        if (minutos < 30) {
            minutos = 30;
        } else if (minutos > 30) {
            minutos = 0;
            horas++;
        }
        str += "- " + (horas > 0 ? horas + "h " : "") + (minutos > 0 ? minutos + "m" : "")/* + " / (Sin redondear " + (hours > 0 ? hours + "h " : "") + minutes + "m)"*/;

        return str;
    }

    public String getFechaFormat() {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(iniciado);
    }

    private String getFormat(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (String s : list) {
            str.append(s).append("\n");
        }
        return str.toString();
    }
}
