package timer;

import java.awt.Component;
import java.awt.Desktop;
import spigot.file.configuration.YamlConfiguration;
import spigot.file.configuration.FileConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.bridj.Pointer;
import org.bridj.cpp.com.COMRuntime;
import org.bridj.cpp.com.shell.ITaskbarList3;
import org.bridj.jawt.JAWTUtils;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

public class Timer extends javax.swing.JFrame {

    private javax.swing.Timer timer;
    private ITaskbarList3 list;
    private long lastTickTime = 0, tiempoPausado = 0, timeCuandoSePauso = 0;
    private boolean pausado = true;
    private Pointer<?> hwnd;

    private File fconfig;
    public static FileConfiguration config;

    private HashMap<String, Proyecto> proyectos = new HashMap<>();
    private HashMap<String, Actividad> actividades = new HashMap<>();
    private Proyecto pActual;
    private Actividad aActual;
    private float quincena = 0;
    public static final float TARIFA = 55.0f;
    public static final String PATH = "C:\\Users\\ElMaps\\Documents\\Timer\\";

    public Timer() throws ClassNotFoundException, UnsupportedEncodingException {
        setIconImage(new ImageIcon(Timer.class.getProtectionDomain().getClassLoader().getResource("timer.png")).getImage());
        initComponents();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (preguntar("¿Quieres dejar una nota para recordarte la próxima vez?", "Recordatorio")) {
                    String txt = JOptionPane.showInputDialog("Recordatorio");
                    config.set("ultimo.recordatorio", txt);
                    saveFile();
                }
            }
        });

        crearArchivo();

        for (String s : config.getKeys(false)) {
            if (s.equals("ultimo") || s.equals("quincena")) {
                continue;
            }
            int numTicket = Integer.valueOf(s);
            String nombreTicket = config.getString(s + ".nombre");
            Proyecto p = new Proyecto(numTicket, nombreTicket);
            proyectos.put(numTicket + "", p);
            cbProyectos.addItem(p.numero + "");
            pActual = p;
        }

        if (config.contains("ultimo")) {
            if (config.contains("ultimo.recordatorio")) {
                msg("Recordatorio: " + config.getString("ultimo.recordatorio"));
                config.set("ultimo.recordatorio", null);
                saveFile();
            }
            if (config.contains("ultimo.proyecto") || config.contains("ultimo.actividad")) {
                pActual = proyectos.get(config.getInt("ultimo.proyecto") + "");
                pActual.cargar(actividades, cbActividades, config);
                cbProyectos.setSelectedItem(config.getInt("ultimo.proyecto") + "");

                cargarActividad(actividades.get(config.getInt("ultimo.actividad") + ""));
                cbActividades.setSelectedItem(config.getInt("ultimo.actividad") + "");

                txtProyecto.setText(pActual.getFullFormato());
            }

        }
        if (config.contains("ultimo")) {
            quincena = (float) config.getDouble("quincena");
        }

        //pActual.cargar(actividades, config);
        long hwndVal = JAWTUtils.getNativePeerHandle(this);
        list = COMRuntime.newInstance(ITaskbarList3.class);
        hwnd = Pointer.pointerToAddress(hwndVal);
        list.SetProgressValue((Pointer) hwnd, 0, 10);
        txtTotalGanancia.setText("$" + String.format("%.2f", quincena));

        timer = new javax.swing.Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long runningTime = System.currentTimeMillis() - lastTickTime;
                Duration duration = Duration.ofMillis(runningTime);
                long hours = duration.toHours();
                duration = duration.minusHours(hours);
                long minutes = duration.toMinutes();
                duration = duration.minusMinutes(minutes);
                long millis = duration.toMillis();
                long seconds = millis / 1000;
                millis -= (seconds * 1000);
                txtTiempo.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds, millis));
                float total = ((float) hours + ((float) minutes / 60) + ((float) seconds / 60 / 60)) * TARIFA;

                txtGanancia.setText("$" + String.format("%.2f", total));
            }
        });
    }

    private void crearArchivo() {
        fconfig = new File(PATH + "save.yml");
        if (!fconfig.exists()) {
            try {
                fconfig.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        config = YamlConfiguration.loadConfiguration(fconfig);
    }

    private void saveFile() {
        try {
            config.save(fconfig);
            System.out.println("Guardado " + fconfig.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setScreenStarted() {
        ITaskbarList3.TbpFlag flag = ITaskbarList3.TbpFlag.TBPF_INDETERMINATE;
        list.SetProgressValue((Pointer) hwnd, 0, 10);
        list.SetProgressState((Pointer) hwnd, flag);
    }

    private void setScreenPaused() {
        ITaskbarList3.TbpFlag flag = ITaskbarList3.TbpFlag.TBPF_PAUSED;
        list.SetProgressValue((Pointer) hwnd, 10, 10);
        list.SetProgressState((Pointer) hwnd, flag);
    }

    private void setScreenReinciar() {
        ITaskbarList3.TbpFlag flag = ITaskbarList3.TbpFlag.TBPF_NOPROGRESS;
        list.SetProgressValue((Pointer) hwnd, 10, 10);
        list.SetProgressState((Pointer) hwnd, flag);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtTiempo = new javax.swing.JLabel();
        btnReiniciar = new javax.swing.JButton();
        btnPausar = new javax.swing.JButton();
        btnComenzar = new javax.swing.JButton();
        txtStatus = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbProyectos = new javax.swing.JComboBox<>();
        txtProyecto = new javax.swing.JLabel();
        btnBorrar = new javax.swing.JButton();
        btnCargar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cbActividades = new javax.swing.JComboBox<>();
        txtActividades = new javax.swing.JLabel();
        btnBorrarAct = new javax.swing.JButton();
        btnCargarAct = new javax.swing.JButton();
        btnGuardarAct = new javax.swing.JButton();
        btnNuevoAct = new javax.swing.JButton();
        btnCambiarTiempo = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnTerminarActividad = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        brnArchivosCreados = new javax.swing.JButton();
        btnArchivosEliminados = new javax.swing.JButton();
        btnArchivosEditados = new javax.swing.JButton();
        txtArchivosCreados = new javax.swing.JLabel();
        txtArchivosEditados = new javax.swing.JLabel();
        txtArchivosEliminados = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnQuerysCreados = new javax.swing.JButton();
        btnQuerysEliminados = new javax.swing.JButton();
        btnQuerysEditados = new javax.swing.JButton();
        txtQuerysCreados = new javax.swing.JLabel();
        txtQuerysEditados = new javax.swing.JLabel();
        txtQuerysEliminados = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnProcesosCreados = new javax.swing.JButton();
        btnProcesosEliminados = new javax.swing.JButton();
        btnProcesosEditados = new javax.swing.JButton();
        txtProcesosCreados = new javax.swing.JLabel();
        txtProcesosEditados = new javax.swing.JLabel();
        txtProcesosEliminados = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        btnAccionesCreadas = new javax.swing.JButton();
        btnAccionesEliminados = new javax.swing.JButton();
        btnAccionesEditadas = new javax.swing.JButton();
        txtAccionesCreados = new javax.swing.JLabel();
        txtAccionesEditados = new javax.swing.JLabel();
        txtAccionesEliminados = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnDatosCreados = new javax.swing.JButton();
        btnDatosEliminados = new javax.swing.JButton();
        btnDatosEditados = new javax.swing.JButton();
        txtDatosCreados = new javax.swing.JLabel();
        txtDatosEditados = new javax.swing.JLabel();
        txtDatosEliminados = new javax.swing.JLabel();
        btnDocumentacion = new javax.swing.JButton();
        btnEditarMensaje = new javax.swing.JButton();
        btnEditarDescripcion = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtGanancia = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtTotalGanancia = new javax.swing.JLabel();
        btnReiniciarQuincena = new javax.swing.JButton();
        btnTrello = new javax.swing.JButton();
        btnTicket = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Tiempo total:");

        txtTiempo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtTiempo.setForeground(new java.awt.Color(0, 153, 102));
        txtTiempo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTiempo.setText("00:00:00");

        btnReiniciar.setText("Reiniciar");
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarActionPerformed(evt);
            }
        });

        btnPausar.setText("Pausar");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });

        btnComenzar.setText("Comenzar");
        btnComenzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComenzarActionPerformed(evt);
            }
        });

        txtStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtStatus.setText("Pausado");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Proyecto"));
        jPanel1.setName("Guardados"); // NOI18N

        txtProyecto.setText("Ningun proyecto seleccionado");

        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnCargar.setText("Cargar");
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCargar)
                .addGap(58, 58, 58)
                .addComponent(btnBorrar))
            .addComponent(cbProyectos, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtProyecto)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(cbProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtProyecto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBorrar)
                    .addComponent(btnCargar)
                    .addComponent(btnNuevo)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Actividad"));
        jPanel2.setName("Guardados"); // NOI18N

        txtActividades.setText("Ninguna actividad seleccionada");

        btnBorrarAct.setText("Borrar");
        btnBorrarAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActActionPerformed(evt);
            }
        });

        btnCargarAct.setText("Cargar");
        btnCargarAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActActionPerformed(evt);
            }
        });

        btnGuardarAct.setText("Guardar");
        btnGuardarAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActActionPerformed(evt);
            }
        });

        btnNuevoAct.setText("Nuevo");
        btnNuevoAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(btnNuevoAct, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGuardarAct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCargarAct, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnBorrarAct))
            .addComponent(cbActividades, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtActividades)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(cbActividades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtActividades)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBorrarAct)
                    .addComponent(btnCargarAct)
                    .addComponent(btnGuardarAct)
                    .addComponent(btnNuevoAct)))
        );

        btnCambiarTiempo.setText("+/-");
        btnCambiarTiempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarTiempoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnTerminarActividad.setText("Terminar actividad");
        btnTerminarActividad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminarActividadActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Archivos"));
        jPanel4.setName("Archivos"); // NOI18N

        brnArchivosCreados.setText("Creados");
        brnArchivosCreados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brnArchivosCreadosActionPerformed(evt);
            }
        });

        btnArchivosEliminados.setText("Eliminados");
        btnArchivosEliminados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivosEliminadosActionPerformed(evt);
            }
        });

        btnArchivosEditados.setText("Editados");
        btnArchivosEditados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivosEditadosActionPerformed(evt);
            }
        });

        txtArchivosCreados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtArchivosCreados.setText("0");

        txtArchivosEditados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtArchivosEditados.setText("0");

        txtArchivosEliminados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtArchivosEliminados.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtArchivosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(brnArchivosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtArchivosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchivosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnArchivosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtArchivosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtArchivosCreados)
                    .addComponent(txtArchivosEditados)
                    .addComponent(txtArchivosEliminados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(brnArchivosCreados)
                    .addComponent(btnArchivosEliminados)
                    .addComponent(btnArchivosEditados))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Querys"));
        jPanel5.setName("Archivos"); // NOI18N

        btnQuerysCreados.setText("Creados");
        btnQuerysCreados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuerysCreadosActionPerformed(evt);
            }
        });

        btnQuerysEliminados.setText("Eliminados");
        btnQuerysEliminados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuerysEliminadosActionPerformed(evt);
            }
        });

        btnQuerysEditados.setText("Editados");
        btnQuerysEditados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuerysEditadosActionPerformed(evt);
            }
        });

        txtQuerysCreados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtQuerysCreados.setText("0");

        txtQuerysEditados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtQuerysEditados.setText("0");

        txtQuerysEliminados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtQuerysEliminados.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtQuerysCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuerysCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtQuerysEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuerysEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnQuerysEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtQuerysEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuerysCreados)
                    .addComponent(txtQuerysEditados)
                    .addComponent(txtQuerysEliminados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQuerysCreados)
                    .addComponent(btnQuerysEliminados)
                    .addComponent(btnQuerysEditados))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Procesos"));
        jPanel6.setName("Archivos"); // NOI18N

        btnProcesosCreados.setText("Creados");
        btnProcesosCreados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesosCreadosActionPerformed(evt);
            }
        });

        btnProcesosEliminados.setText("Eliminados");
        btnProcesosEliminados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesosEliminadosActionPerformed(evt);
            }
        });

        btnProcesosEditados.setText("Editados");
        btnProcesosEditados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesosEditadosActionPerformed(evt);
            }
        });

        txtProcesosCreados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProcesosCreados.setText("0");

        txtProcesosEditados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProcesosEditados.setText("0");

        txtProcesosEliminados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtProcesosEliminados.setText("0");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtProcesosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcesosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProcesosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcesosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnProcesosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtProcesosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProcesosCreados)
                    .addComponent(txtProcesosEditados)
                    .addComponent(txtProcesosEliminados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProcesosCreados)
                    .addComponent(btnProcesosEliminados)
                    .addComponent(btnProcesosEditados))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Acciones"));
        jPanel7.setName("Archivos"); // NOI18N

        btnAccionesCreadas.setText("Creados");
        btnAccionesCreadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccionesCreadasActionPerformed(evt);
            }
        });

        btnAccionesEliminados.setText("Eliminados");
        btnAccionesEliminados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccionesEliminadosActionPerformed(evt);
            }
        });

        btnAccionesEditadas.setText("Editados");
        btnAccionesEditadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccionesEditadasActionPerformed(evt);
            }
        });

        txtAccionesCreados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAccionesCreados.setText("0");

        txtAccionesEditados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAccionesEditados.setText("0");

        txtAccionesEliminados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtAccionesEliminados.setText("0");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtAccionesCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAccionesCreadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAccionesEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAccionesEditadas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAccionesEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAccionesEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAccionesCreados)
                    .addComponent(txtAccionesEditados)
                    .addComponent(txtAccionesEliminados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAccionesCreadas)
                    .addComponent(btnAccionesEliminados)
                    .addComponent(btnAccionesEditadas))
                .addGap(122, 122, 122))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos"));
        jPanel8.setName("Archivos"); // NOI18N

        btnDatosCreados.setText("Creados");
        btnDatosCreados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatosCreadosActionPerformed(evt);
            }
        });

        btnDatosEliminados.setText("Eliminados");
        btnDatosEliminados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatosEliminadosActionPerformed(evt);
            }
        });

        btnDatosEditados.setText("Editados");
        btnDatosEditados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatosEditadosActionPerformed(evt);
            }
        });

        txtDatosCreados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDatosCreados.setText("0");

        txtDatosEditados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDatosEditados.setText("0");

        txtDatosEliminados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDatosEliminados.setText("0");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtDatosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDatosCreados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDatosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDatosEditados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDatosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDatosEliminados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDatosCreados)
                    .addComponent(txtDatosEditados)
                    .addComponent(txtDatosEliminados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDatosCreados)
                    .addComponent(btnDatosEliminados)
                    .addComponent(btnDatosEditados))
                .addContainerGap())
        );

        btnDocumentacion.setText("Copiar documentación");
        btnDocumentacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDocumentacionActionPerformed(evt);
            }
        });

        btnEditarMensaje.setText("Editar mensaje");
        btnEditarMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarMensajeActionPerformed(evt);
            }
        });

        btnEditarDescripcion.setText("Editar descripción");
        btnEditarDescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarDescripcionActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Ganado en este ticket");

        txtGanancia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtGanancia.setForeground(new java.awt.Color(0, 153, 0));
        txtGanancia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtGanancia.setText("$0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtGanancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(txtGanancia, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Total quincena");

        txtTotalGanancia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotalGanancia.setForeground(new java.awt.Color(0, 153, 51));
        txtTotalGanancia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTotalGanancia.setText("$0");

        btnReiniciarQuincena.setText("Reiniciar quincena");
        btnReiniciarQuincena.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarQuincenaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTotalGanancia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(btnReiniciarQuincena, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtTotalGanancia, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReiniciarQuincena))
        );

        btnTrello.setText("Trello");
        btnTrello.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTrelloActionPerformed(evt);
            }
        });

        btnTicket.setText("Ticket");
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCambiarTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTrello, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnComenzar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPausar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReiniciar)))
                        .addGap(21, 21, 21)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnDocumentacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditarMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnEditarDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnTerminarActividad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTrello)
                            .addComponent(btnTicket)
                            .addComponent(btnCambiarTiempo))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStatus)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnComenzar)
                            .addComponent(btnPausar)
                            .addComponent(btnReiniciar))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnEditarMensaje)
                                    .addComponent(btnEditarDescripcion))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnTerminarActividad)
                                    .addComponent(btnDocumentacion)))
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnComenzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComenzarActionPerformed
        if (pausado) {
            if (timeCuandoSePauso != 0) {
                aActual.addTimePaused(System.currentTimeMillis() - timeCuandoSePauso, config);
                saveFile();
            }
            lastTickTime = System.currentTimeMillis() + tiempoPausado;
            pausado = false;
        } else {
            lastTickTime = System.currentTimeMillis();
        }
        timer.start();
        txtStatus.setText("CORRIENDO");

        setScreenStarted();
    }//GEN-LAST:event_btnComenzarActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
        if (!pausado) {
            timer.stop();
            timeCuandoSePauso = System.currentTimeMillis();
            tiempoPausado = lastTickTime - System.currentTimeMillis();
            txtStatus.setText("PAUSADO");
            pausado = true;
            setScreenPaused();

            aActual.addPaused(config);

            guardarActividad();
        }
    }//GEN-LAST:event_btnPausarActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        lastTickTime = System.currentTimeMillis();
        txtTiempo.setText("00:00:00");
        txtStatus.setText("REINICIADO");
        pausado = false;
        setScreenReinciar();
        if (preguntar("¿Deseas guardar los cambios?", pActual.getFullFormato())) {
            guardarActividad();
        }
    }//GEN-LAST:event_btnReiniciarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        NewProyectoForm form = new NewProyectoForm(this);
        form.show();
        form.setEnabled(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        if (!validarProyectoSeleccionado()) {
            return;
        }
        String value = cbProyectos.getSelectedItem().toString();
        Proyecto p = proyectos.get(value);
        if (preguntar("¿Estas seguro de borrar el proyecto " + p.getFullFormato() + "?", p.getFullFormato())) {
            System.out.println("Borrado: " + p.getNombre());
            txtProyecto.setText("Ningun proyecto seleccionado");
            msg("Proyecto " + p.getNombre() + " borrado.");

            cbProyectos.removeItem(value);
            proyectos.remove(value);
            config.set(p.getNumero() + "", null);

            saveFile();
            if (p == pActual) {
                pActual = null;
            }
        }

    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        String value = cbProyectos.getSelectedItem().toString();
        Proyecto p = proyectos.get(value);
        p.cargar(actividades, cbActividades, config);
        pActual = p;
        txtProyecto.setText(pActual.getFullFormato());
        txtActividades.setText("Ninguna actividad seleccionada");
        refreshReloj(0);
        txtGanancia.setText("$0");
    }//GEN-LAST:event_btnCargarActionPerformed

    private void btnCargarActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActActionPerformed
        if (!validarProyectoSeleccionado()) {
            return;
        }
        String value = cbActividades.getSelectedItem().toString();
        Actividad a = actividades.get(value);
        cargarActividad(a);
    }//GEN-LAST:event_btnCargarActActionPerformed

    private void btnGuardarActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActActionPerformed
        guardarActividad();
    }//GEN-LAST:event_btnGuardarActActionPerformed

    private void btnNuevoActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActActionPerformed
        if (preguntar("¿Seguro que quieres crear una actividad?", pActual.getFullFormato())) {
            pActual.nuevaActividad(actividades, cbActividades, config);
            saveFile();
            String value = cbActividades.getSelectedItem().toString();
            Actividad a = actividades.get(value);
            cargarActividad(a);
        }
    }//GEN-LAST:event_btnNuevoActActionPerformed

    private void btnBorrarActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActActionPerformed
        if (!validarActividadSeleccionada() || !validarProyectoSeleccionado()) {
            return;
        }
        String value = cbActividades.getSelectedItem().toString();
        Actividad a = aActual;

        if (preguntar("¿Seguro que quieres eliminar la actividad " + a.id + "?", pActual.getFullFormato())) {
            pActual.eliminarActividad(a, actividades, config);
            cbActividades.removeItem(value);
            saveFile();
            msg("Actividad " + a.id + " del proyecto " + pActual.getFullFormato() + " borrada.");
            txtActividades.setText("Ninguna actividad seleccionada");
            aActual = null;
        }
    }//GEN-LAST:event_btnBorrarActActionPerformed

    private void btnCambiarTiempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarTiempoActionPerformed
        if (!pausado) {
            msg("Debes de poner pausa primero.");
            return;
        }
        String input = JOptionPane.showInputDialog("Dame el tiempo en minutos (Si quieres remover, usa número negativo. Si quieres remover segundos, usa s al final de tu número)");
        boolean segundos = input.contains("s");
        int num = Integer.valueOf(input.replaceAll("s", ""));
        tiempoPausado -= segundos ? (num * 1000) : (num * 1000 * 60);
        refreshReloj(-tiempoPausado);
    }//GEN-LAST:event_btnCambiarTiempoActionPerformed

    private void btnTerminarActividadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminarActividadActionPerformed
        if (!pausado) {
            msg("Necesitas tener pausado el tiempo para terminar la actividad.");
            return;
        }
        aActual.finish(config);
        quincena += aActual.getGanancia();
        config.set("quincena", quincena);
        saveFile();
        txtTotalGanancia.setText("$" + String.format("%.2f", quincena));
        msg("Actividad " + aActual.id + " finalizada.");
    }//GEN-LAST:event_btnTerminarActividadActionPerformed

    private void brnArchivosCreadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brnArchivosCreadosActionPerformed
        if (aActual.archivos.abrirCreados(txtArchivosCreados)) {
            saveFile();
        }
    }//GEN-LAST:event_brnArchivosCreadosActionPerformed

    private void btnArchivosEditadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivosEditadosActionPerformed
        if (aActual.archivos.abrirEditados(txtArchivosEditados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnArchivosEditadosActionPerformed

    private void btnArchivosEliminadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivosEliminadosActionPerformed
        if (aActual.archivos.abrirEliminados(txtArchivosEliminados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnArchivosEliminadosActionPerformed

    private void btnQuerysCreadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuerysCreadosActionPerformed
        if (aActual.querys.abrirCreados(txtQuerysCreados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnQuerysCreadosActionPerformed

    private void btnQuerysEditadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuerysEditadosActionPerformed
        if (aActual.querys.abrirEditados(txtQuerysEditados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnQuerysEditadosActionPerformed

    private void btnQuerysEliminadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuerysEliminadosActionPerformed
        if (aActual.querys.abrirEliminados(txtQuerysEliminados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnQuerysEliminadosActionPerformed

    private void btnProcesosCreadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesosCreadosActionPerformed
        if (aActual.procesos.abrirCreados(txtProcesosCreados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnProcesosCreadosActionPerformed

    private void btnProcesosEditadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesosEditadosActionPerformed
        if (aActual.procesos.abrirEditados(txtProcesosEditados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnProcesosEditadosActionPerformed

    private void btnProcesosEliminadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesosEliminadosActionPerformed
        if (aActual.procesos.abrirEliminados(txtProcesosEliminados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnProcesosEliminadosActionPerformed

    private void btnAccionesCreadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccionesCreadasActionPerformed
        if (aActual.acciones.abrirCreados(txtAccionesCreados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnAccionesCreadasActionPerformed

    private void btnAccionesEditadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccionesEditadasActionPerformed
        if (aActual.acciones.abrirEditados(txtAccionesEditados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnAccionesEditadasActionPerformed

    private void btnAccionesEliminadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccionesEliminadosActionPerformed
        if (aActual.acciones.abrirEliminados(txtAccionesEliminados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnAccionesEliminadosActionPerformed

    private void btnDatosCreadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatosCreadosActionPerformed
        if (aActual.datos.abrirCreados(txtDatosCreados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnDatosCreadosActionPerformed

    private void btnDatosEditadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatosEditadosActionPerformed
        if (aActual.datos.abrirEditados(txtDatosEditados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnDatosEditadosActionPerformed

    private void btnDatosEliminadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatosEliminadosActionPerformed
        if (aActual.datos.abrirEliminados(txtDatosEliminados)) {
            saveFile();
        }
    }//GEN-LAST:event_btnDatosEliminadosActionPerformed

    private void btnDocumentacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDocumentacionActionPerformed
        String str = "";
        str += aActual.getDocumentacion();
        copiarClipBoard(str);
    }//GEN-LAST:event_btnDocumentacionActionPerformed

    private void btnEditarMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarMensajeActionPerformed
        if (aActual.abrirEditarMensaje()) {
            saveFile();
        }
    }//GEN-LAST:event_btnEditarMensajeActionPerformed

    private void btnEditarDescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarDescripcionActionPerformed
        if (aActual.abrirEditarDescripcion()) {
            saveFile();
        }
    }//GEN-LAST:event_btnEditarDescripcionActionPerformed

    private void btnReiniciarQuincenaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarQuincenaActionPerformed
        if (preguntar("¿Estas seguro de reiniciar esta quincena?", "")) {
            quincena = 0;
            config.set("quincena", quincena);
            saveFile();
            txtTotalGanancia.setText("$" + quincena);
        }
    }//GEN-LAST:event_btnReiniciarQuincenaActionPerformed

    private void btnTrelloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTrelloActionPerformed
        openURL(pActual.link);
    }//GEN-LAST:event_btnTrelloActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        openURL(pActual.ticket);
    }//GEN-LAST:event_btnTicketActionPerformed

    public static String textAreaDialog(Object obj, String text, String title) {
        if (title == null) {
            title = "Your input";
        }
        JTextArea textArea = new JTextArea(text);
        textArea.setColumns(30);
        textArea.setRows(10);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(textArea.getPreferredSize().width, textArea.getPreferredSize().height);
        int ret = JOptionPane.showConfirmDialog((Component) obj, new JScrollPane(textArea), title, JOptionPane.OK_OPTION);
        if (ret == 0) {
            return textArea.getText();
        }
        return null;
    }

    public void openURL(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void msg(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    private boolean preguntar(String msg, String title) {
        return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public void guardarNuevoProyecto(NewProyectoForm form) {
        //cbProyectos.removeAllItems();

        Proyecto p = new Proyecto(form.getNumero(), form.getNombre());
        p.anio = form.getAnio();
        p.ambiente = form.getAmbiente();
        p.link = form.getLink();
        p.ticket = form.getTicket();

        proyectos.put(p.numero + "", p);
        cbProyectos.addItem(p.numero + "");
        pActual = p;

        config.set(p.numero + ".numero", p.numero);
        config.set(p.numero + ".anio", p.anio);
        config.set(p.numero + ".nombre", p.nombre);
        config.set(p.numero + ".ambiente", p.ambiente);
        config.set(p.numero + ".link", p.link);
        config.set(p.numero + ".ticket", p.ticket);
        saveFile();

        msg("Proyecto " + p.nombre + " guardado.");
    }

    public void cargarActividad(Actividad a) {
        aActual = a;
        txtActividades.setText("Actividad: " + a.id + ", " + a.getFechaFormat());
        pausado = true;
        lastTickTime = a.tiempo;
        tiempoPausado = lastTickTime;

        txtArchivosCreados.setText(aActual.archivos.getCreados() + "");
        txtArchivosEditados.setText(aActual.archivos.getEditados() + "");
        txtArchivosEliminados.setText(aActual.archivos.getEliminados() + "");

        txtQuerysCreados.setText(aActual.querys.getCreados() + "");
        txtQuerysEditados.setText(aActual.querys.getEditados() + "");
        txtQuerysEliminados.setText(aActual.querys.getEliminados() + "");

        txtProcesosCreados.setText(aActual.procesos.getCreados() + "");
        txtProcesosEditados.setText(aActual.procesos.getEditados() + "");
        txtProcesosEliminados.setText(aActual.procesos.getEliminados() + "");

        txtAccionesCreados.setText(aActual.acciones.getCreados() + "");
        txtAccionesEditados.setText(aActual.acciones.getEditados() + "");
        txtAccionesEliminados.setText(aActual.acciones.getEliminados() + "");

        txtDatosCreados.setText(aActual.datos.getCreados() + "");
        txtDatosEditados.setText(aActual.datos.getEditados() + "");
        txtDatosEliminados.setText(aActual.datos.getEliminados() + "");

        Duration duration = Duration.ofMillis(-tiempoPausado);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long millis = duration.toMillis();
        long seconds = millis / 1000;
        millis -= (seconds * 1000);
        txtTiempo.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds, millis));
        float total = ((float) hours + ((float) minutes / 60) + ((float) seconds / 60 / 60)) * TARIFA;
        txtGanancia.setText("$" + String.format("%.2f", total));
    }

    private boolean validarProyectoSeleccionado() {
        if (txtProyecto.getText().equals("Ningun proyecto seleccionado")) {
            msg("Debes cargar algun proyecto.");
            return false;
        }
        return true;
    }

    private boolean validarActividadSeleccionada() {
        if (txtActividades.getText().equals("Ninguna actividad seleccionada")) {
            msg("Debes cargar alguna actividad.");
            return false;
        }
        return true;
    }

    private void refreshReloj(long runningTime) {
        Duration duration = Duration.ofMillis(runningTime);
        long hours = duration.toHours();
        duration = duration.minusHours(hours);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long millis = duration.toMillis();
        long seconds = millis / 1000;
        millis -= (seconds * 1000);
        txtTiempo.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds, millis));
    }

    private void guardarActividad() {
        if (!validarActividadSeleccionada() || !validarProyectoSeleccionado()) {
            return;
        }

        long runningTime = tiempoPausado;

        aActual.tiempo = runningTime;
        aActual.save(config);

        config.set("ultimo.proyecto", pActual.numero);
        config.set("ultimo.actividad", aActual.id);

        saveFile();
    }

    private void copiarClipBoard(String mensaje) {
        StringSelection stringSelection = new StringSelection(mensaje);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        msg("Texto copiado en el portapapeles.");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Timer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Timer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Timer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Timer.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Timer().setVisible(true);

                } catch (Exception ex) {
                    Logger.getLogger(Timer.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton brnArchivosCreados;
    private javax.swing.JButton btnAccionesCreadas;
    private javax.swing.JButton btnAccionesEditadas;
    private javax.swing.JButton btnAccionesEliminados;
    private javax.swing.JButton btnArchivosEditados;
    private javax.swing.JButton btnArchivosEliminados;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnBorrarAct;
    private javax.swing.JButton btnCambiarTiempo;
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnCargarAct;
    private javax.swing.JButton btnComenzar;
    private javax.swing.JButton btnDatosCreados;
    private javax.swing.JButton btnDatosEditados;
    private javax.swing.JButton btnDatosEliminados;
    private javax.swing.JButton btnDocumentacion;
    private javax.swing.JButton btnEditarDescripcion;
    private javax.swing.JButton btnEditarMensaje;
    private javax.swing.JButton btnGuardarAct;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnNuevoAct;
    private javax.swing.JButton btnPausar;
    private javax.swing.JButton btnProcesosCreados;
    private javax.swing.JButton btnProcesosEditados;
    private javax.swing.JButton btnProcesosEliminados;
    private javax.swing.JButton btnQuerysCreados;
    private javax.swing.JButton btnQuerysEditados;
    private javax.swing.JButton btnQuerysEliminados;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JButton btnReiniciarQuincena;
    private javax.swing.JButton btnTerminarActividad;
    private javax.swing.JButton btnTicket;
    private javax.swing.JButton btnTrello;
    private javax.swing.JComboBox<String> cbActividades;
    private javax.swing.JComboBox<String> cbProyectos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel txtAccionesCreados;
    private javax.swing.JLabel txtAccionesEditados;
    private javax.swing.JLabel txtAccionesEliminados;
    private javax.swing.JLabel txtActividades;
    private javax.swing.JLabel txtArchivosCreados;
    private javax.swing.JLabel txtArchivosEditados;
    private javax.swing.JLabel txtArchivosEliminados;
    private javax.swing.JLabel txtDatosCreados;
    private javax.swing.JLabel txtDatosEditados;
    private javax.swing.JLabel txtDatosEliminados;
    private javax.swing.JLabel txtGanancia;
    private javax.swing.JLabel txtProcesosCreados;
    private javax.swing.JLabel txtProcesosEditados;
    private javax.swing.JLabel txtProcesosEliminados;
    private javax.swing.JLabel txtProyecto;
    private javax.swing.JLabel txtQuerysCreados;
    private javax.swing.JLabel txtQuerysEditados;
    private javax.swing.JLabel txtQuerysEliminados;
    private javax.swing.JLabel txtStatus;
    private javax.swing.JLabel txtTiempo;
    private javax.swing.JLabel txtTotalGanancia;
    // End of variables declaration//GEN-END:variables
}
