package org.orcid.core.oauth.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import junit.framework.Assert;

public class NamespacedRandomCodeGeneratorTest {

    private static final String base62 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    @Test
    public void test(){
        //check 1 node uses all chars
        NamespacedRandomCodeGenerator g = new NamespacedRandomCodeGenerator(1,1);
        Set<Character> unique = new HashSet<Character>();
        for(char c : base62.toCharArray()) {
            unique.add(c);
        }
        for (int i=0;i<100000;i++){
            String code = g.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique.remove(code.charAt(0));
        }
        System.out.println(unique);
        Assert.assertEquals(0,unique.size());
        
        //check two nodes are 0-30 and 31-61
        NamespacedRandomCodeGenerator g1_2 = new NamespacedRandomCodeGenerator(1,2);
        NamespacedRandomCodeGenerator g2_2 = new NamespacedRandomCodeGenerator(2,2);
        Set<Character> unique1_2 = new HashSet<Character>();
        Set<Character> unique2_2 = new HashSet<Character>();
        int j = 0;
        for(char c : base62.toCharArray()) {
            if (j<31)
                unique1_2.add(c);
            else
                unique2_2.add(c);
            j++;
        }
        for (int i=0;i<100000;i++){
            String code = g1_2.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique1_2.remove(code.charAt(0));
            unique2_2.remove(code.charAt(0));
        }
        Assert.assertEquals(0,unique1_2.size()); //only removed from first half
        Assert.assertEquals(31,unique2_2.size());//all still there
        
        //do it again for the second generator
        j = 0;
        for(char c : base62.toCharArray()) {
            if (j<31)
                unique1_2.add(c);
            else
                unique2_2.add(c);
            j++;
        }
        for (int i=0;i<100000;i++){
            String code = g2_2.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique1_2.remove(code.charAt(0));
            unique2_2.remove(code.charAt(0));
        }
        Assert.assertEquals(31,unique1_2.size()); //all still there
        Assert.assertEquals(0,unique2_2.size());//only removed from second half

        //--- now three nodes
       //check two nodes are 0-30 and 31-61
        NamespacedRandomCodeGenerator g1_3 = new NamespacedRandomCodeGenerator(1,3);
        NamespacedRandomCodeGenerator g2_3 = new NamespacedRandomCodeGenerator(2,3);
        NamespacedRandomCodeGenerator g3_3 = new NamespacedRandomCodeGenerator(3,3);
        Set<Character> unique1_3 = new HashSet<Character>();
        Set<Character> unique2_3 = new HashSet<Character>();
        Set<Character> unique3_3= new HashSet<Character>();
        j = 0;
        for(char c : base62.toCharArray()) {
            if (j<20)
                unique1_3.add(c);
            else if (j<40)
                unique2_3.add(c);
            else
                unique3_3.add(c); // has the extra two chars!
            j++;
        }
        for (int i=0;i<100000;i++){
            String code = g1_3.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique1_3.remove(code.charAt(0));
            unique2_3.remove(code.charAt(0));
            unique3_3.remove(code.charAt(0));
        }
        Assert.assertEquals(0,unique1_3.size()); //only removed from first third
        Assert.assertEquals(20,unique2_3.size());//all still there
        Assert.assertEquals(22,unique3_3.size());//all still there
        
        //node 2 of 3
        j = 0;
        for(char c : base62.toCharArray()) {
            if (j<20)
                unique1_3.add(c);
            else if (j<40)
                unique2_3.add(c);
            else
                unique3_3.add(c); // has the extra two chars!
            j++;
        }
        for (int i=0;i<100000;i++){
            String code = g2_3.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique1_3.remove(code.charAt(0));
            unique2_3.remove(code.charAt(0));
            unique3_3.remove(code.charAt(0));
        }
        Assert.assertEquals(20,unique1_3.size());
        Assert.assertEquals(0,unique2_3.size());
        Assert.assertEquals(22,unique3_3.size());
        
        //node 3 of 3
        j = 0;
        for(char c : base62.toCharArray()) {
            if (j<20)
                unique1_3.add(c);
            else if (j<40)
                unique2_3.add(c);
            else
                unique3_3.add(c); // has the extra two chars!
            j++;
        }
        for (int i=0;i<100000;i++){
            String code = g3_3.nextRandomCode();
            if (!base62.contains(""+code.charAt(0))){
                fail("Char outside base 62");
            }
            unique1_3.remove(code.charAt(0));
            unique2_3.remove(code.charAt(0));
            unique3_3.remove(code.charAt(0));
        }
        Assert.assertEquals(20,unique1_3.size());
        Assert.assertEquals(20,unique2_3.size());
        Assert.assertEquals(0,unique3_3.size());
    }
}
