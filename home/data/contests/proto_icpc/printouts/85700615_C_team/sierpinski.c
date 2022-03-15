#include <stdio.h>
#include <stdlib.h>


int main() {
  int n,i,j,old;
  int *v;

  scanf("%d",&n);

  v=(int *) malloc(n*sizeof(int));
  if (v==NULL) exit(-1);

  for(i=0;i<n;i++) {
    old=0;
    for(j=0;j<i;j++) {
      int k;
      k=v[j];
      v[j]=(old+v[j])%2;
      old=k;
      if (v[j]) printf("*"); else printf(".");
    }
    v[i]=1;
    printf("*\n"); 
  }

  free(v);
  return (0);
} 


