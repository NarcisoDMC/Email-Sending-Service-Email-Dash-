# 📧 Cliente de Correo Java (JavaMail)

Aplicación de escritorio desarrollada en **Java** con interfaz gráfica **Swing** que permite el envío de correos electrónicos utilizando el servidor SMTP de Gmail. Incluye autenticación de usuario, gestión de destinatarios múltiples (CC/CCO) y soporte para archivos adjuntos.

## Características principales

* **Autenticación de Usuario:**
    * Ventana de `Login` segura con validación de credenciales.
    * Sistema de seguridad que cierra la aplicación tras **3 intentos fallidos**.
* **Interfaz Moderna:** Diseño en "Modo Oscuro" (Dark Mode) utilizando componentes Swing personalizados.
* **Redacción de Correos:**
    * Soporte para múltiples destinatarios.
    * Opciones habilitables para **Copia de Carbón (CC)** y **Copia Oculta (CCO)**.
    * Campos para Asunto y Cuerpo del mensaje.
* **Gestión de Adjuntos:**
    * Permite adjuntar múltiples archivos simultáneamente.
    * Filtro de extensiones permitidas: `ZIP`, `PDF`, `RAR`, `TXT`, `JPG`, `PNG`, `JAR`.
* **Feedback Visual:** Ventanas emergentes para confirmar envíos y notificar errores o éxito.

## Tecnologías Utilizadas

* **Lenguaje:** [Java JDK 8+](https://www.oracle.com/java/technologies/downloads/)
* **Interfaz Gráfica:** Java Swing & AWT
* **Librería de Correo:** [JavaMail API](https://javaee.github.io/javamail/) (`javax.mail` y `activation`)
* **IDE:** Desarrollado compatible con NetBeans (utiliza archivos `.form`).

## Guía de Instalación y Uso

### 1. Requisitos Previos
Asegúrate de tener instaladas las librerías necesarias en tu `Classpath`:
* `javax.mail.jar`
* `activation.jar`

### 2. Configuración de Credenciales (Importante)
El proyecto viene configurado con credenciales de prueba. Para usarlo con tu propia cuenta:

1.  Abre el archivo `src/correo/EnviarCorreo.java`.
2.  Localiza las variables estáticas al inicio de la clase y actualízalas:

```java
// EnviarCorreo.java
private static String emailFrom = "TU_CORREO@gmail.com";
private static String passwordFrom = "TU_CONTRASEÑA_DE_APLICACION";
```

**Nota:** Para cuentas de Gmail, debes usar una Contraseña de Aplicación (generada desde Gestionar tu cuenta de Google > Seguridad), no tu contraseña de inicio de sesión habitual.

### 3. Ejecución
1. **Compila el proyecto.**
2. **Ejecuta la clase principal Login.java.**
3. **Ingresa las credenciales de acceso para desbloquear la ventana de envío.**

### 📂 Estructura del Proyecto

src/correo/
 Login.java          # Ventana de inicio de sesión y lógica de intentos
 EnviarCorreo.java   # Ventana principal para componer y enviar emails
 mAIL icon.png       # Ícono de la aplicación
 Login.form          # Diseño UI (NetBeans)
 EnviarCorreo.form   # Diseño UI (NetBeans)

### 👤 Autor
**David Alexis De La Torre Rios**

Desarrollado como proyecto académico de programación en Java.
