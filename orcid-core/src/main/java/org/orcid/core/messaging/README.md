Add message listeners here.
Spring config for messaging is in orcid-persistence

Example listener.  use @Component to have the bean registered in the context.  Replies are optional.

	@Component
	public class AMessageListener {
	
	    @JmsListener(destination=JmsMessageSender.TEST)
	    @SendTo(JmsMessageSender.TEST_REPLY)
	    public String processMessage(String text) {
	      lastMessage = text;
	      return "Echo: "+ text;
	    }
	}