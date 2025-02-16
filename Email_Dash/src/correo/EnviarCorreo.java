/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package correo;

import java.awt.Color;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author David Alexis De La Torre Rios 22130523
 */
public class EnviarCorreo extends javax.swing.JFrame {

    //Se declaran variables String privadas que usaremos para enviar el correo
    private static String emailFrom = "direcciondepruebastop@gmail.com"; //Variable estatica emailFrom donde recibira la direccion de correo emisor
    private static String passwordFrom = "vldkvxascittjont"; //Variable estatica passwordFrom donde recibira la contraseña de la direccion de correo emisor
    private String emailTo; //Variable emailTo guarda el o los destinatarios que recibiran el correo
    private String ccEmails; //Variable ccEmails guarda el o los destinatarios que recibiran una copia de carbon tanto oculta como normal
    private String subject; //Variable subject guarda el asunto del correo
    private String content; //Variable content guarda el contenido del correo
    
    //Se declaran variables del tipo: properties, Session y MimeMessage provenientes de la libreria JavaMail
    private Properties mProperties; //Guarda las propiedades necesarias para enviar el correo
    private Session mSession; //Guarda la sesion iniciada para JavaMail
    private MimeMessage mCorreo; //Guarda las propiedades, la sesion y todo lo relacionado al correo para enviarlo
    
    //Se declaran variables del tipo: File[] y String
    private File[] mArchivosAdjuntos; //Arreglo file[] mArchivosAdjuntos que guarda los archivos adjuntos
    private String nombres_archivos; //String para guardar los nombres de los archivos adjuntos
    
    /**
     * Creates new form EnviarCorreo
     */
    public EnviarCorreo() {
        initComponents();
        //Se crea un nuevo objeto properties y se guarda en mProperties 
        mProperties = new Properties();
        
        //Los nombres de los archivos adjuntados se borran al iniciar el jFrame: EnviarCorreo
        nombres_archivos = "";
        
        /*Al iniciar el jFrame, No se muestran los componentes encargados de recibir datos e instrucciones
        de las direcciones de correo para las copias de carbono y copias de carbono ocultas*/
        jTextCC.setVisible(false);
        jLabelCC.setVisible(false);
        jRadioButtonCC.setVisible(false);
        jRadioButtonCCO.setVisible(false);
        
        //Se cambia el color de fondo del JFrame a negro con setBackground
        this.getContentPane().setBackground(Color.black);
    }
    
    //Metodo privado createEmail encargado de crear el correo
    private void createEmail() {
        //Se llenan las variables privadas con lo tecleado en los jTextField correspondientes
        emailTo = jTextTo.getText().trim();
        ccEmails = jTextCC.getText().trim();
        subject = jTextSubject.getText().trim();
        content = jTextContent.getText().trim();
        //Se crean dos arreglos encargados de separar los correos en caso de haber mas de un correo
        String emailsTo[] = emailTo.split(", "); //Arreglo que separa los correos del campo para:
        String emails[] = ccEmails.split(", "); //Arreglo que separa los correos para usar la funcion CC y CCO
        
        // Simple mail transfer protocol
        // Se trnasfieren a mProperties los protocoloes necesarios para enviar el correo
        //Usa el protocolo smtp de Google y el puerto seguro de Google 587
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user",emailFrom);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");
        
        //Se abre una session a partir de las propiedades guardadas en mProperties
        mSession = Session.getDefaultInstance(mProperties);
        
