package behaviours;

import agents.BookBuyerAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.swing.JOptionPane;

public class RequestPerformer extends Behaviour{
  private AID bestSeller;
  private int bestPrice;
  private int repliesCount = 0;
  private MessageTemplate mt;
  private int step = 0;
  private BookBuyerAgent bbAgent;
  private String bookTitle;
  
  public RequestPerformer(BookBuyerAgent a) {
    bbAgent = a;
    bookTitle = a.getBookTitle();
  }
  
  public void action() {
    switch(step) {
    case 0:
      ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
      for(int i = 0; i < bbAgent.getSellerAgents().length; i++) {
        cfp.addReceiver(bbAgent.getSellerAgents()[i]);
      }
      
      cfp.setContent(bookTitle);
      cfp.setConversationId("book-trade");
      cfp.setReplyWith("cfp" + System.currentTimeMillis());
      myAgent.send(cfp);
      
      mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
          MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
      step = 1;
    break;
    
    case 1:
      ACLMessage reply = bbAgent.receive(mt);
      if(reply != null) {
        if(reply.getPerformative() == ACLMessage.PROPOSE) {
          int price = Integer.parseInt(reply.getContent());
          if(bestSeller == null || price < bestPrice) {
            bestPrice = price;
            bestSeller = reply.getSender();
          }
        }
        repliesCount++;
        if(repliesCount >= bbAgent.getSellerAgents().length) {
          step = 2;
        }
      } else {
        block();
      }
    break;
    
    case 2:
      ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
      order.addReceiver(bestSeller);
      order.setContent(bookTitle);
      order.setConversationId("book-trade");
      order.setReplyWith("order" + System.currentTimeMillis());
      bbAgent.send(order);
      
      mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
          MessageTemplate.MatchInReplyTo(order.getReplyWith()));
      
      step = 3;
      
    break;
    
    case 3:      
      reply = myAgent.receive(mt);
      if (reply != null) {
         if (reply.getPerformative() == ACLMessage.INFORM) {
            JOptionPane.showMessageDialog(null,bookTitle+" comprado con éxito del agente "+reply.getSender().getName());
            System.out.println("Precio = "+bestPrice);
            myAgent.doDelete();
         }
         else {
            System.out.println("Intento fallido: el libro solicitado ya se vendió.");
         }

         step = 4;
      }
      else {
         block();
      }
      break;
    }
  }
  
  public boolean done() {
    if (step == 2 && bestSeller == null) {
       System.out.println("Intento fallido: "+bookTitle+" no disponible para vender");
    }
    return ((step == 2 && bestSeller == null) || step == 4);
 }
}
