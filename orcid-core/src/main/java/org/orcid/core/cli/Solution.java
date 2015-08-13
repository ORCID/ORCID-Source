package org.orcid.core.cli;

public class Solution {
    public int solution(int[] A) {
        long leftSum = 0;
        long rightSum = 0;
        
        for(int i = 0; i < A.length; i++) {
            leftSum = rightSum = 0;
            
            if(i == 0) {
                leftSum = 0;
            } else {
                for(int l = 0; l < i; l++) {
                    leftSum += A[l];
                }
            }
            
            if(i == (A.length - 1)) {
                rightSum = 0;
            } else {
                for(int r = i + 1; r < A.length; r++) {
                    rightSum += A[r];
                }
            }
            
            if(leftSum == rightSum) {
                return i;
            }
        }
        
        return -1;
    }
    
    
}
