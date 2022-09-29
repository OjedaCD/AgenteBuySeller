package agents;

import java.util.Hashtable;

import behaviours.OfferRequestServer;
import behaviours.PurchaseOrderServer;
import gui.BookSellerGui;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookSellerAgent extends Agent{

	private Hashtable catalogue;//Objeto para manejar el catalogo
	private BookSellerGui gui;//Objeto para la comunicación con la gui
	
	protected void setup() {
	  catalogue = new Hashtable();//Se crea el catalogo 
	  
	  gui = new BookSellerGui(this);//Se abre la interfaz en esta clase
	  gui.showGui();//Se inicia la interfaz
	  
	  DFAgentDescription dfd = new DFAgentDescription();
	  dfd.setName(getAID());//Creamos el objeto que almacenara los libros en el agente
	  
	  ServiceDescription sd = new ServiceDescription();
	  sd.setType("book-selling");//Almacenamos el precio 
	  sd.setName("book-trading");//Almacenamos el nombre
	  dfd.addServices(sd);
	  
	  try {
	    DFService.register(this, dfd);//Lo registramos en las paginas amarillas
	  }catch(FIPAException fe) {
	    fe.printStackTrace();
	  }
	  //Añadimos los comportamientos de los agentes
	  addBehaviour(new OfferRequestServer(this));//El comportamiento que atiende a las consultas de los compradores
	  
	  addBehaviour(new PurchaseOrderServer(this));//El comportamiento de entrega de órdenes de compra del comprador
	}
	
	protected void takeDown() {
	  try {
	    DFService.deregister(this);//Desregistramos el libro que hayamos vendido del hash
	  }catch(FIPAException fe) {
	    fe.printStackTrace();
	  }
	  
	  gui.dispose();//Limpiamos la interfaz 
	  
	  System.out.println("Agente Vendedro " + getAID().getName() + " finalizado");//Finaliza el agente
	}
	
	public void updateCatalogue(final String title, final int price) {
	  addBehaviour(new OneShotBehaviour() {
	    public void action() {
	      catalogue.put(title, price);
	      System.out.println(title + " insertado con un precio de " + price);
	    }
	  });
	}
	
	public Hashtable getCatalogue() {
	  return catalogue;
	}
}
