package agents;
import gui.BookBuyerGui;
import jade.core.Agent;
import behaviours.RequestPerformer;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookBuyerAgent extends Agent {
  private String bookTitle;//El titulo del libro
  private AID[] sellerAgents;//El array contiene a los agentes vendedores posibles para comprar el libro
  private int ticker_timer = 10000;//Cada 10 segundos recibe se actualización de los vendedores disponibles
  private BookBuyerAgent this_agent = this;
  private BookBuyerGui gui;
  private boolean ban = true;
  protected void setup() {
    System.out.println("Agente comprador " + getAID().getName() + " listo");
    gui = new BookBuyerGui(this);//Se abre la interfaz en esta clase
    gui.showGui();
    

    if(ban) {
      bookTitle = getBookTitle();

      System.out.println("Libro: " + bookTitle);
      
      addBehaviour(new TickerBehaviour(this, ticker_timer) {
        protected void onTick() {
          System.out.println("Trying to buy " + bookTitle);
          
          DFAgentDescription template = new DFAgentDescription();
          ServiceDescription sd = new ServiceDescription();
          sd.setType("book-selling");
          template.addServices(sd);
          
          try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            System.out.println("Encontré los siguientes agentes de ventas:");
            sellerAgents = new AID[result.length];
            for(int i = 0; i < result.length; i++) {
              sellerAgents[i] = result[i].getName();
              System.out.println(sellerAgents[i].getName());
            }
            
          }catch(FIPAException fe) {
            fe.printStackTrace();
          }

          myAgent.addBehaviour(new RequestPerformer(this_agent));
        }
      });
    } else {
      System.out.println("No se ha especificado ningún título de libro a comprar");
      doDelete();
    }
  }
  
  protected void takeDown() {
      gui.dispose();//Limpiamos la interfaz
      System.out.println("Agente comprador " + getAID().getName() + " finalizado");
      ban = false;
  }
  
  public AID[] getSellerAgents() {
    return sellerAgents;
  }
  
  public String getBookTitle() {
    return bookTitle;
  }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
  
}
