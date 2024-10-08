package org.orcid.core.cli;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.Locale;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class MigrateFundingAmountToANumericValue {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrateFundingAmountToANumericValue.class);
    private ProfileFundingDao profileFundingDao;
    private TransactionTemplate transactionTemplate;
    private ProfileDao profileDao;
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        profileDao = (ProfileDao) context.getBean("profileDao");
    }
    
    public void execute() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<ProfileFundingEntity> allEntityesWithAmount = profileFundingDao.getProfileFundingWithAmount();
                for(ProfileFundingEntity entity : allEntityesWithAmount) {
                    //Amount filed was removed by card https://trello.com/c/WxbJLlCt/1884-remove-the-amount-field-from-the-profilefundingentity-since-we-now-use-the-bigdecimal-numericamount
                    //String amount = entity.getAmount();
                    //So, lets leave this empty just in case we need a rollback later
                    String amount = "";
                    String currencyCode = entity.getCurrencyCode();
                    ProfileEntity profile = profileDao.find(entity.getOrcid());
                    Locale locale = getLocaleFromProfile(profile);
                    String fixedAmount = fixAmount(amount);
                    try {
                        BigDecimal bigDecimal = getAmountAsBigDecimal(fixedAmount, currencyCode, locale);
                        LOGGER.info("FROM: " + amount + " TO: " + fixedAmount + " BigDecimal: " + bigDecimal);
                        entity.setNumericAmount(bigDecimal);
                        profileFundingDao.merge(entity);
                    } catch (Exception e) {
                        LOGGER.error("Exception migrating: " + entity.getOrcid() + ", " + amount + " = " + fixedAmount);                        
                    }
                }
            }
        });
    }
    
    private BigDecimal getAmountAsBigDecimal(String amount, String currencyCode, Locale locale) throws Exception {
        try {                  
            ParsePosition parsePosition = new ParsePosition(0);
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Number number = null;
            if(!PojoUtil.isEmpty(currencyCode))  {                    
                Currency currency = Currency.getInstance(currencyCode);
                String currencySymbol = currency.getSymbol();            
                number = numberFormat.parse(amount.replace(currencySymbol, StringUtils.EMPTY), parsePosition);
            } else {
                number = numberFormat.parse(amount, parsePosition);
            }
            if(parsePosition.getIndex() != amount.length())
                throw new Exception("Unable to parse amount into BigDecimal"); 
            return new BigDecimal(number.toString());                          
        } catch(Exception e) {                
            throw e;
        }
    }
    
    private Locale getLocaleFromProfile(ProfileEntity profile){
        org.orcid.jaxb.model.common_v2.Locale orcidLocale = org.orcid.jaxb.model.common_v2.Locale.valueOf(profile.getLocale());
        String value = orcidLocale.value();
        Locale locale = null;
        if(value!= null) {
            if(value.contains("_")){
                String [] localeTokens = value.split("_");
                locale = new Locale(localeTokens[0], localeTokens[1]);
            } else {
                locale = new Locale(value);
            }
        }
        
        return locale;
    }
    
    public String fixAmount(String amount){
        if(StringUtils.isNotBlank(amount)) {
            amount = amount.trim();
            if(amount.contains("$")){
                amount = amount.replace("$", StringUtils.EMPTY);            
            } 
            
            if (amount.contains("€")) {
                amount = amount.replace("€", StringUtils.EMPTY);
            } 
            
            if (amount.contains(":")) {
                amount = amount.replace(":", ".");
            }
            
            if(amount.contains(" ")) {
                amount= amount.replaceAll("\\s", "");
            }
            
            if(amount.matches(".*\\,(\\d{1,2})")) {
                amount = amount.replaceAll("\\,(\\d{1,2})", "\\.$1");
            }
            
            if(amount.matches(".*\\.\\d{3}.*")) {
                amount = amount.replaceAll("\\.(\\d{3})", "\\,$1");
            }
        }
        
        return amount;
    }
    
    public void finish() {
        LOGGER.info("PROCESS FINISHED");
        System.exit(0);
    }
    
    public static void main(String[] args) {
        MigrateFundingAmountToANumericValue process = new MigrateFundingAmountToANumericValue();
        process.init();
        process.execute();  
        process.finish();
    }
}