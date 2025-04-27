package vistas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import mensajeria.Contacto;
import mensajeria.Mensaje;
import mensajeria.Usuario;

public class Controlador implements ActionListener {
	private InterfazMensajeria vistaPrincipal;
	private Usuario usuario;
	private ServerSocket serverSocket;
	private Thread hiloReceptorMensajes;
	private Thread hiloReceptorListaNickname;
	private boolean escuchando = true;
	
	
	public Controlador(InterfazMensajeria vistaPrincipal, Usuario usuario) {
		this.usuario = usuario;
		this.vistaPrincipal = vistaPrincipal;
		// Iniciar el servidor en un hilo separado
		this.iniciarRecibeMensajes();
	}

    // Método para iniciar el hilo que escucha mensajes
	private void iniciarRecibeMensajes() {
	    escuchando = true;
	    hiloReceptorMensajes = new Thread(() -> {
	        try {
	            ServerSocket serverSocketMensajes = new ServerSocket(usuario.getPuerto());
	            while (escuchando) {
	                Socket socketRecibeMensaje = serverSocketMensajes.accept();
	                ObjectInputStream input = new ObjectInputStream(socketRecibeMensaje.getInputStream());
	                
	                Object recibido = input.readObject(); // leemos el objeto genérico
	                    Mensaje mensaje = (Mensaje) recibido;
	                    this.vistaPrincipal.recibirMensaje(mensaje, socketRecibeMensaje);
	            }
	        } catch (Exception e) {
	            if (escuchando) e.printStackTrace(); // solo si no fue cerrado intencionalmente
	        }
	    });
	    hiloReceptorMensajes.start();
	}
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equalsIgnoreCase(InterfazVista.ABRIRVENTAGREGARCONTACTO)) {
        	 try {
        		 //System.out.println("entro al try");
        		 Socket socket = new Socket("localhost", 10002);
        		 ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        		 @SuppressWarnings("unchecked")
				HashMap<String, Usuario> directorioUsuarios = (HashMap<String, Usuario>) in.readObject();
                 //System.out.println("Recibi el directorioUsuarios del servidor");
                 this.vistaPrincipal.abrirVentanaAgregarContacto(directorioUsuarios);
                 in.close();
                 socket.close();
             } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this.vistaPrincipal, "Error al pedir la lista de usuarios registrados en el servidos.", "Lista de usuarios registrados en el servidos.", JOptionPane.ERROR_MESSAGE);
             }
        }
        else if(e.getActionCommand().equalsIgnoreCase(InterfazVista.ENVIARMENSAJE)) {
            this.vistaPrincipal.formarMensaje();
        }
    }
    
    

    // Métodos para finalizar la conexión
  //  public void cerrarConexion() {
      //  try {
      //      escuchando = false;
        //    if (serverSocket != null && !serverSocket.isClosed()) {
       //         socket.close();
       //     }
       // } catch (Exception e) {
      //      e.printStackTrace();
       // }
   // }
}
