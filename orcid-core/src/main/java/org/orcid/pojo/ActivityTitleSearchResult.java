package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class ActivityTitleSearchResult implements ErrorsInterface {
    
   private  List<ActivityTitle> results;
   private int offset;
   private int totalCount;
   private int pageSize;
   private List<String> errors = new ArrayList<String>();
   
   
   public ActivityTitleSearchResult(List<ActivityTitle> results, int offset, int totalCount) {
       this.results = results;
       this.offset = offset;
       this.totalCount = totalCount;
   }
   
   public List<ActivityTitle> getResults() {
       return results;
   }
   
   public void setResults(List<ActivityTitle> results) {
       this.results = results;
   }
   
   public int getOffset() {
       return offset;
   }
   
   public void setOffset(int offset) {
       this.offset = offset;
   }
   
   public int getTotalCount() {
       return totalCount;
   }
   
       
   public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
   }
   
   @Override
   public List<String> getErrors() {
       return errors;
   }
   
   @Override
   public void setErrors(List<String> errors) {
       this.errors = errors;
   } 
   
   public int getPageSize() {
       return pageSize;
   }
   
   public void setPageSize(int pageSize) {
       this.pageSize = pageSize;
   }
     
}
