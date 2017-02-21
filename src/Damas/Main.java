package Damas;

import static Damas.Main.HEIGHT;
import static Damas.Main.TAMANY;
import static Damas.Main.WIDTH;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.input.KeyCode;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Main extends Application {
    
    private static final String HOST = "172.20.11.237"; //localhost
    private static final int PORT = 1099;
    private static Registry registre;

    
    int movimientoBlanco = 0;
    int movimientoNegro = 2;
    public static final int TAMANY = 80;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public Tablero[][] Tabla = new Tablero[WIDTH][HEIGHT];

    public Group TableroGroup = new Group();
    public Group PiezaGroup = new Group();
    public static Font font;
    public CajaMenu menu;

    public Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TAMANY, HEIGHT * TAMANY);
        root.getChildren().addAll(TableroGroup, PiezaGroup);

        ItemsMenu itemContador = new ItemsMenu("CONTADOR");
        itemContador.setOnMouseClicked(event3 -> {
            Label secondLabel = new Label("Not yet implemented");
            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(secondLabel);
            Scene secondScene = new Scene(secondaryLayout, 400, 110);
            Stage secondStage = new Stage();
            secondStage.setTitle("CONTADOR");
            secondStage.setScene(secondScene);
            secondStage.setMaxWidth(230);
            secondStage.setMaxHeight(110);
            secondStage.show();

        });

        ItemsMenu itemNewGame = new ItemsMenu("MODO CAMPAÑA");
        itemNewGame.setOnMouseClicked(event -> {

            if (menu.isVisible()) {
                menu.hide();
            } else {
                menu.show();
            }

        });

        ItemsMenu itemOptions = new ItemsMenu("MULTIJUGADOR");
        itemOptions.setOnMouseClicked(event2 -> {

        });

        ItemsMenu itemCredits = new ItemsMenu("CREDITS");
        itemCredits.setOnMouseClicked(event1 -> {
            Label secondLabel = new Label("Hecho por Marc llobera, \n Victor Marchante y \n Sheng Ye ");
            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(secondLabel);
            Scene secondScene = new Scene(secondaryLayout, 210, 110);
            Stage secondStage = new Stage();
            secondStage.setTitle("Creditos");
            secondStage.setScene(secondScene);
            secondStage.setMaxWidth(210);
            secondStage.setMaxHeight(110);
            secondStage.setMinWidth(210);
            secondStage.setMinHeight(110);
            secondStage.show();

        });

        ItemsMenu itemQuit = new ItemsMenu("SALIR");
        itemQuit.setOnMouseClicked(event -> System.exit(0));

        menu = new CajaMenu("LAS DAMAS",
                itemNewGame,
                itemContador,
                itemOptions,
                itemCredits,
                new ItemsMenu(""),
                itemQuit);

        root.getChildren().add(menu);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tablero Tablero = new Tablero((x + y) % 2 == 0, x, y);
                Tabla[x][y] = Tablero;

                TableroGroup.getChildren().add(Tablero);
                Pieza Pieza = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    Pieza = makePieza(TiposPieza.NEGRO, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    Pieza = makePieza(TiposPieza.BLANCO, x, y);
                }

                if (Pieza != null) {
                    Tablero.setPieza(Pieza);
                    PiezaGroup.getChildren().add(Pieza);
                }
            }
        }

        return root;
    }

    public ResultadoMovimiento tryMove(Pieza Pieza, int newX, int newY) {

        MovimientoA blanco = new MovimientoA(movimientoBlanco);
        MovimientoA negro = new MovimientoA(movimientoNegro);
        int x0 = toTabla(Pieza.getOldX());
        int y0 = toTabla(Pieza.getOldY());

        if (Tabla[newX][newY].hasPieza() || (newX + newY) % 2 == 0) {
            return new ResultadoMovimiento(TiposMovimiento.NONE);
        }

        if (Math.abs(newX - x0) == 1 && newY - y0 == Pieza.getType().moveDir) {
            if (blanco.compareTo(negro) < movimientoNegro) {
                ClaseGenerica<String> moveBlanco = new ClaseGenerica<String>("moveBlanco");
                moveBlanco.moveBlanco();
                movimientoBlanco = movimientoBlanco + 1;
                movimientoNegro = movimientoNegro - 1;

            } else if (negro.compareTo(blanco) > movimientoBlanco) {
                ClaseGenerica<String> moveNegro = new ClaseGenerica<String>("moveNegro");
                moveNegro.moveNegro();
                movimientoNegro = movimientoNegro + 1;
                movimientoBlanco = movimientoBlanco - 1;

            }
            return new ResultadoMovimiento(TiposMovimiento.NORMAL);

        } else if (Math.abs(newX - x0) == 2 && newY - y0 == Pieza.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (Tabla[x1][y1].hasPieza() && Tabla[x1][y1].getPieza().getType() != Pieza.getType()) {

                return new ResultadoMovimiento(TiposMovimiento.KILL, Tabla[x1][y1].getPieza());
            }
            if (blanco.compareTo(negro) < movimientoNegro) {
                ClaseGenerica<String> moveBlanco = new ClaseGenerica<String>("moveBlanco");
                moveBlanco.moveBlanco();
                movimientoBlanco = movimientoBlanco + 1;
                movimientoNegro = movimientoNegro - 1;

            } else if (negro.compareTo(blanco) > movimientoBlanco) {
                ClaseGenerica<String> moveNegro = new ClaseGenerica<String>("moveNegro");
                moveNegro.moveNegro();
                movimientoNegro = movimientoNegro + 1;
                movimientoBlanco = movimientoBlanco - 1;

            }
        }

        return new ResultadoMovimiento(TiposMovimiento.NONE);
    }

    public int toTabla(double pixel) {
        return (int) (pixel + TAMANY / 2) / TAMANY;
    }

    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (menu.isVisible()) {
                    menu.hide();
                } else {
                    menu.show();
                }
            }
        });
        primaryStage.setTitle("Damas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pieza makePieza(TiposPieza type, int x, int y) {
        Pieza Pieza = new Pieza(type, x, y);

        Pieza.setOnMouseReleased(e -> {
            int newX = toTabla(Pieza.getLayoutX());
            int newY = toTabla(Pieza.getLayoutY());

            ResultadoMovimiento result = tryMove(Pieza, newX, newY);

            int x0 = toTabla(Pieza.getOldX());
            int y0 = toTabla(Pieza.getOldY());

            switch (result.getType()) {
                case NONE:
                    Pieza.abortMove();
                    ClaseGenerica<String> nulo = new ClaseGenerica<String>("nulo");
                    nulo.nulo();
                    break;
                case NORMAL:
                    Pieza.move(newX, newY);
                    Tabla[x0][y0].setPieza(null);
                    Tabla[newX][newY].setPieza(Pieza);
                    break;
                case KILL:
                    Pieza.move(newX, newY);
                    Tabla[x0][y0].setPieza(null);
                    Tabla[newX][newY].setPieza(Pieza);
                    Pieza otherPieza = result.getPieza();
                    Tabla[toTabla(otherPieza.getOldX())][toTabla(otherPieza.getOldY())].setPieza(null);
                    PiezaGroup.getChildren().remove(otherPieza);

                    ClaseGenerica<String> matar = new ClaseGenerica<String>("Test");
                    matar.classType();
                    // Label secondLabel = new Label("Not yet implemented");

                    break;
            }
        });

        return Pieza;
    }

    public static class CajaMenu extends StackPane {

        public CajaMenu(String title, ItemsMenu... items) {
            Rectangle bg = new Rectangle(639.4, 650);
            bg.setOpacity(0.2);

            DropShadow shadow = new DropShadow(7, 5, 0, Color.BLACK);
            shadow.setSpread(0.8);

            bg.setEffect(shadow);

            Text text = new Text(title + "");
            text.setFont(font);
            text.setFill(Color.WHITE);

            Line hSep = new Line();
            hSep.setEndX(250);
            hSep.setStroke(Color.DARKGREEN);
            hSep.setOpacity(0.4);

            Line vSep = new Line();
            vSep.setStartX(300);
            vSep.setEndX(300);
            vSep.setEndY(600);
            vSep.setStroke(Color.DARKGREEN);
            vSep.setOpacity(0.4);

            VBox vbox = new VBox();
            vbox.setAlignment(Pos.TOP_LEFT);
            vbox.setPadding(new Insets(60, 0, 0, 0));
            vbox.getChildren().addAll(text, hSep);
            vbox.getChildren().addAll(items);

            setAlignment(Pos.TOP_RIGHT);
            getChildren().addAll(bg, vSep, vbox);
        }

        public void show() {
            setVisible(true);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), this);
            tt.setToX(0);
            tt.play();
        }

        public void hide() {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), this);
            tt.setToX(-639.4);
            tt.setOnFinished(event -> setVisible(false));
            tt.play();
        }
    }

    public static class ItemsMenu extends StackPane {

        public ItemsMenu(String name) {
            Rectangle bg = new Rectangle(300, 24);

            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
                new Stop(0, Color.BLACK),
                new Stop(0.2, Color.DARKGREY)
            });

            bg.setFill(gradient);
            bg.setVisible(false);
            bg.setEffect(new DropShadow(5, 0, 5, Color.BLACK));

            Text text = new Text(name + "      ");
            text.setFill(Color.LIGHTGREY);
            text.setFont(Font.font(20));

            setAlignment(Pos.CENTER_RIGHT);
            getChildren().addAll(bg, text);

            setOnMouseEntered(event -> {
                bg.setVisible(true);
                text.setFill(Color.WHITE);
            });

            setOnMouseExited(event -> {
                bg.setVisible(false);
                text.setFill(Color.LIGHTGREY);
            });

            setOnMousePressed(event -> {
                bg.setFill(Color.WHITE);
                text.setFill(Color.BLACK);
            });

            setOnMouseReleased(event -> {
                bg.setFill(gradient);
                text.setFill(Color.WHITE);
            });
        }
    }
   static Jugador f;
    public static void main(String[] args) throws RemoteException, NotBoundException {

        Jugador f = new Jugador("Marc", 12) {};
        Jugador t = new Jugador("Víctor", 12) {};
        Jugador r = new Jugador("Sheng", 12) {};
        HashSet<Jugador> puntos = new HashSet<>();

        puntos.add((Jugador) f);
        puntos.add((Jugador) t);
        puntos.add((Jugador) r);
        System.out.println("JUGADORES SET:"+puntos.size());
        Map<Jugador, String> m = new HashMap<>();

        m.put((Jugador) f, "12");
        m.put((Jugador) t, "12");
        m.put((Jugador) r, "12");

        System.out.println(m.keySet());
        System.out.println("JUGADORES:"+m.size());


        LinkedList<String> gente = new LinkedList<>();
        gente.addLast("MARC");
        gente.addLast("SHENG");
        gente.addLast("VICTOR");


        ListIterator<String> iterador = gente.listIterator(); 
        iterador.next();
        iterador.next();
        iterador.next();


        // Imprimimos todos los elementos
        for(String nombre : gente)
               System.out.print(nombre + " 12 ");
        System.out.println();
        
        try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("JUEGO");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("jugador");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr attr = doc.createAttribute("id");
		attr.setValue("1001");
		staff.setAttributeNode(attr);

		// shorten way
		// staff.setAttribute("id", "1");

		// firstname elements
		Element firstname = doc.createElement("nombre");
		firstname.appendChild(doc.createTextNode("Victor"));
		staff.appendChild(firstname);

		// lastname elements
		Element lastname = doc.createElement("apellido");
		lastname.appendChild(doc.createTextNode("Marchante"));
		staff.appendChild(lastname);

		// nickname elements
		Element nickname = doc.createElement("nick");
		nickname.appendChild(doc.createTextNode("VictorM"));
		staff.appendChild(nickname);

		// salary elements
		Element salary = doc.createElement("puntos");
		salary.appendChild(doc.createTextNode("12"));
		staff.appendChild(salary);
                
                Element staf = doc.createElement("jugador");
		rootElement.appendChild(staf);

		// set attribute to staff element
		Attr att = doc.createAttribute("id");
		att.setValue("2001");
		staf.setAttributeNode(att);

		// shorten way
		// staff.setAttribute("id", "1");

		// firstname elements
		Element firstnam = doc.createElement("nombre");
		firstnam.appendChild(doc.createTextNode("Sheng"));
		staf.appendChild(firstnam);

		// lastname elements
		Element lastnam = doc.createElement("apellido");
		lastnam.appendChild(doc.createTextNode("Ye"));
		staf.appendChild(lastnam);

		// nickname elements
		Element nicknam = doc.createElement("nick");
		nicknam.appendChild(doc.createTextNode("ShengY"));
		staf.appendChild(nicknam);

		// salary elements
		Element salar = doc.createElement("puntos");
		salar.appendChild(doc.createTextNode("12"));
		staf.appendChild(salar);
                
                
                Element sta = doc.createElement("jugador");
		rootElement.appendChild(sta);

		// set attribute to staff element
		Attr at = doc.createAttribute("id");
		at.setValue("3001");
		sta.setAttributeNode(at);

		// shorten way
		// staff.setAttribute("id", "1");

		// firstname elements
		Element firstna = doc.createElement("nombre");
		firstna.appendChild(doc.createTextNode("Marc"));
		sta.appendChild(firstna);

		// lastname elements
		Element lastna = doc.createElement("apellido");
		lastna.appendChild(doc.createTextNode("Llobera"));
		sta.appendChild(lastna);

		// nickname elements
		Element nickna = doc.createElement("nick");
		nickna.appendChild(doc.createTextNode("MarcL"));
		sta.appendChild(nickna);

		// salary elements
		Element sala = doc.createElement("puntos");
		sala.appendChild(doc.createTextNode("12"));
		sta.appendChild(sala);

                
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("file2.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("File saved!");

	  } catch (ParserConfigurationException | TransformerException pce) {
	  }
        try {
               File fXmlFile = new File("C:\\Users\\victor\\Documents\\NetBeansProjects\\Damas3\\damas3/file2.xml");
		
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);

	
	doc.getDocumentElement().normalize();

	NodeList nList = doc.getElementsByTagName("jugador");

	System.out.println("----------------------------");

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			System.out.println("Jugador id : " + eElement.getAttribute("id"));
			System.out.println("Nombre : " + eElement.getElementsByTagName("nombre").item(0).getTextContent());
			System.out.println("Apellido : " + eElement.getElementsByTagName("apellido").item(0).getTextContent());
			System.out.println("Nick  : " + eElement.getElementsByTagName("nick").item(0).getTextContent());
			System.out.println("Puntos : " + eElement.getElementsByTagName("puntos").item(0).getTextContent());

		}
	}
    } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
    }
        
        launch(args);
    }

}

