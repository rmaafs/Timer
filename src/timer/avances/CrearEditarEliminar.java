package timer.avances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import spigot.file.configuration.FileConfiguration;
import static timer.Timer.config;
import static timer.Timer.msg;
import static timer.Timer.textAreaDialog;

public class CrearEditarEliminar {

    List<String> creados = new ArrayList<>();
    List<String> editados = new ArrayList<>();
    List<String> eliminados = new ArrayList<>();
    String key;

    public CrearEditarEliminar(String key, FileConfiguration config) {
        this.key = key;
        if (config.contains(key)) {
            creados = config.getStringList(key + "creado");
            editados = config.getStringList(key + "editado");
            eliminados = config.getStringList(key + "eliminado");
        }
    }
    
    public int getCreados() {
        return creados.size();
    }
    
    public int getEditados() {
        return editados.size();
    }
    
    public int getEliminados() {
        return eliminados.size();
    }

    public boolean abrirCreados(javax.swing.JLabel txt) {
        String nuevo = textAreaDialog(null, getFormat(creados), "Creados");
        if (nuevo != null) {
            creados = Arrays.asList(nuevo.split("\n"));
            config.set(key + "creado", creados);
            msg("CREADOS Guardados con éxito.");
            txt.setText(creados.size() + "");
            return true;
        }
        return false;
    }
    
    public boolean abrirEditados(javax.swing.JLabel txt) {
        String nuevo = textAreaDialog(null, getFormat(editados), "Editados");
        if (nuevo != null) {
            editados = Arrays.asList(nuevo.split("\n"));
            config.set(key + "editado", editados);
            msg("EDITADOS Guardados con éxito.");
            txt.setText(editados.size() + "");
            return true;
        }
        return false;
    }
    
    public boolean abrirEliminados(javax.swing.JLabel txt) {
        String nuevo = textAreaDialog(null, getFormat(eliminados), "Eliminados");
        if (nuevo != null) {
            eliminados = Arrays.asList(nuevo.split("\n"));
            config.set(key + "eliminado", eliminados);
            msg("ELIMINADOS Guardados con éxito.");
            txt.setText(eliminados.size() + "");
            return true;
        }
        return false;
    }
    
    public String getCreadosDocumentacion() {
        StringBuilder str = new StringBuilder();
        for (String s : creados) {
            str.append("\n\n- ").append(s);
        }
        return str.toString();
    }
    
    public String getEditadosDocumentacion() {
        StringBuilder str = new StringBuilder();
        for (String s : editados) {
            str.append("\n\n- ").append(s);
        }
        return str.toString();
    }
    
    public String getEliminadosDocumentacion() {
        StringBuilder str = new StringBuilder();
        for (String s : eliminados) {
            str.append("\n\n- ").append(s);
        }
        return str.toString();
    }
    
    public boolean isModificado() {
        return !(creados.isEmpty() && editados.isEmpty() && eliminados.isEmpty());
    }
    
    private String getFormat(List<String> list) {
        StringBuilder str = new StringBuilder();
        for (String s : list) {
            str.append(s).append("\n");
        }
        return str.toString();
    }
}
