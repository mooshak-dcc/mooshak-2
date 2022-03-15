#include <stdio.h>

main(){
   int vetor [1000];
   int n=0,i=0,j=0,qn=0, result=0;

  scanf ("%d", &n);

  for (i=0; i<=n;i++){
    scanf ("%d", &qn);
    vetor[i]=qn;
    }

  for (j=0; j<=vetor[j]; j++) {
     if (j==0){
       if (vetor[j]>vetor[j+1]*2){
        result++;
        }
     }
     else if ((vetor[j]>vetor[j+1]*2) && (vetor[j]>vetor[j-1]*2)) {
        result++;
     }
  }
  printf("%d", result);
  return 0;

}