class ClaseGenerica<T> {

    T obj;

    public ClaseGenerica(T o) {
        obj = o;
    }

    public static void classType() {

        Label secondLabel = new Label("pieza muerta ");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        Scene secondScene = new Scene(secondaryLayout, 400, 110);
        Stage secondStage = new Stage();
        secondStage.setTitle("pieza Muerta");
        secondStage.setScene(secondScene);
        secondStage.setMaxWidth(230);
        secondStage.setMaxHeight(110);
        secondStage.show();
    }

    public static void nulo() {

        Label secondLabel = new Label("error movimiento");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        Scene secondScene = new Scene(secondaryLayout, 400, 110);
        Stage secondStage = new Stage();
        secondStage.setTitle("pieza mismo sitio");
        secondStage.setScene(secondScene);
        secondStage.setMaxWidth(230);
        secondStage.setMaxHeight(110);
        secondStage.show();
    }

    public static void moveNegro() {

        Label secondLabel = new Label("moveNegro");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        Scene secondScene = new Scene(secondaryLayout, 400, 110);
        Stage secondStage = new Stage();
        secondStage.setTitle("moveNegro");
        secondStage.setScene(secondScene);
        secondStage.setMaxWidth(230);
        secondStage.setMaxHeight(110);
        secondStage.show();
    }

    public static void moveBlanco() {

        Label secondLabel = new Label("moveBlanco");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);
        Scene secondScene = new Scene(secondaryLayout, 400, 110);
        Stage secondStage = new Stage();
        secondStage.setTitle("moveBlanco");
        secondStage.setScene(secondScene);
        secondStage.setMaxWidth(230);
        secondStage.setMaxHeight(110);
        secondStage.show();
    }
}
