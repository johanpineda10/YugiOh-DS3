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

    private int cartaSeleccionada = 1; // empieza en 1
    private int numCartas = 0;

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
                    System.out.println("Seleccionado: " + seleccionado.getText());
                    System.out.println("Seleccionado: " + seleccionado2.getText());

                }else{
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
                System.out.println("Error: " + response.statusCode());
            }
        }catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
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
                atk = card.has("atk") ? card.getInt("atk") : 0; // algunos no tienen atk
                def = card.has("def") ? card.getInt("def") : 0; // algunos no tienen def


                JSONArray images = card.getJSONArray("card_images");
                String imageUrl = images.getJSONObject(0).getString("image_url");

                Image img = ImageIO.read(new URL(imageUrl));
                Image scaledImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);

                switch (slotCartas) {
                    case 1:
                        nomUsu1.setText("Nombre: " + nombre);
                        atkUsu1.setText("Ataque: " + atk);
                        defUsu1.setText("Defensa: " + def);
                        imgUsu1.setIcon(new ImageIcon(scaledImg));
                        checkBox1.setVisible(true);
                        break;
                    case 2:
                        nomUsu2.setText("Nombre: " + nombre);
                        atkUsu2.setText("Ataque: " + atk);
                        defUsu2.setText("Defensa: " + def);
                        imgUsu2.setIcon(new ImageIcon(scaledImg));
                        checkBox2.setVisible(true);
                        break;
                    case 3:
                        nomUsu3.setText("Nombre: " + nombre);
                        atkUsu3.setText("Ataque: " + atk);
                        defUsu3.setText("Defensa: " + def);
                        imgUsu3.setIcon(new ImageIcon(scaledImg));
                        checkBox3.setVisible(true);
                        break;
                }

            } else {
                System.out.println("Error: " + response.statusCode());
            }
        }catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
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
                        atk = card.has("atk") ? card.getInt("atk") : 0; // algunos no tienen atk
                        def = card.has("def") ? card.getInt("def") : 0; // algunos no tienen def


                        JSONArray images = card.getJSONArray("card_images");

                        String imageUrl = images.getJSONObject(0).getString("image_url");

                        Image img = ImageIO.read(new URL(imageUrl));
                        Image scaledImg = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);

                        numCartas++;
                        switch (numCartas) {
                            case 1:
                                nomMaq1.setText("Nombre: " + nombre);
                                atkMaq1.setText("Ataque: " + atk);
                                defMaq1.setText("Defensa: " + def);
                                imgMaq1.setIcon(new ImageIcon(scaledImg));
                                checkBox4.setVisible(true);
                                break;
                            case 2:
                                nomMaq2.setText("Nombre: " + nombre);
                                atkMaq2.setText("Ataque: " + atk);
                                defMaq2.setText("Defensa: " + def);
                                imgMaq2.setIcon(new ImageIcon(scaledImg));
                                checkBox5.setVisible(true);
                                break;
                            case 3:
                                nomMaq3.setText("Nombre: " + nombre);
                                atkMaq3.setText("Ataque: " + atk);
                                defMaq3.setText("Defensa: " + def);
                                imgMaq3.setIcon(new ImageIcon(scaledImg));
                                checkBox6.setVisible(true);
                                break;
                        }

                    } else {
                        System.out.println("Error: " + response.statusCode());
                    }
                }

                JOptionPane.showMessageDialog(mainPanel, "3 cartas Monster cargadas!");

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

    }

    public void fight(String Usuario, String Maquina){

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("YugiOh");
        frame.setContentPane(new YugiOhGui().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(800,800);

    }
}