        //Bloque Try-Catch para obtener los guardar los elementos necesarios para enviar el corre
        try {
            //Creamos el cuerpo del correo
            MimeMultipart mElementosCorreo = new MimeMultipart();
            //Creamos el contenido del correo
            MimeBodyPart mContenido = new MimeBodyPart();
            //Formato del contenido del correo es html
            mContenido.setContent(content, "text/plain");
            mElementosCorreo.addBodyPart(mContenido);
            
            //Agregar archivos adjuntos.
            //Se crea una condicional que comprueba si el Array mArchivos esta vacio en caso de que no haya archivos adjuntos
            if(mArchivosAdjuntos == null){
                //Se envia el correo con solo el texto en caso de no tener archivo adjunto
                MimeBodyPart mAdjuntos = null;
                if(mAdjuntos != null){
                    for (int i = 0; i < mArchivosAdjuntos.length; i++) {
                        mAdjuntos = new MimeBodyPart();
                        mAdjuntos.setDataHandler(new DataHandler(new FileDataSource(mArchivosAdjuntos[i].getAbsolutePath())));
                        mAdjuntos.setFileName(mArchivosAdjuntos[i].getName());
                        mElementosCorreo.addBodyPart(mAdjuntos);
                    }
                }
            }else{
                /*Si no se cumple la condicion se crea un ciclo que obtiene los archivos a traves del arreglo 
                mArchivosAdjuntos y los agrega a los elementos del correo*/
                for (int i = 0; i < mArchivosAdjuntos.length; i++) {
                    MimeBodyPart Adjuntos = new MimeBodyPart();
                    Adjuntos.setDataHandler(new DataHandler(new FileDataSource(mArchivosAdjuntos[i].getAbsolutePath())));
                    Adjuntos.setFileName(mArchivosAdjuntos[i].getName());
                    mElementosCorreo.addBodyPart(Adjuntos);
                }
            }
            /*Crea una nueva sesion para enviar el correo obteniendo el correo emisor, las direcciones de correo destinatarias, el asunto, 
            y el contenido*/
            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(emailFrom));
            
            /*Se crean condiciones necesarias para obtener y diferenciar el tipo de destinatario, Si es destinatario comun, 
            destinatario que recibe copia de carbon o copia de carbon oculta a traves de los componentes: jCheckHabilitar, 
            jRadioButtonCC y jRadioButtonCCO*/
            if(jCheckHabilitar.isSelected() == false){
                InternetAddress Address[] = new InternetAddress[emailsTo.length];
                for(int i = 0; i < emailsTo.length; i++){
                    Address[i] = new InternetAddress(emailsTo[i]);
                    mCorreo.addRecipient(Message.RecipientType.TO, Address[i]);
                }
                mCorreo.setSubject(subject);
                mCorreo.setContent(mElementosCorreo);
            }else{
                if(jCheckHabilitar.isSelected()){
                    if(jRadioButtonCC.isSelected()){
                        InternetAddress ccAddress[] = new InternetAddress[emails.length];
                        for(int i = 0; i < emails.length; i++){
                            ccAddress[i] = new InternetAddress(emails[i]);
                            mCorreo.addRecipient(Message.RecipientType.CC, ccAddress[i]);
                        }
                        InternetAddress Address[] = new InternetAddress[emailsTo.length];
                        for(int i = 0; i < emailsTo.length; i++){
                            Address[i] = new InternetAddress(emailsTo[i]);
                            mCorreo.addRecipient(Message.RecipientType.TO, Address[i]);
                        }
                        mCorreo.setSubject(subject);
                        mCorreo.setContent(mElementosCorreo);
                    }else{
                        if(jRadioButtonCCO.isSelected()){
                            InternetAddress bccAddress[] = new InternetAddress[emails.length];
                            for(int i = 0; i < emails.length; i++){
                                bccAddress[i] = new InternetAddress(emails[i]);
                                mCorreo.addRecipient(Message.RecipientType.BCC, bccAddress[i]);
                            }
                            InternetAddress Address[] = new InternetAddress[emailsTo.length];
                            for(int i = 0; i < emailsTo.length; i++){
                                Address[i] = new InternetAddress(emailsTo[i]);
                                mCorreo.addRecipient(Message.RecipientType.TO, Address[i]);
                            }
                            mCorreo.setSubject(subject);
                            mCorreo.setContent(mElementosCorreo);
                        }
                    }
                }
            }
          
