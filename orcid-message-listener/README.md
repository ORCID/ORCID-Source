ORCID - ActiveMQ
================

Stand-alone web application that listens to message queues on a JMS broker (ActiveMQ)

Use
---
All classes in the org.orcid.listener package will be scanned by the spring context.  Any with @Component annotations will be picked up and added to the context.

To make a listener, simply create a a class with the @Component annotation, and use the spring JMS annotations from there.  e.g. to listen to the "updated_orcids" queue:

    @JmsListener(id="LastModifiedListener", destination=MessageConstants.Queues.UPDATED_ORCIDS)
    public void processMessage(final Map<String,String> map) {
        LOG.debug("Recieved last updated message");
        map.forEach((k,v)->LOG.debug(k+"->"+v));            
    }

Or use a common class from orcid-utils
    LastModifiedMessage m = new LastModifiedMessage(map);
    LOG.debug(m.getOrcid());

To find the type of a message or any other field:
    String type = map.get(MessageConstants.TYPE);