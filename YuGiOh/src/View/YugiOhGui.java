package View;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class YugiOhGui {
    private JPanel mainPanel;
    private JLabel imgUsu1;
    private JLabel imgUsu2;
    private JLabel imgUsu3;
    private JLabel imgMaq1;
    private JLabel imgMaq2;
    private JLabel imgMaq3;
    private JComboBox nameCarta;
    private JButton seleccionarButton;
    private JLabel nomUsu1;
    private JLabel atkUsu1;
    private JLabel defUsu1;
    private JLabel nomUsu2;
    private JLabel atkUsu2;
    private JLabel defUsu2;
    private JLabel nomUsu3;
    private JLabel atkUsu3;
    private JLabel defUsu3;
    private JLabel nomMaq1;
    private JLabel nomMaq3;
    private JLabel nomMaq2;
    private JLabel atkMaq1;
    private JLabel atkMaq2;
    private JLabel atkMaq3;
    private JLabel defMaq1;
    private JLabel defMaq2;
    private JLabel defMaq3;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;
    private JCheckBox checkBox6;
    private JButton PELEARButton;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JProgressBar progressBar3;
    private JProgressBar progressBar4;
    private JProgressBar progressBar5;
    private JProgressBar progressBar6;
    private JButton LOGButton;
    private JLabel punUsu;
    private JLabel punMaq;

    private int cartaSeleccionada = 1; // empieza en 1
    private int numCartas = 0;
    private JLabel etiquetaDefUsuarioActual;
    private JLabel etiquetaDefMaquinaActual;
    private int puntajeUsuario = 0;
    private int puntajeMaquina = 0;

    public YugiOhGui() {


        obtenerCartas();
        checkBox1.setVisible(false);
        checkBox2.setVisible(false);
        checkBox3.setVisible(false);
        checkBox4.setVisible(false);
        checkBox5.setVisible(false);
        checkBox6.setVisible(false);
        ButtonGroup grupo1 = new ButtonGroup();
        grupo1.add(checkBox1);
        grupo1.add(checkBox2);
        grupo1.add(checkBox3);

        ButtonGroup grupo2 = new ButtonGroup();
        grupo2.add(checkBox4);
        grupo2.add(checkBox5);
        grupo2.add(checkBox6);


        seleccionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) nameCarta.getSelectedItem();

                if(seleccion != null){
                    rellenar(seleccion, cartaSeleccionada);

                    if(cartaSeleccionada < 3){
                        cartaSeleccionada++;
                    }else{
                        JOptionPane.showMessageDialog(mainPanel, "Ya seleccionaste las 3 cartas");
                        rellenarMaquina();
                    }
                }
            }
        });
        PELEARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox seleccionado = null;
                JCheckBox seleccionado2 = null;

                if (checkBox1.isSelected()) seleccionado = checkBox1;
                if (checkBox2.isSelected()) seleccionado = checkBox2;
                if (checkBox3.isSelected()) seleccionado = checkBox3;

                if (checkBox4.isSelected()) seleccionado2 = checkBox4;
                if (checkBox5.isSelected()) seleccionado2 = checkBox5;
                if (checkBox6.isSelected()) seleccionado2 = checkBox6;

                if (seleccionado != null && seleccionado2 != null) {
                    int atkUsuario = 0, defUsuario = 0;
                    int atkMaquina = 0, defMaquina = 0;

                    // Jugador
                    if (seleccionado == checkBox1) {
                        atkUsuario = Integer.parseInt(atkUsu1.getText());
                        defUsuario = Integer.parseInt(defUsu1.getText());
                        etiquetaDefUsuarioActual = defUsu1;
                    } else if (seleccionado == checkBox2) {
                        atkUsuario = Integer.parseInt(atkUsu2.getText());
                        defUsuario = Integer.parseInt(defUsu2.getText());
                        etiquetaDefUsuarioActual = defUsu2;
                    } else if (seleccionado == checkBox3) {
                        atkUsuario = Integer.parseInt(atkUsu3.getText());
                        defUsuario = Integer.parseInt(defUsu3.getText());
                        etiquetaDefUsuarioActual = defUsu3;
                    }

                    // Máquina
                    if (seleccionado2 == checkBox4) {
                        atkMaquina = Integer.parseInt(atkMaq1.getText());
                        defMaquina = Integer.parseInt(defMaq1.getText());
                        etiquetaDefMaquinaActual = defMaq1;
                    } else if (seleccionado2 == checkBox5) {
                        atkMaquina = Integer.parseInt(atkMaq2.getText());
                        defMaquina = Integer.parseInt(defMaq2.getText());
                        etiquetaDefMaquinaActual = defMaq2;
                    } else if (seleccionado2 == checkBox6) {
                        atkMaquina = Integer.parseInt(atkMaq3.getText());
                        defMaquina = Integer.parseInt(defMaq3.getText());
                        etiquetaDefMaquinaActual = defMaq3;
                    }

                    // Llamar al método fight()
                    fight(atkUsuario, defUsuario, atkMaquina, defMaquina, seleccionado, seleccionado2);

                } else {
                    JOptionPane.showMessageDialog(mainPanel,"Aun te faltan cartas por elegir");
                }
            }
        });
    }

    public void obtenerCartas(){

        try{
            HttpClient httpClient = HttpClient.newHttpClient();
        // Se crea la solicitud
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://db.ygoprodeck.com/api/v7/cardinfo.php?&num=30&offset=20"))
                    .build();
        // Ejecutamos la solicitud
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray data = json.getJSONArray("data");

                // 🔹 Recorremos cada carta y agregamos su nombre al combo
                for (int i = 0; i < data.length(); i++) {
                    JSONObject card = data.getJSONObject(i);
                    if(card.getString("type").contains("Monster")) {
                        nameCarta.addItem(card.getString("name"));
                    }
                }

            } else {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "No se conecto a la API.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }catch (IOException | InterruptedException e)
        {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Hubo un problema al conectar con el servidor o la respuesta tardó demasiado.",
                    "Error de conexión",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void rellenar(String nombreYugi, int slotCartas){
        try{
            HttpClient httpClient = HttpClient.newHttpClient();
            // Se crea la solicitud
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://db.ygoprodeck.com/api/v7/cardinfo.php?name="
                            + URLEncoder.encode(nombreYugi, StandardCharsets.UTF_8))) //Se utiliza para borrar espacios en el nombre
                    .build();
            // Ejecutamos la solicitud
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                int id = 0;
                int atk = 0;
                int def = 0;
                String nombre = "";

                JSONObject json = new JSONObject(response.body());
                JSONArray data = json.getJSONArray("data");

                JSONObject card = data.getJSONObject(0);
                id = card.getInt("id");
                nombre = card.getString("name");
                atk = card.has("atk") && !card.isNull("atk") ? card.getInt("atk") : 0; // algunos no tienen atk
                def = card.has("def") && !card.isNull("def") ? card.getInt("def") : 0; // algunos no tienen def


                JSONArray images = card.getJSONArray("card_images");
                String imageUrl = images.getJSONObject(0).getString("image_url");

                Image img = ImageIO.read(new URL(imageUrl));
                Image scaledImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);

                switch (slotCartas) {
                    case 1:
                        nomUsu1.setText(nombre);
                        atkUsu1.setText(String.valueOf(atk));
                        defUsu1.setText(String.valueOf(def));
                        imgUsu1.setIcon(new ImageIcon(scaledImg));
                        checkBox1.setVisible(true);
                        break;
                    case 2:
                        nomUsu2.setText(nombre);
                        atkUsu2.setText(String.valueOf(atk));
                        defUsu2.setText(String.valueOf(def));
                        imgUsu2.setIcon(new ImageIcon(scaledImg));
                        checkBox2.setVisible(true);
                        break;
                    case 3:
                        nomUsu3.setText(nombre);
                        atkUsu3.setText(String.valueOf(atk));
                        defUsu3.setText(String.valueOf(def));
                        imgUsu3.setIcon(new ImageIcon(scaledImg));
                        checkBox3.setVisible(true);
                        break;
                }

            } else {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "No ingreso a la API.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }catch (IOException | InterruptedException e)
        {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Hubo un problema al conectar con el servidor o la respuesta tardó demasiado.",
                    "Error de conexión",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void rellenarMaquina(){

            try {
                while(numCartas < 3) {
                    HttpClient httpClient = HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.ALWAYS)
                            .build();
                    // Se crea la solicitud
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://db.ygoprodeck.com/api/v7/randomcard.php"))
                            .build();
                    // Ejecutamos la solicitud
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        int id = 0;
                        int atk = 0;
                        int def = 0;
                        String type = "";
                        String nombre = "";

                        JSONObject json = new JSONObject(response.body());
                        JSONArray data = json.getJSONArray("data");
                        JSONObject card = data.getJSONObject(0);

                        type = card.getString("type");
                        if (!type.contains("Monster")) {
                            // si no es Monster, repetimos sin aumentar numCartas
                            continue;
                        }

                        id = card.getInt("id");
                        nombre = card.getString("name");
                        atk = card.has("atk") && !card.isNull("atk") ? card.getInt("atk") : 0; // algunos no tienen atk
                        def = card.has("def") && !card.isNull("def") ? card.getInt("def") : 0; // algunos no tienen def


                        JSONArray images = card.getJSONArray("card_images");

                        String imageUrl = images.getJSONObject(0).getString("image_url");

                        Image img = ImageIO.read(new URL(imageUrl));
                        Image scaledImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);

                        numCartas++;
                        switch (numCartas) {
                            case 1:
                                nomMaq1.setText(nombre);
                                atkMaq1.setText(String.valueOf(atk));
                                defMaq1.setText(String.valueOf(def));
                                imgMaq1.setIcon(new ImageIcon(scaledImg));
                                checkBox4.setVisible(true);
                                break;
                            case 2:
                                nomMaq2.setText(nombre);
                                atkMaq2.setText(String.valueOf(atk));
                                defMaq2.setText(String.valueOf(def));
                                imgMaq2.setIcon(new ImageIcon(scaledImg));
                                checkBox5.setVisible(true);
                                break;
                            case 3:
                                nomMaq3.setText(nombre);
                                atkMaq3.setText(String.valueOf(atk));
                                defMaq3.setText(String.valueOf(def));
                                imgMaq3.setIcon(new ImageIcon(scaledImg));
                                checkBox6.setVisible(true);
                                break;
                        }

                    } else {
                        JOptionPane.showMessageDialog(
                                mainPanel,
                                "No ingreso a la API.",
                                "Error de conexión",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }

                JOptionPane.showMessageDialog(mainPanel, "3 cartas Monster cargadas!");

            } catch (IOException | InterruptedException e) {
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "Hubo un problema al conectar con el servidor o la respuesta tardó demasiado.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE
                );
            }

    }

    public void fight(int atkUsu, int defUsu, int atkMaq, int defMaq, JCheckBox cartaUsuario, JCheckBox cartaMaquina) {

        int nuevaDefUsu = defUsu;
        int nuevaDefMaq = defMaq;

        boolean estadoAtkUsu = atkUsu > defUsu;
        boolean estadoAtkMaq = atkMaq > defMaq;

        System.out.println("usuario en ataque?: " + estadoAtkUsu);
        System.out.println("maquina en ataque?: " + estadoAtkMaq);

        // Lógica del enfrentamiento
        if (estadoAtkUsu && estadoAtkMaq) {
            if (atkUsu > atkMaq) {
                JOptionPane.showMessageDialog(mainPanel, "Gana Usuario");
                puntajeUsuario++;
            } else if (atkUsu < atkMaq) {
                JOptionPane.showMessageDialog(mainPanel, "Gana Máquina");
                puntajeMaquina++;
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Empate");
            }
        } else if (!estadoAtkUsu && estadoAtkMaq) {
            nuevaDefUsu = defUsu - (atkMaq - defUsu);
            if (nuevaDefUsu < 0) nuevaDefUsu = 0;
            JOptionPane.showMessageDialog(mainPanel, "Gana Máquina (la defensa del usuario bajó a " + nuevaDefUsu + ")");
            puntajeMaquina++;
        } else if (estadoAtkUsu && !estadoAtkMaq) {
            nuevaDefMaq = defMaq - (atkUsu - defMaq);
            if (nuevaDefMaq < 0) nuevaDefMaq = 0;
            JOptionPane.showMessageDialog(mainPanel, "Gana Usuario (la defensa de la máquina bajó a " + nuevaDefMaq + ")");
            puntajeUsuario++;
        }

        // Actualizar defensas visualmente
        etiquetaDefUsuarioActual.setText("" + nuevaDefUsu);
        etiquetaDefMaquinaActual.setText("" + nuevaDefMaq);
        punUsu.setText("Puntaje: " + puntajeUsuario);
        punMaq.setText("Puntaje: " + puntajeMaquina);
        // Desactivar cartas usadas
        cartaUsuario.setEnabled(false);
        cartaMaquina.setEnabled(false);
        cartaUsuario.setSelected(false);
        cartaMaquina.setSelected(false);

        // 🔹 Mostrar marcador actual
        System.out.println("Marcador -> Usuario: " + puntajeUsuario + " | Máquina: " + puntajeMaquina);

        // 🔹 Verificar si alguien ganó 2 combates
        if (puntajeUsuario == 2) {
            JOptionPane.showMessageDialog(mainPanel, "🎉 ¡El jugador gana la partida!");

            reiniciarJuego();
        } else if (puntajeMaquina == 2) {
            JOptionPane.showMessageDialog(mainPanel, "🤖 ¡La máquina gana la partida!");

            reiniciarJuego();
        }
    }

    private void reiniciarJuego() {
        puntajeUsuario = 0;
        puntajeMaquina = 0;
        cartaSeleccionada = 1;
        numCartas = 0;

        // Aquí podrías limpiar los labels e imágenes
        // o simplemente reiniciar el programa si quieres
        JOptionPane.showMessageDialog(mainPanel, "Partida reiniciada. ¡Selecciona nuevas cartas!");
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("YugiOh");
        frame.setContentPane(new YugiOhGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1500,1500);

    }
}
