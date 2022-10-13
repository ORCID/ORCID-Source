package org.orcid.utils.jersey;

public class JerseyClientResponse<T, E> {

    private int status;
    
    private T entity;
    
    private E error;
    
    public JerseyClientResponse(int status, T t, E e) {
        this.status = status;
        this.entity = t;
        this.error = e;
    }

    public int getStatus() {
        return status;
    }

    public T getEntity() {
        return entity;
    }

    public E getError() {
        return error;
    }            
}