            //Caths necesarios para las excepciones del bloque
        } catch (AddressException ex) {
            Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);  
            JOptionPane.showMessageDialog(null, "La direccion o las direcciones de correo son invalidas", "Comprobar direccion de correo", JOptionPane.ERROR_MESSAGE);
        } catch (MessagingException ex) {
            Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Metodo privado sendEmail encargado de enviar el correo a los destinatarios
    private void sendEmail() {
        //Bloque Try-Catch que usa el objeto transport
        try {
            //Se crea un objeto transport llamado mTrasnport donde se le asigna el protocolo "smtp"
            Transport mTransport = mSession.getTransport("smtp");
            //El objeto mTransport se conecta a la cuenta a traves de la direccion de correo y la contraseña del emisor
            mTransport.connect(emailFrom, passwordFrom);
            //El objeto mTransport envia el mensaje a cualquier tipo de destinatario
            mTransport.sendMessage(mCorreo, mCorreo.getAllRecipients());
            //Se cierra el transporte del correo
            mTransport.close();
            
            //Si se envia el correo mostrara un anuncio que indica que el correo ha sido enviado
            JOptionPane.showMessageDialog(null, "Correo enviado");
            
         //Respectivos Caths para las excepciones del bloque   
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Hubo un problema al enviar el correo verifique los datos en los campos", "Verificar correo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonCCyCCO = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextTo = new javax.swing.JTextField();
        jTextSubject = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextContent = new javax.swing.JTextArea();
        jLabAdjuntos = new javax.swing.JLabel();
        jButEnviar = new javax.swing.JButton();
        jButAdjuntar = new javax.swing.JButton();
        jLabLogin = new javax.swing.JLabel();
        jRadioButtonCC = new javax.swing.JRadioButton();
        jRadioButtonCCO = new javax.swing.JRadioButton();
        jButLimpiar = new javax.swing.JButton();
        jTextCC = new javax.swing.JTextField();
        jLabelCC = new javax.swing.JLabel();
        jCheckHabilitar = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Para:");
        jLabel1.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Asunto:");
        jLabel2.setOpaque(true);

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Contenido:");
        jLabel3.setOpaque(true);

        jTextTo.setBackground(new java.awt.Color(102, 102, 102));
        jTextTo.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jTextTo.setForeground(new java.awt.Color(255, 255, 255));
        jTextTo.setOpaque(true);
        jTextTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextToActionPerformed(evt);
            }
        });

        jTextSubject.setBackground(new java.awt.Color(102, 102, 102));
        jTextSubject.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jTextSubject.setForeground(new java.awt.Color(255, 255, 255));
        jTextSubject.setText("Este es un correo de prueba");
        jTextSubject.setOpaque(true);

        jTextContent.setBackground(new java.awt.Color(102, 102, 102));
        jTextContent.setColumns(20);
        jTextContent.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jTextContent.setForeground(new java.awt.Color(255, 255, 255));
        jTextContent.setRows(5);
        jTextContent.setText("Si recibiste este correo el envio fue exitoso\n");
        jScrollPane1.setViewportView(jTextContent);

        jLabAdjuntos.setBackground(new java.awt.Color(51, 51, 51));
        jLabAdjuntos.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabAdjuntos.setForeground(new java.awt.Color(255, 255, 255));
        jLabAdjuntos.setOpaque(true);

        jButEnviar.setBackground(new java.awt.Color(102, 102, 102));
        jButEnviar.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jButEnviar.setForeground(new java.awt.Color(255, 255, 255));
        jButEnviar.setText("Enviar");
        jButEnviar.setOpaque(true);
        jButEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButEnviarActionPerformed(evt);
            }
        });

        jButAdjuntar.setBackground(new java.awt.Color(102, 102, 102));
        jButAdjuntar.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jButAdjuntar.setForeground(new java.awt.Color(255, 255, 255));
        jButAdjuntar.setText("Adjuntar");
        jButAdjuntar.setOpaque(true);
        jButAdjuntar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButAdjuntarActionPerformed(evt);
            }
        });

        jLabLogin.setBackground(new java.awt.Color(102, 102, 102));
        jLabLogin.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabLogin.setForeground(new java.awt.Color(255, 255, 255));
        jLabLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabLogin.setText("EmailDash");
        jLabLogin.setOpaque(true);

        jRadioButtonCC.setBackground(new java.awt.Color(102, 102, 102));
        buttonCCyCCO.add(jRadioButtonCC);
        jRadioButtonCC.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jRadioButtonCC.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonCC.setText("CC");
        jRadioButtonCC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButtonCC.setOpaque(true);
        jRadioButtonCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCCActionPerformed(evt);
            }
        });

        jRadioButtonCCO.setBackground(new java.awt.Color(102, 102, 102));
        buttonCCyCCO.add(jRadioButtonCCO);
        jRadioButtonCCO.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jRadioButtonCCO.setForeground(new java.awt.Color(255, 255, 255));
        jRadioButtonCCO.setText("CCO");
        jRadioButtonCCO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jRadioButtonCCO.setOpaque(true);
        jRadioButtonCCO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCCOActionPerformed(evt);
            }
        });

        jButLimpiar.setBackground(new java.awt.Color(102, 102, 102));
        jButLimpiar.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jButLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        jButLimpiar.setText("Limpiar");
        jButLimpiar.setOpaque(true);
        jButLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButLimpiarActionPerformed(evt);
            }
        });

        jTextCC.setBackground(new java.awt.Color(102, 102, 102));
        jTextCC.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jTextCC.setForeground(new java.awt.Color(255, 255, 255));
        jTextCC.setOpaque(true);
        jTextCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextCCActionPerformed(evt);
            }
        });

        jLabelCC.setBackground(new java.awt.Color(0, 0, 0));
        jLabelCC.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabelCC.setForeground(new java.awt.Color(255, 255, 255));
        jLabelCC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelCC.setText("CC:");
        jLabelCC.setOpaque(true);

        jCheckHabilitar.setBackground(new java.awt.Color(102, 102, 102));
        jCheckHabilitar.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jCheckHabilitar.setForeground(new java.awt.Color(255, 255, 255));
        jCheckHabilitar.setText("Habilitar CC y CCO");
        jCheckHabilitar.setOpaque(true);
        jCheckHabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckHabilitarActionPerformed(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Si desea enviar correos a varios destinatarios separe las direcciones con \", \" tanto en  el campo \"Para\" como en el campo \"CCyCCO\"");

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Enviar Correo");
        jLabel5.setOpaque(true);

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Versión 1.0.0");
        jLabel6.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabLogin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabelCC))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                                .addComponent(jTextSubject, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabAdjuntos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButAdjuntar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextCC)
                            .addComponent(jTextTo, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jRadioButtonCC, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jRadioButtonCCO, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jCheckHabilitar))
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(16, 16, 16))))
            .addGroup(layout.createSequentialGroup()
                .addGap(383, 383, 383)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckHabilitar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButtonCC, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButtonCCO, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextCC, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelCC))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(35, 35, 35))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jTextSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabAdjuntos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButAdjuntar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButEnviarActionPerformed
        // Boton Enviar que muestra un mensaje de opcion dando la posibilidad de elegir al usario de estar seguro o no de enviar el correo
        int aceptar = JOptionPane.showConfirmDialog(null, "¿Estas seguro de enviar el correo?", "Confirmacion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        //Se crea una condicional para comprobar la opcion elegida en el JOptionPane OK_CANCEL_OPTION
        if (aceptar == JOptionPane.OK_OPTION){
            //Si se selecciona aceptar se enviara el correo
            createEmail();
            sendEmail();
            //En caso de sleccionar cancelar se cerrara el anuncio y se reanudara con la edicion del envio de correo
        }else if(aceptar == JOptionPane.CANCEL_OPTION){
            
        }
    }//GEN-LAST:event_jButEnviarActionPerformed

    private void jButAdjuntarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButAdjuntarActionPerformed
        // Boton Ajuntar encargado de adjuntar los archivos que se enviaran al correo
        //Se crea un objeto choose de la libreria jFileChooser usada para adjuntar archivos
        JFileChooser chooser = new JFileChooser();
        
        //Se crea un filtro para el FileChooser que filtra las extensiones de los archivos como archivos permitidos
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos ZIP, PDF, RAR, TXT, JPG, PNG y JAR", "zip", "pdf", "rar", "txt", "jpg", "png", "jar"); 
        //Se le asigna al objeto chooser el filtro
        chooser.setFileFilter(filtro);
        
        //Se le permite al objeto chooser agregar mas de un solo archivo
        chooser.setMultiSelectionEnabled(true);
        //Se le agrega un modo de seleccion al objeto chooser para que solo adjunte archivos
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        //Condicional que guarda los archivos al arreglo del tipo file[] mArchivosAdjuntos
        if (chooser.showOpenDialog(this) != JFileChooser.CANCEL_OPTION) {
            mArchivosAdjuntos = chooser.getSelectedFiles();

            //Ciclo for para guardar en la variable nombre_archivos los nombres de los archivos
            for (File archivo : mArchivosAdjuntos) {
                nombres_archivos += archivo.getName();
            }
            
            //Despues del ciclo for se Muestran en un jLabel los nombres de los archivos
            jLabAdjuntos.setText("\n" + nombres_archivos + "\n");
        }//Se cierra la condicional
    }//GEN-LAST:event_jButAdjuntarActionPerformed
    
    private void jButLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButLimpiarActionPerformed
        // Boton Limpiar para borrar los jTextField, el jLabel de los archivos adjuntos y baciar el arreglo mArchivosAdjuntos
        jTextTo.setText("");
        jTextCC.setText("");
        jTextSubject.setText("");
        jTextContent.setText("");
        jLabAdjuntos.setText("");
        mArchivosAdjuntos = null;
        nombres_archivos = "";
    }//GEN-LAST:event_jButLimpiarActionPerformed

    private void jRadioButtonCCOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCCOActionPerformed
        // Boton radio para la copia de carbono oculta
        //con condicional para cambiar el texto del label que muestra el tipo de destinatario (CCO)
        if(jRadioButtonCCO.isSelected()){
            jLabelCC.setText("CCO:");
        }
    }//GEN-LAST:event_jRadioButtonCCOActionPerformed

    private void jRadioButtonCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCCActionPerformed
        // Boton radio para la copia de carbono normal
        //con condicional para cambiar el texto del label que muestra el tipo de destinatario (CC)
        if(jRadioButtonCC.isSelected()){
            jLabelCC.setText("CC:");
        }
    }//GEN-LAST:event_jRadioButtonCCActionPerformed

    private void jCheckHabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckHabilitarActionPerformed
        // Casilla CheckBox para habilitar si el correo tendra destinatarios con copia de carbono tanto oculta como normal
        /* Tambien se mostraran los componentes encargados de recibir datos e instrucciones
        de las direcciones de correo para las copias de carbono y copias de carbono ocultas*/
        if(jCheckHabilitar.isSelected()){
            jLabelCC.setVisible(true);
            jTextCC.setVisible(true);
            jRadioButtonCC.setVisible(true);
            jRadioButtonCCO.setVisible(true);
        }else{
            if(jCheckHabilitar.isSelected() == false){
                jLabelCC.setVisible(false);
                jTextCC.setVisible(false);
                jRadioButtonCC.setVisible(false);
                jRadioButtonCCO.setVisible(false);
                buttonCCyCCO.clearSelection();
            }
        }
    }//GEN-LAST:event_jCheckHabilitarActionPerformed

    private void jTextCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextCCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextCCActionPerformed

    private void jTextToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextToActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextToActionPerformed

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
            java.util.logging.Logger.getLogger(EnviarCorreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnviarCorreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnviarCorreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnviarCorreo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EnviarCorreo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonCCyCCO;
    private javax.swing.JButton jButAdjuntar;
    private javax.swing.JButton jButEnviar;
    private javax.swing.JButton jButLimpiar;
    private javax.swing.JCheckBox jCheckHabilitar;
    private javax.swing.JLabel jLabAdjuntos;
    private javax.swing.JLabel jLabLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelCC;
    private javax.swing.JRadioButton jRadioButtonCC;
    private javax.swing.JRadioButton jRadioButtonCCO;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextCC;
    private javax.swing.JTextArea jTextContent;
    private javax.swing.JTextField jTextSubject;
    private javax.swing.JTextField jTextTo;
    // End of variables declaration//GEN-END:variables
}
